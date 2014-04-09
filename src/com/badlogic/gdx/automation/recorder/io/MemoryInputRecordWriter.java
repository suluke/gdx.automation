package com.badlogic.gdx.automation.recorder.io;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.automation.recorder.InputValue;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue.Text;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValueVisitor;
import com.badlogic.gdx.automation.recorder.InputValue.StaticValues;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue;

/**
 * Simple writer backend primarily designed for testing
 * 
 */
public class MemoryInputRecordWriter implements InputRecordWriter {
	private static final String LOG_TAG = "MemoryInputRecordWriter";

	private boolean open = false;

	private final LinkedList<SyncValue> syncValues;
	private final AsyncValueQueues asyncValueQueues;
	private final StaticValues staticValues;

	public MemoryInputRecordWriter() {
		syncValues = new LinkedList<InputValue.SyncValue>();
		asyncValueQueues = new AsyncValueQueues();
		staticValues = new StaticValues();
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
	public void writeStaticValues(StaticValues values) throws IOException {
		if (!open) {
			throw new IOException("Cannot write to closed writer");
		}
		staticValues.set(values);
	}

	@Override
	public void writeSyncValues(SyncValue values) throws IOException {
		if (!open) {
			throw new IOException("Cannot write to closed writer");
		}
		syncValues.add(values);
	}

	@Override
	public void writeAsyncValues(AsyncValue value) throws IOException {
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

	static class AsyncValueQueues implements AsyncValueVisitor {
		final List<AsyncValue.Text> textValues;
		final List<AsyncValue.PlaceholderText> placeholderTextValues;

		AsyncValueQueues() {
			textValues = new LinkedList<InputValue.AsyncValue.Text>();
			placeholderTextValues = new LinkedList<InputValue.AsyncValue.PlaceholderText>();
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

		public void storeValue(AsyncValue value) {
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

	List<SyncValue> getSyncValues() {
		return syncValues;
	}

	AsyncValueQueues getAsyncValueQueues() {
		return asyncValueQueues;
	}

	StaticValues getStaticValues() {
		return staticValues;
	}
}
