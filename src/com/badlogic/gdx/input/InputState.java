package com.badlogic.gdx.input;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InputState {

	private final int MAX_POINTERS;

	public float accelerometerX;
	public float accelerometerY;
	public float accelerometerZ;

	public final int[] x;
	public final int[] y;
	public final int[] deltaX;
	public final int[] deltaY;
	public final boolean[] touched;

	public boolean justTouched;

	public boolean button0;
	public boolean button1;
	public boolean button2;

	public final SparseArray<Boolean> pressedKeys;

	public final ArrayList<EventBufferAccessHelper.KeyEvent> keyEvents;
	public final ArrayList<EventBufferAccessHelper.TouchEvent> touchEvents;

	public float pitch;
	public float roll;
	public float azimuth;

	public final float[] rotationMatrix = new float[16];

	public int orientation;

	public boolean cursorCatched;

	public InputState() {
		this(3);
	}

	public InputState(int maxPointers) {
		if (maxPointers > 20) {
			Gdx.app.log(
					InputRecorder.LOG_TAG,
					"Warning: Most of the libGDX backends only use 20 pointers internally. Trying to use "
							+ maxPointers);
		}
		this.MAX_POINTERS = maxPointers;
		x = new int[MAX_POINTERS];
		y = new int[MAX_POINTERS];
		deltaX = new int[MAX_POINTERS];
		deltaY = new int[MAX_POINTERS];
		touched = new boolean[MAX_POINTERS];
		pressedKeys = new SparseArray<Boolean>();
		keyEvents = new ArrayList<EventBufferAccessHelper.KeyEvent>();
		touchEvents = new ArrayList<EventBufferAccessHelper.TouchEvent>();
	}

	public void set(Input input) {
		setAccelerometer(input);
		setX(input);
		setY(input);
		setDeltaX(input);
		setDeltaY(input);
		setTouched(input);
		setButtons(input);
		setPressedKeys(input);
		setKeyEvents(input);
		setTouchEvents(input);
		setRotation(input);
	}

	private void setAccelerometer(Input input) {
		accelerometerX = input.getAccelerometerX();
		accelerometerY = input.getAccelerometerY();
		accelerometerZ = input.getAccelerometerZ();
	}

	private void setX(Input input) {
		for (int i = 0; i < MAX_POINTERS; i++) {
			x[i] = input.getX(i);
		}
	}

	private void setY(Input input) {
		for (int i = 0; i < MAX_POINTERS; i++) {
			y[i] = input.getY(i);
		}
	}

	private void setDeltaX(Input input) {
		for (int i = 0; i < MAX_POINTERS; i++) {
			deltaX[i] = input.getDeltaX(i);
		}
	}

	private void setDeltaY(Input input) {
		for (int i = 0; i < MAX_POINTERS; i++) {
			deltaY[i] = input.getDeltaY(i);
		}
	}

	private void setTouched(Input input) {
		for (int i = 0; i < MAX_POINTERS; i++) {
			touched[i] = input.isTouched(i);
		}
		justTouched = input.justTouched();
	}

	private void setButtons(Input input) {
		button0 = input.isButtonPressed(0);
		button1 = input.isButtonPressed(1);
		button2 = input.isButtonPressed(2);
	}

	private void setPressedKeys(Input input) {
		EventBufferAccessHelper.copyPressedKeys(input, pressedKeys);
	}

	private void setKeyEvents(Input input) {
		EventBufferAccessHelper.copyKeyEvents(input, keyEvents);
	}

	private void setTouchEvents(Input input) {
		EventBufferAccessHelper.copyTouchEvents(input, touchEvents);
	}

	private void setRotation(Input input) {
		pitch = input.getPitch();
		roll = input.getRoll();
		azimuth = input.getAzimuth();
		input.getRotationMatrix(rotationMatrix);
	}

	/*
	 * Getter methods "inherited" from Input interface
	 */

	private void verifyPointer(int pointer) {
		if (pointer < 0) {
			throw new IllegalArgumentException(
					"Did not expect negative pointer indices");
		}
		if (pointer >= MAX_POINTERS) {
			throw new IllegalArgumentException("Did not expect more than "
					+ MAX_POINTERS + " pointers");
		}
	}

	public float getAccelerometerX() {
		return accelerometerX;
	}

	public float getAccelerometerY() {
		return accelerometerY;
	}

	public float getAccelerometerZ() {
		return accelerometerZ;
	}

	public int getX() {
		return x[0];
	}

	public int getX(int pointer) {
		verifyPointer(pointer);
		return x[pointer];
	}

	public int getDeltaX() {
		return deltaX[0];
	}

	public int getDeltaX(int pointer) {
		verifyPointer(pointer);
		return deltaX[pointer];
	}

	public int getY() {
		return y[0];
	}

	public int getY(int pointer) {
		verifyPointer(pointer);
		return y[pointer];
	}

	public int getDeltaY() {
		return deltaY[0];
	}

	public int getDeltaY(int pointer) {
		verifyPointer(pointer);
		return deltaY[pointer];
	}

	public boolean justTouched() {
		return justTouched;
	}

	public boolean isTouched() {
		return touched[0];
	}

	public boolean isTouched(int pointer) {
		verifyPointer(pointer);
		return touched[pointer];
	}

	public boolean isButtonPressed(int button) {
		switch (button) {
		case 0:
			return button0;
		case 1:
			return button1;
		case 2:
			return button2;
		default:
			return false;
		}
	}

	public boolean isKeyPressed(int key) {
		if (key == Input.Keys.ANY_KEY) {
			return pressedKeys.size() > 0;
		} else {
			return pressedKeys.get(key) != null;
		}
	}

	public float getAzimuth() {
		return azimuth;
	}

	public float getPitch() {
		return pitch;
	}

	public float getRoll() {
		return roll;
	}

	public void getRotationMatrix(float[] matrix) {
		if (matrix.length < 16 && matrix.length >= 9) {
			matrix[0] = rotationMatrix[0];
			matrix[1] = rotationMatrix[1];
			matrix[2] = rotationMatrix[2];

			matrix[3] = rotationMatrix[4];
			matrix[4] = rotationMatrix[5];
			matrix[5] = rotationMatrix[6];

			matrix[6] = rotationMatrix[8];
			matrix[7] = rotationMatrix[9];
			matrix[8] = rotationMatrix[10];
		} else if (matrix.length >= 16) {
			System.arraycopy(rotationMatrix, 0, matrix, 0, 16);
		}
	}

	public int getRotation() {
		return orientation;
	}

	public boolean isCursorCatched() {
		return cursorCatched;
	}

}
