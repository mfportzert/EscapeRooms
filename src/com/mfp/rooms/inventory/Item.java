package com.mfp.rooms.inventory;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.State;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.mfp.rooms.inventory.Inventory.ItemType;

public class Item extends ButtonSprite {

	private ItemType mItemType;
	
	public Item(float pX, float pY, ITextureRegion pNormalTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager,
			OnClickListener pOnClickListener, ItemType pItemType) {
		super(pX, pY, pNormalTextureRegion, pVertexBufferObjectManager,pOnClickListener);
		
		this.mItemType = pItemType;
	}
	
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		
		if (pSceneTouchEvent.isActionUp() && this.getState() == State.PRESSED) {
			/*
			this.changeState(State.NORMAL);

			if(this.mOnClickListener != null) {
				this.mOnClickListener.onClick(this, pTouchAreaLocalX, pTouchAreaLocalY);
			}*/
		}
		
		return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
	}
	
	public ItemType getType() {
		return this.mItemType;
	}
}
