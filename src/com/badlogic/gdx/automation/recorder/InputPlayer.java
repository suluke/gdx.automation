package com.badlogic.gdx.automation.recorder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.Accelerometer;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.Button;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.KeyEvent;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.KeyPressed;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.Orientation;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.Pointer;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.PointerEvent;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncPropertyVisitor;
import com.badlogic.gdx.automation.recorder.io.InputRecordReader;

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
	private final RecordProperties properties;
	/**
	 * the original input provided by the libGdx back end in use
	 */
	private final Iterator<SyncProperty> syncIterator;

	public static final String LOG_TAG = "InputPlayer";

	public InputPlayer(InputRecordReader reader) {
		properties = reader.getRecordProperties();
		playback = new PlaybackInput(reader.getTextIterator(),
				reader.getPlaceholderTextIterator(), reader.getStaticValues());
		listeners = new ArrayList<PlaybackListener>();
		syncIterator = reader.getSyncValueIterator();

		mainThread = new MainThreadRunnable();
		readerThread = new ReaderThreadRunnable();
	}

	public void startPlayback() {
		synchronized (Gdx.input) {
			Input gdxInput = Gdx.input;
			playback.setProxiedInput(Gdx.input);
			Gdx.input = playback;
			playback.setInputProcessor(gdxInput.getInputProcessor());
			gdxInput.setInputProcessor(null);
		}
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
		readerThread.delay((int) (seconds * 1000));
	}

	public void stopPlayback() {
		mainThread.stop();
		readerThread.stop();
		if (!InputProxy.removeProxyFromGdx(playback)) {
			Gdx.app.log(LOG_TAG, "Could not remove player from Gdx.input");
		}
		Gdx.input.setInputProcessor(playback.getInputProcessor());
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
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				for (PlaybackListener listener : listeners) {
					listener.onSyncPropertiesFinish();
				}
			}
		});
	}

	/**
	 * Hooks into the application's main thread/loop to notify the current
	 * {@link InputProcessor} about key and touch events as any other backend
	 * would do.
	 * 
	 */
	private class MainThreadRunnable implements Runnable {
		private boolean interrupted = true;
		private boolean stopped = true;

		public synchronized void start() {
			if (interrupted) {
				// busy waiting
				while (!stopped)
					continue;
				interrupted = false;
				Gdx.app.postRunnable(this);
				stopped = false;
			}
		}

		public synchronized void stop() {
			interrupted = true;
		}

		@Override
		public void run() {
			if (!interrupted) {
				playback.processEvents();
				Gdx.app.postRunnable(this);
			} else {
				stopped = true;
			}
		}
	}

	/**
	 * A separate thread to apply {@link InputProperty} changes read from the
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
		private int delayMs = 0;

		public synchronized void start() {
			if (thread != null && thread.isAlive()) {
				if (thread.isInterrupted()) {
					try {
						thread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					return;
				}
			}
			thread = new Thread(this);
			thread.setDaemon(true);
			thread.start();
		}

		public void delay(int ms) {
			// TODO care about ms < 0?
			synchronized (thread) {
				delayMs += ms;
			}
		}

		public synchronized void stop() {
			if (thread != null) {
				thread.interrupt();
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void run() {
			if (properties.absouluteCoords) {
				denormalizedProcessing();
			} else {
				normalizedProcessing();
			}
		}

		private void denormalizedProcessing() {
			int sleep;
			SyncProperty currentVal;
			InputState state = playback.getState();
			while (!Thread.currentThread().isInterrupted()) {
				sleep = 0;
				do {
					if (delayMs != 0) {
						break;
					}
					if (!syncIterator.hasNext()) {
						notifyFinished();
						return;
					}
					currentVal = syncIterator.next();
					sleep += currentVal.timeDelta;

					synchronized (state) {
						state.apply(currentVal);
					}
				} while (sleep <= MIN_SLEEP);

				if (delayMs != 0) {
					synchronized (thread) {
						sleep += delayMs;
						delayMs = 0;
					}
				}

				try {
					Thread.sleep(sleep);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}

		private void normalizedProcessing() {
			int sleep;
			SyncProperty currentVal;
			InputState state = playback.getState();
			while (!Thread.currentThread().isInterrupted()) {
				sleep = 0;
				do {
					if (delayMs != 0) {
						break;
					}
					if (!syncIterator.hasNext()) {
						notifyFinished();
						return;
					}
					currentVal = syncIterator.next();
					PropertyDenormalizer.denormalize(currentVal);
					sleep += currentVal.timeDelta;

					synchronized (state) {
						state.apply(currentVal);
					}
				} while (sleep <= MIN_SLEEP);

				if (delayMs != 0) {
					synchronized (thread) {
						sleep += delayMs;
						delayMs = 0;
					}
				}

				try {
					Thread.sleep(sleep);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
	}

	/**
	 * A class providing static functionality to denormalize screen coordinates
	 * and deltas that are part of {@link SyncProperty SyncProperties}. This
	 * mainly affects {@link Pointer} and {@link PointerEvent};
	 * 
	 * @author Lukas Böhm
	 * 
	 */
	public static class PropertyDenormalizer implements SyncPropertyVisitor {

		private static PropertyDenormalizer denormalizer;;

		private PropertyDenormalizer() {
		}

		public static void denormalize(SyncProperty property) {
			if (denormalizer == null) {
				denormalizer = new PropertyDenormalizer();
			}
			property.accept(denormalizer);
		}

		@Override
		public void visitAccelerometer(Accelerometer accelerometer) {
		}

		@Override
		public void visitKeyPressed(KeyPressed keyPressed) {
		}

		@Override
		public void visitPointerEvent(PointerEvent pointerEvent) {
			int width = Gdx.graphics.getWidth();
			int height = Gdx.graphics.getHeight();
			pointerEvent.x *= width;
			pointerEvent.y *= height;
		}

		@Override
		public void visitKeyEvent(KeyEvent keyEvent) {
		}

		@Override
		public void visitOrientation(Orientation orientation) {
		}

		@Override
		public void visitPointer(Pointer pointer) {
			int width = Gdx.graphics.getWidth();
			int height = Gdx.graphics.getHeight();
			pointer.x *= width;
			pointer.deltaX *= width;
			pointer.y *= height;
			pointer.deltaY *= height;
		}

		@Override
		public void visitButton(Button button) {
		}

	}
}
