package com.badlogic.gdx.automation.recorder;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.automation.recorder.formats.InputRecordReader;

/**
 * Standard implementation of a player playing back recorded or generated input.
 * 
 */
public class InputRecordPlayer {
	private final PlaybackInput playback;
	private final List<PlaybackListener> listeners;

	public InputRecordPlayer(InputRecordReader reader) {
		playback = new PlaybackInput(reader.getTextIterator(),
				reader.getPlaceholderTextIterator());
		listeners = new ArrayList<PlaybackListener>();
	}

	public void startPlayback() {
		playback.setProxiedInput(Gdx.input);
		Gdx.input = playback;
		notifyStart();
	}

	public void setPlaybackPaused(boolean paused) {
		playback.setPaused(paused);
		notifyPaused();
	}

	/**
	 * Delays the playback of the recorded input by the given amount of time (in
	 * seconds).
	 * 
	 * @param seconds
	 */
	public void playbackTimeout(float seconds) {
	}

	public void stopPlayback() {
		InputProxy.removeProxyFromGdx(playback);
		notifyStopped();
	}

	public void addPlaybackListener(PlaybackListener listener) {
		listeners.add(listener);
	}

	public void removePlaybackListener(PlaybackListener listener) {
		listeners.remove(listener);
	}

	public void clearPlaybackListeners() {
		listeners.clear();
	}

	private void notifyPaused() {
		for (PlaybackListener listener : listeners) {
			listener.onPause();
		}
	}

	private void notifyStopped() {
		for (PlaybackListener listener : listeners) {
			listener.onStop();
		}
	}

	private void notifyStart() {
		for (PlaybackListener listener : listeners) {
			listener.onStart();
		}
	}

	private void notifyFinished() {
		for (PlaybackListener listener : listeners) {
			listener.onFinish();
		}
	}
}
