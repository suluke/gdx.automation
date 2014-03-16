package com.badlogic.gdx.input;

import com.badlogic.gdx.files.FileHandle;

/**
 * A class to configure new instances of {@link InputRecorder}
 * 
 */
public class InputRecorderConfiguration {
	/**
	 * 
	 */
	public boolean absoluteCoords = false;

	/**
	 * Will be ignored if you set a different InputRecordWriter later
	 */
	public FileHandle outputFile = null;

	/**
	 * 
	 */
	public boolean recordAccelerometer = false;

	/**
	 * 
	 */
	public boolean recordButtonsPressed = true;

	/**
	 * 
	 */
	public boolean recordCoordinates = true;

	/**
	 * 
	 */
	public boolean recordDeviceOrientation = false;

	/**
	 * 
	 */
	public int recordedPointerCount = 3;

	/**
	 * 
	 */
	public boolean recordKeysPressed = false;

	/**
	 * The default value is null, meaning that the {@link InputRecorder} will
	 * create a default {@link InputRecordWriter} (a
	 * {@link SimpleInputRecordWriter})
	 */
	public InputRecordWriter writer = null;

	public InputRecorderConfiguration copy() {
		return new InputRecorderConfiguration().set(this);
	}

	public InputRecorderConfiguration set(InputRecorderConfiguration original) {
		InputRecorderConfiguration copy = this;
		copy.recordAccelerometer = original.recordAccelerometer;
		copy.recordButtonsPressed = original.recordButtonsPressed;
		copy.recordCoordinates = original.recordCoordinates;
		copy.recordDeviceOrientation = original.recordDeviceOrientation;
		copy.recordedPointerCount = original.recordedPointerCount;
		copy.recordKeysPressed = original.recordKeysPressed;
		copy.absoluteCoords = original.absoluteCoords;
		copy.outputFile = original.outputFile;
		copy.writer = original.writer;
		return copy;
	}
}
