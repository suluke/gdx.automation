package com.badlogic.gdx.input.recorder;

import java.io.IOException;

import com.badlogic.gdx.input.recorder.InputValue.AsyncValue;
import com.badlogic.gdx.input.recorder.InputValue.StaticValues;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue;

/**
 * Interface for classes providing means to save or transmit user input recorded
 * by am {@link InputRecorder}. This could be a {@link JsonInputRecordWriter} as
 * well as a writer to send input over the network to a receiver.
 * 
 */
public interface InputRecordWriter extends java.io.Flushable, java.io.Closeable {
	void writeStaticValues(StaticValues values) throws IOException;

	void writeSyncValues(SyncValue values);

	void writeAsyncValues(AsyncValue values);

	void open() throws IOException;
}
