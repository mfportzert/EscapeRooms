/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfp.rooms.scenes;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;

import android.content.Context;

import com.mfp.rooms.activities.LevelActivity;
import com.mfp.rooms.scenes.base.BaseLevel;
import com.mfp.rooms.utils.LevelUtils;

/**
 *
 * @author M-F.P
 */
public class Level008 extends BaseLevel implements OnClickListener {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final int LEVEL_NUMBER = 8;
	
	public static final int BACKGROUND_ID = 0;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private Sound mOpenDoorSound;
	
	private Sprite mDoor;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public Level008(SmoothCamera pCamera) {
		super(pCamera, LEVEL_NUMBER);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public void onClick(final ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		
		if (this.mCleared) {
			this.accessNextLevel(pButtonSprite, pTouchAreaLocalX, pTouchAreaLocalY);
		}
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
	public void createScene(Engine pEngine, Context pContext) {
		super.createScene(pEngine, pContext);
		
		/*
		this.mDoor = new Sprite(139, 277, this.mLevelTexturesFactory.getRegion(DOOR_ID), 
				pEngine.getVertexBufferObjectManager());
		
		background.attachChild(this.mDoor);
		*/
		
		this.setOnSceneTouchListener(this);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		this.setOnAreaTouchTraversalFrontToBack();
	}
	
	/**
	 * @param pEngine
	 * @param pContext
	 */
	@Override
	public void createResources(final Engine pEngine, final Context pContext) throws IOException {
		super.createResources(pEngine, pContext);
		
		this.mOpenDoorSound = createSound(pContext, "door_open.ogg");
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	// ===========================================================
	// Listeners
	// ===========================================================
	
	OnClickListener mArcadeOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
			/*Level007.this.mTopPressableBrick.setAlpha(1);
			pButtonSprite.setAlpha(0);*/
		}
	};
}
