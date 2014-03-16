package com.badlogic.gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Orientation;
import com.badlogic.gdx.Input.Peripheral;

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

	public static class SyncValue {
		public enum Types {
			touchCoords(1), buttons(2), touchEvents(4), keyEvents(8), pressedKeys(
					16), orientation(32);

			int key;

			private Types(int key) {
				this.key = key;
			}
		}

		/**
		 * Milliseconds passed since the last {@link InputValue} changed
		 */
		public long timeDelta;

		public static class Accelerometer extends SyncValue {
			public float x;
			public float y;
			public float z;
		}

		public static class TouchPosition extends SyncValue {
			public int pointer;
			public int x;
			public int y;
			public int deltaX;
			public int deltaY;
		}

		public static class MouseButton extends SyncValue {
			public boolean button0;
			public boolean button1;
			public boolean button2;
		}
	}

	public static class AsyncValue {
		public static class Text extends AsyncValue {
			public Text(String text) {
				input = text;
			}

			public Text() {

			}

			String input;
		}

		public static class PlaceholderText extends AsyncValue {
			public PlaceholderText(String text) {
				input = text;
			}

			public PlaceholderText() {

			}

			String input;
		}
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
