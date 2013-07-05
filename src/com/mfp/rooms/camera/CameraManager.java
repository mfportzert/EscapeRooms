/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfp.rooms.camera;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;

/**
 *
 * @author M-F.P
 */
public class CameraManager extends Scene implements IScrollDetectorListener, 
        IOnSceneTouchListener, IPinchZoomDetectorListener{
    
	// ===========================================================
    // Constants
    // ===========================================================
	
	public static float DEFAULT_CAMERA_WIDTH = 600;
	public static float DEFAULT_CAMERA_HEIGHT = 1024;
	
    public static float CAMERA_WIDTH = DEFAULT_CAMERA_WIDTH;
    public static float CAMERA_HEIGHT = DEFAULT_CAMERA_HEIGHT;
    
    // ===========================================================
 	// Fields
 	// ===========================================================
    
    protected final SmoothCamera mCamera;
    
    //private final SurfaceScrollDetector mScrollDetector;
    //private PinchZoomDetector mPinchZoomDetector;
    private float mPinchZoomStartedCameraZoomFactor;
    
    // ===========================================================
 	// Constructors
 	// ===========================================================
    
    public CameraManager(final SmoothCamera pCamera) {
        
        this.mCamera = pCamera;
        
        //this.mScrollDetector = new SurfaceScrollDetector(this);
        //this.mPinchZoomDetector = new PinchZoomDetector(this);
    }
    
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    
    @Override
	public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
		final float zoomFactor = this.mCamera.getZoomFactor();
		this.mCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}

	@Override
	public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
		final float zoomFactor = this.mCamera.getZoomFactor();
		this.mCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}
	
	@Override
	public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
		final float zoomFactor = this.mCamera.getZoomFactor();
		this.mCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
	}
    
    @Override
    public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent) {
        this.mPinchZoomStartedCameraZoomFactor = this.mCamera.getZoomFactor();
    }
    
    @Override
    public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector, 
            final TouchEvent pTouchEvent, final float pZoomFactor) {
        
        final float newFactor = this.mPinchZoomStartedCameraZoomFactor * pZoomFactor;
        if (newFactor > 0.5f && newFactor < 1.5f) {
            this.mCamera.setZoomFactor(newFactor);
        }
    }

    @Override
    public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector, 
            final TouchEvent pTouchEvent, final float pZoomFactor) {        
        this.onPinchZoom(pPinchZoomDetector, pTouchEvent, pZoomFactor);
    }

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        /*
    	this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);

		if(this.mPinchZoomDetector.isZooming()) {
			this.mScrollDetector.setEnabled(false);
		} else {
			if(pSceneTouchEvent.isActionDown()) {
				this.mScrollDetector.setEnabled(true);
			}
			this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
		}*/

        return true;
    }
}
