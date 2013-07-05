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

import com.mfp.rooms.scenes.LevelsListScene;

public class LevelsListActivity extends SimpleBaseGameActivity {
	
	// ===========================================================
    // Constants
    // ===========================================================
    
    public static float CAMERA_WIDTH = 600;
    public static float CAMERA_HEIGHT = 1024;
    
    public static final String TAG = LevelActivity.class.getSimpleName();
    
    // ===========================================================
    // Fields
    // ===========================================================
    
    private SmoothCamera mCamera;
    private LevelsListScene mLevelsListScene;
    
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
    	
    	CAMERA_WIDTH = this.getResources().getDisplayMetrics().widthPixels;
    	CAMERA_HEIGHT = this.getResources().getDisplayMetrics().heightPixels;
    	
        this.mCamera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, CAMERA_WIDTH, CAMERA_HEIGHT, 2);
        
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
        engineOptions.getAudioOptions().setNeedsSound(true);
        
        return engineOptions;
    }
    
    @Override
    public void onCreateResources() {
    	
        SoundFactory.setAssetBasePath("mfx/");
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        
        this.mLevelsListScene = new LevelsListScene(this.mCamera);
        try {
        	this.mLevelsListScene.createResources(this.mEngine, this);
        } catch (IOException e) {
        	Debug.e(e);
        }
    }

    @Override
    public Scene onCreateScene() {
    	
        this.mLevelsListScene.createScene(this.mEngine, this);
        return this.mLevelsListScene;
    }
    
    // ===========================================================
    // Methods
    // ===========================================================
    

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
