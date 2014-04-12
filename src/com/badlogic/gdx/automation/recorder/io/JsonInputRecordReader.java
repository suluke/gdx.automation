package com.badlogic.gdx.automation.recorder.io;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.badlogic.gdx.Input.Orientation;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty.PlaceholderText;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty.Text;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncPropertyVisitor;
import com.badlogic.gdx.automation.recorder.InputProperty.StaticProperties;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.Accelerometer;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.Button;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.KeyEvent;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.KeyPressed;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.Pointer;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.PointerEvent;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncPropertyVisitor;
import com.badlogic.gdx.automation.recorder.RecordProperties;
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
	private final RecordProperties recordProperties;
	private final StaticProperties staticProperties;
	private final JsonReader reader;

	private JsonValue syncValues;
	private JsonValue asyncValues;

	public JsonInputRecordReader(FileHandle input) {
		super(input);
		reader = new JsonReader();
		recordProperties = readRecordProperties(new RecordProperties());
		staticProperties = readStaticValues(new StaticProperties());
		readSyncProperties();
		readAsyncProperties();
	}

	private StaticProperties readStaticValues(StaticProperties values) {
		JsonValue json = reader.parse(staticPropertiesFile.reader());
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

	private RecordProperties readRecordProperties(RecordProperties properties) {
		JsonValue json = reader.parse(recordPropertiesFile.reader());
		properties.absouluteCoords = json.getBoolean("absouluteCoords");
		return recordProperties;
	}

	private void readSyncProperties() {
		syncValues = reader.parse(syncPropertiesFile.reader());
	}

	private void readAsyncProperties() {
		asyncValues = reader.parse(asyncPropertiesFile.reader());
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

	private class AsyncFilterIterator<T extends AsyncProperty> implements
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

		private class ValueBuilder implements AsyncPropertyVisitor {
			private JsonValue json;

			public void build(AsyncProperty val, JsonValue json) {
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
	public Iterator<SyncProperty> getSyncValueIterator() {
		return new SyncIterator();
	}

	private class SyncIterator implements Iterator<SyncProperty> {
		private final JsonIterator it = syncValues.iterator();
		private final ValueBuilder builder = new ValueBuilder();

		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public SyncProperty next() {
			JsonValue val = it.next();
			String clazz = val.getString("class");
			SyncProperty result = null;
			if (clazz.equals("Accelerometer")) {
				result = new SyncProperty.Accelerometer();
			} else if (clazz.equals("KeyPressed")) {
				result = new SyncProperty.KeyPressed();
			} else if (clazz.equals("PointerEvent")) {
				result = new SyncProperty.PointerEvent();
			} else if (clazz.equals("KeyEvent")) {
				result = new SyncProperty.KeyEvent();
			} else if (clazz.equals("Orientation")) {
				result = new SyncProperty.Orientation();
			} else if (clazz.equals("Pointer")) {
				result = new SyncProperty.Pointer();
			} else if (clazz.equals("Button")) {
				result = new SyncProperty.Button();
			}
			builder.build(result, val);
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private class ValueBuilder implements SyncPropertyVisitor {
			private JsonValue json;

			public void build(SyncProperty val, JsonValue json) {
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
			public void visitOrientation(SyncProperty.Orientation orientation) {
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
	public StaticProperties getStaticValues() {
		return staticProperties;
	}

	public void reset() {
		readRecordProperties(recordProperties);
		readStaticValues(staticProperties);
		readSyncProperties();
		readAsyncProperties();
	}

	@Override
	public void close() {
	}

	@Override
	public RecordProperties getRecordProperties() {
		return recordProperties;
	}

}
