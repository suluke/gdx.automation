package com.badlogic.gdx.input.recorder;

import com.badlogic.gdx.Gdx;

public class InputRecordPlayer {
	private final PlaybackInput playback;

	public InputRecordPlayer(InputRecordReader reader) {
		playback = new PlaybackInput(reader.getTextIterator(),
				reader.getPlaceholderTextIterator());
	}

	public void startPlayback() {
		playback.setProxiedInput(Gdx.input);
		Gdx.input = playback;
	}

	public void setPlaybackPaused(boolean paused) {
		playback.setPaused(paused);
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
	}
}
