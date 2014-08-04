/*
 * PubMatic Inc. (“PubMatic”) CONFIDENTIAL
 * Unpublished Copyright (c) 2006-2014 PubMatic, All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of PubMatic. The intellectual and technical concepts contained
 * herein are proprietary to PubMatic and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from PubMatic.  Access to the source code contained herein is hereby forbidden to anyone except current PubMatic employees, managers or contractors who have executed 
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes  
 * information that is confidential and/or proprietary, and is a trade secret, of  PubMatic.   ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE, 
 * OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF PubMatic IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE 
 * LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS  
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.                
 */

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
