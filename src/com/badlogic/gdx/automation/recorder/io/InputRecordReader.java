package com.badlogic.gdx.automation.recorder.io;

import java.util.Iterator;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.automation.recorder.InputProperty;
import com.badlogic.gdx.automation.recorder.InputProperty.StaticProperties;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty;
import com.badlogic.gdx.automation.recorder.RecordProperties;

/**
 * Interface for classes that provide access to a stream of data describing
 * changes to an instance of {@link Input}. This does not necessarily mean that
 * the supplied data is from a record. For example, the
 * {@link RandomInputRecordReader} generates this data on the fly
 */
public interface InputRecordReader {
	RecordProperties getRecordProperties();

	Iterator<InputProperty.AsyncProperty.Text> getTextIterator();

	Iterator<InputProperty.AsyncProperty.PlaceholderText> getPlaceholderTextIterator();

	Iterator<SyncProperty> getSyncValueIterator();

	StaticProperties getStaticValues();

	void close();
}
