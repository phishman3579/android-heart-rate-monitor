package com.jwetherell.heart_rate_monitor;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;


/**
 * This class extends Activity to handle a picture preview, process the preview for a luma value 
 * and determine a heart beat.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class HeartRateMonitor extends Activity {
	private static final String TAG = "HeartRateMonitor";

	private static SurfaceView preview = null;
	private static SurfaceHolder previewHolder = null;
	private static Camera camera = null;
	private static boolean inPreview = false;
	private static ImageView image = null;

	private static int averageIndex = 0;
	private static final int averageArraySize = 10;
	private static final int[] averageArray = new int[averageArraySize];

	private static enum TYPE { GREEN, RED };
	private static TYPE current = TYPE.GREEN;
	
	private static final AtomicBoolean processing = new AtomicBoolean(false);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		preview = (SurfaceView)findViewById(R.id.preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		image = (ImageView) findViewById(R.id.image);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onResume() {
		super.onResume();

		camera = Camera.open();
	}

	@Override
	public void onPause() {
		super.onPause();

		camera.setPreviewCallback(null);
		if (inPreview) camera.stopPreview();
		inPreview = false;
		camera.release();
		camera = null;
	}

	private PreviewCallback previewCallback = new PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] data, Camera cam) {
			if (!processing.compareAndSet(false, true)) return;
			
			if (data == null) return;
			Camera.Size size = cam.getParameters().getPreviewSize();
			if (size == null) return;

			int width = size.width;
			int height = size.height;
			
			long bConversion = System.currentTimeMillis();
			
			int[] img = ImageProcessing.decodeYUV420SPtoLuma(data.clone(), width, height);
			int imgAvg = ImageProcessing.determineAverageGivenLuma(img, height, width);
			Log.w(TAG, "Image average="+imgAvg);
			
			if (imgAvg>50 && imgAvg<200) {
				if (averageIndex==averageArraySize) averageIndex = 0;
				averageArray[averageIndex] = imgAvg;
				averageIndex++;
				
				int localAvg=0;
				int localCnt=0;
				for (int i=0; i<averageArray.length; i++) {
					if (averageArray[i]>0) {
						localAvg += averageArray[i];
						localCnt++;
					}
				}
				int rollingAverage = (localAvg/localCnt);
				if (imgAvg<rollingAverage) {
					if (current == TYPE.GREEN) {
						image.setImageResource(R.drawable.red_icon);
						image.invalidate();
						current = TYPE.RED;
					}
					Log.e(TAG, "Average="+rollingAverage);
				} else {
					if (current == TYPE.RED) {
						image.setImageResource(R.drawable.green_icon);
						image.invalidate();
						current = TYPE.GREEN;
					}
					Log.w(TAG, "Average="+rollingAverage);
				}
			}
			
			long aConversion = System.currentTimeMillis();
			Log.d(TAG, "Processing="+(aConversion-bConversion));
			
			processing.set(false);
		}
	};

	private SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera.setPreviewDisplay(previewHolder);
				camera.setPreviewCallback(previewCallback);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			Camera.Parameters parameters = camera.getParameters();
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			Camera.Size size = getBestPreviewSize(width, height, parameters);
			if (size!=null) {
				parameters.setPreviewSize(size.width, size.height);
				Log.d(TAG, "Using width="+size.width+" height="+size.height);
			}
			camera.setParameters(parameters);
			camera.startPreview();
			inPreview=true;
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// Ignore
		}
	};

	private static Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
		Camera.Size result=null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width<=width && size.height<=height) {
				if (result==null) {
					result=size;
				} else {
					int resultArea=result.width*result.height;
					int newArea=size.width*size.height;

					if (newArea>resultArea) result=size;
				}
			}
		}

		return result;
	}
}