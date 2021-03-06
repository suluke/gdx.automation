package com.badlogic.gdx.automation.recorder.io;

import java.io.IOException;
import java.io.Writer;

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
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.Orientation;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.Pointer;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.PointerEvent;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncPropertyVisitor;
import com.badlogic.gdx.automation.recorder.RecordProperties;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

/**
 * Simple implementation of an {@link InputRecordWriter} that writes given user
 * input data to a set of three files (one each for static, sync and async
 * values) using the {@link JsonWriter json} format.
 * 
 */
public class JsonInputRecordWriter extends JsonInputRecord implements
		InputRecordWriter {
	private final SyncValuesHandler syncHandler = new SyncValuesHandler();
	private final AsyncValuesHandler asyncHandler = new AsyncValuesHandler();
	private Writer syncFileWriter;
	private JsonWriter syncJsonWriter;
	private Writer asyncFileWriter;
	private JsonWriter asyncJsonWriter;

	public JsonInputRecordWriter(FileHandle output) throws IOException {
		super(output);
	}

	@Override
	public void flush() throws IOException {
		if (syncJsonWriter != null) {
			syncJsonWriter.flush();
		}
		if (asyncJsonWriter != null) {
			asyncJsonWriter.flush();
		}
	}

	@Override
	public void writeStaticValues(StaticProperties values) throws IOException {
		Writer staticFileWriter = staticPropertiesFile.writer(false);
		JsonWriter writer = new JsonWriter(staticFileWriter);
		writer.setOutputType(OutputType.minimal);

		writer.object();
		writer.set("accelerometerAvailable", values.accelerometerAvailable);
		writer.set("compassAvailable", values.compassAvailable);
		writer.set("hasMultitouch", values.hasMultitouch);
		writer.set("keyboardAvailable", values.keyboardAvailable);
		writer.set("nativeOrientation", values.nativeOrientation);
		writer.set("onscreenKeyboard", values.onscreenKeyboard);
		writer.set("vibrator", values.vibrator);

		writer.close();
	}

	@Override
	public void writeSyncValues(SyncProperty values) {
		values.accept(syncHandler);
	}

	private class SyncValuesHandler implements SyncPropertyVisitor {

		@Override
		public void visitAccelerometer(Accelerometer accelerometer) {
			try {
				syncJsonWriter.object();
				syncJsonWriter.set("class", "Accelerometer");
				syncJsonWriter.set("accelerometerX",
						accelerometer.accelerometerX);
				syncJsonWriter.set("accelerometerY",
						accelerometer.accelerometerY);
				syncJsonWriter.set("accelerometerZ",
						accelerometer.accelerometerZ);
				syncJsonWriter.set("timeDelta", accelerometer.timeDelta);
				syncJsonWriter.pop();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void visitKeyPressed(KeyPressed keyPressed) {
			try {
				syncJsonWriter.object();
				syncJsonWriter.set("class", "KeyPressed");
				syncJsonWriter.set("type", keyPressed.type);
				syncJsonWriter.set("keyCode", keyPressed.keyCode);
				syncJsonWriter.set("timeDelta", keyPressed.timeDelta);
				syncJsonWriter.pop();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void visitPointerEvent(PointerEvent pointerEvent) {
			try {
				syncJsonWriter.object();
				syncJsonWriter.set("class", "PointerEvent");
				syncJsonWriter.set("button", pointerEvent.button);
				syncJsonWriter.set("pointer", pointerEvent.pointer);
				syncJsonWriter.set("scrollAmount", pointerEvent.scrollAmount);
				syncJsonWriter.set("type", pointerEvent.type);
				syncJsonWriter.set("x", pointerEvent.x);
				syncJsonWriter.set("y", pointerEvent.y);
				syncJsonWriter.set("timeDelta", pointerEvent.timeDelta);
				syncJsonWriter.pop();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void visitKeyEvent(KeyEvent keyEvent) {
			try {
				syncJsonWriter.object();
				syncJsonWriter.set("class", "KeyEvent");
				syncJsonWriter.set("keyChar", keyEvent.keyChar);
				syncJsonWriter.set("keyCode", keyEvent.keyCode);
				syncJsonWriter.set("type", keyEvent.type);
				syncJsonWriter.set("timeDelta", keyEvent.timeDelta);
				syncJsonWriter.pop();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void visitOrientation(Orientation orientation) {
			try {
				syncJsonWriter.object();
				syncJsonWriter.set("class", "Orientation");
				syncJsonWriter.set("azimuth", orientation.azimuth);
				syncJsonWriter.set("orientation", orientation.orientation);
				syncJsonWriter.set("pitch", orientation.pitch);
				syncJsonWriter.set("roll", orientation.roll);

				syncJsonWriter.array();
				for (float i : orientation.rotationMatrix) {
					syncJsonWriter.value(i);
				}
				syncJsonWriter.pop();

				syncJsonWriter.set("timeDelta", orientation.timeDelta);
				syncJsonWriter.pop();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void visitPointer(Pointer pointer) {
			try {
				syncJsonWriter.object();
				syncJsonWriter.set("class", "Pointer");
				syncJsonWriter.set("deltaX", pointer.deltaX);
				syncJsonWriter.set("deltaY", pointer.deltaY);
				syncJsonWriter.set("pointer", pointer.pointer);
				syncJsonWriter.set("x", pointer.x);
				syncJsonWriter.set("y", pointer.y);
				syncJsonWriter.set("timeDelta", pointer.timeDelta);
				syncJsonWriter.pop();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void visitButton(Button button) {
			try {
				syncJsonWriter.object();
				syncJsonWriter.set("class", "Button");
				syncJsonWriter.set("button0", button.button0);
				syncJsonWriter.set("button1", button.button1);
				syncJsonWriter.set("button2", button.button2);
				syncJsonWriter.set("timeDelta", button.timeDelta);
				syncJsonWriter.pop();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void writeAsyncValues(AsyncProperty values) {
		values.accept(asyncHandler);
	}

	private class AsyncValuesHandler implements AsyncPropertyVisitor {

		@Override
		public void visitText(Text text) {
			try {
				asyncJsonWriter.object();
				asyncJsonWriter.set("class", "Text");
				asyncJsonWriter.set("input", text.input);
				asyncJsonWriter.pop();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void visitPlaceholderText(PlaceholderText text) {
			try {
				asyncJsonWriter.object();
				asyncJsonWriter.set("class", "PlaceholderText");
				asyncJsonWriter.set("input", text.input);
				asyncJsonWriter.pop();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Override
	public void close() throws IOException {
		if (syncJsonWriter != null) {
			syncJsonWriter.close();
		}
		if (asyncJsonWriter != null) {
			asyncJsonWriter.close();
		}
	}

	@Override
	public void open() throws IOException {
		close();
		syncFileWriter = syncPropertiesFile.writer(false);
		asyncFileWriter = asyncPropertiesFile.writer(false);

		syncJsonWriter = new JsonWriter(syncFileWriter);
		asyncJsonWriter = new JsonWriter(asyncFileWriter);

		syncJsonWriter.setOutputType(OutputType.minimal);
		asyncJsonWriter.setOutputType(OutputType.minimal);

		syncJsonWriter.array();
		asyncJsonWriter.array();
	}

	@Override
	public void writeRecordProperties(RecordProperties properties)
			throws IOException {
		Writer propertiesWriter = recordPropertiesFile.writer(false);
		JsonWriter writer = new JsonWriter(propertiesWriter);
		writer.setOutputType(OutputType.minimal);

		writer.object();
		writer.set("absouluteCoords", properties.absouluteCoords);

		writer.close();
	}
}
