package com.badlogic.gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Orientation;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.input.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.input.InputValue.AsyncValue.Text;
import com.badlogic.gdx.input.InputValue.SyncValue.Button;
import com.badlogic.gdx.input.InputValue.SyncValue.Pointer;

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
		public enum Types {
			POINTERS(1), BUTTONS(2), TOUCH_EVENTS(4), KEY_EVENTS(8), KEYS_PRESSED(
					16), ORIENTATION(32);

			int key;

			private Types(int key) {
				this.key = key;
			}
		}

		public abstract void accept(SyncValueVisitor visitor);

		/**
		 * Milliseconds passed since the last {@link InputValue} changed
		 */
		public long timeDelta;

		public static class Orientation extends SyncValue {
			public float accelerometerX;
			public float accelerometerY;
			public float accelerometerZ;

			@Override
			public void accept(SyncValueVisitor visitor) {
				visitor.visitOrientation(this);
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
		void visitOrientation(
				com.badlogic.gdx.input.InputValue.SyncValue.Orientation orientation);

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
		Orientation nativeOrientation;
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
