/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfp.rooms.scenes;

import java.io.IOException;
import java.util.ArrayList;

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
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.EmptyBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.decorator.BaseBitmapTextureAtlasSourceDecorator;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.DeepCopyNotSupportedException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.mfp.rooms.inventory.Inventory.ItemType;
import com.mfp.rooms.inventory.Item;
import com.mfp.rooms.scenes.base.BaseLevel;
import com.mfp.rooms.utils.BitmapUtils;
import com.mfp.rooms.utils.LevelUtils;
import com.mfp.rooms.utils.SoundUtils;

/**
 *
 * @author M-F.P
 */
public class Level004 extends BaseLevel implements OnClickListener {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final int LEVEL_NUMBER = 4;
	
	public static final int BACKGROUND_ID = 0;
	public static final int BIG_DIGICODE_BG_ID = 1;
	public static final int CLOSE_BUTTON_ID = 2;
	public static final int DIGICODE_ID = 3;
	public static final int DIGICODE_ACCESS_DENIED_BUTTON_ID = 4;
	public static final int DIGICODE_ACCESS_GRANTED_BUTTON_ID = 5;
	public static final int DIGICODE_BUTTON_ID = 6;
	public static final int DOOR_CLOSED_ID = 7;
	public static final int DOOR_OPENED_ID = 8;
	public static final int DRAWER_DOOR_ID = 9;
	public static final int DRAWER_DOOR_RIGHT_ID = 10;
	public static final int STAIN_ID = 11;
	public static final int TOWEL_ID = 12;
	
	private static final int ACCESS_CODE = 1950;
	private static final float DIGICODE_ZOOM = 3;
	private static final int NUMBER_DIGITS_CODE = 4;
	private static final String DIGICODE_DIGITS = "123456789*0#";
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private boolean mDrawerDoorTouchedDown = false;
	
	private int mCode = 0;
	private int mDigitsPressedNb = 0;
	
	private boolean mDrawerOpened = false;
	private float mStartDrawerDoorPosX;
	
	private Sound mOpenDoorSound;
	private Sound mDigicodeAccessDeniedSound;
	private Sound mDigicodeAccessGrantedSound;
	private Sound mDigicodeButtonPressedSound;
	private Sound mTowelPickUpSound;
	private Sound mDrawerOpenSound;
	
	private Sprite mStain;
	private Sprite mDrawerDoor;
	private ButtonSprite mDoorClosed;
	private Sprite mDoorOpened;
	private Sprite mDigicodeBig;
	private Sprite mDigicodeRedLight;
	private Sprite mDigicodeGreenLight;
	private Item mTowel;
	private ButtonSprite mDigicode;
	private ArrayList<ButtonSprite> mDigicodeButtonsList = new ArrayList<ButtonSprite>();
	
	private Bitmap mStainBitmap;
	private BitmapTextureAtlas mStainBitmapTextureAtlas;
	private IBitmapTextureAtlasSource mStainBitmapTextureAtlasSource;
	private ITextureRegion mStainTextureRegion;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public Level004(SmoothCamera pCamera) {
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
	public void createScene(final Engine pEngine, final Context pContext) {
		super.createScene(pEngine, pContext);
		
		this.mDrawerDoor = new Sprite(417, 338, getRegion(DRAWER_DOOR_ID), 
				pEngine.getVertexBufferObjectManager()) {
			
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, 
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				switch (pSceneTouchEvent.getAction()) {
					
					case TouchEvent.ACTION_DOWN:
						mDrawerDoorTouchedDown = true;
						mStartDrawerDoorPosX = pSceneTouchEvent.getX();
						break;
						
					case TouchEvent.ACTION_MOVE:
						if (mDrawerDoorTouchedDown) {
							
							float offset = mStartDrawerDoorPosX - pSceneTouchEvent.getX();
							if ((this.getX() - offset) > 417) {
								
								this.setPosition(this.getX() - offset, this.getY());
								mStartDrawerDoorPosX = pSceneTouchEvent.getX();
								if (this.getX() >= 470) {
									
									if (!mDrawerOpened) {
										SoundUtils.playSound(mDrawerOpenSound);
										mDrawerOpened = true;
									}
									this.setVisible(false);
								}
							}
						}
						break;
						
					case TouchEvent.ACTION_UP:
						mDrawerDoorTouchedDown = false;
						break;
				}
				
				return true;
			}
		};
		
		Sprite drawerDoorRight = createSprite(481, 338, DRAWER_DOOR_RIGHT_ID);
		this.mTowel = new Item(417, 353, getRegion(TOWEL_ID), pEngine.getVertexBufferObjectManager(), 
				this.mTowelOnClickListener, ItemType.TOWEL_004);
		this.mStain = new Sprite(131, 264, this.mStainTextureRegion, pEngine.getVertexBufferObjectManager()) {
			
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, 
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				if (getInventory().getSelectedItem() == mTowel) {
					
					float[] localCoordonates = this.convertSceneToLocalCoordinates(
							pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
					if (pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove()) {
						wipeStain(localCoordonates[0], localCoordonates[1]);
					}
				}
				
				return true;
			}
		};
		
		this.mDoorClosed = createButtonSprite(115, 256, DOOR_CLOSED_ID, this);
		this.mDoorOpened = createSprite(317, 214, DOOR_OPENED_ID);
		this.mDoorOpened.setVisible(false);
		
		this.buildDigicode();
		
		mBackground.attachChild(this.mDoorClosed);
		mBackground.attachChild(this.mStain);
		mBackground.attachChild(this.mDigicode);
		mBackground.attachChild(this.mTowel);
		mBackground.attachChild(this.mDrawerDoor);
		mBackground.attachChild(drawerDoorRight);
		mBackground.attachChild(this.mDoorOpened);
		mBackground.attachChild(this.mDigicodeBig);
		this.attachChild(mBackground);
		
		this.registerTouchArea(this.mStain);
		this.registerTouchArea(this.mDrawerDoor);
		this.registerTouchArea(this.mDigicode);
		this.registerTouchArea(this.mTowel);
		this.registerTouchArea(this.mDoorClosed);
		
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
		this.mDigicodeAccessDeniedSound = createSound(pContext, "digicode_access_denied.ogg");
		this.mDigicodeAccessGrantedSound = createSound(pContext, "digicode_access_granted.ogg");
		this.mDigicodeButtonPressedSound = createSound(pContext, "digicode_button_pressed.ogg");
		this.mTowelPickUpSound = createSound(pContext, "towel_pickup.ogg");
		this.mDrawerOpenSound = createSound(pContext, "drawer_open.ogg");
		
		TextureRegion stainTextureRegion = getRegion(STAIN_ID);
		this.mStainBitmapTextureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 
				(int) stainTextureRegion.getWidth(), (int) stainTextureRegion.getHeight(), TextureOptions.BILINEAR);
		
		this.mStainBitmapTextureAtlasSource = new EmptyBitmapTextureAtlasSource(
				(int) stainTextureRegion.getWidth(), (int) stainTextureRegion.getHeight());
		final IBitmapTextureAtlasSource decoratedTextureAtlasSource = 
				new BaseBitmapTextureAtlasSourceDecorator(this.mStainBitmapTextureAtlasSource) {
			
			@Override
			protected void onDecorateBitmap(Canvas pCanvas) throws Exception {
				
				Bitmap bmp = BitmapFactory.decodeStream(pContext.getAssets().open("gfx/special/004/stain.png"));
				mStainBitmap = bmp.copy(Config.ARGB_8888, true);
				bmp.recycle();
				mStainBitmap.setHasAlpha(true);
				pCanvas.drawBitmap(mStainBitmap, 0, 0, this.mPaint);
			}
			
			@Override
			public BaseBitmapTextureAtlasSourceDecorator deepCopy() {
				throw new DeepCopyNotSupportedException();
			}
		};
		
		this.mStainTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromSource(
				this.mStainBitmapTextureAtlas, decoratedTextureAtlasSource, 0, 0);
		this.mStainBitmapTextureAtlas.load();
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	private void checkCode() {
		
		if (this.mCode == ACCESS_CODE) {
			
			this.registerEntityModifier(new DelayModifier(0.3f, new IEntityModifierListener() {
				
				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					
					SoundUtils.playSound(mDigicodeAccessGrantedSound);
					mDigicodeGreenLight.setVisible(true);					
					unregisterTouchArea(Level004.this.mStain);
					for (ButtonSprite digicodeButton : mDigicodeButtonsList) {
						unregisterTouchArea(digicodeButton);
					}
				}

				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					
					SoundUtils.playSound(Level004.this.mOpenDoorSound);
					mCleared = true;
					mDoorClosed.setAlpha(0);
					mDoorOpened.setVisible(true);
					mStain.setVisible(false);
					mDigicodeBig.setVisible(false);
					mDoorClosed.setEnabled(true);
				}
			}));
			
		} else {
			
			this.registerEntityModifier(new DelayModifier(2f, new IEntityModifierListener() {
				
				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					
					mDigicodeRedLight.setVisible(true);
					SoundUtils.playSound(mDigicodeAccessDeniedSound);
					resetDigicode();
				}

				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					mDigicodeRedLight.setVisible(false);
				}
			}));
		}
	}
	
	private void resetDigicode() {
		
		this.mCode = 0;
		this.mDigitsPressedNb = 0;
		for (ButtonSprite digicodeButton : this.mDigicodeButtonsList) {
			
			digicodeButton.setEnabled(true);
			digicodeButton.setVisible(true);
		}
	}
	
	private void wipeStain(final float pX, final float pY) {
		
		BitmapUtils.drawAlphaSquareReduction((int) pX, (int) pY, this.mStainBitmap, 50, 10);
		refreshStain();
	}
	
	private void refreshStain() {
		
		final IBitmapTextureAtlasSource decoratedTextureAtlasSource = 
				new BaseBitmapTextureAtlasSourceDecorator(this.mStainBitmapTextureAtlasSource) {
			
			@Override
			protected void onDecorateBitmap(Canvas pCanvas) throws Exception {
				
				//this.mPaint.setColorFilter(new LightingColorFilter(Color.argb(128, 128, 128, 255), Color.TRANSPARENT));
				pCanvas.drawBitmap(mStainBitmap, 0, 0, this.mPaint);
				//this.mPaint.setColorFilter(null);
			}
			
			@Override
			public BaseBitmapTextureAtlasSourceDecorator deepCopy() {
				throw new DeepCopyNotSupportedException();
			}
		};
		
		this.mStainBitmapTextureAtlas.clearTextureAtlasSources();
		this.mStainTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromSource(
				this.mStainBitmapTextureAtlas, decoratedTextureAtlasSource, 0, 0);
	}
	
	private void buildDigicode() {
		
		this.mDigicode = createButtonSprite(16, 354, DIGICODE_ID, mDigicodeOnClickListener);
		
		TextureRegion bigDigicodeRegion = getRegion(BIG_DIGICODE_BG_ID);
		float bigDigicodeCenterX = (getWidth() - bigDigicodeRegion.getWidth()) / 2;
		float bigDigicodeCenterY = (getHeight() - bigDigicodeRegion.getHeight()) / 2;
		
		this.mDigicodeBig = createSprite(bigDigicodeCenterX, bigDigicodeCenterY, bigDigicodeRegion);
		this.mDigicodeBig.setScale(DIGICODE_ZOOM);
		this.mDigicodeBig.setVisible(false);
		
		this.mDigicodeRedLight = createSprite(39, 48, DIGICODE_ACCESS_DENIED_BUTTON_ID);
		this.mDigicodeGreenLight = createSprite(87, 48, DIGICODE_ACCESS_GRANTED_BUTTON_ID);
		
		this.mDigicodeRedLight.setVisible(false);
		this.mDigicodeGreenLight.setVisible(false);
		this.mDigicodeBig.attachChild(this.mDigicodeRedLight);
		this.mDigicodeBig.attachChild(this.mDigicodeGreenLight);
		
		TextureRegion digicodeButtonRegion = getRegion(DIGICODE_BUTTON_ID);
		for (int y = 0, offsetY = (int) (64); y < 4; y++, 
				offsetY += (digicodeButtonRegion.getHeight()) + (9)) {
			
			for (int x = 0, offsetX = (int) (30); x < 3; x++, 
					offsetX += (digicodeButtonRegion.getWidth()) + (9)) {
				
				ButtonSprite digicodeButton = createButtonSprite(offsetX, offsetY, 
						digicodeButtonRegion, mDigicodeButtonOnClickListener);
				digicodeButton.setTag(DIGICODE_DIGITS.charAt((y * 3) + x));
				
				this.mDigicodeButtonsList.add(digicodeButton);
				
				this.registerTouchArea(digicodeButton);
				this.mDigicodeBig.attachChild(digicodeButton);
			}
		}
		
		ButtonSprite digicodeCloseButton = createButtonSprite(309, 17, CLOSE_BUTTON_ID, 1, 2, 
				mDigicodeCloseButtonOnClickListener);
		this.mDigicodeBig.attachChild(digicodeCloseButton);
		this.registerTouchArea(digicodeCloseButton);
	}
	
	// ===========================================================
	// Listeners
	// ===========================================================
	
	OnClickListener mTowelOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			
			SoundUtils.playSound(mTowelPickUpSound);
			getGui().addInBag(mTowel);
		}
	};
	
	OnClickListener mDigicodeOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			
			mDigicodeBig.setVisible(!mDigicodeBig.isVisible());
			if (mCleared) {
				mDoorClosed.setEnabled(!mDigicodeBig.isVisible());
			}
		}
	};
	
	OnClickListener mDigicodeButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			
			if (mDigicodeBig.isVisible()) {
				
				SoundUtils.playSound(mDigicodeButtonPressedSound);
				pButtonSprite.setEnabled(false);
				pButtonSprite.setVisible(false);
				
				mDigitsPressedNb++;
				int digit = Character.getNumericValue(pButtonSprite.getTag());
				if (digit == -1) {
					resetDigicode();
				} else {
					mCode += digit;
					if (mDigitsPressedNb >= NUMBER_DIGITS_CODE) {
						mDigitsPressedNb = 0;
						checkCode();
					} else {
						mCode *= 10;
					}
				}
			}
		}
	};
	
	OnClickListener mDigicodeCloseButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			
			mDigicodeBig.setVisible(false);
			if (mCleared) {
				mDoorClosed.setEnabled(true);
			}
		}
	};
}
