package com.badlogic.gdx.automation.recorder;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.automation.recorder.io.InputRecordWriter;
import com.badlogic.gdx.automation.recorder.io.JsonInputRecordWriter;

/**
 * The main class for input recording in a libGDX application.
 * 
 * @author Lukas Böhm
 */
public class InputRecorder {

	private final InputStateTracker valueTracker;
	private final TextInputTracker textTracker;

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
				config.writer = new JsonInputRecordWriter(config.outputFile);
			} catch (IOException e) {
				throw new IllegalStateException(
						"Unable to create InputRecordWriter for file "
								+ config.outputFile);
			}
		}
	}

	public void startRecording() throws IOException {
		config.writer.open();
		RecordProperties properties = new RecordProperties();
		properties.absouluteCoords = config.absoluteCoords;
		config.writer.writeRecordProperties(properties);
		config.writer.writeStaticValues(InputProperty.getCurrentStaticValues());
		textTracker.startTracking();
		valueTracker.startTracking();
	}

	public void stopRecording() throws IOException {
		textTracker.stopTracking();
		valueTracker.stopTracking();
		config.writer.close();
	}

	public void flush() throws IOException {
		config.writer.flush();
	}

	public synchronized void setInputRecordWriter(InputRecordWriter writer) {
		boolean textTrackerRunning = textTracker.isTracking();
		boolean stateTrackerRunning = valueTracker.isTracking();

		textTracker.stopTracking();
		valueTracker.stopTracking();

		synchronized (config.writer) {
			try {
				flush();
			} catch (IOException e) {
				e.printStackTrace();
				Gdx.app.log(LOG_TAG,
						"Probable loss of recorded data (see exception trace)");
			}
			config.writer = writer;
		}

		if (textTrackerRunning) {
			textTracker.startTracking();
		}
		if (stateTrackerRunning) {
			valueTracker.startTracking();
		}
	}

	InputRecordWriter getRecordWriter() {
		return config.writer;
	}

	InputRecorderConfiguration getConfiguration() {
		return config;
	}

	void notifyError(Throwable cause) {
		throw new UndeclaredThrowableException(cause);
	}
}
