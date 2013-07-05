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
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.util.debug.Debug;

import android.content.Context;

import com.mfp.rooms.scenes.base.BaseLevel;
import com.mfp.rooms.utils.LevelUtils;
import com.mfp.rooms.utils.SoundUtils;

/**
 *
 * @author M-F.P
 */
public class Level002 extends BaseLevel implements OnClickListener, IAccelerationListener {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final int LEVEL_NUMBER = 2;
	
	public static final int BACKGROUND_ID = 0;
	public static final int BALCONY_ID = 1;
	public static final int DOOR_ID = 2;
	public static final int LADDER_ID = 3;
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private float mStartLadderPostY;
	private double mLastTimeScrolled = System.currentTimeMillis();
	private int mLadderScrollMinDelay = 500;
	private int mScaleDownLadderCount = 0;
	
	private Sound mOpenDoorSound;
	private Sound mLadderScrollSound;
	private Sound mLadderHitGroundSound;
    private Sprite mLadder;
    
    private ButtonSprite mDoor;
    
	// ===========================================================
	// Constructors
	// ===========================================================

	public Level002(SmoothCamera pCamera) {
		super(pCamera, LEVEL_NUMBER);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		
		if (this.mCleared) {
			this.accessNextLevel(pButtonSprite, pTouchAreaLocalX, pTouchAreaLocalY);
		} else {
			pButtonSprite.setAlpha(0);
			SoundUtils.playSound(this.mOpenDoorSound);
			this.mCleared = true;
		}
	}
	
	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void onAccelerationChanged(AccelerationData pAccelerationData) {
		
		if (pAccelerationData.getY() < -5.0){
            
			double currTime = System.currentTimeMillis();
			
			double delay = currTime - this.mLastTimeScrolled;
			if (delay > this.mLadderScrollMinDelay) {
				
				this.scaleDownLadder();
				this.mLastTimeScrolled = currTime;
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
		
		this.mLadder = createSprite(420, -640, LADDER_ID);
		this.mStartLadderPostY = this.mLadder.getY();
		
		Sprite balcony = createSprite(0, 220, BALCONY_ID);
		
		this.mDoor = createButtonSprite(126, 16, DOOR_ID, 1, 2, this);
		this.mDoor.setEnabled(false);
		
		this.mBackground.attachChild(this.mDoor);
		this.mBackground.attachChild(this.mLadder);
		this.mBackground.attachChild(balcony);
		this.attachChild(mBackground);
		
		this.registerTouchArea(this.mDoor);
		
		this.setOnSceneTouchListener(this);
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
	}
	
	/**
	 * @param pEngine
	 * @param pContext
	 * @throws IOException 
	 */
	@Override
	public void createResources(final Engine pEngine, final Context pContext) throws IOException {
		super.createResources(pEngine, pContext);
		
		this.mOpenDoorSound = createSound(pContext, "metal_door_open.ogg");
		this.mLadderScrollSound = createSound(pContext, "ladder_scroll.ogg");
		this.mLadderHitGroundSound = createSound(pContext, "ladder_hit_ground.ogg");
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	private void scaleDownLadder() {
		
		if (this.mScaleDownLadderCount < 2) {
			
			Path path = new Path(2)
				.to(this.mLadder.getX(), this.mLadder.getY())
				.to(this.mLadder.getX(), this.mStartLadderPostY+((this.mScaleDownLadderCount+1)*285));
			
			this.mLadder.registerEntityModifier(new PathModifier(0.5f, path, null, new IPathModifierListener() {
				
				@Override
				public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
					
					mScaleDownLadderCount++;
					SoundUtils.playSound(mLadderScrollSound);
				}
				
				@Override
				public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
					
				}
				
				@Override
				public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

				}
				
				@Override
				public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {
					
					if (mScaleDownLadderCount == 2) {
						
						SoundUtils.playSound(mLadderHitGroundSound);
						mDoor.setEnabled(true);
					}
				}
			}));
		}
	}
}
