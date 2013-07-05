package com.mfp.rooms.sprites;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.mfp.rooms.utils.SoundUtils;

public class Level005Chain extends Sprite {
		
	private enum State { FREE, PULLED, SCROLLING_BACK };
	
	private State mState = State.FREE;
	
	private float mMinPosY;
	private float mMaxPosY;
	private float mLastPosY;
	
	private Sound mChainPullSound;
	private OnChainPulledListener mChainPulledListener;
	
	public Level005Chain(final float pX, final float pY, TextureRegion region, 
			VertexBufferObjectManager vertexBufferObjectManager, Sound chainPulledSound, 
			OnChainPulledListener chainPulledListener) {
		super(pX, pY, region, vertexBufferObjectManager);
		
		this.mMinPosY = pY;
		this.mMaxPosY = pY + 100;
		this.mChainPullSound = chainPulledSound;
		this.mChainPulledListener = chainPulledListener;
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, 
			final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		
		switch (pSceneTouchEvent.getAction()) {
			
			case TouchEvent.ACTION_DOWN:
				
				if (this.mState == State.FREE) {
					
					this.mState = State.PULLED;
					this.mLastPosY = pSceneTouchEvent.getY();
				}
				break;
				
			case TouchEvent.ACTION_MOVE:
				
				if (this.mState == State.PULLED) {
					
					float offset = this.mLastPosY - pSceneTouchEvent.getY();
					float newPositionY = this.getY() - offset;
					if (newPositionY < this.mMaxPosY && newPositionY > this.mMinPosY) {
						
						if (newPositionY > this.mMinPosY + ((this.mMaxPosY - this.mMinPosY) / 2)) {
							
							Path path = new Path(3).to(this.getX(), this.getY())
								.to(this.getX(), this.mMaxPosY)
								.to(this.getX(), this.mMinPosY);
							
							this.registerEntityModifier(new PathModifier(0.5f, path, null, new IPathModifierListener() {
								
								@Override
								public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
									
									SoundUtils.playSound(Level005Chain.this.mChainPullSound, 0.1f);
									Level005Chain.this.mState = State.SCROLLING_BACK;
									if (Level005Chain.this.mChainPulledListener != null) {
										Level005Chain.this.mChainPulledListener.onChainPulled(Level005Chain.this.getTag());
									}
								}
								
								@Override
								public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
									
								}
								
								@Override
								public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
				
								}
								
								@Override
								public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {
									Level005Chain.this.mState = State.FREE;
								}
							}));
							
						} else {
							this.setPosition(this.getX(), this.getY() - offset);
							this.mLastPosY = pSceneTouchEvent.getY();
						}
					}
				}
				break;
				
			case TouchEvent.ACTION_UP:
				
				if (this.getY() > this.mMinPosY && this.mState != State.SCROLLING_BACK) {
					
					Path path = new Path(2).to(this.getX(), this.getY())
							.to(this.getX(), this.mMinPosY);
					
					this.registerEntityModifier(new PathModifier(0.2f, path, null, new IPathModifierListener() {
						
						@Override
						public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
							Level005Chain.this.mState = State.SCROLLING_BACK;
						}
						
						@Override
						public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
							
						}
						
						@Override
						public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
		
						}
						
						@Override
						public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {
							Level005Chain.this.mState = State.FREE;
						}
					}));
				}
				break;
		}
		
		return true;
	}
	
	// ===========================================================
	// Inner classes
	// ===========================================================
	
	public interface OnChainPulledListener {
		
		public void onChainPulled(int chainNumber);
	}
};
