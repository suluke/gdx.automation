package com.badlogic.gdx.automation.recorder.formats;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue.Text;
import com.badlogic.gdx.automation.recorder.InputValue.StaticValues;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue;
import com.badlogic.gdx.automation.recorder.formats.MemoryInputRecordWriter.AsyncValueQueues;

public class MemoryInputRecordReader implements InputRecordReader {

	private final AsyncValueQueues asyncValues;
	private final List<SyncValue> syncValues;
	private final StaticValues staticValues;
	private final MemoryInputRecordWriter writer;

	public MemoryInputRecordReader(MemoryInputRecordWriter writer) {
		this.writer = writer;
		asyncValues = writer.getAsyncValueQueues().copy();
		syncValues = new LinkedList<SyncValue>();
		staticValues = new StaticValues();
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
	public Iterator<SyncValue> getSyncValueIterator() {
		return syncValues.iterator();
	}

	@Override
	public StaticValues getStaticValues() {
		return staticValues;
	}

	@Override
	public void reset() {
		syncValues.clear();
		syncValues.addAll(writer.getSyncValues());
		asyncValues.set(writer.getAsyncValueQueues());
		staticValues.set(writer.getStaticValues());
	}

	@Override
	public void close() {

	}

}
