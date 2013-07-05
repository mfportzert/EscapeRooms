package com.mfp.rooms.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

public class BitmapUtils {

	public static void drawAlphaSquareReduction(int x, int y, Bitmap bitmap, int squareSize, int alphaReduction) {
	
		int pixel;
		int alpha;
		int maskColor;
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		for (int i = x - (squareSize / 2); i < x + (squareSize / 2); i++) {
			
			for (int j = y - (squareSize / 2); j < y + (squareSize / 2); j++) {
				
				if (i > 0 && j > 0 && i < width && j < height) {
					
					pixel = bitmap.getPixel(i, j);
					alpha = Color.alpha(pixel);
					if (alpha > alphaReduction) {
						alpha -= alphaReduction;
					} else {
						alpha = 0;
					}
					
					maskColor = (alpha << 24) | 0x00ffffff;
					bitmap.setPixel(i, j, pixel & maskColor);
				}
			}
		}
	}
}
