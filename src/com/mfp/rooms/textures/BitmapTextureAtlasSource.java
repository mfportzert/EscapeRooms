package com.mfp.rooms.textures;

import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.source.BaseTextureAtlasSource;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class BitmapTextureAtlasSource extends BaseTextureAtlasSource implements IBitmapTextureAtlasSource {
	
    private Bitmap mBitmap;
 
    public BitmapTextureAtlasSource(Bitmap pBitmap) {
        super(0, 0, pBitmap.getWidth(), pBitmap.getHeight());
        this.mBitmap = pBitmap.copy(Bitmap.Config.ARGB_8888, false);
    }
 
    public int getWidth() {
        return mBitmap.getWidth();
    }
 
    public int getHeight() {
        return mBitmap.getHeight();
    }
 
    @Override
    public BitmapTextureAtlasSource clone() {
        return new BitmapTextureAtlasSource(Bitmap.createBitmap(mBitmap));
    }
 
    @Override
    public Bitmap onLoadBitmap(Config pBitmapConfig) {
    	return mBitmap;
    }

	@Override
	public IBitmapTextureAtlasSource deepCopy() {
		// TODO Auto-generated method stub
		return null;
	}
}
