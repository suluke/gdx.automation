package com.badlogic.gdx.input.recorder;

import java.io.IOException;

import com.badlogic.gdx.input.recorder.InputValue.AsyncValue;
import com.badlogic.gdx.input.recorder.InputValue.StaticValues;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue;

public interface InputRecordWriter extends java.io.Flushable, java.io.Closeable {
	void writeStaticValues(StaticValues values) throws IOException;

	void writeSyncValues(SyncValue values);

	void writeAsyncValues(AsyncValue values);

	void open() throws IOException;
}
