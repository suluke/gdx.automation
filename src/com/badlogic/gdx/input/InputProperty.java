package com.badlogic.gdx.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Orientation;
import com.badlogic.gdx.Input.Peripheral;

public abstract class InputProperty {

	public enum Types {
		touchCoords(1), buttons(2), touchEvents(4), keyEvents(8), pressedKeys(
				16), orientation(32);

		int key;

		private Types(int key) {
			this.key = key;
		}
	}

	public static class Accelerometer extends InputProperty {
		public float x;
		public float y;
		public float z;
	}

	public static class TouchPosition extends InputProperty {
		public int x;
		public int y;
		public int deltaX;
		public int deltaY;
	}

	public static class MouseButton extends InputProperty {
		public boolean button0;
		public boolean button1;
		public boolean button2;
	}

	public static class StaticProperties extends InputProperty {
		public boolean accelerometerAvailable;
		public boolean compassAvailable;
		public boolean keyboardAvailable;
		public boolean onscreenKeyboard;
		public boolean vibrator;
		public boolean hasMultitouch;
		Orientation nativeOrientation;
	}

	public static StaticProperties getCurrentStaticProperties() {
		StaticProperties result = new StaticProperties();
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
