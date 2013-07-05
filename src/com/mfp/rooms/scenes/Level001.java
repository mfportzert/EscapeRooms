/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfp.rooms.scenes;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;

import android.content.Context;

import com.mfp.rooms.scenes.base.BaseLevel;
import com.mfp.rooms.texturepacker.TxLevel001;
import com.mfp.rooms.utils.SoundUtils;

/**
 *
 * @author M-F.P
 */
public class Level001 extends BaseLevel implements OnClickListener, TxLevel001 {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final int LEVEL_NUMBER = 1;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private Sound mOpenDoorSound;
	private ButtonSprite mDoor;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public Level001(SmoothCamera camera) {
		super(camera, LEVEL_NUMBER);
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		
		if (this.mCleared) {
			this.accessNextLevel(pButtonSprite, pTouchAreaLocalX, pTouchAreaLocalY);
		} else {
			SoundUtils.playSound(this.mOpenDoorSound);
			pButtonSprite.setAlpha(0);
			this.mCleared = true;
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		mCleared = false;
	}
	
	@Override
	protected int getBackgroundRegionId() {
		return BACKGROUND_ID;
	}
	
	/**
	 * @param pEngine
	 * @param pContext
	 */
	@Override
	public void createScene(final Engine pEngine, final Context pContext) {
		super.createScene(pEngine, pContext);
		
		mDoor = createButtonSprite(185, 286, DOOR_ID, 1, 2, this);
		
		mBackground.attachChild(mDoor);
		this.attachChild(mBackground);
		
		registerTouchArea(mDoor);
		
		this.setScale(1.0f);
		
		setOnSceneTouchListener(this);
		setTouchAreaBindingOnActionDownEnabled(true);
		setTouchAreaBindingOnActionMoveEnabled(true);
	}
	
	/**
	 * @param pEngine
	 * @param pContext
	 */
	@Override
	public void createResources(final Engine pEngine, final Context pContext) throws IOException {
		super.createResources(pEngine, pContext);
		
		this.mOpenDoorSound = createSound(pContext, "metal_door_open.ogg");
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
}
