package com.mfp.rooms.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.andengine.engine.Engine;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.mfp.rooms.activities.HomeActivity;
import com.mfp.rooms.activities.LevelActivity;
import com.mfp.rooms.camera.CameraManager;

public class LevelUtils {
	
	public static String toLevelNbFormat(int levelNb) {
		
		NumberFormat numberFormat = new DecimalFormat("000");
        return numberFormat.format(levelNb);
	}
	
	public static Entity scaleToScreen(Entity entity, float entityWidth, float entityHeight) {
		
		float screenRatio = CameraManager.CAMERA_WIDTH / CameraManager.CAMERA_HEIGHT;
		float entityRatio = entityWidth / entityHeight;
		
		if (entityRatio < screenRatio) {
			entity.setScale(CameraManager.CAMERA_WIDTH / entityWidth, 
					CameraManager.CAMERA_HEIGHT / entityHeight);
		} else {
			// We let black borders on top and on bottom...
			entity.setScale(CameraManager.CAMERA_WIDTH / entityWidth);
		}
		
		return entity;
	}
	
	public static Sprite scaleToScreen(Sprite sprite) {
		return (Sprite) LevelUtils.scaleToScreen(sprite, sprite.getWidth(), sprite.getHeight());
	}
	
	public static Rectangle scaleToScreen(Rectangle rectangle) {
		return (Rectangle) LevelUtils.scaleToScreen(rectangle, rectangle.getWidth(), rectangle.getHeight());
	}
	
	public static Scene scaleToScreen(Scene scene, float sceneWidth, float sceneHeight) {
		return (Scene) LevelUtils.scaleToScreen((Entity) scene, sceneWidth, sceneHeight);
	}
	
	public static void applyBrightness() {
		
	}
}
