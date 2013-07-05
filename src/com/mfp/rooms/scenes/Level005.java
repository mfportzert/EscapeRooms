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
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;

import android.content.Context;

import com.mfp.rooms.scenes.base.BaseLevel;
import com.mfp.rooms.sprites.Level005Chain;
import com.mfp.rooms.sprites.Level005Chain.OnChainPulledListener;
import com.mfp.rooms.utils.LevelUtils;
import com.mfp.rooms.utils.SoundUtils;

/**
 *
 * @author M-F.P
 */
public class Level005 extends BaseLevel implements OnClickListener, IAccelerationListener, OnChainPulledListener {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final int LEVEL_NUMBER = 5;
	
	public static final int BACKGROUND_ID = 0;
	public static final int CHAIN_SMALL_ID = 1;
	public static final int DOOR_CLOSED_ID = 2;
	public static final int WATER_ID = 3;
	public static final int WATER_TILE1_ID = 4;
	public static final int WATER_TILE2_ID = 5;
	public static final int WATER_TILE3_ID = 6;
	public static final int WATER_TILE4_ID = 7;
	public static final int WATER_TILE5_ID = 8;
	public static final int WATER_TOP_ID = 9;
	
	public static final int[] WATER_TILES_ID = {
		WATER_TILE1_ID, 
		WATER_TILE2_ID, 
		WATER_TILE3_ID, 
		WATER_TILE4_ID, 
		WATER_TILE5_ID
	};
	
	public static final int[] TILES_MARGIN_LEFT = { 12, 13, 8, 18, 0 };
	public static final int[] TILES_DOWN_MARGIN_LEFT = { 15, 10, 0, -11, -16 };
	
	public static final boolean[][] TILES_MOVES = { 
		{ true, true, false, true, false }, 
		{ false, true, true, true, false }, 
		{ true, false, true, false, true }, 
		{ false, true, false, true, true } 
	};
	
	public static final int TILE_UP = 0;
	public static final int TILE_DOWN = 1;
	
	public static final float TILE_DOWN_ZOOM = 1.06f;
	
	public static final int OFFSET_WATER_LEVEL = 100;
	public static final long WATER_FRAMES_DURATION = 250;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private Sound mOpenDoorSound;
	private Sound mChainPulledSound;
	private Sound mWaterFlowSound;
	
	private float mWaterBasePosY;
	private float mWaterTopBasePosY;
	private float[] mWaterTextureBasePosY;
	
	private int mNbTilesDown = 0;
	private boolean mMovesAvailable = true;
	private boolean mDoorOpened = false;
	
	private ButtonSprite mClosedDoor;
	private AnimatedSprite mWater;
	private AnimatedSprite mWaterTop;
	private MoveYModifier mWaterMoveYModifier;
	
	private Level005Chain[] mChains = new Level005Chain[4];
	private AnimatedSprite[] mWaterTiles = new AnimatedSprite[5];
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public Level005(SmoothCamera pCamera) {
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
		
    }
	
	@Override
	public void onChainPulled(int chainNumber) {
		
		if (this.mMovesAvailable && !this.mCleared) {
			
			this.mMovesAvailable = false;
			for (int i = 0; i < TILES_MOVES[chainNumber].length; i++) {
				
				if (TILES_MOVES[chainNumber][i]) {
					
					final AnimatedSprite tile = this.mWaterTiles[i];
					Path path = new Path(2).to(tile.getX(), tile.getY())
							.to((tile.getTag() == TILE_UP) ? 
									tile.getX() - TILES_DOWN_MARGIN_LEFT[i] : tile.getX() + TILES_DOWN_MARGIN_LEFT[i], 
									tile.getY() + ((tile.getTag() == TILE_UP) ? 62 : -62));
					
					tile.setTag((tile.getTag() == TILE_UP) ? TILE_DOWN : TILE_UP);
					tile.registerEntityModifier(new ParallelEntityModifier(
							new PathModifier(0.6f, path, null, new IPathModifierListener() {
								
								@Override
								public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
									tile.animate(WATER_FRAMES_DURATION, true);
								}
								
								@Override
								public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
									
								}
								
								@Override
								public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
				
								}
								
								@Override
								public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {
									
								}
							}), 
							new ScaleModifier(0.6f, (tile.getTag() == TILE_UP) ? TILE_DOWN_ZOOM : 1, 
									(tile.getTag() == TILE_UP) ? 1 : TILE_DOWN_ZOOM)));
				}
				
				this.registerEntityModifier(new DelayModifier(0.6f, new IEntityModifierListener() {
					
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
						
					}
	
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						
						Level005.this.mMovesAvailable = true;
						Level005.this.changeWaterLevel();
					}
				}));
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
	public void createScene(Engine pEngine, final Context pContext) {
		super.createScene(pEngine, pContext);
		
		TiledTextureRegion waterTiledRegion = getTiledRegion(WATER_ID, 1, 2);
		this.mWater = createAnimatedSprite(0, 231, waterTiledRegion);
		
		int nbTiles = waterTiledRegion.getTileCount();
		this.mWaterTextureBasePosY = new float[nbTiles];
		for (int i = 0; i < nbTiles; i++) {
			this.mWaterTextureBasePosY[i] = this.mWater.getTiledTextureRegion().getTextureY(i);
		}
		
		this.mWaterTop = createAnimatedSprite(0, 218, WATER_TOP_ID, 1, 3);
		
		this.mWaterBasePosY = this.mWater.getY();
		this.mWaterTopBasePosY = this.mWaterTop.getY();
		
		this.mClosedDoor = createButtonSprite(314, 283, DOOR_CLOSED_ID, this);
		this.mClosedDoor.setEnabled(false);
		
		mBackground.attachChild(this.mClosedDoor);
		mBackground.attachChild(this.mWater);
		
		for (int i = 0, offsetX = 18; i < WATER_TILES_ID.length; i++) {
			
			TiledTextureRegion region = getTiledRegion(WATER_TILES_ID[i], 1, 2);
			AnimatedSprite tile = createAnimatedSprite(offsetX, 775, region);
			
			tile.setTag(TILE_UP);
			this.mWaterTiles[i] = tile;
			offsetX += region.getWidth(0) - TILES_MARGIN_LEFT[i];
			
			mBackground.attachChild(tile);
		}
		
		mBackground.attachChild(this.mWaterTop);
		
		TextureRegion chainRegion = getRegion(CHAIN_SMALL_ID);
		for (int i = 0, offsetX = 22; i < this.mChains.length; i++, offsetX += (chainRegion.getWidth())) {
			
			Level005Chain chain = new Level005Chain(offsetX, -100, chainRegion, 
					pEngine.getVertexBufferObjectManager(), this.mChainPulledSound, this);
			chain.setTag(i);
			
			this.mChains[i] = chain;
			mBackground.attachChild(chain);
			this.registerTouchArea(chain);
		}
		
		this.attachChild(mBackground);
		
		this.registerTouchArea(this.mClosedDoor);
		
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
		this.mChainPulledSound = createSound(pContext, "chain_pull.ogg");
		this.mWaterFlowSound = createSound(pContext, "water_flow.ogg");
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	private void openAccessNextLevel() {
		
		this.mDoorOpened = true;
		for (int i = 0; i < Level005.this.mWaterTiles.length; i++) {
			Level005.this.mWaterTiles[i].setVisible(false);
		}
		
		this.mClosedDoor.setAlpha(0);
		this.mClosedDoor.setEnabled(true);
		SoundUtils.playSound(this.mOpenDoorSound);
	}
	
	private int getNumberTilesDown() {
		
		int nbTilesDown = 0;
		for (AnimatedSprite tile : Level005.this.mWaterTiles) {
			if (tile.getTag() == TILE_DOWN) {
				nbTilesDown++;
			}
		}
		
		return nbTilesDown;
	}
	
	private void resetAnimations() {
		
		mWater.stopAnimation();
		mWater.setCurrentTileIndex(0);
		mWaterTop.stopAnimation();
		mWaterTop.setCurrentTileIndex(0);
		for (int i = 0; i < mWaterTiles.length; i++) {
			
			mWaterTiles[i].stopAnimation();
			mWaterTiles[i].setCurrentTileIndex(0);
		}
	}
	
	private void changeWaterLevel() {
		
		int nbTilesDown = this.getNumberTilesDown();
		if (nbTilesDown == this.mNbTilesDown) {
			this.resetAnimations();
			return;
		}
		
		int nbTilesMoving = Math.abs(this.mNbTilesDown - nbTilesDown);
		this.mNbTilesDown = nbTilesDown;
		if (this.mNbTilesDown == this.mWaterTiles.length) {
			this.mCleared = true;
			nbTilesDown += 3;
		}
		
		MoveYModifier waterMoveYModifier = new MoveYModifier(0.5f * nbTilesMoving, 
				this.mWater.getY(), this.mWaterBasePosY + (OFFSET_WATER_LEVEL * nbTilesDown)) {
			
			@Override
			protected void onSetInitialValue(final IEntity pEntity, final float pY) {
				
				SoundUtils.playSound(Level005.this.mWaterFlowSound, 0.3f);
				Level005.this.mWater.animate(WATER_FRAMES_DURATION, true);
				Level005.this.mWaterTop.animate(WATER_FRAMES_DURATION, true);
				for (int i = 0; i < Level005.this.mWaterTiles.length; i++) {
					Level005.this.mWaterTiles[i].animate(WATER_FRAMES_DURATION, true);
				}
			}
			
			@Override
			protected void onSetValue(final IEntity pEntity, final float pPercentageDone, final float pY) {
				
				float offsetTop = pY - Level005.this.mWaterBasePosY;
				ITiledTextureRegion waterTiledRegion = Level005.this.mWater.getTiledTextureRegion();
				for (int i = 0; i < waterTiledRegion.getTileCount(); i++) {
					waterTiledRegion.setTextureY(i, Level005.this.mWaterTextureBasePosY[i] + offsetTop);
				}
				
				Level005.this.mWater.setPosition(Level005.this.mWater.getX(), pY);
				Level005.this.mWaterTop.setPosition(Level005.this.mWater.getX(), Level005.this.mWaterTopBasePosY + offsetTop);
				Level005.this.mWater.getVertexBufferObject().onUpdateTextureCoordinates(Level005.this.mWater);
				
				if (Level005.this.mCleared && !Level005.this.mDoorOpened && 
						(pY > Level005.this.mWaterTiles[0].getY() + (Level005.this.mWaterTiles[0].getHeight() / 2))) {
					Level005.this.openAccessNextLevel();
				}
				if (pPercentageDone == 1f) {
					Level005.this.resetAnimations();
				}
			}
		};
		
		if (this.mWaterMoveYModifier != null && !this.mWaterMoveYModifier.isFinished()) {
			this.mWater.unregisterEntityModifier(this.mWaterMoveYModifier);
		}
		
		this.mWaterMoveYModifier = waterMoveYModifier;
		this.mWater.registerEntityModifier(this.mWaterMoveYModifier);
	}
}
