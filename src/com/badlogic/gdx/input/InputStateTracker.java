package com.badlogic.gdx.input;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

class InputStateTracker {

	private final InputRecorder recorder;
	private final Processor processor;
	private Thread processorThread;
	private List<InputState> bufferStates;
	private List<InputState> processStates;
	private final Pool<InputState> statePool;

	private final InputStateGrabber grabber;
	private final GrabberArmer grabberArmer;

	private boolean running = false;

	private static final int STATES_UNTIL_PROCESS = 20;

	public void startTracking() {
		running = true;
		if (processorThread.isAlive()) {
			processorThread.interrupt();
		}
		processorThread = new Thread(processor);
		processorThread.setDaemon(true);
		processorThread.start();
	}

	public void stopTracking() {
		running = false;
	}

	public boolean isTracking() {
		return running;
	}

	public InputStateTracker(InputRecorder inputRecorder) {
		this.recorder = inputRecorder;
		bufferStates = new ArrayList<InputState>();
		processStates = new ArrayList<InputState>();

		statePool = new ReflectionPool<InputState>(InputState.class, 50);
		processor = new Processor();

		grabber = new InputStateGrabber();
		grabberArmer = new GrabberArmer();
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

	private class InputStateGrabber extends InputProcessorProxy {

		private boolean armed = false;

		public void arm() {
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
		@Override
		public void run() {
			grabber.arm();
			if (running) {
				Gdx.app.postRunnable(this);
			}
		}
	}

	private class Processor implements Runnable {
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
