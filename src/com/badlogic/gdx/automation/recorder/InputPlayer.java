package com.badlogic.gdx.automation.recorder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue;
import com.badlogic.gdx.automation.recorder.formats.InputRecordReader;

/**
 * Standard implementation of a player playing back recorded or generated input.
 * 
 * @author Lukas Böhm
 */
public class InputPlayer {
	private final PlaybackInput playback;
	private final List<PlaybackListener> listeners;
	private final MainThreadRunnable mainThread;
	private final ReaderThreadRunnable readerThread;
	/**
	 * the original input provided by the libGdx back end in use
	 */
	private final Input gdxInput;
	private final Iterator<SyncValue> syncIterator;

	public InputPlayer(InputRecordReader reader) {
		playback = new PlaybackInput(reader.getTextIterator(),
				reader.getPlaceholderTextIterator());
		listeners = new ArrayList<PlaybackListener>();
		gdxInput = Gdx.input;
		syncIterator = reader.getSyncValueIterator();

		mainThread = new MainThreadRunnable();
		readerThread = new ReaderThreadRunnable();
	}

	public void startPlayback() {
		playback.setProxiedInput(gdxInput);
		Gdx.input = playback;
		readerThread.start();
		mainThread.start();
		notifyStart();
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
		mainThread.stop();
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
			listener.onSynchronousFinish();
		}
	}

	/**
	 * Hooks into the application's main thread/loop to notify the current
	 * {@link InputProcessor} about key and touch events as any other backend
	 * would do.
	 * 
	 */
	private class MainThreadRunnable implements Runnable {
		private boolean paused = true;

		public synchronized void start() {
			if (paused) {
				paused = false;
				Gdx.app.postRunnable(this);
			}
		}

		public synchronized void stop() {
			paused = true;
		}

		@Override
		public void run() {
			if (!paused) {
				playback.processEvents();
				Gdx.app.postRunnable(this);
			}
		}
	}

	/**
	 * A separate thread to apply {@link InputValue} changes read from the
	 * {@link InputRecordReader} depending on time.
	 * 
	 * @author Lukas Böhm
	 * 
	 */
	private class ReaderThreadRunnable implements Runnable {
		/**
		 * Defines how long the reader thread will at least sleep after a
		 * sequence of events has been processed. It can also be interpreted as
		 * the number of milliseconds that can lie between two events so that
		 * those events are still considered as having happened at the same
		 * time.
		 */
		private static final int MIN_SLEEP = 10;

		private Thread thread = null;

		public synchronized void start() {
			if (thread == null || !thread.isAlive()) {
				thread = new Thread(this);
				thread.setDaemon(true);
				thread.start();
			}
		}

		public synchronized void stop() {
			if (thread != null) {
				thread.interrupt();
			}
		}

		@Override
		public void run() {
			int sleep;
			SyncValue currentVal;
			while (!Thread.currentThread().isInterrupted()) {
				sleep = 0;
				do {
					if (!syncIterator.hasNext()) {
						notifyFinished();
						return;
					}
					currentVal = syncIterator.next();
					sleep += currentVal.timeDelta;

					// TODO apply currentVal
				} while (sleep <= MIN_SLEEP);
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
	}
}
