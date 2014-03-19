package com.badlogic.gdx.input.recorder;

import com.badlogic.gdx.input.recorder.EventBufferAccessHelper.KeyEvent;
import com.badlogic.gdx.input.recorder.EventBufferAccessHelper.PointerEvent;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.Accelerometer;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.Button;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.KeyPressed;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.Orientation;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.Pointer;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.Type;

/**
 * A class to be fed InputStates via {@link #process(InputState)} so it can
 * process them and write it using {@link InputRecorder#getRecordWriter()}
 * 
 */
class InputStateProcessor {
	private final InputRecorder recorder;
	private InputState lastState = null;
	private long timeDelta;

	private final int copiedValuesFlag;
	private final int trackedValuesFlag;

	public InputStateProcessor(InputRecorder recorder) {
		this.recorder = recorder;
		int flags = 0;
		InputRecorderConfiguration config = recorder.getConfiguration();
		if (config.recordButtons) {
			flags |= Type.BUTTONS.key;
		}
		if (config.recordOrientation) {
			flags |= Type.ORIENTATION.key;
		}
		if (config.recordPointers) {
			flags |= Type.POINTERS.key;
		}
		copiedValuesFlag = flags;

		if (config.recordKeyEvents) {
			flags |= Type.KEY_EVENTS.key;
		}
		if (config.recordKeysPressed) {
			flags |= Type.KEYS_PRESSED.key;
		}
		if (config.recordPointerEvents) {
			flags |= Type.POINTER_EVENTS.key;
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
		timeDelta = lastState == null ? 0 : state.timeStamp
				- lastState.timeStamp;
		if ((trackedValuesFlag & Type.BUTTONS.key) != 0) {
			processButtons(state);
		}
		if ((trackedValuesFlag & Type.KEY_EVENTS.key) != 0) {
			processKeyEvents(state);
		}
		if ((trackedValuesFlag & Type.KEYS_PRESSED.key) != 0) {
			processKeysPressed(state);
		}
		if ((trackedValuesFlag & Type.ORIENTATION.key) != 0) {
			processOrientation(state);
		}
		if ((trackedValuesFlag & Type.POINTERS.key) != 0) {
			processPointers(state);
		}
		if ((trackedValuesFlag & Type.POINTER_EVENTS.key) != 0) {
			processPointerEvents(state);
		}
		if (lastState == null) {
			lastState = new InputState(
					recorder.getConfiguration().recordedPointerCount);
		}
		lastState.set(state, copiedValuesFlag);
	}

	private void processButtons(InputState state) {
		if (lastState == null || state.button0 != lastState.button0
				|| state.button1 != lastState.button1
				|| state.button2 != lastState.button2) {
			Button buttonChange = new Button();
			buttonChange.button0 = state.button0;
			buttonChange.button1 = state.button1;
			buttonChange.button2 = state.button2;
			buttonChange.timeDelta = this.timeDelta;
			recorder.getRecordWriter().writeSyncValues(buttonChange);
		}
	}

	private void processKeyEvents(InputState state) {
		InputRecordWriter writer = recorder.getRecordWriter();
		for (KeyEvent event : state.keyEvents) {
			com.badlogic.gdx.input.recorder.InputValue.SyncValue.KeyEvent valueEvent = new com.badlogic.gdx.input.recorder.InputValue.SyncValue.KeyEvent(
					event);
			valueEvent.timeDelta = timeDelta;
			writer.writeSyncValues(valueEvent);
		}
	}

	private void processKeysPressed(InputState state) {
		InputRecordWriter writer = recorder.getRecordWriter();
		for (int key : state.pressedKeys.keySet()) {
			KeyPressed pressed = new KeyPressed();
			pressed.keyCode = key;
			pressed.timeDelta = timeDelta;
			writer.writeSyncValues(pressed);
		}
	}

	private void processOrientation(InputState state) {
		if (lastState == null
				|| state.accelerometerX != lastState.accelerometerX
				|| state.accelerometerY != lastState.accelerometerY
				|| state.accelerometerZ != lastState.accelerometerZ) {
			Accelerometer accelChange = new Accelerometer();
			accelChange.accelerometerX = state.accelerometerX;
			accelChange.accelerometerY = state.accelerometerY;
			accelChange.accelerometerZ = state.accelerometerZ;
			accelChange.timeDelta = timeDelta;
		}
		if (lastState == null || state.roll != lastState.roll
				|| state.pitch != lastState.pitch
				|| state.azimuth != lastState.azimuth
				|| state.orientation != lastState.orientation) {
			Orientation oChange = new Orientation();
			oChange.pitch = state.pitch;
			oChange.roll = state.roll;
			oChange.azimuth = state.azimuth;
			oChange.orientation = state.orientation;
			System.arraycopy(state.rotationMatrix, 0, oChange.rotationMatrix,
					0, 16);
			oChange.timeDelta = timeDelta;
		}
	}

	private void processPointers(InputState state) {
		int maxPtrs = recorder.getConfiguration().recordedPointerCount;
		InputRecordWriter writer = recorder.getRecordWriter();
		// TODO are the deltas really dependent on the coordinates?
		for (int i = 0; i < maxPtrs; i++) {
			if (lastState == null || state.justTouched != lastState.justTouched
					|| state.x[i] != lastState.x[i]
					|| state.y[i] != lastState.y[i]) {
				Pointer ptrChange = new Pointer();
				ptrChange.x = state.x[i];
				ptrChange.y = state.y[i];
				ptrChange.deltaX = state.deltaX[i];
				ptrChange.deltaY = state.deltaY[i];
				ptrChange.pointer = i;
				ptrChange.timeDelta = this.timeDelta;
				writer.writeSyncValues(ptrChange);
			}
		}
	}

	private void processPointerEvents(InputState state) {
		InputRecordWriter writer = recorder.getRecordWriter();
		for (PointerEvent event : state.pointerEvents) {
			com.badlogic.gdx.input.recorder.InputValue.SyncValue.PointerEvent ptrEvent = new com.badlogic.gdx.input.recorder.InputValue.SyncValue.PointerEvent(
					event);
			ptrEvent.timeDelta = timeDelta;
			writer.writeSyncValues(ptrEvent);
		}
	}

	/**
	 * Resets the processor so it thinks that the next {@link InputState} given
	 * to process is the first one ever recorded.
	 */
	public void reset() {
		lastState = null;
	}
}
