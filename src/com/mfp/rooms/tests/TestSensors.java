/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfp.rooms.tests;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.mfp.rooms.activities.LevelActivity;
import com.mfp.rooms.gui.LevelGui;
import com.mfp.rooms.scenes.base.BaseLevel;

/**
 *
 * @author M-F.P
 */
public class TestSensors extends BaseLevel implements SensorEventListener, OnClickListener {

	// ===========================================================
	// Fields
	// ===========================================================
	
	private LevelGui mGui;
	private Sound mOpenDoorSound;
	
	private float[] mGravity;
	private float[] mGeomagnetic;
	
	private Sprite mWater;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public TestSensors(final SmoothCamera pCamera) {
		super(pCamera, 5);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public LevelGui getGUI() {
		return this.mGui;
	}
	
	public SmoothCamera getCamera() {
		return this.mCamera;
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		
		if (pButtonSprite.isEnabled()) {
			
			this.mOpenDoorSound.play();
			pButtonSprite.setEnabled(false);
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	
	@Override
    public void onSensorChanged(SensorEvent event) {
		
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			this.mGravity = event.values;
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			this.mGeomagnetic = event.values;
		}
		
		if (this.mGravity != null && this.mGeomagnetic != null) {
			
			float R[] = new float[9];
			float I[] = new float[9];
			
			boolean success = SensorManager.getRotationMatrix(R, I, this.mGravity, this.mGeomagnetic);
			if (success) {
				
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				
				if (this.mWater != null) {
					this.mWater.setRotation(-orientation[2]*360/(2*3.14159f));
				}
			}
	    }
    }
    
	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * @param pEngine
	 * @param pContext
	 */
	@Override
	public void createScene(final Engine pEngine, final Context pContext) {
		
		Log.d("Level 05", "Creating scene");
		
		try {
			this.mOpenDoorSound = SoundFactory.createSoundFromAsset(pEngine.getSoundManager(), 
					pContext, "metal_door_open.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}
		
		this.mGui = (LevelGui) pEngine.getCamera().getHUD();
	    
		this.setOnSceneTouchListener(this);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		/*
		this.mCamera.setBounds(0, 0, tmxLayer.getHeight(), tmxLayer.getWidth());
		this.mCamera.setBoundsEnabled(true);
		*/
		
		BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(
				pEngine.getTextureManager(), 2048, 1024);
		BitmapTextureAtlas bitmapTextureAtlas2 = new BitmapTextureAtlas(
				pEngine.getTextureManager(), 2048, 1024);
		
		bitmapTextureAtlas.load();
		bitmapTextureAtlas2.load();
		
		TextureRegion backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "levels/01/background.png", 0, 0);
		TextureRegion closedDoorRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "levels/01/door_closed.png", 600, 0);
		TextureRegion pressedDoorRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "levels/01/door_pressed.png", 829, 0);
		TextureRegion openDoorRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas, pContext, "levels/01/door_open.png", 1058, 0);
		
		TextureRegion waterRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				bitmapTextureAtlas2, pContext, "levels/05/water.png", 0, 0);
		
		float centerX = (CAMERA_WIDTH - backgroundRegion.getWidth()) / 2;
		float centerY = (CAMERA_HEIGHT - backgroundRegion.getHeight()) / 2;
		
		Sprite background = new Sprite(centerX, centerY, 
				backgroundRegion, pEngine.getVertexBufferObjectManager());
		
		ButtonSprite door = new ButtonSprite(background.getX()+185, background.getY()+286, 
				closedDoorRegion, pressedDoorRegion, openDoorRegion, 
				pEngine.getVertexBufferObjectManager(), this);
		
		door.setOnClickListener(this);
		
		Sprite water = new Sprite(-300, 400, waterRegion, pEngine.getVertexBufferObjectManager());
		this.mWater = water;
		
    	this.attachChild(door);
		this.attachChild(background);
		this.attachChild(water);
		this.registerTouchArea(door);
	}

	@Override
	protected int getBackgroundRegionId() {
		// TODO Auto-generated method stub
		return 0;
	}
}
