package com.badlogic.gdx.automation.recorder;

/**
 * Dummy implementation of {@link PlaybackListener} to be extended by listeners
 * that do not care for most of the events and only need to overwrite/implement
 * the callback for a few special events.
 * 
 * @author Lukas Böhm
 * 
 */
public class PlaybackAdapter implements PlaybackListener {

	@Override
	public void onStart() {
	}

	@Override
	public void onSyncPropertiesFinish() {
	}

	@Override
	public void onStop() {
	}
}
