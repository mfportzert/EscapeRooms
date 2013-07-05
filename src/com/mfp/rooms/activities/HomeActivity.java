package com.mfp.rooms.activities;

import java.io.IOException;

import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import com.mfp.rooms.camera.CameraManager;
import com.mfp.rooms.scenes.HomeScreenScene;

public class HomeActivity extends SimpleBaseGameActivity {
	public static final String TAG = LevelActivity.class.getSimpleName();
	
    // ===========================================================
    // Constants
    // ===========================================================
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private SmoothCamera mCamera;
    private HomeScreenScene mHomeScene;
    
    // ===========================================================
    // Constructors
    // ===========================================================
    
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    
    @Override
    public EngineOptions onCreateEngineOptions() {
    	
    	CameraManager.CAMERA_WIDTH = this.getResources().getDisplayMetrics().widthPixels;
    	CameraManager.CAMERA_HEIGHT = this.getResources().getDisplayMetrics().heightPixels;
    	
        this.mCamera = new SmoothCamera(0, 0, CameraManager.CAMERA_WIDTH, CameraManager.CAMERA_HEIGHT, 
        		CameraManager.CAMERA_WIDTH, CameraManager.CAMERA_HEIGHT, 2);
        
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
                new RatioResolutionPolicy(CameraManager.CAMERA_WIDTH, CameraManager.CAMERA_HEIGHT), 
                this.mCamera);
        engineOptions.getAudioOptions().setNeedsSound(true);
        
        return engineOptions;
    }
    
    @Override
    public void onCreateResources() {
    	
        SoundFactory.setAssetBasePath("mfx/");
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        
        this.mHomeScene = new HomeScreenScene(this.mCamera);
        try {
        	this.mHomeScene.createResources(this.mEngine, this);
        } catch (IOException e) {
			Debug.e(e);
		}
    }

    @Override
    public Scene onCreateScene() {
    	
      	this.mHomeScene.createScene(this.mEngine, this);
        return this.mHomeScene;
    }
    
    // ===========================================================
    // Methods
    // ===========================================================
    

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}