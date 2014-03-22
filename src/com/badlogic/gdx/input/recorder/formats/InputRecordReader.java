package com.badlogic.gdx.input.recorder.formats;

import java.util.Iterator;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.input.recorder.InputValue;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue;
import com.badlogic.gdx.input.recorder.InputValue.StaticValues;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.Text;

/**
 * Interface for classes that provide access to a stream of data describing
 * changes to an instance of {@link Input}. This does not necessarily mean that
 * the supplied data is from a record. For example, the
 * {@link RandomInputRecordReader} generates this data on the fly
 */
public interface InputRecordReader {
	Iterator<InputValue.AsyncValue.Text> getTextIterator();

	Iterator<InputValue.AsyncValue.PlaceholderText> getPlaceholderTextIterator();

	Iterator<SyncValue> getSyncValueIterator();

	StaticValues getStaticValues();

	void reset();

	void close();
}
