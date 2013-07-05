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
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.mfp.rooms.activities.LevelActivity;
import com.mfp.rooms.activities.LevelsListActivity;
import com.mfp.rooms.scenes.base.BaseScene;
import com.mfp.rooms.sprites.ScalableButtonSprite;
import com.mfp.rooms.texturepacker.TxLevelsList;
import com.mfp.rooms.utils.LevelUtils;
import com.mfp.rooms.utils.SoundUtils;

/**
 *
 * @author M-F.P
 */
public class LevelsListScene extends BaseScene implements TxLevelsList {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final String XML_FILENAME = "levels_list";
	
	private static final int LEVELS_PER_ROW = 4;
	private static final int FONT_SIZE = 50;
	private static final int TEXT_LEVEL_TAG = 1;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private Font mFffTusjFont;
    //private Font mSketchetikFont;
    
    private BitmapTextureAtlas mFffTusjFontTexture;
    //private BitmapTextureAtlas mSketchetikFontTexture;
    
	private Context mContext;
	private ArrayList<ButtonSprite> mLevelsButtons = new ArrayList<ButtonSprite>(LevelActivity.LEVELS_NUMBER);
	
	private Sound mButtonSound;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public LevelsListScene(SmoothCamera pCamera) {
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
		TextureRegion levelBoxRegion = getRegion(LEVEL_BOX_ID);
		Sprite background = createBackground(BACKGROUND_ID);
		
		float x = 40;
    	float y = 270;
    	for (int i = 0; i < (LevelActivity.LEVELS_NUMBER / LEVELS_PER_ROW); i++) {
    		
    		for (int j = 0; (j < LEVELS_PER_ROW) && 
    				(((i * LEVELS_PER_ROW) + j) < LevelActivity.LEVELS_NUMBER); j++) {
    			
    			ButtonSprite levelButton = new ScalableButtonSprite(x, y, levelBoxRegion, 1.1f, 1f, 
    					pEngine.getVertexBufferObjectManager(), this.mLevelButtonOnClickListener);
    			
    			Text levelNumber = new Text(0, 0, this.mFffTusjFont, String.valueOf((i * LEVELS_PER_ROW) + j + 1), 
    					pEngine.getVertexBufferObjectManager());
    			levelNumber.setPosition((levelButton.getWidth() - levelNumber.getWidth()) / 2, 
    					(levelButton.getHeight() - levelNumber.getHeight()) / 2);
    			levelNumber.setColor(Color.BLACK);
    			levelNumber.setTag(TEXT_LEVEL_TAG);
    			
    			levelButton.attachChild(levelNumber);
    			background.attachChild(levelButton);
    			
    			this.registerTouchArea(levelButton);
    			this.mLevelsButtons.add(levelButton);
    			
    			x += levelBoxRegion.getWidth() + 35;
    		}
    		
    		y += levelBoxRegion.getHeight() + 50;
    		x = 40;
    	}
		
    	this.setTouchAreaBindingOnActionMoveEnabled(true);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		
		this.attachChild(background);
	}
	
	/**
	 * @param pEngine
	 * @param pContext
	 */
	@Override
	public void createResources(final Engine pEngine, final Context pContext) throws IOException {
		super.createResources(pEngine, pContext);
		
		String dir = "levels_list/";
		this.mButtonSound = createSound(pContext, dir+"bip.ogg");
		
		this.mFffTusjFontTexture = new BitmapTextureAtlas(pEngine.getTextureManager(), 
        		256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        /*
        this.mSketchetikFontTexture = new BitmapTextureAtlas(pEngine.getTextureManager(), 
        		256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        */
        
        FontFactory.setAssetBasePath("font/");
        this.mFffTusjFont = FontFactory.createFromAsset(pEngine.getFontManager(), this.mFffTusjFontTexture, 
        		pContext.getAssets(), "FFF_Tusj.ttf", FONT_SIZE, true, Color.WHITE_ARGB_PACKED_INT);
        /*
        this.mSketchetikFont = FontFactory.createFromAsset(pEngine.getFontManager(), this.mSketchetikFontTexture, 
        		pContext.getAssets(), "Sketchetik-Light.ttf", FONT_SIZE, true, Color.WHITE_ARGB_PACKED_INT);
        */
        //this.mSketchetikFontTexture.load();
        this.mFffTusjFontTexture.load();
        
        //this.mSketchetikFont.load();
        this.mFffTusjFont.load();
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	// ===========================================================
	// Listeners
	// ===========================================================
	
	OnClickListener mLevelButtonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
				float pTouchAreaLocalY) {
			
			Text levelText = (Text) pButtonSprite.getChildByTag(TEXT_LEVEL_TAG);
			int levelNumber = (int) Integer.valueOf(levelText.getText().toString());
			
			SoundUtils.playSound(mButtonSound);
			Intent intent = new Intent(mContext, LevelActivity.class);
			intent.putExtra(LevelActivity.EXTRA_LEVEL_NUMBER, levelNumber);
			mContext.startActivity(intent);
			((Activity) mContext).finish();
		}
	};
}
