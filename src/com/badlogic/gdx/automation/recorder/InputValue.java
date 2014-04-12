package com.badlogic.gdx.automation.recorder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Orientation;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue.PlaceholderText;
import com.badlogic.gdx.automation.recorder.InputValue.AsyncValue.Text;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue.Accelerometer;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue.Button;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue.KeyEvent;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue.KeyPressed;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue.Pointer;
import com.badlogic.gdx.automation.recorder.InputValue.SyncValue.PointerEvent;

/**
 * Parent class of all (groups of) properties that make up an {@link InputState}
 * . Since all properties are merely aggregations of primitives (you would call
 * them "structs" in C) they are not declared in their own files but as inner
 * classes of this class.
 * 
 * @author Lukas Böhm
 */
public abstract class InputValue {
	private InputValue() {
	}

	/**
	 * 
	 * @author Lukas Böhm
	 * 
	 */
	public static abstract class SyncValue {
		/**
		 * Enum helping to create the selection flags used by some methods like
		 * {@link InputState#set(InputState, int)}. By providing a
		 * {@link Type#key key} that needs to be OR'ed together only the
		 * corresponding {@link SyncValue}s will be considered in such a
		 * process.
		 * 
		 * @author Lukas Böhm
		 * 
		 */
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

		/**
		 * Method to accept {@link SyncValueVisitor}s, so that concrete child
		 * classes of {@link SyncValue} can execute child-specific code of the
		 * concrete visitor implementation. This is great to omit
		 * <code>instancof</code> or <code>getClass()</code> by having the jvm
		 * doing the dispatching
		 * 
		 * @param visitor
		 */
		public abstract void accept(SyncValueVisitor visitor);

		/**
		 * Milliseconds passed since the last {@link InputValue} changed
		 */
		public long timeDelta;

		/**
		 * 
		 * @author Lukas Böhm
		 * 
		 */
		public static class Accelerometer extends SyncValue {
			public float accelerometerX;
			public float accelerometerY;
			public float accelerometerZ;

			@Override
			public void accept(SyncValueVisitor visitor) {
				visitor.visitAccelerometer(this);
			}
		}

		/**
		 * 
		 * @author Lukas Böhm
		 * 
		 */
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

		/**
		 * 
		 * @author Lukas Böhm
		 * 
		 */
		public static class KeyPressed extends SyncValue {
			public enum Type {
				PRESS, RELEASE
			}

			public Type type;
			public int keyCode;

			@Override
			public void accept(SyncValueVisitor visitor) {
				visitor.visitKeyPressed(this);
			}

		}

		/**
		 * 
		 * @author Lukas Böhm
		 * 
		 */
		public static class KeyEvent extends SyncValue {
			public enum Type {
				KEY_DOWN, KEY_UP, KEY_TYPED;
				static Type mapAndroid(int i) {
					switch (i) {
					case 0:
						return KEY_DOWN;
					case 1:
						return KEY_UP;
					case 2:
						return KEY_TYPED;
					default:
						throw new IllegalArgumentException(i
								+ " out of sensible KeyState range");
					}
				}

				static Type mapDesktop(int i) {
					// luckily they have the same implementation
					return mapAndroid(i);
				}
			}

			public Type type;
			public int keyCode;
			public char keyChar;

			public KeyEvent() {

			}

			KeyEvent(
					com.badlogic.gdx.automation.recorder.EventBufferAccessHelper.KeyEvent event) {
				type = event.type;
				keyCode = event.keyCode;
				keyChar = event.keyChar;
			}

			@Override
			public void accept(SyncValueVisitor visitor) {
				visitor.visitKeyEvent(this);
			}
		}

		/**
		 * 
		 * @author Lukas Böhm
		 * 
		 */
		public static class PointerEvent extends SyncValue {
			public enum Type {
				TOUCH_DOWN, TOUCH_UP, TOUCH_DRAGGED, TOUCH_SCROLLED, TOUCH_MOVED;
				static Type mapAndroid(int i) {
					switch (i) {
					case 0:
						return TOUCH_DOWN;
					case 1:
						return TOUCH_UP;
					case 2:
						return TOUCH_DRAGGED;
					default:
						throw new IllegalArgumentException(i
								+ " out of Android's sensible TouchState range");
					}
				}

				static Type mapDesktop(int i) {
					switch (i) {
					case 0:
						return TOUCH_DOWN;
					case 1:
						return TOUCH_UP;
					case 2:
						return TOUCH_DRAGGED;
					case 3:
						return TOUCH_SCROLLED;
					case 4:
						return TOUCH_MOVED;
					default:
						throw new IllegalArgumentException(i
								+ " out of Desktop's sensible TouchState range");
					}
				}
			}

			public Type type;
			public int x;
			public int y;
			public int scrollAmount;
			public int button;
			public int pointer;

			public PointerEvent() {

			}

			PointerEvent(
					com.badlogic.gdx.automation.recorder.EventBufferAccessHelper.PointerEvent event) {
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

		/**
		 * 
		 * @author Lukas Böhm
		 * 
		 */
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

		/**
		 * 
		 * @author Lukas Böhm
		 * 
		 */
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

	/**
	 * An interface to enable the visitor pattern on {@link SyncValue}s. This
	 * means, it helps to have the JVM do the dispatching of the concrete
	 * underlying {@link SyncValue} type of an instance. Using this interface
	 * also adds compiler support for adding new types, since extending the
	 * interface automatically breaks concrete implementations that are not
	 * adapted for the newly added types.
	 * 
	 */
	public static interface SyncValueVisitor {
		void visitAccelerometer(Accelerometer accelerometer);

		void visitKeyPressed(KeyPressed keyPressed);

		void visitPointerEvent(PointerEvent pointerEvent);

		void visitKeyEvent(KeyEvent keyEvent);

		void visitOrientation(
				com.badlogic.gdx.automation.recorder.InputValue.SyncValue.Orientation orientation);

		void visitPointer(Pointer pointer);

		void visitButton(Button button);
	}

	/**
	 * 
	 * @author Lukas Böhm
	 * 
	 */
	public static abstract class AsyncValue {
		/**
		 * Method to accept {@link AsyncValueVisitor}s, so that concrete child
		 * classes of {@link AsyncValue} can execute child-specific code of the
		 * concrete visitor implementation. This is great to omit
		 * <code>instancof</code> or <code>getClass()</code> by having the jvm
		 * doing the dispatching
		 * 
		 * @param visitor
		 */
		public abstract void accept(AsyncValueVisitor visitor);

		public static class Text extends AsyncValue {
			public Text(String text) {
				input = text;
			}

			public Text() {

			}

			public String input;

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

			public String input;

			@Override
			public void accept(AsyncValueVisitor visitor) {
				visitor.visitPlaceholderText(this);
			}
		}
	}

	/**
	 * An interface to enable the visitor pattern on {@link AsyncValue}s. This
	 * means, it helps to have the JVM do the dispatching of the concrete
	 * underlying {@link AsyncValue} type of an instance. Using this interface
	 * also adds compiler support for adding new types, since extending the
	 * interface automatically breaks concrete implementations that are not
	 * adapted for the newly added types.
	 * 
	 */
	public static interface AsyncValueVisitor {
		void visitText(Text text);

		void visitPlaceholderText(PlaceholderText text);
	}

	/**
	 * A struct to aggregate all properties that a libGdx environment can
	 * exhibit.
	 * 
	 * @author Lukas Böhm
	 * 
	 */
	public static class StaticValues extends InputValue {
		public boolean accelerometerAvailable;
		public boolean compassAvailable;
		public boolean keyboardAvailable;
		public boolean onscreenKeyboard;
		public boolean vibrator;
		public boolean hasMultitouch;
		public Orientation nativeOrientation;

		public void set(StaticValues other) {
			accelerometerAvailable = other.accelerometerAvailable;
			compassAvailable = other.compassAvailable;
			keyboardAvailable = other.keyboardAvailable;
			onscreenKeyboard = other.onscreenKeyboard;
			vibrator = other.vibrator;
			hasMultitouch = other.hasMultitouch;
			nativeOrientation = other.nativeOrientation;
		}
	}

	/**
	 * Copies the {@link StaticValues} from the current {@link Gdx libGdx}
	 * environment.
	 * 
	 * @return the {@link StaticValues} of the current environment
	 */
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
