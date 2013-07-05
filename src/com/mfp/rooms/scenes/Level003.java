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
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;

import android.content.Context;

import com.mfp.rooms.inventory.Inventory.ItemType;
import com.mfp.rooms.inventory.Item;
import com.mfp.rooms.scenes.base.BaseLevel;
import com.mfp.rooms.utils.LevelUtils;
import com.mfp.rooms.utils.SoundUtils;

/**
 *
 * @author M-F.P
 */
public class Level003 extends BaseLevel implements OnClickListener {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final int LEVEL_NUMBER = 3;
	
	public static final int BACKGROUND_ID = 0;
	public static final int CARPET_ID = 1;
	public static final int DOOR_ID = 2;
	public static final int KEYS_GROUND_ID = 3;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private boolean mCarpetTouchedDown = false;
	private boolean mLockedDoor = true;
	
	private float mStartCarpetPosX;
	
	private Sound mOpenDoorSound;
	private Sound mDoorUnlockedSound;
	private Sound mDoorLockedSound;
	private Sound mKeysPickUpSound;
	
	private Sprite mCarpet;
	private Item mKeys;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public Level003(SmoothCamera pCamera) {
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
		} else if (this.getInventory().getSelectedItem() == this.mKeys) {
			
			if (this.mLockedDoor) {
				
				this.registerEntityModifier(new DelayModifier(1.3f, new IEntityModifierListener() {
				
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
						Level003.this.mLockedDoor = false;
						SoundUtils.playSound(Level003.this.mDoorUnlockedSound);
					}

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						Level003.this.mCleared = true;
						Level003.this.getGui().removeFromBag(Level003.this.mKeys);
						pButtonSprite.setAlpha(0);
						SoundUtils.playSound(Level003.this.mOpenDoorSound);
					}
				}));
			}
			
		} else {
			SoundUtils.playSound(this.mDoorLockedSound);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	@Override
	protected int getBackgroundRegionId() {
		return BACKGROUND_ID;
	}
	
	/**
	 * @param pEngine
	 * @param pContext
	 */
	@Override
	public void createScene(Engine pEngine, final Context pContext) {
		super.createScene(pEngine, pContext);
		
		this.mCarpet = new Sprite(68, 765, getRegion(CARPET_ID), 
				pEngine.getVertexBufferObjectManager()) {
			
				@Override
				public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, 
						final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					
					switch (pSceneTouchEvent.getAction()) {
						
						case TouchEvent.ACTION_DOWN:
							Level003.this.mCarpetTouchedDown = true;
							Level003.this.mStartCarpetPosX = pSceneTouchEvent.getX();
							break;
							
						case TouchEvent.ACTION_MOVE:							
							
							if (Level003.this.mCarpetTouchedDown) {
								
								float offset = Level003.this.mStartCarpetPosX - pSceneTouchEvent.getX();
								this.setPosition(this.getX() - offset, this.getY());
								Level003.this.mStartCarpetPosX = pSceneTouchEvent.getX();
							}
							break;
							
						case TouchEvent.ACTION_UP:
							Level003.this.mCarpetTouchedDown = false;
							break;
					}
					
					return true;
				}
		};
		
		this.mKeys = new Item(237, 785, getRegion(KEYS_GROUND_ID), pEngine.getVertexBufferObjectManager(), 
				this.mKeysOnClickListener, ItemType.KEYS_003);
		
		ButtonSprite door = createButtonSprite(199, 225, DOOR_ID, 1, 2, this);
		
		mBackground.attachChild(door);
		mBackground.attachChild(this.mKeys);
		mBackground.attachChild(this.mCarpet);
		this.attachChild(mBackground);
		
		this.registerTouchArea(door);
		this.registerTouchArea(this.mCarpet);
		this.registerTouchArea(this.mKeys);
		
		this.setOnSceneTouchListener(this);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
	}
	
	/**
	 * @param pEngine
	 * @param pContext
	 */
	@Override
	public void createResources(final Engine pEngine, final Context pContext) throws IOException {
		super.createResources(pEngine, pContext);
		
		this.mOpenDoorSound = createSound(pContext, "door_open.ogg");
		this.mDoorUnlockedSound = createSound(pContext, "unlock_door.ogg");
		this.mDoorLockedSound = createSound(pContext, "door_locked.ogg");
		this.mKeysPickUpSound = createSound(pContext, "keys_pick_up.ogg");
	}
	
	// ===========================================================
	// Listeners
	// ===========================================================
	
	OnClickListener mKeysOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(final ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
			SoundUtils.playSound(mKeysPickUpSound);
			getGui().addInBag(mKeys);
		}
	};
}
