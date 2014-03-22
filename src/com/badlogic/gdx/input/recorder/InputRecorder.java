package com.badlogic.gdx.input.recorder;

import java.io.IOException;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.input.recorder.formats.InputRecordWriter;
import com.badlogic.gdx.input.recorder.formats.JsonInputRecordWriter;
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
			try {
				writer = new JsonInputRecordWriter(config.outputFile);
			} catch (IOException e) {
				throw new IllegalStateException(
						"Unable to create InputRecordWriter for file "
								+ config.outputFile);
			}
		}
	}

	public void startRecording() throws IOException {
		writer.open();
		writer.writeStaticValues(InputValue.getCurrentStaticValues());
		textTracker.startTracking();
		valueTracker.startTracking();
	}

	public void stopRecording() throws IOException {
		textTracker.stopTracking();
		valueTracker.startTracking();
		writer.close();
	}

	public void flush() throws IOException {
		writer.flush();
	}

	public synchronized void setInputSequenceWriter(InputRecordWriter writer) {
		boolean textTrackerRunning = textTracker.isTracking();
		boolean stateTrackerRunning = valueTracker.isTracking();

		textTracker.stopTracking();
		valueTracker.stopTracking();

		synchronized (this.writer) {
			try {
				flush();
			} catch (IOException e) {
				e.printStackTrace();
				Gdx.app.log(LOG_TAG,
						"Probable loss of recorded data (see exception trace)");
			}
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
