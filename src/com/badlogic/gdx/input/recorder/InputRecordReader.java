package com.badlogic.gdx.input.recorder;

import java.util.Iterator;

import com.badlogic.gdx.Input;

/**
 * Interface for classes that provide access to a stream of data describing
 * changes to an instance of {@link Input}. This does not necessarily mean that
 * the supplied data is from a record. For example, the
 * {@link RandomInputRecordReader} generates this data on the fly
 */
public interface InputRecordReader {
	Iterator<InputValue.AsyncValue.Text> getTextIterator();

	Iterator<InputValue.AsyncValue.PlaceholderText> getPlaceholderTextIterator();
}
