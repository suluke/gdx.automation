package com.badlogic.gdx.input;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;

/**
 * The main class for input recording in a libGDX application.
 * 
 */
public class InputRecorder {

	private final InputStateTracker valueTracker;
	private final TextInputTracker textTracker;
	private InputRecordWriter writer;

	private final InputRecorderConfiguration config;

	private final static FileType standardOutputLocation = FileType.Local;
	private final static String standardOutputPath = "gdxInputRecording.json";

	final static String LOG_TAG = "InputTracker";

	public InputRecorder() {
		this(new InputRecorderConfiguration());
	}

	public InputRecorder(InputRecorderConfiguration config) {
		this.config = new InputRecorderConfiguration();
		init(config);

		valueTracker = new InputStateTracker(this);
		textTracker = new TextInputTracker(this);
	}

	private void init(InputRecorderConfiguration newConfig) {
		config.set(newConfig);
		if (config.writer == null) {
			if (config.outputFile == null) {
				config.outputFile = Gdx.files.getFileHandle(standardOutputPath,
						standardOutputLocation);
			}
			writer = new SimpleInputRecordWriter(config.outputFile);
		}
	}

	public void startRecording() {
		writer.writeStaticValues(InputValue.getCurrentStaticValues());
		textTracker.startTracking();
		valueTracker.startTracking();
	}

	public void stopRecording() {
		textTracker.stopTracking();
		valueTracker.startTracking();
	}

	public void flush() {
		writer.flush();
	}

	public synchronized void setInputSequenceWriter(InputRecordWriter writer) {
		boolean textTrackerRunning = textTracker.isTracking();
		boolean stateTrackerRunning = valueTracker.isTracking();

		textTracker.stopTracking();
		valueTracker.stopTracking();

		synchronized (this.writer) {
			flush();
			this.writer = writer;
		}

		if (textTrackerRunning) {
			textTracker.startTracking();
		}
		if (stateTrackerRunning) {
			valueTracker.startTracking();
		}
	}

	InputRecordWriter getRecordWriter() {
		synchronized (writer) {
			return writer;
		}
	}

	InputRecorderConfiguration getConfiguration() {
		return config;
	}
}
