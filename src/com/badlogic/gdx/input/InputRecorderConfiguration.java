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
	public boolean recordButtons = true;

	/**
	 * 
	 */
	public boolean recordPointers = true;

	/**
	 * 
	 */
	public boolean recordOrientation = false;

	/**
	 * 
	 */
	public int recordedPointerCount = 3;

	/**
	 * 
	 */
	public boolean recordKeysPressed = false;

	/**
	 * 
	 */
	public boolean recordTouchEvents = true;

	/**
	 * 
	 */
	public boolean recordKeyEvents = true;

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
		copy.recordButtons = original.recordButtons;
		copy.recordOrientation = original.recordOrientation;
		copy.recordKeyEvents = original.recordKeyEvents;
		copy.recordKeysPressed = original.recordKeysPressed;
		copy.recordPointers = original.recordPointers;
		copy.recordedPointerCount = original.recordedPointerCount;
		copy.recordTouchEvents = original.recordTouchEvents;
		copy.absoluteCoords = original.absoluteCoords;
		copy.outputFile = original.outputFile;
		copy.writer = original.writer;
		return copy;
	}
}
