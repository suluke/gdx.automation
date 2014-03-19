package com.badlogic.gdx.input.recorder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.Type;
import com.badlogic.gdx.utils.TimeUtils;

public class InputState {

	private int MAX_POINTERS;

	public float accelerometerX;
	public float accelerometerY;
	public float accelerometerZ;

	public int[] x;
	public int[] y;
	public int[] deltaX;
	public int[] deltaY;
	public boolean[] touched;

	public boolean justTouched;

	public boolean button0;
	public boolean button1;
	public boolean button2;

	public final SparseArray<Boolean> pressedKeys;

	public final ArrayList<EventBufferAccessHelper.KeyEvent> keyEvents;
	public final ArrayList<EventBufferAccessHelper.PointerEvent> pointerEvents;

	public float pitch;
	public float roll;
	public float azimuth;

	public final float[] rotationMatrix = new float[16];

	public int orientation;

	public long timeStamp;

	public InputState() {
		pressedKeys = new SparseArray<Boolean>();
		keyEvents = new ArrayList<EventBufferAccessHelper.KeyEvent>();
		pointerEvents = new ArrayList<EventBufferAccessHelper.PointerEvent>();
	}

	public InputState(int maxPointers) {
		this();
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
	}

	public void initialize(int maxPointers) {
		pressedKeys.clear();
		keyEvents.clear();
		pointerEvents.clear();
		if (maxPointers != MAX_POINTERS) {
			if (maxPointers > MAX_POINTERS) {
				if (maxPointers > 20) {
					Gdx.app.log(
							InputRecorder.LOG_TAG,
							"Warning: Most of the libGDX backends only use 20 pointers internally. Trying to use "
									+ maxPointers);
				}
				x = new int[maxPointers];
				y = new int[maxPointers];
				deltaX = new int[maxPointers];
				deltaY = new int[maxPointers];
				touched = new boolean[maxPointers];
			} else {
				Arrays.fill(x, 0);
				Arrays.fill(y, 0);
				Arrays.fill(deltaX, 0);
				Arrays.fill(deltaY, 0);
				Arrays.fill(touched, false);
			}
			MAX_POINTERS = maxPointers;
		}
	}

	public void set(InputState state, int copyFlags) {
		if ((copyFlags & Type.BUTTONS.key) != 0) {
			button0 = state.button0;
			button1 = state.button1;
			button2 = state.button2;
		}
		if ((copyFlags & Type.KEY_EVENTS.key) != 0) {

		}
		if ((copyFlags & Type.KEYS_PRESSED.key) != 0) {

		}
		if ((copyFlags & Type.ORIENTATION.key) != 0) {
			accelerometerX = state.accelerometerX;
			accelerometerY = state.accelerometerY;
			accelerometerZ = state.accelerometerZ;
			pitch = state.pitch;
			roll = state.roll;
			azimuth = state.azimuth;
			orientation = state.orientation;
			System.arraycopy(state.rotationMatrix, 0, rotationMatrix, 0, 16);
		}
		if ((copyFlags & Type.POINTERS.key) != 0) {
			System.arraycopy(state.x, 0, x, 0, MAX_POINTERS);
			System.arraycopy(state.y, 0, y, 0, MAX_POINTERS);
			System.arraycopy(state.deltaX, 0, deltaX, 0, MAX_POINTERS);
			System.arraycopy(state.deltaY, 0, deltaY, 0, MAX_POINTERS);
			System.arraycopy(state.touched, 0, touched, 0, MAX_POINTERS);
			justTouched = state.justTouched;
		}
		if ((copyFlags & Type.POINTER_EVENTS.key) != 0) {

		}
	}

	/**
	 * Use OR'ed {@link InputValue.Type#key}s to define properties
	 * 
	 * @param input
	 * @param properties
	 * @param updateEvents
	 *            whether to force-pull events from their backend-specific
	 *            sources before copying them into this InputState.
	 */
	public void set(Input input, int properties, boolean updateEvents) {
		timeStamp = TimeUtils.millis(); // input in milliseconds should be
										// sufficient
		if ((InputValue.SyncValue.Type.POINTERS.key & properties) != 0) {
			setX(input);
			setY(input);
			setDeltaX(input);
			setDeltaY(input);
			setTouched(input);
		}
		if ((InputValue.SyncValue.Type.BUTTONS.key & properties) != 0) {
			setButtons(input);
		}
		if ((InputValue.SyncValue.Type.KEYS_PRESSED.key & properties) != 0) {
			setPressedKeys(input);
		}
		if ((InputValue.SyncValue.Type.KEY_EVENTS.key & properties) != 0) {
			setKeyEvents(input, updateEvents);
		}
		if ((InputValue.SyncValue.Type.POINTER_EVENTS.key & properties) != 0) {
			setPointerEvents(input, updateEvents);
		}
		if ((InputValue.SyncValue.Type.ORIENTATION.key & properties) != 0) {
			setOrientation(input);
		}
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

	private void setKeyEvents(Input input, boolean update) {
		EventBufferAccessHelper.copyKeyEvents(input, keyEvents, update);
	}

	private void setPointerEvents(Input input, boolean update) {
		EventBufferAccessHelper.copyPointerEvents(input, pointerEvents, update);
	}

	private void setOrientation(Input input) {
		if (Gdx.app.getType() == ApplicationType.Android) {
			while (input instanceof InputProxy) {
				input = ((InputProxy) input).getProxiedInput();
			}
			try {
				Class<?> inputClass = input.getClass();
				if (Class.forName(
						"com.badlogic.gdx.backends.android.AndroidInput")
						.isAssignableFrom(inputClass)) {
					Method updateOrientation = inputClass.getDeclaredMethod(
							"updateOrientation", new Class<?>[0]);
					updateOrientation.setAccessible(true);
					updateOrientation.invoke(input, new Object[0]);
				}
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(
						"Android application without AndroidInput?");
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException("No updateOrientation?");
			} catch (SecurityException e) {
				throw e;
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(
						"Running in a too restrictive environment...");
			} catch (IllegalArgumentException e) {
				throw e;
			} catch (InvocationTargetException e) {
				throw new IllegalStateException("updateOrientation:"
						+ e.getLocalizedMessage());
			}
		}

		accelerometerX = input.getAccelerometerX();
		accelerometerY = input.getAccelerometerY();
		accelerometerZ = input.getAccelerometerZ();

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

	public long getTimeStamp() {
		return timeStamp;
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

	/**
	 * Returns the maximum number of pointers that were tracked by this
	 * {@link InputState}. This should correspond to
	 * {@link InputRecorderConfiguration#recordedPointerCount}
	 * 
	 * @return the maximum number of pointers stored
	 */
	public int getPointerCount() {
		return MAX_POINTERS;
	}
}
