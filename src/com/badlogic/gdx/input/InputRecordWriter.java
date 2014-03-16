package com.badlogic.gdx.input;

import com.badlogic.gdx.input.InputValue.AsyncValue;
import com.badlogic.gdx.input.InputValue.StaticValues;
import com.badlogic.gdx.input.InputValue.SyncValue;

public interface InputRecordWriter {

	/**
	 * Writes all input changes that were not yet persisted back to the
	 * underlying datastructure.
	 */
	void flush();

	void writeStaticValues(StaticValues values);

	void writeSyncValues(SyncValue values);

	void writeAsyncValues(AsyncValue values);
}
