
package com.adserver.adview.ormma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.StatFs;
import android.view.View;
import android.view.Window;
import android.webkit.URLUtil;

import com.adserver.adview.MASTAdServerViewCore;
import com.adserver.adview.ormma.util.FileComparatorByDate;

public class OrmmaAssetController extends OrmmaController {
	private static final String ASSETS_DIRECTORY = "ormma_assets";

	public OrmmaAssetController(MASTAdServerViewCore adView, Context c) {
		super(adView, c);
	}

	public String copyTextFromJarIntoAssetDir(String alias, String source) {
		try {
			InputStream in = OrmmaAssetController.class.getResourceAsStream(source);
			File writeFile = new File(mContext.getFilesDir(), alias);
			return writeToDisk(in, writeFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addAsset(String url, String alias) {
		try {
			mOrmmaView.ormmaEvent("addasset", "url="+url+";alias="+alias);
			if(url.startsWith("ormma://screenshot")) {
				Activity parent = (Activity)mContext;
		        Window window = parent.getWindow();
				InputStream in = getScreenshot(window.getDecorView());

				if(in != null) {
					writeAssetToDisk(in, alias);
					mOrmmaView.injectJavaScript("Ormma.addedAsset('" + alias + "' )");
				}
			} else if(url.startsWith("ormma://photo")) {
				try {
					final String copyAlias = new String(alias);
					Camera.PictureCallback jpegPictureCallback = new Camera.PictureCallback() {
						@Override
						public void onPictureTaken(byte[] data, Camera  camera) {
							try {
								InputStream in = new ByteArrayInputStream(data);
								writeAssetToDisk(in, copyAlias);
								mOrmmaView.injectJavaScript("Ormma.addedAsset('" + copyAlias + "' )");
							} catch (Exception e) {
								mOrmmaView.injectJavaScript("Ormma.fireError(\"addAsset\",\"File is not saved in cache\")");
							}
						}
					};
					
					Camera camera = Camera.open();
					try {
						camera.startPreview();
					} catch (Exception e) {
					}
					Thread.sleep(1000);
					camera.takePicture(null, null, jpegPictureCallback);
				} catch (Exception e) {
					mOrmmaView.injectJavaScript("Ormma.fireError(\"addAsset\",\"It is impossible to take a photo\")");
				}
			} else {
				HttpEntity entity = getHttpEntity(url);
				InputStream in = entity.getContent();
				writeAssetToDisk(in, alias);
				mOrmmaView.injectJavaScript("Ormma.addedAsset('" + alias + "' )");
	
				try {
					entity.consumeContent();
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"addAsset\",\"File is not saved in cache\")");
		}
	}

	public void addAssets(String assets) {
		try {
			JSONObject json = new JSONObject(assets);

			for(Iterator iterator = json.keys(); iterator.hasNext();) {
				String alias = (String)iterator.next();
				String url = json.getString(alias);
				addAsset(url, alias);
			}
		} catch (JSONException e) {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"addAssets\",\"Incorrect argument\")");
		}
	}

	public String getAssetURL(String alias) {
		File dir = getAssetDir(getAssetPath(alias));
		File writeFile = new File(dir, getAssetName(alias));
		return "file://" + writeFile.getAbsolutePath();
	}
	
	private InputStream getScreenshot(View view) {
		try {
			view.setDrawingCacheEnabled(true);
			Bitmap screenshot = Bitmap.createBitmap(view.getDrawingCache());
			view.setDrawingCacheEnabled(false); 
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			screenshot.compress(Bitmap.CompressFormat.PNG, 90, out);
			
			byte[] bs = out.toByteArray();
			return new ByteArrayInputStream(bs);
		} catch (Exception e) {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"addAsset\",\"It is impossible to make a screenshot\")");
		}
		return null;
	}

	public static String getHttpContent(String url) throws Exception {
		HttpEntity entity = getHttpEntity(url);
		InputStream in = entity.getContent();
		
		byte buff[] = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		do {
			int numread = in.read(buff);
			if (numread <= 0)
				break;
			out.write(buff, 0, numread);
		} while (true);
		
		return out.toString();
	}
	
	/**
	 * get the http entity at a given url
	 */
	private static HttpEntity getHttpEntity(String url) {
		HttpEntity entity = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);
			entity = response.getEntity();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return entity;
	}

	public long getCacheRemaining() {
		File filesDir = mContext.getFilesDir();
		StatFs stats = new StatFs(filesDir.getPath());
		long free = stats.getAvailableBlocks() * stats.getBlockSize();
		return free;
	}

	public void storePicture(String url) {
		try {
			HttpEntity entity = getHttpEntity(url);
			InputStream in = entity.getContent();
			
			File dir = new File("/sdcard/ORMMAGallery/");
			dir.mkdirs();
			
			Header contentTypeHeader = entity.getContentType();
			String contentType = null;
			if(contentTypeHeader != null) {
				contentType = contentTypeHeader.getValue();
			}
			String fileName = URLUtil.guessFileName(url, null, contentType);
			File writeFile = new File(dir, fileName);
			
			writeToDisk(in, writeFile);
	
			try {
				entity.consumeContent();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"storePicture\",\"File is not saved in the device's photo album\")");
		}
	}
	
	public void writeAssetToDisk(InputStream in, String file) throws IllegalStateException, IOException {
		int i = 0;
		byte buff[] = new byte[1024];
		FileOutputStream out = getAssetOutputString(file);

		do {
			int numread = in.read(buff);
			if (numread <= 0)
				break;

			if(!retireAssets(numread)) {
				mOrmmaView.injectJavaScript("Ormma.fireError(\"addAsset\",\"No free memory\")");
				return;
			}
			
			out.write(buff, 0, numread);
			i++;
		} while (true);
		
		out.flush();
		out.close();
	}

	public String writeToDisk(InputStream in, File writeFile) throws IllegalStateException, IOException {
		int i = 0;
		byte buff[] = new byte[1024];
		FileOutputStream out = new FileOutputStream(writeFile);

		do {
			int numread = in.read(buff);
			if (numread <= 0)
				break;

			out.write(buff, 0, numread);
			i++;
		} while (true);
		
		out.flush();
		out.close();
		return writeFile.getAbsolutePath();
//		return "";
	}
	
	public FileOutputStream getAssetOutputString(String asset) throws IOException {
		File dir = getAssetDir(getAssetPath(asset));
		dir.mkdirs();
		File file = new File(dir, getAssetName(asset));
		return new FileOutputStream(file);
	}

	public void removeAsset(String alias) {
		mOrmmaView.ormmaEvent("removeasset", "alias="+alias);
		File dir = getAssetDir(getAssetPath(alias));
		File file = new File(dir, getAssetName(alias));
		
		if(file.exists()) {
			file.delete();
			mOrmmaView.injectJavaScript("Ormma.assetRemoved('" + alias + "' )");
		} else {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"removeAsset\",\"File not exists\")");
		}
	}

	public void removeAllAssets() {
		mOrmmaView.ormmaEvent("removeallassets", "");
		try {
			File dir = getAssetDir("");
			removeAssetsFolder(dir);
		} catch (Exception e) {
			mOrmmaView.injectJavaScript("Ormma.fireError(\"removeAllAssets\",\"Internal error\")");
		}
	}
	
	private boolean retireAssets(long needBytes) {
		File assetDir = getAssetDir("");
		
		if(getCacheRemaining() > needBytes) {
			return true;
		} else {
			List<File> assetFiles = getAllFilesSortedByDate(assetDir);
			int n = 0;
			int numberFiles = assetFiles.size();
			
			while((getCacheRemaining() < needBytes) && (numberFiles > 0) && (n < numberFiles)) {
				File assetFile = assetFiles.get(0);

				if(assetFile.delete()) {
					assetFiles.remove(0);
					String alias = getAlias(assetFile);

					if(alias != null) {
						mOrmmaView.injectJavaScript("Ormma.assetRetired('" + alias + "' )");
					}
				}
				n++;
			}
			
			if(getCacheRemaining() < needBytes) {
				return false;
			} else {
				return true;
			}
		}
	}

	private List<File> getAllFilesSortedByDate(File folder) {
		List<File> files = new Vector<File>();
		files = getAllFiles(folder, files);
        sort(files, new FileComparatorByDate());
		return files;
	}
	
	private <T> void sort(List<T> list, Comparator<? super T> c) {
		Object[] a = list.toArray();
		Arrays.sort(a, (Comparator)c);
		ListIterator i = list.listIterator();
		for (int j=0; j<a.length; j++) {
			i.next();
			i.set(a[j]);
		}
	}
	
	private List<File> getAllFiles(File folder, List<File> files) {
		if (folder == null)
			return files;
		if (folder.exists() && folder.isDirectory()) {
			File[] innerFiles = folder.listFiles();
			for (File file : innerFiles) {
				if (file.isFile()) {
					files.add(file);
				} else if(file.isDirectory()) {
					getAllFiles(file, files);
				}
			}
			return files;
		} else {
			return files;
		}
	}
	
	private boolean removeAssetsFolder(File folder) {
		if (folder == null)
			return false;
		if (folder.exists() && folder.isDirectory()) {
			File[] innerFiles = folder.listFiles();
			for (File file : innerFiles) {
				if (file.isFile()) {
					if (!file.delete()) {
						return false;
					} else {
						String alias = getAlias(file);

						if(alias != null) {
							mOrmmaView.injectJavaScript("Ormma.assetRemoved('" + alias + "' )");
						}
					}
				} else if(file.isDirectory())
					removeAssetsFolder(file);
			}
			return folder.delete();
		} else {
			return false;
		}
	}

	private String getAlias(File assetFile) {
		File assetDir = getAssetDir("");
		int startAlias = assetFile.getAbsolutePath().indexOf(assetDir.getAbsolutePath());

		if(startAlias >= 0) {
			return assetFile.getAbsolutePath().substring(startAlias + 
					assetDir.getAbsolutePath().length() + 1);
		} else {
			return null;
		}
	}
	
	private File getAssetDir(String path) {
		File filesDir = mContext.getFilesDir();
		File newDir = new File(filesDir.getPath() + java.io.File.separator + 
				ASSETS_DIRECTORY + java.io.File.separator + path);
		return newDir;
	}

	private String getAssetPath(String asset) {
		int lastSep = asset.lastIndexOf(java.io.File.separatorChar);
		String path = "/";

		if (lastSep >= 0) {
			path = asset.substring(0, lastSep);
		}
		return path;
	}

	private String getAssetName(String asset) {
		int lastSep = asset.lastIndexOf(java.io.File.separatorChar);
		String name = asset;

		if (lastSep >= 0) {
			name = asset.substring(lastSep + 1);
		}
		return name;
	}

}
