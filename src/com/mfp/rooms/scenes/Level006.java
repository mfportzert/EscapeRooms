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
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;

import android.content.Context;

import com.mfp.rooms.inventory.Inventory.ItemType;
import com.mfp.rooms.inventory.Item;
import com.mfp.rooms.scenes.base.BaseLevel;
import com.mfp.rooms.sprites.Level006Curtain;
import com.mfp.rooms.utils.LevelUtils;
import com.mfp.rooms.utils.SoundUtils;

/**
 *
 * @author M-F.P
 */
public class Level006 extends BaseLevel implements OnClickListener, IAccelerationListener {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final int LEVEL_NUMBER = 6;
	
	public static final int ARMOR_ID = 0;
	public static final int ARMOR_SHIELD_ID = 1;
	public static final int ARMOR_SWORD_ID = 2;
	public static final int BACKGROUND_ID = 3;
	public static final int CLOSED_DOOR_ID = 4;
	public static final int CURTAIN_LEFT_ID = 5;
	public static final int MURAL_SHIELD_ID = 6;
	public static final int RIGHT_CURTAIN_ID = 7;
	public static final int SWORD_ID = 8;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private Sound mOpenDoorSound;
	private Sound mSwordGroundHitSound;
	private Sound mSwordSound;
	private Sound mShieldSound;
	
	private Sprite mLeftCurtain;
	private Sprite mRightCurtain;
	private Sprite mLeftArmor;
	private Sprite mLeftArmorSword;
	private Sprite mLeftArmorShield;
	private Sprite mRightArmor;
	private Sprite mRightArmorSword;
	private Sprite mRightArmorShield;
	private ButtonSprite mDoor;
	
	private Item mSword;
	private Item mShield;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public Level006(SmoothCamera pCamera) {
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
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		
	}
	
	@Override
    public void onAccelerationChanged(AccelerationData pAccelerationData) {
		/*
		Log.d("sdgsg", "X: "+pAccelerationData.getX()+", Y: "+pAccelerationData.getY()+"," +
				"Z: "+pAccelerationData.getZ());*/
		
		if (pAccelerationData.getZ() < -3.0f){
            
			if (!this.mSword.isEnabled()) {
				
				this.mSword.setEnabled(true);
				this.mSword.setY(this.mSword.getY() + 589);
				SoundUtils.playSound(this.mSwordGroundHitSound);
			}
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
		
		this.mLeftCurtain = new Level006Curtain(0, 119, getRegion(CURTAIN_LEFT_ID), 
				pEngine.getVertexBufferObjectManager(), 220);
		this.mRightCurtain = new Level006Curtain(394, 122, getRegion(RIGHT_CURTAIN_ID), 
				pEngine.getVertexBufferObjectManager(), 220);
		
		this.mLeftArmorShield = createSprite(0, 512, ARMOR_SHIELD_ID);
		this.mLeftArmorSword = createSprite(63, 383, ARMOR_SWORD_ID);
		this.mRightArmorShield = createSprite(515, 512, ARMOR_SHIELD_ID);
		this.mRightArmorSword = createSprite(452, 383, ARMOR_SWORD_ID);
		
		this.mRightArmorShield.setVisible(false);
		this.mRightArmorSword.setVisible(false);
		this.mRightArmorShield.setFlippedHorizontal(true);
		this.mRightArmorSword.setFlippedHorizontal(true);
		
		this.mSword = new Item(182, 168, getRegion(SWORD_ID), pEngine.getVertexBufferObjectManager(), 
				this.mSwordOnClickListener, ItemType.SWORD_006);
		this.mSword.setEnabled(false);
		
		this.mShield = new Item(432, 318, getRegion(MURAL_SHIELD_ID), pEngine.getVertexBufferObjectManager(), 
				this.mShieldOnClickListener, ItemType.SHIELD_006);
		
		this.mLeftArmor = new Sprite(0, 359, getRegion(ARMOR_ID), pEngine.getVertexBufferObjectManager()) {
			
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, 
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				return true;
			}
		};
		
		this.mRightArmor = createButtonSprite(439, 359, ARMOR_ID, this.mRightArmorOnClickListener);
		this.mRightArmor.setFlippedHorizontal(true);
		
		this.mDoor = createButtonSprite(196, 337, CLOSED_DOOR_ID, this);
		
		this.mBackground.attachChild(this.mDoor);
		this.mBackground.attachChild(this.mShield);
		this.mBackground.attachChild(this.mLeftCurtain);
		this.mBackground.attachChild(this.mRightCurtain);
		this.mBackground.attachChild(this.mLeftArmor);
		this.mBackground.attachChild(this.mLeftArmorShield);
		this.mBackground.attachChild(this.mLeftArmorSword);
		this.mBackground.attachChild(this.mRightArmor);
		this.mBackground.attachChild(this.mRightArmorShield);
		this.mBackground.attachChild(this.mRightArmorSword);
		this.mBackground.attachChild(this.mSword);
		this.attachChild(this.mBackground);
		
		this.registerTouchArea(this.mDoor);
		this.registerTouchArea(this.mLeftCurtain);
		this.registerTouchArea(this.mRightCurtain);
		this.registerTouchArea(this.mShield);
		this.registerTouchArea(this.mSword);
		this.registerTouchArea(this.mRightArmor);
		
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
		this.mSwordGroundHitSound = createSound(pContext, "sword_hit_ground.ogg");
		this.mSwordSound = createSound(pContext, "sword.ogg");
		this.mShieldSound = createSound(pContext, "shield.ogg");
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	// ===========================================================
	// Listeners
	// ===========================================================
	
	OnClickListener mSwordOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
			SoundUtils.playSound(Level006.this.mSwordSound);
			Level006.this.getGui().addInBag(Level006.this.mSword);
		}
	};
	
	OnClickListener mShieldOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
			SoundUtils.playSound(Level006.this.mShieldSound);
			Level006.this.getGui().addInBag(Level006.this.mShield);			
		}
	};
	
	OnClickListener mRightArmorOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
			if (Level006.this.getInventory().getSelectedItem() == Level006.this.mShield) {
				
				SoundUtils.playSound(Level006.this.mShieldSound);
				Level006.this.mRightArmorShield.setVisible(true);
				Level006.this.getGui().removeFromBag(Level006.this.mShield);
				
			} else if (Level006.this.getInventory().getSelectedItem() == Level006.this.mSword) {
				
				SoundUtils.playSound(Level006.this.mSwordSound);
				Level006.this.mRightArmorSword.setVisible(true);
				Level006.this.getGui().removeFromBag(Level006.this.mSword);
			}
			
			if (Level006.this.mRightArmorSword.isVisible() 
					&& Level006.this.mRightArmorShield.isVisible()) {
				
				Level006.this.mCleared = true;
				SoundUtils.playSound(Level006.this.mOpenDoorSound);
				Level006.this.mDoor.setAlpha(0);
			}
		}
	};
}
