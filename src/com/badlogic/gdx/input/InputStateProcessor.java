package com.badlogic.gdx.input;

/**
 * A class to be fed InputStates via {@link #process(InputState)} so it can
 * process them.
 * 
 */
class InputStateProcessor {
	private final InputRecorder recorder;
	private InputState lastState = null;

	public InputStateProcessor(InputRecorder recorder) {
		this.recorder = recorder;
	}

	/**
	 * Compares the last state given to the processor with the given one and
	 * writes the differences using the {@link InputRecorder}'s
	 * {@link InputRecordWriter}
	 * 
	 * @param state
	 */
	public void process(InputState state) {

	}

	private void processPointers(InputState state) {

	}

	private void processButtons(InputState state) {

	}

	public void reset() {
		lastState = null;
	}
}
