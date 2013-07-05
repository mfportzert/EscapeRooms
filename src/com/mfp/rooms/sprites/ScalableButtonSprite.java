package com.mfp.rooms.sprites;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.State;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ScalableButtonSprite extends ButtonSprite {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	private float mSelectedScale = 1;
	private float mUnselectedScale = 1;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public ScalableButtonSprite(final float pX, final float pY, final ITextureRegion pNormalTextureRegion, final float pSelectedScale, final float pUnselectedScale, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pNormalTextureRegion, pSelectedScale, pUnselectedScale, pVertexBufferObjectManager, (OnClickListener) null);
	}

	public ScalableButtonSprite(final float pX, final float pY, final ITextureRegion pNormalTextureRegion, final float pSelectedScale, final float pUnselectedScale, final VertexBufferObjectManager pVertexBufferObjectManager, final OnClickListener pOnClickListener) {
		this(pX, pY, new TiledTextureRegion(pNormalTextureRegion.getTexture(), pNormalTextureRegion), pSelectedScale, pUnselectedScale, pVertexBufferObjectManager, pOnClickListener);
	}

	public ScalableButtonSprite(final float pX, final float pY, final ITextureRegion pNormalTextureRegion, final ITextureRegion pPressedTextureRegion, final float pSelectedScale, final float pUnselectedScale, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pNormalTextureRegion, pPressedTextureRegion, pSelectedScale, pUnselectedScale, pVertexBufferObjectManager, (OnClickListener) null);
	}

	public ScalableButtonSprite(final float pX, final float pY, final ITextureRegion pNormalTextureRegion, final ITextureRegion pPressedTextureRegion, final float pSelectedScale, final float pUnselectedScale, final VertexBufferObjectManager pVertexBufferObjectManager, final OnClickListener pOnClickListener) {
		this(pX, pY, new TiledTextureRegion(pNormalTextureRegion.getTexture(), pNormalTextureRegion, pPressedTextureRegion), pSelectedScale, pUnselectedScale, pVertexBufferObjectManager, pOnClickListener);
	}

	public ScalableButtonSprite(final float pX, final float pY, final ITextureRegion pNormalTextureRegion, final ITextureRegion pPressedTextureRegion, final ITextureRegion pDisabledTextureRegion, final float pSelectedScale, final float pUnselectedScale, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pNormalTextureRegion, pPressedTextureRegion, pDisabledTextureRegion, pSelectedScale, pUnselectedScale, pVertexBufferObjectManager, (OnClickListener) null);
	}

	public ScalableButtonSprite(final float pX, final float pY, final ITextureRegion pNormalTextureRegion, final ITextureRegion pPressedTextureRegion, final ITextureRegion pDisabledTextureRegion, final float pSelectedScale, final float pUnselectedScale, final VertexBufferObjectManager pVertexBufferObjectManager, final OnClickListener pOnClickListener) {
		this(pX, pY, new TiledTextureRegion(pNormalTextureRegion.getTexture(), pNormalTextureRegion, pPressedTextureRegion, pDisabledTextureRegion), pSelectedScale, pUnselectedScale, pVertexBufferObjectManager, pOnClickListener);
	}

	public ScalableButtonSprite(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final float pSelectedScale, final float pUnselectedScale, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pTiledTextureRegion, pSelectedScale, pUnselectedScale, pVertexBufferObjectManager, (OnClickListener) null);
	}

	public ScalableButtonSprite(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final float pSelectedScale, final float pUnselectedScale, final VertexBufferObjectManager pVertexBufferObjectManager, final OnClickListener pOnClickListener) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager, pOnClickListener);
		
		this.mSelectedScale = pSelectedScale;
		this.mUnselectedScale = pUnselectedScale;

		this.setScale(pUnselectedScale);
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public void setSelectedScale(float selectedScale) {
		this.mSelectedScale = selectedScale;
	}
	
	public void setUnselectedScale(float unselectedScale) {
		this.mUnselectedScale = unselectedScale;
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		
		if(pSceneTouchEvent.isActionDown()) {
			this.setScale(this.mSelectedScale);
		} else if(pSceneTouchEvent.isActionCancel() || !this.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
			this.setScale(this.mUnselectedScale);
		} else if(pSceneTouchEvent.isActionUp() && this.getState() == State.PRESSED) {
			this.setScale(this.mUnselectedScale);
		}
		
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
