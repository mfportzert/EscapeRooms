package com.mfp.rooms.camera;

import org.andengine.engine.camera.SmoothCamera;

import android.util.Log;

import com.mfp.rooms.utils.LevelUtils;

public class AdjustedSmoothCamera extends SmoothCamera {
	
	public AdjustedSmoothCamera(float pX, float pY, float pWidth, float pHeight, 
			float pMaxVelocityX, float pMaxVelocityY, float pMaxZoomFactorChange) {
		super(pX, pY, pWidth, pHeight, pMaxVelocityX, pMaxVelocityY, pMaxZoomFactorChange);
	}
	
	@Override
	public void onUpdate(float pSecondsElapsed) {
		super.onUpdate(pSecondsElapsed);
		
		float minX = getXMin();
		if (minX < 0) {
			mXMax -= minX;
			mXMin -= minX;
		}/* else if (!mStickRight && this.getXMax() > LevelUtils.sCurrentSceneMaxX) {
			mStickRight = true;
			stick = true;
		}*/
		
		float minY = getYMin();
		if (getYMin() < 0) {
			mYMax -= minY;
			mYMin -= minY;
		}/* else if (!mStickBottom && this.getYMax() > LevelUtils.sCurrentSceneMaxY) {
			mStickBottom = true;
			stick = true;
		}*/
	}
}
