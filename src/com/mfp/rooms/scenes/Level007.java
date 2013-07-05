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
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.content.Context;

import com.mfp.rooms.activities.LevelActivity;
import com.mfp.rooms.inventory.Inventory.ItemType;
import com.mfp.rooms.inventory.Inventory.OnItemSelectedListener;
import com.mfp.rooms.inventory.Item;
import com.mfp.rooms.scenes.base.BaseLevel;
import com.mfp.rooms.utils.LevelUtils;

/**
 *
 * @author M-F.P
 */
public class Level007 extends BaseLevel implements OnClickListener, OnItemSelectedListener {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final int LEVEL_NUMBER = 7;
	
	public static final int BACKGROUND_ID = 0;
	public static final int BIG_BLACK_HALO_ID = 1;
	public static final int BIG_FIRE_HALO_ID = 2;
	public static final int BIG_FLAME_ID = 3;
	public static final int BIG_TORCH_ID = 4;
	public static final int BOTTOM_PRESSABLE_BRICK_ID = 5;
	public static final int DOOR_ID = 6;
	public static final int KEY_ID = 7;
	public static final int REMOVABLE_BRICK_ID = 8;
	public static final int SMALL_BLACK_HALO_ID = 9;
	public static final int SMALL_FIRE_HALO_ID = 10;
	public static final int SMALL_FLAME_ID = 11;
	public static final int SMALL_TORCH_ID = 12;
	public static final int TOP_PRESSABLE_BRICK_ID = 13;
	public static final int TORCH_BRACELET_ID = 14;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private boolean mTorchTaken = false;
	private AnalogOnScreenControl mTorchControl;
	private Sound mOpenDoorSound;
	
	private AnimatedSprite mTorchFlameSmall;
	private AnimatedSprite mTorchFlameBig;
	private Sprite mTorchBracelet;
	private Sprite mFireHaloSmall;
	private Sprite mBlackHaloSmall;
	private Sprite mFireHaloBig;
	private Sprite mBlackHaloBig;
	private Sprite mTorchBig;
	private Sprite mDoor;
	private ButtonSprite mTopPressableBrick;
	private ButtonSprite mBottomPressableBrick;
	private Sprite mRemovableBrick;
	private Rectangle mBlackScreen;
	
	private Item mTorch;
	private Item mKey;
	
	private float mBigBackHaloRealPosX;
	private float mBigBackHaloRealPosY;
	private boolean mBigTorchLightEffectRegistered = false;
	private LoopEntityModifier mBigTorchLightEffect;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public Level007(SmoothCamera pCamera) {
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
	public void onItemSelected(Item item) {
		
		if (item != null) {	
			switch (item.getType()) {
			
				case TORCH_007:
					showFpsViewTorch();
					this.getGui().closeInventoryBag();
					break;
			}
		}
	}

	@Override
	public void onItemUnselected(Item item) {
		
		if (item != null) { 
			switch (item.getType()) {
		
				case TORCH_007:
					hideFpsViewTorch();
					break;
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
		
		this.mTorchFlameSmall = createAnimatedSprite(-40, -140, SMALL_FLAME_ID, 1, 4);
		this.mTorchFlameBig = createAnimatedSprite(-75, -300, BIG_FLAME_ID, 1, 4);
		
		this.mTorch = new Item(516, 357, getRegion(SMALL_TORCH_ID), pEngine.getVertexBufferObjectManager(), 
				this.mTorchOnClickListener, ItemType.TORCH_007);
		
		this.mTorchBig = createSprite(498, 785, BIG_TORCH_ID);
		
		TextureRegion blackHaloSmall = getRegion(SMALL_BLACK_HALO_ID);
		this.mBlackHaloSmall = createSprite(-26 + (blackHaloSmall.getWidth() / 2),
				-11 + (blackHaloSmall.getHeight() / 2), blackHaloSmall);
		
		TextureRegion blackHaloBig = getRegion(BIG_BLACK_HALO_ID);
		this.mBlackHaloBig = createSprite(-275 + ((blackHaloSmall.getWidth() * 4) / 2), 
				-275 + ((blackHaloSmall.getWidth() * 4) / 2), blackHaloBig);
		
		TextureRegion fireHaloSmall = getRegion(SMALL_FIRE_HALO_ID);
		this.mFireHaloSmall = createSprite(fireHaloSmall.getWidth() / 2, fireHaloSmall.getHeight() / 2, 
				fireHaloSmall);
		
		TextureRegion fireHaloBig = getRegion(BIG_FIRE_HALO_ID);
		this.mFireHaloBig = createSprite(-275 + ((blackHaloSmall.getWidth() * 4) / 2), 
				-275 + ((blackHaloSmall.getWidth() * 4) / 2), fireHaloBig);
		
		this.mTorchBracelet = createSprite(517, 412, TORCH_BRACELET_ID);
		this.mTopPressableBrick = createButtonSprite(83, 309, TOP_PRESSABLE_BRICK_ID, 
				this.mTopPressableBrickOnClickListener);
		
		this.mBottomPressableBrick = createButtonSprite(52, 443, BOTTOM_PRESSABLE_BRICK_ID, 
				this.mBottomPressableBrickOnClickListener);
		
		this.mKey = new Item(68, 385, getRegion(KEY_ID), pEngine.getVertexBufferObjectManager(), 
				this.mTorchOnClickListener, ItemType.KEY_007);
		this.mKey.setEnabled(false);
		
		this.mRemovableBrick = createSprite(74, 398, REMOVABLE_BRICK_ID);
		this.mDoor = createSprite(139, 277, DOOR_ID);
		
		this.mTorchFlameSmall.setScale(0.9f);
		this.mTorchFlameSmall.setAlpha(0.85f);
		this.mTorchFlameSmall.animate(140);
		this.mTorchBig.setScale(1.1f);
		this.mBlackHaloSmall.setScale(2f);
		this.mFireHaloSmall.setScale(2f);
		this.mFireHaloBig.setScale(5f);
		this.mBlackHaloBig.setScale(5f);
		
		Path movementHaloBlackSmall = new Path(3).to(this.mBlackHaloSmall.getX(), this.mBlackHaloSmall.getY())
				.to(this.mBlackHaloSmall.getX() + 7, this.mBlackHaloSmall.getY() - 3)
				.to(this.mBlackHaloSmall.getX(), this.mBlackHaloSmall.getY() + 3);
		
		this.mBigBackHaloRealPosX = this.mBlackHaloBig.getX();
		this.mBigBackHaloRealPosY = this.mBlackHaloBig.getY();
		Path movementHaloBlackBig = new Path(3).to(this.mBigBackHaloRealPosX, this.mBigBackHaloRealPosY)
				.to(this.mBigBackHaloRealPosX + 21, this.mBigBackHaloRealPosY - 9)
				.to(this.mBigBackHaloRealPosX, this.mBigBackHaloRealPosY + 9);
		
		this.mBlackHaloSmall.registerEntityModifier(new LoopEntityModifier(new PathModifier(0.5f, movementHaloBlackSmall)));
		this.mBigTorchLightEffect = new LoopEntityModifier(new PathModifier(0.5f, movementHaloBlackBig));
		this.mBlackHaloBig.registerEntityModifier(this.mBigTorchLightEffect);
		this.mBigTorchLightEffectRegistered = true;
		
		this.mTorchControl = this.getGui().addAnalogController(
				260, 800, new IAnalogOnScreenControlListener() {
			
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, 
					final float pValueX, final float pValueY) {
				
				if (Math.abs(pValueX) > 0 || Math.abs(pValueY) > 0) {
					
					float offsetX = pValueX * 20;
					float offsetY = pValueY * 20;
					
					mBigBackHaloRealPosX += offsetX;
					mBigBackHaloRealPosY += offsetY;
					
					mBlackHaloBig.setX(mBigBackHaloRealPosX);
					mBlackHaloBig.setY(mBigBackHaloRealPosY);
					mTorchBig.setX(mTorchBig.getX() + offsetX);
					mTorchBig.setY(mTorchBig.getY() + offsetY);
					mFireHaloBig.setX(mFireHaloBig.getX() + offsetX);
					mFireHaloBig.setY(mFireHaloBig.getY() + offsetY);
					
					if (mBigTorchLightEffectRegistered) {
						mBlackHaloBig.unregisterEntityModifier(mBigTorchLightEffect);
						mBigTorchLightEffectRegistered = false;
					}
					
				} else if (!Level007.this.mBigTorchLightEffectRegistered) {
					
					mBigTorchLightEffectRegistered = true;
					Path movementHaloBlackBig = new Path(3).to(mBigBackHaloRealPosX, mBigBackHaloRealPosY)
							.to(mBigBackHaloRealPosX + 21, mBigBackHaloRealPosY - 9)
							.to(mBigBackHaloRealPosX, mBigBackHaloRealPosY + 9);
				
					mBigTorchLightEffect = new LoopEntityModifier(new PathModifier(0.5f, movementHaloBlackBig));
					mBlackHaloBig.registerEntityModifier(mBigTorchLightEffect);					
				}
			}
			
			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				
			}
		});
		
		this.hideFpsViewTorch();
		
		mBackground.attachChild(this.mDoor);
		mBackground.attachChild(this.mTopPressableBrick);
		mBackground.attachChild(this.mBottomPressableBrick);
		mBackground.attachChild(this.mKey);
		mBackground.attachChild(this.mRemovableBrick);
		mBackground.attachChild(this.mFireHaloSmall);
		this.mTorch.attachChild(this.mTorchFlameSmall);
		
		mBackground.attachChild(this.mTorch);
		mBackground.attachChild(this.mTorchBracelet);
		mBackground.attachChild(this.mBlackHaloSmall);
		mBackground.attachChild(this.mBlackScreen);
		mBackground.attachChild(this.mFireHaloBig);
		mBackground.attachChild(this.mBlackHaloBig);
		this.mTorchBig.attachChild(this.mTorchFlameBig);
		
		mBackground.attachChild(this.mTorchBig);
		this.attachChild(mBackground);
		//this.attachChild(door);
		
		//this.registerTouchArea(door);
		this.registerTouchArea(this.mTorch);
		this.registerTouchArea(this.mKey);
		this.registerTouchArea(this.mTopPressableBrick);
		this.registerTouchArea(this.mBottomPressableBrick);
		
		this.setOnSceneTouchListener(this);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		this.setOnAreaTouchTraversalFrontToBack();
		
		this.getInventory().setOnItemSelectedListener(this);
	}
	
	/**
	 * @param pEngine
	 * @param pContext
	 */
	@Override
	public void createResources(final Engine pEngine, final Context pContext) throws IOException {
		super.createResources(pEngine, pContext);
		
		this.mOpenDoorSound = createSound(pContext, "door_open.ogg");
		
		this.mBlackScreen = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 
				pEngine.getVertexBufferObjectManager());
		this.mBlackScreen.setColor(Color.BLACK);
		this.mBlackScreen.setAlpha(0);
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	private void showFpsViewTorch() {
		
		this.mFireHaloBig.setAlpha(1);
		this.mBlackHaloBig.setAlpha(1);
		this.mTorchBig.setAlpha(1);
		this.mTorchFlameBig.setAlpha(1);
		this.mBlackScreen.setAlpha(0);
		this.mTorchFlameBig.animate(140);
		this.mTorchControl.setVisible(true);
	}
	
	private void hideFpsViewTorch() {
		
		this.mFireHaloBig.setAlpha(0);
		this.mBlackHaloBig.setAlpha(0);
		this.mTorchBig.setAlpha(0);
		this.mTorchFlameBig.setAlpha(0);
		if (this.mTorchTaken) {
			this.mBlackScreen.setAlpha(0.92f);
		}
		this.mTorchFlameBig.stopAnimation();
		this.mTorchControl.setVisible(false);
	}
	
	// ===========================================================
	// Listeners
	// ===========================================================
	
	OnClickListener mTorchOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
			Level007.this.mTorchTaken = true;
			Level007.this.getGui().addInBag(Level007.this.mTorch);
			Level007.this.mBlackHaloSmall.setAlpha(0);
			Level007.this.mFireHaloSmall.setAlpha(0);
			Level007.this.mBlackScreen.setAlpha(0.92f);
		}
	};
	
	OnClickListener mKeyOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
			Level007.this.getGui().addInBag(Level007.this.mKey);
		}
	};
	
	OnClickListener mTopPressableBrickOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
			Level007.this.mBottomPressableBrick.setAlpha(1);
			pButtonSprite.setAlpha(0);
		}
	};
	
	OnClickListener mBottomPressableBrickOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
			Level007.this.mTopPressableBrick.setAlpha(1);
			pButtonSprite.setAlpha(0);
		}
	};
}
