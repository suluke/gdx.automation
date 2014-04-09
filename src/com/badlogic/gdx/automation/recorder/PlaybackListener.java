package com.badlogic.gdx.automation.recorder;

import com.badlogic.gdx.automation.recorder.io.InputRecordReader;

/**
 * Listener interface for providing callbacks to an {@link InputPlayer} for
 * getting notified on various occasions during a playback
 * 
 * @author Lukas Böhm
 * 
 */
public interface PlaybackListener {
	void onStart();

	/**
	 * Called when the player reads <code>null</code> from the
	 * {@link InputRecordReader}, indicating that there are no synchronous
	 * events to be played back left.
	 */
	void onSynchronousFinish();

	void onStop();
}
