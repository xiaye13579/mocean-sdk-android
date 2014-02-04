//
// Copyright (C) 2013 Mocean Mobile. All Rights Reserved. 
//
package com.moceanmobile.mast;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageView extends android.widget.ImageView 
{
	private GifDecoder gifDecoder = null;
	private Worker gifWorker = null;
	
	public ImageView(Context context)
	{
		super(context);
	}

	@Override
	public void setImageDrawable(Drawable drawable)
	{
		resetGifState();
		super.setImageDrawable(drawable);
	}
	
	// main thread
	public void setImageGifDecoder(GifDecoder gifDecoder)
	{
		if ((gifDecoder != null) && (gifDecoder.getFrameCount() == 0))
			gifDecoder = null;
		
		resetGifState();
		
		if (gifDecoder == null)
			return;
		
		this.gifDecoder = gifDecoder;
		
		gifWorker = new Worker();
		gifWorker.start();
	}
	
	public GifDecoder getImageGifDecoder()
	{
		return gifDecoder;
	}
	
	private void resetGifState()
	{
		if (gifWorker != null)
		{
			gifWorker.interrupt();
			gifWorker = null;
		}
		
		super.setImageDrawable(null);
	}
	
	private void setGifBitmap(Bitmap bitmap)
	{
		if (bitmap == null)
			return;
		
		// Fetch the current bitmap so it can be recycled.
		Bitmap oldBitmap = null;
		Drawable drawable = getDrawable();
		if (drawable instanceof BitmapDrawable)
		{
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			oldBitmap = bitmapDrawable.getBitmap();
		}
		
		drawable = new BitmapDrawable(getContext().getResources(), bitmap);
		super.setImageDrawable(drawable);
		
		if (oldBitmap != null)
		{
			oldBitmap.recycle();
		}
	}
	
	private class Worker extends Thread
	{
		public void run()
		{
			try
			{
				int frameCount = gifDecoder.getFrameCount();
				int loopCount = gifDecoder.getLoopCount();
				boolean repeat = false;
				if (loopCount == 0)
					repeat = true;
				
				while ((gifWorker == this) && (repeat || (loopCount-- > 0)))
				{
					for (int i = 0; i < frameCount; ++i)
					{
						int interval = gifDecoder.getDelay(i);
						if (interval < 0)
							interval = 100;

						int[] frame = gifDecoder.getFrame(i);
						
						final Bitmap bitmap = Bitmap.createBitmap(frame, 
								gifDecoder.getWidth(), gifDecoder.getHeight(), Config.ARGB_4444);
						
						((Activity) getContext()).runOnUiThread(new Runnable()
						{
							public void run()
							{
								setGifBitmap(bitmap);
							}
						});
						
						sleep(interval);
					}
				}
			}
			catch (Exception ex)
			{
				// Let the thread die
			}
		}
	}
}
