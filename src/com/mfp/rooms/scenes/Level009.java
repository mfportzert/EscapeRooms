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
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;

import com.mfp.rooms.activities.LevelActivity;
import com.mfp.rooms.scenes.base.BaseLevel;
import com.mfp.rooms.texturepacker.TxLevel009;
import com.mfp.rooms.utils.BitmapUtils;
import com.mfp.rooms.utils.LevelUtils;
import com.mfp.rooms.utils.SoundUtils;

/**
 *
 * @author M-F.P
 */
public class Level009 extends BaseLevel implements TxLevel009, OnClickListener {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final int LEVEL_NUMBER = 9;
	
	private static final int ACCESS_CODE = 2015;
	private static final float DIGICODE_ZOOM = 3;
	private static final int NUMBER_DIGITS_CODE = 4;
	private static final String PADLOCK_DIGITS = "123456789*0#";
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private int mCode = 0;
	private int mDigitsPressedNb = 0;
	
	private AnalogOnScreenControl mMirrorControl;
	private Sound mOpenDoorSound;
	
	private Sprite mDoor;
	private Sprite mChainsClosed;
	private Sprite mChainsOpenLeft;
	private Sprite mChainsOpenRight;
	private ButtonSprite mPadlockSmall;
	private Sprite mPadlockSmallGround;
	private Sprite mPadlockBig;
	private Sprite mPadlockBigTop;
	private ArrayList<ButtonSprite> mDPadlockButtonsList = new ArrayList<ButtonSprite>();
	private Sprite mMirrorFrame;
	private Sprite mMirrorDirt;
	private Sprite mOppositeWall;
	
	private ArrayList<ButtonSprite> mDigicodeButtonsList = new ArrayList<ButtonSprite>();
	
	private Bitmap mDirtBitmap;
	private BitmapTextureAtlas mDirtBitmapTextureAtlas;
	private IBitmapTextureAtlasSource mDirtBitmapTextureAtlasSource;
	private TextureRegion mDirtTextureRegion;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public Level009(SmoothCamera pCamera) {
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
	public void createScene(final Engine pEngine, final Context pContext) {
		super.createScene(pEngine, pContext);
		
		mDoor = createButtonSprite(272, 293, DOOR_CLOSED_ID, this);
		mOppositeWall = createSprite(32, 332, OPPOSITE_WALL_ID);
		mChainsClosed = createSprite(241, 456, CHAINS_CLOSED_ID);
		mChainsOpenLeft = createSprite(234, 451, CHAINS_OPEN_LEFT_ID);
		mChainsOpenRight = createSprite(543, 456, CHAINS_OPEN_RIGHT_ID);
		mPadlockSmall = createButtonSprite(400, 492, PADLOCK_SMALL_ID, mPadlockSmallOnClickListener);
		mPadlockSmallGround = createSprite(393, 739, PADLOCK_GROUND_ID);
		mMirrorDirt = createSprite(30, 32, mDirtTextureRegion);
		mPadlockBig = createSprite(0, 0, PADLOCK_BOTTOM_ID);
		mPadlockBigTop = createSprite(0, 0, PADLOCK_TOP_ID);
		
		this.mMirrorControl = this.getGui().addAnalogController(
				50, 460, new IAnalogOnScreenControlListener() {
			
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, 
					final float pValueX, final float pValueY) {
				
				if (Math.abs(pValueX) > 0 || Math.abs(pValueY) > 0) {
					
					float offsetX = pValueX * 20;
					float offsetY = pValueY * 20;
					
					float frameOffsetTop = 20;
					float frameOffsetLeft = 18;
					float frameOffsetRight = 18;
					float frameOffsetBottom = 20;
					
					float newMirrorFrameX = mMirrorFrame.getX() + offsetX;
					float newMirrorFrameY = mMirrorFrame.getY() + offsetY;

					if (newMirrorFrameX < mOppositeWall.getX()) {
						newMirrorFrameX = mOppositeWall.getX();
					}

					if (newMirrorFrameY < mOppositeWall.getY()) {
						newMirrorFrameY = mOppositeWall.getY();
					}
					
					if (newMirrorFrameX > mOppositeWall.getX() + mOppositeWall.getWidth()) {
						newMirrorFrameX = mOppositeWall.getX() + mOppositeWall.getWidth();
					}
					
					if (newMirrorFrameY > mOppositeWall.getY() + mOppositeWall.getHeight()) {
						newMirrorFrameY = mOppositeWall.getY() + mOppositeWall.getHeight();
					}
					
					mMirrorFrame.setX(newMirrorFrameX);
					mMirrorFrame.setY(newMirrorFrameY);
				}
			}
			
			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				
			}
		});
		
		mMirrorFrame = new Sprite(61, 365, getRegion(MIRROR_FRAME_ID),
				pEngine.getVertexBufferObjectManager()) {
			
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, 
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
					mCamera.setCenter(mOppositeWall.getX() + mOppositeWall.getWidth() / 2, 
							mOppositeWall.getY() + mOppositeWall.getHeight() / 2);
					mCamera.setZoomFactor(2.f);
				}
				/*
				float frameOffsetTop = 20;
				float frameOffsetLeft = 18;
				float frameOffsetRight = 18;
				float frameOffsetBottom = 20;
				
				float newMirrorFrameX = pSceneTouchEvent.getX();
				float newMirrorFrameY = pSceneTouchEvent.getY();

				if (newMirrorFrameX < mOppositeWall.getX()) {
					newMirrorFrameX = mOppositeWall.getX();
				}

				if (newMirrorFrameY < mOppositeWall.getY()) {
					newMirrorFrameY = mOppositeWall.getY();
				}
				
				if (newMirrorFrameX > mOppositeWall.getX() + mOppositeWall.getWidth()) {
					newMirrorFrameX = mOppositeWall.getX() + mOppositeWall.getWidth();
				}
				
				if (newMirrorFrameY > mOppositeWall.getY() + mOppositeWall.getHeight()) {
					newMirrorFrameY = mOppositeWall.getY() + mOppositeWall.getHeight();
				}
				
				mMirrorFrame.setPosition(newMirrorFrameX - (this.getWidth() / 2), 
						newMirrorFrameY - (this.getHeight() / 2));
				*/
				return true;
			};
		};
		
		mPadlockBig.attachChild(mPadlockBigTop);
		
		mBackground.attachChild(mDoor);
		mBackground.attachChild(mOppositeWall);
		mBackground.attachChild(mChainsOpenLeft);
		mBackground.attachChild(mChainsOpenRight);
		mBackground.attachChild(mPadlockSmall);
		mBackground.attachChild(mChainsClosed);
		mBackground.attachChild(mPadlockSmallGround);
		mBackground.attachChild(mMirrorFrame);
		mMirrorFrame.attachChild(mMirrorDirt);
		mBackground.attachChild(mPadlockBig);
		this.attachChild(mBackground);
		
		mChainsOpenLeft.setVisible(false);
		mChainsOpenRight.setVisible(false);
		mPadlockSmallGround.setVisible(false);
		mPadlockBig.setVisible(false);
		
		this.registerTouchArea(mDoor);
		this.registerTouchArea(mMirrorFrame);
		
		this.setScale(1.0f);
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
		
		TextureRegion stainTextureRegion = getRegion(MIRROR_DIRT_ID);
		this.mDirtBitmapTextureAtlas = new BitmapTextureAtlas(pEngine.getTextureManager(), 
				(int) stainTextureRegion.getWidth(), (int) stainTextureRegion.getHeight(), TextureOptions.BILINEAR);
		
		this.mDirtBitmapTextureAtlasSource = new EmptyBitmapTextureAtlasSource(
				(int) stainTextureRegion.getWidth(), (int) stainTextureRegion.getHeight());
		final IBitmapTextureAtlasSource decoratedTextureAtlasSource = 
				new BaseBitmapTextureAtlasSourceDecorator(this.mDirtBitmapTextureAtlasSource) {
			
			@Override
			protected void onDecorateBitmap(Canvas pCanvas) throws Exception {
				
				Bitmap bmp = BitmapFactory.decodeStream(pContext.getAssets().open("gfx/special/009/mirror_dirt.png"));
				mDirtBitmap = bmp.copy(Config.ARGB_8888, true);
				bmp.recycle();
				mDirtBitmap.setHasAlpha(true);
				pCanvas.drawBitmap(mDirtBitmap, 0, 0, this.mPaint);
			}
			
			@Override
			public BaseBitmapTextureAtlasSourceDecorator deepCopy() {
				throw new DeepCopyNotSupportedException();
			}
		};
		
		this.mDirtTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromSource(
				this.mDirtBitmapTextureAtlas, decoratedTextureAtlasSource, 0, 0);
		this.mDirtBitmapTextureAtlas.load();
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	/*
	private void wipeDirt(final float pX, final float pY) {
		
		BitmapUtils.drawAlphaSquareReduction((int) pX, (int) pY, this.mDirtBitmap, 50, 10);
		refreshDirt();
	}
	
	private void refreshDirt() {
		
		final IBitmapTextureAtlasSource decoratedTextureAtlasSource = 
				new BaseBitmapTextureAtlasSourceDecorator(this.mDirtBitmapTextureAtlasSource) {
			
			@Override
			protected void onDecorateBitmap(Canvas pCanvas) throws Exception {
				
				//this.mPaint.setColorFilter(new LightingColorFilter(Color.argb(128, 128, 128, 255), Color.TRANSPARENT));
				pCanvas.drawBitmap(mDirtBitmap, 0, 0, this.mPaint);
				//this.mPaint.setColorFilter(null);
			}
			
			@Override
			public BaseBitmapTextureAtlasSourceDecorator deepCopy() {
				throw new DeepCopyNotSupportedException();
			}
		};
		
		this.mDirtBitmapTextureAtlas.clearTextureAtlasSources();
		this.mDirtTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromSource(
				this.mDirtBitmapTextureAtlas, decoratedTextureAtlasSource, 0, 0);
	}
	
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

	private void resetPadlock() {
		
		this.mCode = 0;
		this.mDigitsPressedNb = 0;
		for (ButtonSprite digicodeButton : this.mDigicodeButtonsList) {
			
			digicodeButton.setEnabled(true);
			digicodeButton.setVisible(true);
		}
	}
	
	private void buildPadlock() {
		
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
	
	private void resetDigicode() {
		
		this.mCode = 0;
		this.mDigitsPressedNb = 0;
		for (ButtonSprite digicodeButton : this.mDigicodeButtonsList) {
			
			digicodeButton.setEnabled(true);
			digicodeButton.setVisible(true);
		}
	}*/
	
	// ===========================================================
	// Listeners
	// ===========================================================
	
	OnClickListener mPadlockSmallOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
		}
	};
}
