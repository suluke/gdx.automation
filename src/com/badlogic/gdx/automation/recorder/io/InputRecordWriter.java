package com.badlogic.gdx.automation.recorder.io;

import java.io.IOException;

import com.badlogic.gdx.automation.recorder.InputRecorder;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue;
import com.badlogic.gdx.automation.recorder.InputValue.StaticValues;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue;

/**
 * Interface for classes providing means to save or transmit user input recorded
 * by am {@link InputRecorder}. This could be a {@link JsonInputRecordWriter} as
 * well as a writer to send input over the network to a receiver.
 * 
 */
public interface InputRecordWriter extends java.io.Flushable, java.io.Closeable {
	void writeStaticValues(StaticValues values) throws IOException;

	void writeSyncValues(SyncValue values) throws IOException;

	void writeAsyncValues(AsyncValue values) throws IOException;

	void open() throws IOException;
}
