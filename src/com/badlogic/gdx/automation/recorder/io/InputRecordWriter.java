package com.badlogic.gdx.automation.recorder.io;

import java.io.IOException;

import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty;
import com.badlogic.gdx.automation.recorder.InputProperty.StaticProperties;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty;
import com.badlogic.gdx.automation.recorder.InputRecorder;
import com.badlogic.gdx.automation.recorder.RecordProperties;

/**
 * Interface for classes providing means to save or transmit user input recorded
 * by am {@link InputRecorder}. This could be a {@link JsonInputRecordWriter} as
 * well as a writer to send input over the network to a receiver.
 * 
 */
public interface InputRecordWriter extends java.io.Flushable, java.io.Closeable {
	void writeRecordProperties(RecordProperties properties) throws IOException;

	void writeStaticValues(StaticProperties values) throws IOException;

	void writeSyncValues(SyncProperty values) throws IOException;

	void writeAsyncValues(AsyncProperty values) throws IOException;

	void open() throws IOException;
}
