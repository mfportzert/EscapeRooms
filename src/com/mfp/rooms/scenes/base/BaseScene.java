package com.mfp.rooms.scenes.base;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.audio.sound.SoundManager;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;

import com.mfp.rooms.camera.CameraManager;
import com.mfp.rooms.interfaces.IScene;
import com.mfp.rooms.textures.TexturesFactory;
import com.mfp.rooms.utils.LevelUtils;

public class BaseScene extends CameraManager implements IScene {
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private SoundManager mSoundManager;
	private VertexBufferObjectManager mVertexBufferObjectManager;
	private TexturesFactory mLevelTexturesFactory;
	private String mXmlSpritesheetFileName;
	
	private float mCenterX = 0;
	private float mCenterY = 0;
	private float mMinX = 0;
	private float mMaxX = 0;
	private float mMinY = 0;
	private float mMaxY = 0;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public BaseScene(SmoothCamera camera, String xmlSpritesheetFileName) {
		super(camera);
		
		this.mXmlSpritesheetFileName = xmlSpritesheetFileName;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public SmoothCamera getCamera() {
		return this.mCamera;
	}
	
	public float getWidth() {
		return mMaxX - mMinX;
	}

	public float getHeight() {
		return mMaxY - mMinY;
	}
	
	public float getCenterX() {
		return mCenterX;
	}

	public float getCenterY() {
		return mCenterY;		
	}
	
	public float getMinX() {
		return mMinX;
	}
	
	public float getMaxX() {
		return mMaxX;		
	}
	
	public float getMinY() {
		return mMinY;		
	}
	
	public float getMaxY() {
		return mMaxY;		
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		return super.onSceneTouchEvent(pScene, pSceneTouchEvent);
	} 
	
	@Override
	public void createResources(Engine pEngine, Context pContext) throws IOException {
		
		mLevelTexturesFactory = new TexturesFactory();
		mLevelTexturesFactory.loadSpritesheets(pEngine, pContext, "xml/"+mXmlSpritesheetFileName+".xml");
		
		mSoundManager = pEngine.getSoundManager();
	}
	
	@Override
	public void createScene(Engine pEngine, Context pContext) {
		mVertexBufferObjectManager = pEngine.getVertexBufferObjectManager();
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	public Sprite createSprite(final float posX, final float posY, TextureRegion region) {
		return new Sprite(posX, posY, region, mVertexBufferObjectManager);
	}
	
	public Sprite createSprite(final float posX, final float posY, int region) {
		return createSprite(posX, posY, mLevelTexturesFactory.getRegion(region));
	}
	
	public AnimatedSprite createAnimatedSprite(final float posX, final float posY, TiledTextureRegion tiledRegion) {
		return new AnimatedSprite(posX, posY, tiledRegion, mVertexBufferObjectManager);
	}
	
	public AnimatedSprite createAnimatedSprite(final float posX, final float posY, int tiledRegion, 
			int rows, int columns) {
		return createAnimatedSprite(posX, posY, mLevelTexturesFactory.getTiled(tiledRegion, rows, columns));
	}
	
	public ButtonSprite createButtonSprite(final float posX, final float posY, TextureRegion region, 
			OnClickListener onClickListener) {
		return new ButtonSprite(posX, posY, region, mVertexBufferObjectManager, onClickListener);
	}
	
	public ButtonSprite createButtonSprite(final float posX, final float posY, 
			TiledTextureRegion tiledRegion, OnClickListener onClickListener) {
		return new ButtonSprite(posX, posY, tiledRegion, mVertexBufferObjectManager, onClickListener);
	}

	public ButtonSprite createButtonSprite(final float posX, final float posY, int region, 
			OnClickListener onClickListener) {
		return createButtonSprite(posX, posY, mLevelTexturesFactory.getRegion(region), onClickListener);
	}
	public ButtonSprite createButtonSprite(final float posX, final float posY, int region, 
			final int rows, final int columns, OnClickListener onClickListener) {
		return createButtonSprite(posX, posY, mLevelTexturesFactory.getTiled(region, rows, columns), 
				onClickListener);
	}
	
	public Sprite createBackground(int backgroundRegionId) {
		
		TextureRegion backgroundRegion = mLevelTexturesFactory.getRegion(backgroundRegionId);
		
		float centerX = (CAMERA_WIDTH - backgroundRegion.getWidth()) / 2;
		float centerY = (CAMERA_HEIGHT - backgroundRegion.getHeight()) / 2;
		
		mCenterX = centerX + backgroundRegion.getWidth() / 2;
		mCenterY = centerY + backgroundRegion.getHeight() / 2;
		mMinX = centerX;
		mMaxX = centerX + backgroundRegion.getWidth();
		mMinY = centerY;
		mMaxY = centerY + backgroundRegion.getHeight();
		
		return LevelUtils.scaleToScreen(createSprite(centerX, centerY, backgroundRegionId));
	}
	
	public Rectangle createBackground() {
		
		float centerX = (CAMERA_WIDTH - DEFAULT_CAMERA_WIDTH) / 2;
		float centerY = (CAMERA_HEIGHT - DEFAULT_CAMERA_HEIGHT) / 2;
		
		mCenterX = centerX + DEFAULT_CAMERA_WIDTH / 2;
		mCenterY = centerY + DEFAULT_CAMERA_HEIGHT / 2;
		mMinX = centerX;
		mMaxX = centerX + DEFAULT_CAMERA_WIDTH;
		mMinY = centerY;
		mMaxY = centerY + DEFAULT_CAMERA_HEIGHT;
		
		return LevelUtils.scaleToScreen(new Rectangle(centerX, centerY, 
				DEFAULT_CAMERA_WIDTH, DEFAULT_CAMERA_HEIGHT, mVertexBufferObjectManager));
	}
	
	public Sound createSound(Context context, String fileName) throws IOException {
		return SoundFactory.createSoundFromAsset(mSoundManager, context, fileName);
	}
	
	public TextureRegion getRegion(int regionId) {
		return this.mLevelTexturesFactory.getRegion(regionId);
	}

	public TiledTextureRegion getTiledRegion(int regionId, int rows, int columns) {
		return this.mLevelTexturesFactory.getTiled(regionId, rows, columns);
	}
}
