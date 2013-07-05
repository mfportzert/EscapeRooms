package com.mfp.rooms.sprites;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Level006Curtain extends Sprite {
	
	private boolean mTouchedDown = false;
	
	private float mStartTouchY;
	private float mMaxWidth;
	private float mMinWidth;
	private float mLastWidth;
	private float mLastPosX;
	
	private static enum State { START, LEFT, RIGHT }
	
	public Level006Curtain(final float pX, final float pY, TextureRegion region, 
			VertexBufferObjectManager vertexBufferObjectManager, float startTouchY) {
		super(pX, pY, region, vertexBufferObjectManager);
		
		this.mStartTouchY = startTouchY;
		this.mMaxWidth = region.getWidth();
		this.mMinWidth = region.getWidth() / 2;
	}

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, 
			final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		
		switch (pSceneTouchEvent.getAction()) {
	
			case TouchEvent.ACTION_DOWN:
				
				if (pSceneTouchEvent.getY() > Level006Curtain.this.mStartTouchY) {
					
					Level006Curtain.this.mTouchedDown = true;
					Level006Curtain.this.mLastWidth = this.getWidth();
					Level006Curtain.this.mLastPosX = pSceneTouchEvent.getX();
				}
				break;
				
			case TouchEvent.ACTION_MOVE:							
				
				if (Level006Curtain.this.mTouchedDown) {
					
					if (pSceneTouchEvent.getY() > Level006Curtain.this.mStartTouchY) {
					
						boolean toLeft = (Level006Curtain.this.mLastPosX > pSceneTouchEvent.getX());
						
						float offset = Level006Curtain.this.mLastPosX - pSceneTouchEvent.getX();
						float newWidth = Level006Curtain.this.mLastWidth - offset;
						if (newWidth > Level006Curtain.this.mMaxWidth) {
							newWidth -= (newWidth - Level006Curtain.this.mMaxWidth) * 2;
						}
						
						if (newWidth >= Level006Curtain.this.mMinWidth) {
							/*
							if (!toLeft) {
								this.setPosition(this.getX() - offset, this.getY());
							}
							*/
							this.setWidth(newWidth);
							Level006Curtain.this.mLastWidth = this.getWidth();
						}
					}
					Level006Curtain.this.mLastPosX = pSceneTouchEvent.getX();
				}
				break;
				
			case TouchEvent.ACTION_UP:
				Level006Curtain.this.mTouchedDown = false;
				break;
		}
		
		return true;
	}
}
