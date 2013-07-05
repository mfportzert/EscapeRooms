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
import org.andengine.entity.sprite.Sprite;

import android.content.Context;

import com.mfp.rooms.scenes.base.BaseScene;
import com.mfp.rooms.texturepacker.TxErrorScene;

/**
 *
 * @author M-F.P
 */
public class ErrorScene extends BaseScene implements TxErrorScene, OnClickListener {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private final static String XML_FILENAME = "error_scene";
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private Sound mButtonSound;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public ErrorScene(SmoothCamera pCamera) {
		super(pCamera, XML_FILENAME);
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	/**
	 * @param pEngine
	 * @param pContext
	 */
	@Override
	public void createScene(Engine pEngine, Context pContext) {
		super.createScene(pEngine, pContext);
		
		Sprite background = createBackground(BACKGROUND_ID);
		this.attachChild(background);
	}
	
	/**
	 * @param pEngine
	 * @param pContext
	 */
	@Override
	public void createResources(final Engine pEngine, final Context pContext) throws IOException {
		super.createResources(pEngine, pContext);
		
		this.mButtonSound = createSound(pContext, "bip.ogg");
	}
}
