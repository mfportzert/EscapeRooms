package com.mfp.rooms.inventory;

import java.util.ArrayList;

import com.mfp.rooms.gui.LevelGui;

public class Inventory {
    
	// ===========================================================
	// Enums
	// ===========================================================
	
	public static enum ItemType {
		
		KEYS_003(LevelGui.KEYS_GROUND_ICON_ID), 
		TOWEL_004(LevelGui.TOWEL_ICON_ID),
		SWORD_006(LevelGui.SWORD_ICON_ID),
		SHIELD_006(LevelGui.SHIELD_ICON_ID),
		TORCH_007(LevelGui.TOWEL_ICON_ID),
		KEY_007(LevelGui.TOWEL_ICON_ID);
		
		private int mRegionId;
		
		private ItemType(int regionId) {
			this.mRegionId = regionId;
		}
		
		public int getRegionId() {
			return this.mRegionId;
		}
	}
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	private int mSelectedIndex = -1;
	private ArrayList<Item> mItems = new ArrayList<Item>();
	private OnItemSelectedListener mOnItemSelectedListener;
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	public int getSelectedItemIndex() {
		return this.mSelectedIndex;
	}
	
	public Item getSelectedItem() {
		
		if (this.mSelectedIndex >= 0 && this.mSelectedIndex < this.mItems.size()) {
			return this.mItems.get(this.mSelectedIndex);
		}
		return null;
	}
	
	public ArrayList<Item> getItems() {
		return this.mItems;
	}
	
	public void remove(Item item) {
		
		if (this.mSelectedIndex == this.mItems.indexOf(item)) {
			this.mSelectedIndex = -1;
		}
		this.mItems.remove(item);
	}
	
	public void add(Item item) {
		if (!this.mItems.contains(item)) {
			this.mItems.add(item);
		}
	}
	
	public void select(int index, boolean selected) {
		
		if (selected) {
			this.mSelectedIndex = index;
			if (this.mOnItemSelectedListener != null) {
				this.mOnItemSelectedListener.onItemSelected(this.getSelectedItem());
			}
		} else {
			if (this.mOnItemSelectedListener != null) {
				this.mOnItemSelectedListener.onItemUnselected(this.getSelectedItem());
			}
			this.mSelectedIndex = -1;
		}
	}
	
	public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
		this.mOnItemSelectedListener = onItemSelectedListener;
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public interface OnItemSelectedListener {
		
		public void onItemSelected(Item item);
		public void onItemUnselected(Item item);
	}
}
