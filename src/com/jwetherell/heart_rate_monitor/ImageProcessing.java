package com.jwetherell.heart_rate_monitor;


/**
 * This abstract class is used to process images.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public abstract class ImageProcessing {
	public static int[] decodeYUV420SPtoLuma(byte[] yuv420sp, int width, int height) {
		if (yuv420sp==null) return null;
		
		final int frameSize = width * height;
		int[] hsl = new int[frameSize];

	    for (int j = 0, yp = 0; j < height; j++) {
	        for (int i = 0; i < width; i++, yp++) {
	            int y = (0xff & ((int) yuv420sp[yp])) - 16;
	            if (y < 0) y = 0;
	            hsl[yp] = y;
	        }
	    }
	    return hsl;
	}
	
	public static int determineAverageGivenLuma(int[] data, int height, int width) {
		int average = 0;
		for (int y = 0, xy=0; y < height; y++) {
			for (int x = 0; x < width; x++, xy++) {
				average += data[xy];
			}
		}
		return (average / (width * height));
	}
}
