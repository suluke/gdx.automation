package com.badlogic.demos.inputrecorder;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Game extends com.badlogic.gdx.Game {
	SpriteBatch batch;
	AssetManager assetManager;

	@Override
	final public void create() {
		batch = new SpriteBatch();
		assetManager = new AssetManager();
		StyleHelper.initialize(assetManager);
		onCreate();
	}

	public abstract void onCreate();

	public static void startDemo(Game demo) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false;
		config.title = "Simple Record&Playback Demo";
		config.useGL20 = true;
		config.width = 1024;
		config.height = 600;
		LwjglApplicationConfiguration.disableAudio = true;
		new LwjglApplication(demo, config);
	}
}
