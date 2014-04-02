package com.badlogic.gdx.automation.recorder;

public interface PlaybackListener {
	void onStart();

	void onFinish();

	void onStop();

	void onPause();
}
