package com.badlogic.gdx.input.recorder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Orientation;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.input.recorder.EventBufferAccessHelper.KeyState;
import com.badlogic.gdx.input.recorder.EventBufferAccessHelper.PointerState;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.input.recorder.InputValue.AsyncValue.Text;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.Accelerometer;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.Button;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.KeyEvent;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.KeyPressed;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.Pointer;
import com.badlogic.gdx.input.recorder.InputValue.SyncValue.PointerEvent;

/**
 * Parent class of all (groups of) properties that make up an {@link InputState}
 * . Since all properties are merely aggregations of primitives (you would call
 * them "structs" in C) they are not declared in their own files but as inner
 * classes of this class.
 * 
 */
public abstract class InputValue {
	private InputValue() {
	}

	public static abstract class SyncValue {
		public enum Type {
			POINTERS(1), BUTTONS(2), POINTER_EVENTS(4), KEY_EVENTS(8), KEYS_PRESSED(
					16), ORIENTATION(32);

			/**
			 * binary flag unique for each {@link Type} to be able to specify
			 * any combination of Types without having to specify an array and
			 * without the overhead to search in it. Just "&" the key to an int.
			 */
			int key;

			private Type(int key) {
				this.key = key;
			}
		}

		public abstract void accept(SyncValueVisitor visitor);

		/**
		 * Milliseconds passed since the last {@link InputValue} changed
		 */
		public long timeDelta;

		public static class Accelerometer extends SyncValue {
			public float accelerometerX;
			public float accelerometerY;
			public float accelerometerZ;

			@Override
			public void accept(SyncValueVisitor visitor) {
				visitor.visitAccelerometer(this);
			}
		}

		public static class Orientation extends SyncValue {
			public float roll;
			public float pitch;
			public float azimuth;
			public int orientation;
			public float[] rotationMatrix = new float[16];

			@Override
			public void accept(SyncValueVisitor visitor) {
				visitor.visitOrientation(this);
			}

		}

		public static class KeyPressed extends SyncValue {

			public int keyCode;

			@Override
			public void accept(SyncValueVisitor visitor) {
				visitor.visitKeyPressed(this);
			}

		}

		public static class KeyEvent extends SyncValue {
			public KeyState type;
			public int keyCode;
			public char keyChar;

			KeyEvent(
					com.badlogic.gdx.input.recorder.EventBufferAccessHelper.KeyEvent event) {
				type = event.type;
				keyCode = event.keyCode;
				keyChar = event.keyChar;
			}

			@Override
			public void accept(SyncValueVisitor visitor) {
				visitor.visitKeyEvent(this);
			}
		}

		public static class PointerEvent extends SyncValue {
			public PointerState type;
			public int x;
			public int y;
			public int scrollAmount;
			public int button;
			public int pointer;

			PointerEvent(
					com.badlogic.gdx.input.recorder.EventBufferAccessHelper.PointerEvent event) {
				type = event.type;
				x = event.x;
				y = event.y;
				scrollAmount = event.scrollAmount;
				button = event.button;
				pointer = event.pointer;
			}

			@Override
			public void accept(SyncValueVisitor visitor) {
				visitor.visitPointerEvent(this);
			}
		}

		public static class Pointer extends SyncValue {
			public int pointer;
			public int x;
			public int y;
			public int deltaX;
			public int deltaY;

			@Override
			public void accept(SyncValueVisitor visitor) {
				visitor.visitPointer(this);
			}
		}

		public static class Button extends SyncValue {
			public boolean button0;
			public boolean button1;
			public boolean button2;

			@Override
			public void accept(SyncValueVisitor visitor) {
				visitor.visitButton(this);
			}
		}
	}

	public static interface SyncValueVisitor {
		void visitAccelerometer(Accelerometer accelerometer);

		void visitKeyPressed(KeyPressed keyPressed);

		void visitPointerEvent(PointerEvent pointerEvent);

		void visitKeyEvent(KeyEvent keyEvent);

		void visitOrientation(
				com.badlogic.gdx.input.recorder.InputValue.SyncValue.Orientation orientation);

		void visitPointer(Pointer pointer);

		void visitButton(Button button);
	}

	public static abstract class AsyncValue {
		public abstract void accept(AsyncValueVisitor visitor);

		public static class Text extends AsyncValue {
			public Text(String text) {
				input = text;
			}

			public Text() {

			}

			String input;

			@Override
			public void accept(AsyncValueVisitor visitor) {
				visitor.visitText(this);
			}
		}

		public static class PlaceholderText extends AsyncValue {
			public PlaceholderText(String text) {
				input = text;
			}

			public PlaceholderText() {

			}

			String input;

			@Override
			public void accept(AsyncValueVisitor visitor) {
				visitor.visitPlaceholderText(this);
			}
		}
	}

	public static interface AsyncValueVisitor {
		void visitText(Text text);

		void visitPlaceholderText(PlaceholderText text);
	}

	public static class StaticValues extends InputValue {
		public boolean accelerometerAvailable;
		public boolean compassAvailable;
		public boolean keyboardAvailable;
		public boolean onscreenKeyboard;
		public boolean vibrator;
		public boolean hasMultitouch;
		public Orientation nativeOrientation;
	}

	public static StaticValues getCurrentStaticValues() {
		StaticValues result = new StaticValues();
		result.accelerometerAvailable = Gdx.input
				.isPeripheralAvailable(Peripheral.Accelerometer);
		result.compassAvailable = Gdx.input
				.isPeripheralAvailable(Peripheral.Compass);
		result.keyboardAvailable = Gdx.input
				.isPeripheralAvailable(Peripheral.HardwareKeyboard);
		result.onscreenKeyboard = Gdx.input
				.isPeripheralAvailable(Peripheral.OnscreenKeyboard);
		result.vibrator = Gdx.input.isPeripheralAvailable(Peripheral.Vibrator);
		result.hasMultitouch = Gdx.input
				.isPeripheralAvailable(Peripheral.MultitouchScreen);
		result.nativeOrientation = Gdx.input.getNativeOrientation();
		return result;
	}
}
