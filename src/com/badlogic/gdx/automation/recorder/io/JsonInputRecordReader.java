package com.badlogic.gdx.automation.recorder.io;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.badlogic.gdx.Input.Orientation;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue.Text;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValueVisitor;
import com.badlogic.gdx.automation.recorder.InputValue.StaticValues;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue.Accelerometer;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue.Button;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue.KeyEvent;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue.KeyPressed;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue.Pointer;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue.PointerEvent;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValueVisitor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;

/**
 * Simple implementation of a {@link InputRecordReader} reading input data from
 * a json formatted file, most likely generated using
 * {@link JsonInputRecordWriter}.
 * 
 * @author Lukas Böhm
 */
public class JsonInputRecordReader extends JsonInputRecord implements
		InputRecordReader {
	private final StaticValues staticValues;
	private final JsonReader reader;

	private JsonValue syncValues;
	private JsonValue asyncValues;

	public JsonInputRecordReader(FileHandle input) {
		super(input);
		reader = new JsonReader();
		staticValues = readStaticValues(new StaticValues());
		readSyncValues();
		readAsyncValues();
	}

	private StaticValues readStaticValues(StaticValues values) {
		JsonValue json = reader.parse(staticValuesFile.reader());
		values.accelerometerAvailable = json
				.getBoolean("accelerometerAvailable");
		values.compassAvailable = json.getBoolean("compassAvailable");
		values.hasMultitouch = json.getBoolean("hasMultitouch");
		values.keyboardAvailable = json.getBoolean("keyboardAvailable");
		values.nativeOrientation = Orientation.valueOf((json
				.getString("nativeOrientation")));
		values.onscreenKeyboard = json.getBoolean("onscreenKeyboard");
		values.vibrator = json.getBoolean("vibrator");
		return values;
	}

	private void readSyncValues() {
		syncValues = reader.parse(syncValuesFile.reader());
	}

	private void readAsyncValues() {
		asyncValues = reader.parse(asyncValuesFile.reader());
	}

	@Override
	public Iterator<Text> getTextIterator() {
		return new AsyncFilterIterator<Text>(Text.class, "Text");
	}

	@Override
	public Iterator<PlaceholderText> getPlaceholderTextIterator() {
		return new AsyncFilterIterator<PlaceholderText>(PlaceholderText.class,
				"PlaceholderText");
	}

	private class AsyncFilterIterator<T extends AsyncValue> implements
			Iterator<T> {
		private final Class<?> filter;
		private final String name;
		private final JsonIterator it;
		private final ValueBuilder builder;
		private T next;

		public AsyncFilterIterator(Class<?> classFilter, String namefilter) {
			builder = new ValueBuilder();
			filter = classFilter;
			name = namefilter;
			it = asyncValues.iterator();
			findNext();
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public T next() {
			if (next == null) {
				throw new NoSuchElementException();
			}
			T current = next;
			findNext();
			return current;
		}

		@SuppressWarnings("unchecked")
		private void findNext() {
			JsonValue nextJson = null;
			JsonValue testedJson;
			while (it.hasNext()) {
				testedJson = it.next();
				if (testedJson.getString("class").equals(name)) {
					nextJson = testedJson;
					break;
				}
			}

			if (nextJson == null) {
				next = null;
			} else {
				try {
					next = (T) filter.newInstance();
					builder.build(next, nextJson);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private class ValueBuilder implements AsyncValueVisitor {
			private JsonValue json;

			public void build(AsyncValue val, JsonValue json) {
				this.json = json;
				val.accept(this);
			}

			@Override
			public void visitText(Text text) {
				text.input = json.getString("input");
			}

			@Override
			public void visitPlaceholderText(PlaceholderText text) {
				text.input = json.getString("input");
			}

		}
	}

	@Override
	public Iterator<SyncValue> getSyncValueIterator() {
		return new SyncIterator();
	}

	private class SyncIterator implements Iterator<SyncValue> {
		private final JsonIterator it = syncValues.iterator();
		private final ValueBuilder builder = new ValueBuilder();

		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public SyncValue next() {
			JsonValue val = it.next();
			String clazz = val.getString("class");
			SyncValue result = null;
			if (clazz.equals("Accelerometer")) {
				result = new SyncValue.Accelerometer();
			} else if (clazz.equals("KeyPressed")) {
				result = new SyncValue.KeyPressed();
			} else if (clazz.equals("PointerEvent")) {
				result = new SyncValue.PointerEvent();
			} else if (clazz.equals("KeyEvent")) {
				result = new SyncValue.KeyEvent();
			} else if (clazz.equals("Orientation")) {
				result = new SyncValue.Orientation();
			} else if (clazz.equals("Pointer")) {
				result = new SyncValue.Pointer();
			} else if (clazz.equals("Button")) {
				result = new SyncValue.Button();
			}
			builder.build(result, val);
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private class ValueBuilder implements SyncValueVisitor {
			private JsonValue json;

			public void build(SyncValue val, JsonValue json) {
				if (val == null) {
					return;
				}
				this.json = json;
				val.timeDelta = json.getLong("timeDelta");
				val.accept(this);
			}

			@Override
			public void visitAccelerometer(Accelerometer accelerometer) {
				accelerometer.accelerometerX = json.getLong("accelerometerX");
				accelerometer.accelerometerY = json.getLong("accelerometerY");
				accelerometer.accelerometerZ = json.getLong("accelerometerZ");
			}

			@Override
			public void visitKeyPressed(KeyPressed keyPressed) {
				keyPressed.keyCode = json.getInt("keyCode");
				keyPressed.type = KeyPressed.Type.valueOf(json
						.getString("type"));
			}

			@Override
			public void visitPointerEvent(PointerEvent pointerEvent) {
				pointerEvent.button = json.getInt("button");
				pointerEvent.pointer = json.getInt("pointer");
				pointerEvent.scrollAmount = json.getInt("scrollAmount");
				pointerEvent.type = PointerEvent.Type.valueOf(json
						.getString("type"));
				pointerEvent.x = json.getInt("x");
				pointerEvent.y = json.getInt("y");
			}

			@Override
			public void visitKeyEvent(KeyEvent keyEvent) {
				keyEvent.keyChar = json.getString("keyChar").charAt(0);
				keyEvent.keyCode = json.getInt("keyCode");
				keyEvent.type = KeyEvent.Type.valueOf(json.getString("type"));
			}

			@Override
			public void visitOrientation(SyncValue.Orientation orientation) {
				orientation.azimuth = json.getFloat("azimuth");
				orientation.orientation = json.getInt("orientation");
				orientation.pitch = json.getFloat("pitch");
				orientation.roll = json.getFloat("roll");
				JsonValue matrixJson = json.get("rotationMatrix");
				float[] matrix = new float[16];
				int i = 0;
				for (JsonValue value : matrixJson) {
					matrix[i] = value.asFloat();
					i++;
				}
				orientation.rotationMatrix = matrix;
			}

			@Override
			public void visitPointer(Pointer pointer) {
				pointer.deltaX = json.getInt("deltaX");
				pointer.deltaY = json.getInt("deltaY");
				pointer.pointer = json.getInt("pointer");
				pointer.x = json.getInt("x");
				pointer.y = json.getInt("y");
			}

			@Override
			public void visitButton(Button button) {
				button.button0 = json.getBoolean("button0");
				button.button1 = json.getBoolean("button1");
				button.button2 = json.getBoolean("button2");
			}
		}
	}

	@Override
	public StaticValues getStaticValues() {
		return staticValues;
	}

	public void reset() {
		readStaticValues(staticValues);
		readSyncValues();
		readAsyncValues();
	}

	@Override
	public void close() {
	}

}
