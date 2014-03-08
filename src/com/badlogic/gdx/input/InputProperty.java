package com.badlogic.gdx.input;

public abstract class InputProperty {
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
}
