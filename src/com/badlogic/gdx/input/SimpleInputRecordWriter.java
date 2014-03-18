package com.badlogic.gdx.input;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.input.InputValue.AsyncValue;
import com.badlogic.gdx.input.InputValue.StaticValues;
import com.badlogic.gdx.input.InputValue.SyncValue;
import com.badlogic.gdx.input.InputValue.SyncValue.Accelerometer;
import com.badlogic.gdx.input.InputValue.SyncValue.Button;
import com.badlogic.gdx.input.InputValue.SyncValue.KeyEvent;
import com.badlogic.gdx.input.InputValue.SyncValue.KeyPressed;
import com.badlogic.gdx.input.InputValue.SyncValue.Orientation;
import com.badlogic.gdx.input.InputValue.SyncValue.Pointer;
import com.badlogic.gdx.input.InputValue.SyncValue.PointerEvent;
import com.badlogic.gdx.input.InputValue.SyncValueVisitor;

public class SimpleInputRecordWriter implements InputRecordWriter {
	private final SyncValuesHandler handler = new SyncValuesHandler();

	public SimpleInputRecordWriter(FileHandle output) {

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeStaticValues(StaticValues values) {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeSyncValues(SyncValue values) {
		values.accept(handler);
	}

	@Override
	public void writeAsyncValues(AsyncValue values) {
		// TODO Auto-generated method stub

	}

	private class SyncValuesHandler implements SyncValueVisitor {

		@Override
		public void visitAccelerometer(Accelerometer accelerometer) {
			System.out.println("SHAKE");
		}

		@Override
		public void visitKeyPressed(KeyPressed keyPressed) {
			System.out.println("PRESSED");
		}

		@Override
		public void visitPointerEvent(PointerEvent pointerEvent) {
			System.out.println("MOUSE");
		}

		@Override
		public void visitKeyEvent(KeyEvent keyEvent) {
			System.out.println("KEY");
		}

		@Override
		public void visitOrientation(Orientation orientation) {
			System.out.println("ROLLED");
		}

		@Override
		public void visitPointer(Pointer pointer) {
			System.out.println("MOVED");
		}

		@Override
		public void visitButton(Button button) {
			System.out.println("CLICKED");
		}

	}
}
