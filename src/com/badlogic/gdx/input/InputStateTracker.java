package com.badlogic.gdx.input;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ReflectionPool;

class InputStateTracker implements Runnable {

	private final InputRecorder recorder;
	private final Thread processor;
	private List<InputState> bufferStates;
	private List<InputState> processStates;
	private final Pool<InputState> statePool;

	private static final int STATES_UNTIL_PROCESS = 20;

	public InputStateTracker(InputRecorder inputRecorder) {
		this.recorder = inputRecorder;
		bufferStates = new ArrayList<InputState>();
		processStates = new ArrayList<InputState>();

		statePool = new ReflectionPool<InputState>(InputState.class, 50);
		processor = new Thread(this);
		processor.setDaemon(true);
		processor.start();
	}

	public synchronized void track() {
		synchronized (bufferStates) {
			InputState state = statePool.obtain();
			state.set(Gdx.input);
			bufferStates.add(state);
			if (bufferStates.size() >= STATES_UNTIL_PROCESS) {
				notify();
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
