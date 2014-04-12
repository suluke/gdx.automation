package com.badlogic.gdx.automation.recorder.io;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty.PlaceholderText;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty.Text;
import com.badlogic.gdx.automation.recorder.InputProperty.StaticProperties;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty;
import com.badlogic.gdx.automation.recorder.RecordProperties;
import com.badlogic.gdx.automation.recorder.io.MemoryInputRecordWriter.AsyncValueQueues;

public class MemoryInputRecordReader implements InputRecordReader {

	private final AsyncValueQueues asyncValues;
	private final List<SyncProperty> syncValues;
	private final StaticProperties staticValues;
	private final RecordProperties recordProperties;
	private final MemoryInputRecordWriter writer;

	public MemoryInputRecordReader(MemoryInputRecordWriter writer) {
		this.writer = writer;
		asyncValues = writer.getAsyncValueQueues().copy();
		syncValues = new LinkedList<SyncProperty>();
		staticValues = new StaticProperties();
		recordProperties = new RecordProperties();
		reset();
	}

	@Override
	public Iterator<Text> getTextIterator() {
		return asyncValues.textValues.iterator();
	}

	@Override
	public Iterator<PlaceholderText> getPlaceholderTextIterator() {
		return asyncValues.placeholderTextValues.iterator();
	}

	@Override
	public Iterator<SyncProperty> getSyncValueIterator() {
		return syncValues.iterator();
	}

	@Override
	public StaticProperties getStaticValues() {
		return staticValues;
	}

	public void reset() {
		syncValues.clear();
		syncValues.addAll(writer.getSyncValues());
		asyncValues.set(writer.getAsyncValueQueues());
		staticValues.set(writer.getStaticValues());
		recordProperties.set(writer.getRecordProperties());
	}

	@Override
	public void close() {

	}

	@Override
	public RecordProperties getRecordProperties() {
		return recordProperties;
	}

}
