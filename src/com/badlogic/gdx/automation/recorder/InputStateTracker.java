package com.badlogic.gdx.automation.recorder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.automation.recorder.io.InputRecordWriter;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

/**
 * Submodule of {@link InputRecorder} responsible for recording
 * {@link InputValue.SyncValue}s
 * 
 * @author Lukas Böhm
 * 
 */
class InputStateTracker {

	private final InputRecorder recorder;
	private final Processor processor;

	/**
	 * Two arraylists: One to be filled by the grabber and one to be emptied by
	 * the processor. Then a rapid switch between both can be performed.
	 */
	private List<InputState> storedStates;
	private List<InputState> processedStates;
	private final Pool<InputState> statePool;

	private final Tracker tracker;

	private final InputEventGrabber grabber;
	private final GrabberArmer grabberArmer;
	private final GrabberKeeper grabberKeeper;
	/**
	 * the flags indicating all values to be tracked on the
	 * {@link Application#postRunnable(Runnable) main thread}, right before most
	 * concrete back end {@link Input} implementations call processEvents()
	 */
	private final int beforePrcessEventsTrackFlags;
	/**
	 * the flags indicating all values to be tracked right at the moment when
	 * most concrete back end {@link Input} implementations call
	 * processEvents(), thus being tracked at the perfect moment when the event
	 * buffers are filled the best
	 */
	private final int onProcessEventsTrackFlags;

	private static final int STATES_UNTIL_PROCESS = 20;

	private boolean tracking = false;
	InputState currentState;

	public InputStateTracker(InputRecorder inputRecorder) {
		this.recorder = inputRecorder;

		int toSet = 0;
		if (recorder.getConfiguration().recordButtons) {
			toSet |= InputValue.SyncValue.Type.BUTTONS.key;
		}
		if (recorder.getConfiguration().recordOrientation) {
			toSet |= InputValue.SyncValue.Type.ORIENTATION.key;
		}
		if (recorder.getConfiguration().recordKeysPressed) {
			toSet |= InputValue.SyncValue.Type.KEYS_PRESSED.key;
		}
		if (recorder.getConfiguration().recordPointers) {
			toSet |= InputValue.SyncValue.Type.POINTERS.key;
		}
		beforePrcessEventsTrackFlags = toSet;

		toSet = 0;
		if (recorder.getConfiguration().recordKeyEvents) {
			toSet |= InputValue.SyncValue.Type.KEY_EVENTS.key;
		}
		if (recorder.getConfiguration().recordPointerEvents) {
			toSet |= InputValue.SyncValue.Type.POINTER_EVENTS.key;
		}
		onProcessEventsTrackFlags = toSet;

		storedStates = new ArrayList<InputState>();
		processedStates = new ArrayList<InputState>();

		statePool = new ReflectionPool<InputState>(InputState.class, 50);
		processor = new Processor();

		tracker = new Tracker();

		grabber = new InputEventGrabber();
		grabberArmer = new GrabberArmer();
		grabberKeeper = new GrabberKeeper();
	}

	public synchronized void startTracking() {
		if (isTracking()) {
			Gdx.app.log(InputRecorder.LOG_TAG,
					"Starting InputStateTracker more than once");
			return;
		}
		tracking = true;
		tracker.start();
		processor.start();
		synchronized (Gdx.input) {
			grabberKeeper.setProxiedInput(Gdx.input);
			Gdx.input = grabberKeeper;
		}
		grabberArmer.start();
	}

	public synchronized void stopTracking() {
		if (!isTracking()) {
			Gdx.app.log(InputRecorder.LOG_TAG,
					"Stopping InputStateTracker more than once");
			return;
		}
		processor.stop();
		grabberArmer.stop();
		tracker.stop();
		InputProxy.removeProxyFromGdx(grabberKeeper);
		InputProcessorProxy.removeProxyFromGdxInput(grabber);
		tracking = false;
	}

	public synchronized boolean isTracking() {
		return tracking;
	}

	private void track() {
		synchronized (processor) {
			synchronized (storedStates) {
				currentState = statePool.obtain();
				currentState
						.initialize(recorder.getConfiguration().recordedPointerCount);

				currentState
						.set(Gdx.input, beforePrcessEventsTrackFlags, false);
				storedStates.add(currentState);
				if (storedStates.size() >= STATES_UNTIL_PROCESS) {
					processor.notify();
				}
			}
		}
	}

	/**
	 * An input that will not allow the InputEventGrabber {@link InputProcessor}
	 * to be overwritten via setInputProcessor
	 * 
	 * @author Lukas Böhm
	 */
	private class GrabberKeeper extends InputProxy {
		@Override
		public void setProxiedInput(Input proxied) {
			super.setProxiedInput(proxied);
			if (proxied.getInputProcessor() != grabber) {
				grabber.setProxied(proxied.getInputProcessor());
				proxied.setInputProcessor(grabber);
			}
		}

		@Override
		public void setInputProcessor(InputProcessor processor) {
			grabber.setProxied(processor);
		}
	}

	/**
	 * Pretends to be an InputProcessor to have one of its method called in
	 * processEvents, the place right after the input event buffers are filled
	 * and right before they are cleared.
	 * 
	 * @author Lukas Böhm
	 */
	private class InputEventGrabber extends InputProcessorProxy {
		private boolean armed = false;

		public void rearm() {
			armed = true;
		}

		@Override
		protected synchronized void onEvent() {
			if (armed) {
				armed = false;
				currentState.set(Gdx.input, onProcessEventsTrackFlags, false);
			}
		}
	}

	/**
	 * A runnable on the main thread to make the InputEventGrabber listen to
	 * events (arm it) just before the first event of the current main loop
	 * cycle is being processed
	 * 
	 * @author Lukas Böhm
	 */
	private class GrabberArmer implements Runnable {
		private boolean running;

		public void start() {
			running = true;
			run();
		}

		public void stop() {
			running = false;
		}

		@Override
		public void run() {
			grabber.rearm();
			if (running) {
				Gdx.app.postRunnable(this);
			}
		}
	}

	/**
	 * A worker thread to process and write input state changes back without
	 * blocking the main loop
	 * 
	 * @author Lukas Böhm
	 */
	private class Processor implements Runnable {
		private Thread processorThread;
		private final InputStateProcessor processor;

		public Processor() {
			processor = new InputStateProcessor(recorder);
		}

		public void start() {
			if (processorThread != null) {
				if (processorThread.isAlive()) {
					return;
				}
				synchronized (processorThread) {
					processorThread = new Thread(this);
					processorThread.setDaemon(true);
					processorThread.start();
				}
			} else {
				processorThread = new Thread(this);
				processorThread.setDaemon(true);
				processorThread.start();
			}
		}

		public void stop() {
			if (processorThread != null && processorThread.isAlive()) {
				synchronized (processorThread) {
					processorThread.interrupt();
					try {
						processorThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * Processes all the InputStates that were collected for some main loop
		 * cycle in one run. That is, creating diffs and writing them using the
		 * {@link InputRecorder}'s {@link InputRecordWriter}
		 */
		private void process() {
			List<InputState> swap = storedStates;
			synchronized (storedStates) {
				storedStates = processedStates;
			}
			processedStates = swap;
			for (InputState state : processedStates) {
				try {
					processor.process(state);
				} catch (IOException e) {
					recorder.notifyError(e);
				}
				statePool.free(state);
			}
			processedStates.clear();
		}

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				// need to finalize what is still in the queue
				process();
			}
		}
	}

	/**
	 * Runnable to be posted on the main loop's thread to repeatedly call
	 * {@link InputStateTracker#track() track()}
	 * 
	 * @author Lukas Böhm
	 */
	private class Tracker implements Runnable {
		private boolean running = false;

		public synchronized void start() {
			boolean wasRunning = running;
			running = true;
			if (!wasRunning) {
				Gdx.app.postRunnable(this);
			}
		}

		public synchronized void stop() {
			running = false;
		}

		@Override
		public void run() {
			track();
			if (running) {
				Gdx.app.postRunnable(this);
			}
		}

	}
}
