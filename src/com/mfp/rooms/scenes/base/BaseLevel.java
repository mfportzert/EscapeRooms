package com.mfp.rooms.scenes.base;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import android.content.Context;

import com.mfp.rooms.gui.LevelGui;
import com.mfp.rooms.interfaces.ILevelActivity;
import com.mfp.rooms.inventory.Inventory;
import com.mfp.rooms.scenes.Level001;
import com.mfp.rooms.utils.LevelUtils;

public abstract class BaseLevel extends BaseScene {
	
	public static final String LEVEL_PKG_NAME = Level001.class.getPackage().getName()+".Level";
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private int mLevelNumber;
	protected boolean mCleared = false;
	
	private ILevelActivity mLevelActivity;
	protected Sprite mBackground;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public BaseLevel(SmoothCamera camera, int levelNumber) {
		super(camera, LevelUtils.toLevelNbFormat(levelNumber));
		mLevelNumber = levelNumber;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public SmoothCamera getCamera() {
		return this.mCamera;
	}
	
	public void setLevelActivity(ILevelActivity levelActivity) {
		this.mLevelActivity = levelActivity;
	}
	
	public Inventory getInventory() {
		return this.getGui().getInventory();
	}
	
	public LevelGui getGui() {
		return (LevelGui) this.mCamera.getHUD();
	}
	
	public Sprite getBackgroundSprite() {
		return this.mBackground;
	}
	
	public int getLevelNumber() {
		return this.mLevelNumber;
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	abstract protected int getBackgroundRegionId();
	
	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		return super.onSceneTouchEvent(pScene, pSceneTouchEvent);
	}
	
	@Override
	public void createResources(Engine pEngine, Context pContext) throws IOException {
		super.createResources(pEngine, pContext);
	}
	
	@Override
	public void createScene(Engine pEngine, Context pContext) {
		super.createScene(pEngine, pContext);
		
		mBackground = createBackground(getBackgroundRegionId());
		this.getGui().resetItemsSelection();
		
		/*
		 * TODO: Add necessary items if not in bag... (access from levels list)
		 * Possible use of xml to define them...
		 */
		
		//this.getGui().initBag();
	}
	
	@Override
	public Sound createSound(Context context, String fileName) throws IOException {
		return super.createSound(context, LevelUtils.toLevelNbFormat(mLevelNumber)+"/"+fileName);
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	protected void accessNextLevel(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		float[] sceneCoords = pButtonSprite.convertLocalToSceneCoordinates(pTouchAreaLocalX, pTouchAreaLocalY);
		this.mLevelActivity.accessNextLevel(sceneCoords[0], sceneCoords[1]);
	}
}
