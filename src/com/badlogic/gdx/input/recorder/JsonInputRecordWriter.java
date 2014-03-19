package com.badlogic.gdx.input.recorder;

import java.io.IOException;
import java.io.Writer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.Text;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValueVisitor;
import com.badlogic.gdx.input.recorder.InputValue.StaticValues;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.Accelerometer;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.Button;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.KeyEvent;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.KeyPressed;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.Orientation;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.Pointer;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.PointerEvent;
import com.badlogic.gdx.input.recorder.InputValue.SyncValueVisitor;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class JsonInputRecordWriter implements InputRecordWriter {
	private final SyncValuesHandler syncHandler = new SyncValuesHandler();
	private final AsyncValuesHandler asyncHandler = new AsyncValuesHandler();
	private final FileHandle outputFile;
	private final FileHandle syncOutputFile;
	private final FileHandle asyncOutputFile;
	private final FileHandle staticOutputFile;
	private Writer syncFileWriter;
	private JsonWriter syncJsonWriter;
	private Writer asyncFileWriter;
	private JsonWriter asyncJsonWriter;

	public JsonInputRecordWriter(FileHandle output) throws IOException {
		this.outputFile = output;
		// TODO be more graceful with existing files, care ore about the actual
		// FileHandle given (maybe merge on close) etc.
		syncOutputFile = Gdx.files.getFileHandle(
				outputFile.pathWithoutExtension() + "-sync.json",
				outputFile.type());
		asyncOutputFile = Gdx.files.getFileHandle(
				outputFile.pathWithoutExtension() + "-async.json",
				outputFile.type());
		staticOutputFile = Gdx.files.getFileHandle(
				outputFile.pathWithoutExtension() + "-static.json",
				outputFile.type());
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
	public void writeStaticValues(StaticValues values) throws IOException {
		Writer staticFileWriter = staticOutputFile.writer(false);
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
	public void writeSyncValues(SyncValue values) {
		values.accept(syncHandler);
	}

	private class SyncValuesHandler implements SyncValueVisitor {

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
	public void writeAsyncValues(AsyncValue values) {
		values.accept(asyncHandler);
	}

	private class AsyncValuesHandler implements AsyncValueVisitor {

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
		syncFileWriter = syncOutputFile.writer(false);
		asyncFileWriter = asyncOutputFile.writer(false);

		syncJsonWriter = new JsonWriter(syncFileWriter);
		asyncJsonWriter = new JsonWriter(asyncFileWriter);

		syncJsonWriter.setOutputType(OutputType.minimal);
		asyncJsonWriter.setOutputType(OutputType.minimal);

		syncJsonWriter.array();
		asyncJsonWriter.array();
	}
}
