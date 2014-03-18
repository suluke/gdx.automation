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
		if (config.recordKeyEvents) {
			flags |= Types.KEY_EVENTS.key;
		}
		if (config.recordKeysPressed) {
			flags |= Types.KEYS_PRESSED.key;
		}
		if (config.recordOrientation) {
			flags |= Types.ORIENTATION.key;
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
		if ((trackedValuesFlag & Types.BUTTONS.key) != 0) {
			processButtons(state);
		}
		if ((trackedValuesFlag & Types.KEY_EVENTS.key) != 0) {
			processKeyEvents(state);
		}
		if ((trackedValuesFlag & Types.KEYS_PRESSED.key) != 0) {
			processKeysPressed(state);
		}
		if ((trackedValuesFlag & Types.ORIENTATION.key) != 0) {
			processOrientation(state);
		}
		if ((trackedValuesFlag & Types.POINTERS.key) != 0) {
			processPointers(state);
		}
		if ((trackedValuesFlag & Types.TOUCH_EVENTS.key) != 0) {
			processTouchEvents(state);
		}
		if (lastState == null) {
			lastState = new InputState(
					recorder.getConfiguration().recordedPointerCount);
		}
		lastState.set(state, trackedValuesFlag);
	}

	private void processButtons(InputState state) {

	}

	private void processKeyEvents(InputState state) {

	}

	private void processKeysPressed(InputState state) {

	}

	private void processOrientation(InputState state) {

	}

	private void processPointers(InputState state) {

	}

	private void processTouchEvents(InputState state) {

	}

	public void reset() {
		lastState = null;
	}
}
