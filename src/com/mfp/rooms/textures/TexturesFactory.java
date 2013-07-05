/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfp.rooms.textures;

import org.andengine.engine.Engine;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackerTextureRegion;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;
import android.util.Log;

/**
 *
 * @author M-F.P
 */
public class TexturesFactory {

	public static final String SPRITESHEET_DIR = "gfx/spritesheets/";
	public static final String TEXTURES_DIR = SPRITESHEET_DIR+"textures/";
	
	private TexturePack mTexturePack;
	private TexturePackTextureRegionLibrary mTextureRegionLibrary;
	
	/**
	 * 
	 * @param pEngine
	 * @param pContext
	 * @param filename
	 */
	public void loadSpritesheets(Engine pEngine, Context pContext, String filename) {

		try {
			this.mTexturePack = new TexturePackLoader(
					pEngine.getTextureManager(), TEXTURES_DIR).loadFromAsset(
							pContext.getAssets(), filename);
			
			this.mTextureRegionLibrary = this.mTexturePack.getTexturePackTextureRegionLibrary();
			this.mTexturePack.getTexture().load();

		} catch (TexturePackParseException ex) {
			Log.e("LevelFactory", ex.getMessage(), ex);
			// TODO: Faire un message d erreur visible
		}
	}
	
	public TextureRegion getRegion(int id) {
		return this.mTextureRegionLibrary.get(id);
	}
	
	public TiledTextureRegion getTiled(int id, final int rows, final int columns) {

		TexturePackerTextureRegion packedTextureRegion = this.mTextureRegionLibrary.get(id);

		return TiledTextureRegion.create(
				packedTextureRegion.getTexture(), 
				(int) packedTextureRegion.getTextureX(), 
				(int) packedTextureRegion.getTextureY(), 
				(int) packedTextureRegion.getWidth(), 
				(int) packedTextureRegion.getHeight(), 
				columns, 
				rows,
				packedTextureRegion.isRotated());
	}
	
	public ITexture getTexture() {
		return this.mTexturePack.getTexture();
	}
}
