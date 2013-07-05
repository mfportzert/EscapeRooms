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
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.content.Intent;

import com.mfp.rooms.activities.HomeActivity;
import com.mfp.rooms.activities.LevelsListActivity;
import com.mfp.rooms.scenes.base.BaseScene;
import com.mfp.rooms.sprites.ScalableButtonSprite;
import com.mfp.rooms.texturepacker.TxHome;
import com.mfp.rooms.utils.LevelUtils;
import com.mfp.rooms.utils.SoundUtils;

/**
 *
 * @author M-F.P
 */
public class HomeScreenScene extends BaseScene implements TxHome {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private final static String XML_FILENAME = "home";
	
	private final static float BACKGROUND_WIDTH = 600;
	private final static float BACKGROUND_HEIGHT = 1024;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private ButtonSprite mStartButton;
	private ButtonSprite mContinueButton;
	private ButtonSprite mLevelsButton;
	private ButtonSprite mSoundButton;
	private ButtonSprite mContactButton;
	private Sprite mGameTitle;
	private Sprite mGameLogo;
	private Rectangle mBackground;
	
	private Sound mButtonSound;
	
	private Context mContext;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public HomeScreenScene(SmoothCamera pCamera) {
		super(pCamera, XML_FILENAME);
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	/**
	 * @param pEngine
	 * @param pContext
	 */
	@Override
	public void createScene(Engine pEngine, Context pContext) {
		super.createScene(pEngine, pContext);
		
		this.mContext = pContext;
		
		TextureRegion startButtonRegion = getRegion(NEW_GAME_BUTTON_ID);
		TiledTextureRegion continueButtonRegion = getTiledRegion(CONTINUE_BUTTON_ID, 1, 2);
		TiledTextureRegion levelsButtonRegion = getTiledRegion(LEVELS_BUTTON_ID, 1, 2);
		TextureRegion gameLogoRegion = getRegion(BACKGROUND_ID);
		TextureRegion gameTitleRegion = getRegion(TITLE_ID);
		
		this.mGameLogo = createSprite((getWidth() - gameLogoRegion.getWidth()) / 2, 80, gameLogoRegion);
		this.mGameTitle = createSprite((getWidth() - gameTitleRegion.getWidth()) / 2, 575, gameTitleRegion);
		
		this.mStartButton = new ScalableButtonSprite((this.mBackground.getWidth() - startButtonRegion.getWidth()) / 2, 
				670, startButtonRegion, 1.3f, 1.45f, pEngine.getVertexBufferObjectManager(), this.mStartButtonOnClickListener);
		
		this.mContinueButton = new ButtonSprite((this.mBackground.getWidth() - continueButtonRegion.getWidth()) / 2, 
				715, continueButtonRegion.getTextureRegion(0), continueButtonRegion.getTextureRegion(1),
				continueButtonRegion.getTextureRegion(1), pEngine.getVertexBufferObjectManager(), this.mContinueButtonOnClickListener);
		
		this.mLevelsButton = new ButtonSprite((this.mBackground.getWidth() - levelsButtonRegion.getWidth()) / 2, 
				815, levelsButtonRegion.getTextureRegion(0), levelsButtonRegion.getTextureRegion(1),
				levelsButtonRegion.getTextureRegion(1), pEngine.getVertexBufferObjectManager(), this.mLevelsButtonOnClickListener);
		
		this.mSoundButton = new ButtonSprite(510, 950, getRegion(VOLUME_ID), 
				pEngine.getVertexBufferObjectManager());
		
		this.mContactButton = new ButtonSprite(50, 950, getRegion(CONTACT_ID), 
				pEngine.getVertexBufferObjectManager());
		
		this.mGameLogo.setScale(1.3f);
		this.mContinueButton.setScale(1.1f);
		this.mLevelsButton.setScale(1.1f);
		
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		
		this.registerTouchArea(this.mSoundButton);
		//this.registerTouchArea(this.mStartButton);
		this.registerTouchArea(this.mContinueButton);
		this.registerTouchArea(this.mLevelsButton);
		this.registerTouchArea(this.mContactButton);
		
		this.mBackground.attachChild(this.mGameLogo);
		this.mBackground.attachChild(this.mGameTitle);
		this.mBackground.attachChild(this.mSoundButton);
		//this.mBackground.attachChild(this.mStartButton);
		this.mBackground.attachChild(this.mContinueButton);
		this.mBackground.attachChild(this.mLevelsButton);
		this.mBackground.attachChild(this.mContactButton);
		this.attachChild(this.mBackground);
	}
	
	/**
	 * @param pEngine
	 * @param pContext
	 */
	@Override
	public void createResources(final Engine pEngine, final Context pContext) throws IOException {
		super.createResources(pEngine, pContext);
		
		String dir = "home/";
		this.mButtonSound = createSound(pContext, dir+"bip.ogg");
		
		this.mBackground = createBackground();
		this.mBackground.setColor(Color.BLACK);
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	// ===========================================================
	// Listeners
	// ===========================================================
	
	OnClickListener mStartButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
			SoundUtils.playSound(mButtonSound);
		}
	};
	
	OnClickListener mLevelsButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
			SoundUtils.playSound(mButtonSound);
			Intent intent = new Intent(mContext, LevelsListActivity.class);
			mContext.startActivity(intent);
		}
	};
	
	OnClickListener mContinueButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
			SoundUtils.playSound(mButtonSound);
			Intent intent = new Intent(mContext, LevelsListActivity.class);
			mContext.startActivity(intent);
		}
	};
}
