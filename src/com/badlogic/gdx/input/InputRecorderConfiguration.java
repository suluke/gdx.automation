package com.badlogic.gdx.input;

import com.badlogic.gdx.files.FileHandle;

public class InputRecorderConfiguration {
	public boolean recordDeviceOrientation = false;
	public boolean recordKeysPressed = false;
	public boolean recordAccelerometer;
	public boolean absoluteCoords = false;

	/**
	 * Will be ignored if you set a different InputRecordWriter later
	 */
	public FileHandle outputFile = null;

}
