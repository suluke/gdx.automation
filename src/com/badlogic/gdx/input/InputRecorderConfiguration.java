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
	public boolean recordButtons = false;

	/**
	 * 
	 */
	public boolean recordPointers = false;

	/**
	 * 
	 */
	public boolean recordOrientation = false;

	/**
	 * The number of pointers recorded if {@link #recordPointers} is enabled.
	 * LibGDX doesn't provide more than 20 buffer spaces for pointer values to
	 * be stored in, so be aware that setting this value higher than 20 is very
	 * likely to cause crashes.
	 */
	public int recordedPointerCount = 0;

	/**
	 * 
	 */
	public boolean recordKeysPressed = false;

	/**
	 * 
	 */
	public boolean recordPointerEvents = true;

	/**
	 * 
	 */
	public boolean recordKeyEvents = true;

	/**
	 * The default value is null, meaning that the {@link InputRecorder} will
	 * create a default {@link InputRecordWriter} (a
	 * {@link JsonInputRecordWriter})
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
		copy.recordPointerEvents = original.recordPointerEvents;
		copy.absoluteCoords = original.absoluteCoords;
		copy.outputFile = original.outputFile;
		copy.writer = original.writer;
		return copy;
	}
}
