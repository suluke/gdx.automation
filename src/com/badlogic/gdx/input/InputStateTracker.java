package com.badlogic.gdx.input;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

class InputStateTracker {

	private final InputRecorder recorder;
	private final Processor processor;

	private List<InputState> bufferStates;
	private List<InputState> processStates;
	private final Pool<InputState> statePool;

	private final InputStateGrabber grabber;
	private final GrabberArmer grabberArmer;
	private final GrabberKeeper grabberKeeper;

	private static final int STATES_UNTIL_PROCESS = 20;

	private boolean tracking = false;

	public InputStateTracker(InputRecorder inputRecorder) {
		this.recorder = inputRecorder;
		bufferStates = new ArrayList<InputState>();
		processStates = new ArrayList<InputState>();

		statePool = new ReflectionPool<InputState>(InputState.class, 50);
		processor = new Processor();

		grabber = new InputStateGrabber();
		grabberArmer = new GrabberArmer();
		grabberKeeper = new GrabberKeeper();
	}

	public synchronized void startTracking() {
		tracking = true;
		stopTracking();

		processor.start();
		grabberKeeper.setProxiedInput(Gdx.input);
		Gdx.input = grabberKeeper;
		grabberArmer.start();
	}

	public synchronized void stopTracking() {
		processor.stop();
		grabberArmer.stop();
		// TODO find way to remove grabberKeeper from within InputProxy
		// hierarchy
		if (Gdx.input == grabberKeeper) {
			Gdx.input = grabberKeeper.getProxiedInput();
		} else {
			Gdx.app.log(InputRecorder.LOG_TAG,
					"Cannot unregister GrabberKeeper");
		}
		// InputStateGrabber can be left as current InputProcessor since it
		// won't be rearmed
		tracking = false;
	}

	public synchronized boolean isTracking() {
		return tracking;
	}

	private void track() {
		synchronized (processor) {
			synchronized (bufferStates) {
				InputState state = statePool.obtain();
				state.set(Gdx.input);
				bufferStates.add(state);
				if (bufferStates.size() >= STATES_UNTIL_PROCESS) {
					processor.notify();
				}
			}
		}
	}

	private void process() {
		List<InputState> swap = bufferStates;
		synchronized (bufferStates) {
			bufferStates = processStates;
		}
		processStates = swap;
		System.out.println("Now processing " + processStates.size()
				+ " InputStates");
		for (InputState state : processStates) {
			// TODO
			statePool.free(state);
		}
		processStates.clear();
	}

	private class GrabberKeeper extends InputProxy {
		@Override
		public void setProxiedInput(Input proxied) {
			super.setProxiedInput(proxied);
			grabber.setProxied(proxied.getInputProcessor());
			proxied.setInputProcessor(grabber);
		}

		@Override
		public void setInputProcessor(InputProcessor processor) {
			grabber.setProxied(processor);
		}
	}

	private class InputStateGrabber extends InputProcessorProxy {
		private boolean armed = false;

		public void rearm() {
			armed = true;
		}

		@Override
		protected synchronized void onEvent() {
			if (armed) {
				armed = false;
				track();
			}
		}
	}

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

	private class Processor implements Runnable {
		private Thread processorThread;

		public synchronized void start() {
			stop();
			processorThread = new Thread(this);
			processorThread.setDaemon(true);
			processorThread.start();
		}

		public synchronized void stop() {
			if (processorThread != null && processorThread.isAlive()) {
				processorThread.interrupt();
			}
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
}
