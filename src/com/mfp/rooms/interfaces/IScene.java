package com.mfp.rooms.interfaces;

import java.io.IOException;

import org.andengine.engine.Engine;

import android.content.Context;

import com.mfp.rooms.gui.LevelGui;
import com.mfp.rooms.scenes.base.BaseLevel;

public interface IScene {
	
	public void createResources(Engine pEngine, Context pContext) throws IOException;
	public void createScene(Engine pEngine, Context pContext);
}
