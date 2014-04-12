package com.badlogic.gdx.automation.recorder.io;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.automation.recorder.InputProperty;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty.PlaceholderText;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty.Text;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncPropertyVisitor;
import com.badlogic.gdx.automation.recorder.InputProperty.StaticProperties;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty;
import com.badlogic.gdx.automation.recorder.RecordProperties;

/**
 * Simple writer backend primarily designed for testing
 * 
 */
public class MemoryInputRecordWriter implements InputRecordWriter {
	private static final String LOG_TAG = "MemoryInputRecordWriter";

	private boolean open = false;

	private final LinkedList<SyncProperty> syncValues;
	private final AsyncValueQueues asyncValueQueues;
	private final StaticProperties staticValues;
	private final RecordProperties recordProperties;

	public MemoryInputRecordWriter() {
		syncValues = new LinkedList<InputProperty.SyncProperty>();
		asyncValueQueues = new AsyncValueQueues();
		staticValues = new StaticProperties();
		recordProperties = new RecordProperties();
	}

	@Override
	public void flush() throws IOException {
		if (!open) {
			Gdx.app.log(LOG_TAG, "Flushing a closed writer");
		}
	}

	@Override
	public void close() throws IOException {
		if (!open) {
			Gdx.app.log(LOG_TAG, "Closing an already closed writer again");
		}
		open = false;
	}

	@Override
	public void writeStaticValues(StaticProperties values) throws IOException {
		if (!open) {
			throw new IOException("Cannot write to closed writer");
		}
		staticValues.set(values);
	}

	@Override
	public void writeSyncValues(SyncProperty values) throws IOException {
		if (!open) {
			throw new IOException("Cannot write to closed writer");
		}
		syncValues.add(values);
	}

	@Override
	public void writeAsyncValues(AsyncProperty value) throws IOException {
		if (!open) {
			throw new IOException("Cannot write to closed writer");
		}
		asyncValueQueues.storeValue(value);
	}

	@Override
	public void open() throws IOException {
		if (open) {
			Gdx.app.log(LOG_TAG, "Reopening an already open writer");
		}
		open = true;
		syncValues.clear();
		asyncValueQueues.clear();
	}

	public MemoryInputRecordReader getReader() {
		return new MemoryInputRecordReader(this);
	}

	boolean isOpen() {
		return open;
	}

	static class AsyncValueQueues implements AsyncPropertyVisitor {
		final List<AsyncProperty.Text> textValues;
		final List<AsyncProperty.PlaceholderText> placeholderTextValues;

		AsyncValueQueues() {
			textValues = new LinkedList<InputProperty.AsyncProperty.Text>();
			placeholderTextValues = new LinkedList<InputProperty.AsyncProperty.PlaceholderText>();
		}

		public void clear() {
			textValues.clear();
			placeholderTextValues.clear();
		}

		@Override
		public void visitText(Text text) {
			textValues.add(text);
		}

		@Override
		public void visitPlaceholderText(PlaceholderText text) {
			placeholderTextValues.add(text);
		}

		public void storeValue(AsyncProperty value) {
			value.accept(this);
		}

		public AsyncValueQueues copy() {
			AsyncValueQueues copy = new AsyncValueQueues();
			copy.textValues.addAll(textValues);
			copy.placeholderTextValues.addAll(placeholderTextValues);
			return copy;
		}

		public AsyncValueQueues set(AsyncValueQueues original) {
			textValues.clear();
			textValues.addAll(original.textValues);
			placeholderTextValues.clear();
			placeholderTextValues.addAll(original.placeholderTextValues);
			return this;
		}
	}

	List<SyncProperty> getSyncValues() {
		return syncValues;
	}

	AsyncValueQueues getAsyncValueQueues() {
		return asyncValueQueues;
	}

	StaticProperties getStaticValues() {
		return staticValues;
	}

	RecordProperties getRecordProperties() {
		return recordProperties;
	}

	@Override
	public void writeRecordProperties(RecordProperties properties) {
		recordProperties.set(properties);
	}
}
