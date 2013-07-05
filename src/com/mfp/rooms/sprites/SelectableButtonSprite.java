package com.mfp.rooms.sprites;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

public class SelectableButtonSprite extends TiledSprite {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final int mStateCount;
	private OnClickListener mOnClickListener;
	
	private boolean mSelected = false;
	private boolean mEnabled = true;
	private State mState;

	// ===========================================================
	// Constructors
	// ===========================================================

	public SelectableButtonSprite(final float pX, final float pY, final ITextureRegion pNormalTextureRegion, final ITextureRegion pSelectedTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pNormalTextureRegion, pSelectedTextureRegion, pVertexBufferObjectManager, (OnClickListener) null);
	}

	public SelectableButtonSprite(final float pX, final float pY, final ITextureRegion pNormalTextureRegion, final ITextureRegion pSelectedTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, final OnClickListener pOnClickListener) {
		this(pX, pY, new TiledTextureRegion(pNormalTextureRegion.getTexture(), pNormalTextureRegion, pSelectedTextureRegion), pVertexBufferObjectManager, pOnClickListener);
	}

	public SelectableButtonSprite(final float pX, final float pY, final ITextureRegion pNormalTextureRegion, final ITextureRegion pSelectedTextureRegion, final ITextureRegion pPressedTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pNormalTextureRegion, pPressedTextureRegion, pVertexBufferObjectManager, (OnClickListener) null);
	}

	public SelectableButtonSprite(final float pX, final float pY, final ITextureRegion pNormalTextureRegion, final ITextureRegion pSelectedTextureRegion, final ITextureRegion pPressedTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, final OnClickListener pOnClickListener) {
		this(pX, pY, new TiledTextureRegion(pNormalTextureRegion.getTexture(), pNormalTextureRegion, pPressedTextureRegion), pVertexBufferObjectManager, pOnClickListener);
	}

	public SelectableButtonSprite(final float pX, final float pY, final ITextureRegion pNormalTextureRegion, final ITextureRegion pSelectedTextureRegion, final ITextureRegion pPressedTextureRegion, final ITextureRegion pDisabledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pNormalTextureRegion, pPressedTextureRegion, pDisabledTextureRegion, pVertexBufferObjectManager, (OnClickListener) null);
	}

	public SelectableButtonSprite(final float pX, final float pY, final ITextureRegion pNormalTextureRegion, final ITextureRegion pSelectedTextureRegion, final ITextureRegion pPressedTextureRegion, final ITextureRegion pDisabledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, final OnClickListener pOnClickListener) {
		this(pX, pY, new TiledTextureRegion(pNormalTextureRegion.getTexture(), pNormalTextureRegion, pPressedTextureRegion, pDisabledTextureRegion), pVertexBufferObjectManager, pOnClickListener);
	}

	public SelectableButtonSprite(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager, (OnClickListener) null);
	}

	public SelectableButtonSprite(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, final OnClickListener pOnClickListener) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);

		this.mOnClickListener = pOnClickListener;
		this.mStateCount = pTiledTextureRegion.getTileCount();

		switch(this.mStateCount) {
			case 1:
				throw new IllegalArgumentException("You must provide a 'selected' textureRegion, otherwise just use the ButtonSprite class.");
			case 2:
				Debug.w("No " + ITextureRegion.class.getSimpleName() + " supplied for " + State.class.getSimpleName() + "." + State.PRESSED + ".");
				break;
			case 3:
				Debug.w("No " + ITextureRegion.class.getSimpleName() + " supplied for " + State.class.getSimpleName() + "." + State.DISABLED + ".");
				break;
			case 4:
				break;
			default:
				throw new IllegalArgumentException("The supplied " + ITiledTextureRegion.class.getSimpleName() + " has an unexpected amount of states: '" + this.mStateCount + "'.");
		}
		
		this.changeState(State.NORMAL);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean isEnabled() {
		return this.mEnabled;
	}

	public void setEnabled(final boolean pEnabled) {
		this.mEnabled = pEnabled;

		if(this.mEnabled && this.mState == State.DISABLED) {
			this.changeState((this.mSelected) ? State.SELECTED : State.NORMAL);
		} else if(!this.mEnabled) {
			this.changeState(State.DISABLED);
		}
	}
	
	public boolean isSelected() {
		return this.mSelected;
	}
	
	public void setSelected(final boolean pSelected) {
		this.mSelected = pSelected;

		if(this.mEnabled) {
			this.changeState((pSelected) ? State.SELECTED : State.NORMAL);
		}
	}
	
	public boolean isPressed() {
		return this.mState == State.PRESSED;
	}

	public State getState() {
		return this.mState;
	}
	
	public void setOnClickListener(final OnClickListener pOnClickListener) {
		this.mOnClickListener = pOnClickListener;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(!this.isEnabled()) {
			this.changeState(State.DISABLED);
		} else if(pSceneTouchEvent.isActionDown()) {
			this.changeState(State.PRESSED);
		} else if(pSceneTouchEvent.isActionCancel() || !this.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
			this.changeState(State.NORMAL);
		} else if(pSceneTouchEvent.isActionUp() && this.mState == State.PRESSED) {
			
			if (this.mSelected) {
				this.changeState(State.NORMAL);
			} else {
				this.changeState(State.SELECTED);
			}
			
			this.mSelected = !this.mSelected;
			
			if(this.mOnClickListener != null) {
				this.mOnClickListener.onClick(this, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		}
		
		return this.isVisible();
	}

	@Override
	public boolean contains(final float pX, final float pY) {
		if(!this.isVisible()) {
			return false;
		} else {
			return super.contains(pX, pY);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void changeState(final State pState) {
		if(pState == this.mState) {
			return;
		}

		this.mState = pState;

		final int stateTiledTextureRegionIndex = this.mState.getTiledTextureRegionIndex();
		if(stateTiledTextureRegionIndex >= this.mStateCount) {
			this.setCurrentTileIndex(0);
			Debug.w(this.getClass().getSimpleName() + " changed its " + State.class.getSimpleName() + " to " + pState.toString() + ", which doesn't have a " + ITextureRegion.class.getSimpleName() + " supplied. Applying default " + ITextureRegion.class.getSimpleName() + ".");
		} else {
			this.setCurrentTileIndex(stateTiledTextureRegionIndex);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public interface OnClickListener {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

		public void onClick(final SelectableButtonSprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY);
	}

	public static enum State {
		// ===========================================================
		// Elements
		// ===========================================================

		NORMAL(0),
		SELECTED(1),
		PRESSED(2),
		DISABLED(3);

		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Fields
		// ===========================================================

		private final int mTiledTextureRegionIndex;

		// ===========================================================
		// Constructors
		// ===========================================================

		private State(final int pTiledTextureRegionIndex) {
			this.mTiledTextureRegionIndex = pTiledTextureRegionIndex;
		}

		// ===========================================================
		// Getter & Setter
		// ===========================================================

		public int getTiledTextureRegionIndex() {
			return this.mTiledTextureRegionIndex;
		}

		// ===========================================================
		// Methods for/from SuperClass/Interfaces
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================
	}
}
