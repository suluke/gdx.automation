package com.badlogic.gdx.input;

import com.badlogic.gdx.input.InputValue.SyncValue.Types;

/**
 * A class to be fed InputStates via {@link #process(InputState)} so it can
 * process them.
 * 
 */
class InputStateProcessor {
	private final InputRecorder recorder;
	private InputState lastState = null;

	private final int trackedValuesFlag;

	public InputStateProcessor(InputRecorder recorder) {
		this.recorder = recorder;
		int flags = 0;
		InputRecorderConfiguration config = recorder.getConfiguration();
		if (config.recordButtons) {
			flags |= Types.BUTTONS.key;
		}
		if (config.recordOrientation) {
			flags |= Types.ORIENTATION.key;
		}
		if (config.recordKeyEvents) {
			flags |= Types.KEY_EVENTS.key;
		}
		if (config.recordKeysPressed) {
			flags |= Types.KEYS_PRESSED.key;
		}
		if (config.recordPointers) {
			flags |= Types.POINTERS.key;
		}
		if (config.recordTouchEvents) {
			flags |= Types.TOUCH_EVENTS.key;
		}
		trackedValuesFlag = flags;
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
