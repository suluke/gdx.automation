package com.badlogic.gdx.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class InputRecorder implements Runnable {

	private final InputStateTracker valueTracker;
	private final TextInputTracker textTracker;
	InputRecordWriter writer;
	private boolean running = false;

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
		running = true;
		Gdx.app.postRunnable(this);
		textTracker.startTracking();
	}

	public void stopRecording() {
		textTracker.stopTracking();
		running = false;
	}

	public void flush() {
		writer.flush();
	}

	public synchronized void setInputSequenceWriter(InputRecordWriter writer) {
		boolean wasRunning = running;
		running = false;
		textTracker.stopTracking();
		writer.flush();
		this.writer = writer;
		if (wasRunning) {
			textTracker.startTracking();
			running = true;
		}
	}

	/**
	 * LibGDX (at least its desktop and android backends) executes
	 * {@link Application#postRunnable(Runnable) posted runnables} just before
	 * it calls processEvent() on the backend's {@link Gdx#input input}.
	 * Therefore it is the perfect place to record the current state of the
	 * input.
	 */
	@Override
	public void run() {
		valueTracker.track();
		if (running) {
			Gdx.app.postRunnable(this);
		}
	}
}
