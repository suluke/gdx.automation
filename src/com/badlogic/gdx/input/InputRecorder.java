package com.badlogic.gdx.input;

public class InputRecorder {

	private final InputStateTracker valueTracker;
	private final TextInputTracker textTracker;
	InputRecordWriter writer;

	final InputRecorderConfiguration config;

	final static String LOG_TAG = "InputTracker";

	public InputRecorder() {
		this(new InputRecorderConfiguration());
	}

	public InputRecorder(InputRecorderConfiguration config) {
		this.config = config;

		valueTracker = new InputStateTracker(this);
		textTracker = new TextInputTracker(this);
	}

	public void startRecording() {
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

		writer.flush();
		this.writer = writer;

		if (textTrackerRunning) {
			textTracker.startTracking();
		}
		if (stateTrackerRunning) {
			valueTracker.startTracking();
		}
	}
}
