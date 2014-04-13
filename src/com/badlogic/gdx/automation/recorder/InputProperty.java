package com.badlogic.gdx.automation.recorder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Orientation;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty.PlaceholderText;
import com.badlogic.gdx.automation.recorder.InputProperty.AsyncProperty.Text;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.Accelerometer;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.Button;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.KeyEvent;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.KeyPressed;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.Pointer;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.PointerEvent;

/**
 * Parent class of all (groups of) properties that make up an {@link InputState}
 * . Since all properties are merely aggregations of primitives (you would call
 * them "structs" in C) they are not declared in their own files but as inner
 * classes of this class.
 * 
 * @author Lukas Böhm
 */
public abstract class InputProperty {
	private InputProperty() {
	}

	/**
	 * Base class of all input values that are directly passed to the program.
	 * This includes input events that are usually received using
	 * {@link InputProcessor}, since the processing of them also happens in the
	 * main thread.
	 * 
	 * @author Lukas Böhm
	 * 
	 */
	public static abstract class SyncProperty {
		/**
		 * Enum helping to create the selection flags used by some methods like
		 * {@link InputState#set(InputState, int)}. By providing a
		 * {@link Type#key key} that needs to be OR'ed together only the
		 * corresponding {@link SyncProperty}s will be considered in such a
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
		 * Method to accept {@link SyncPropertyVisitor}s, so that concrete child
		 * classes of {@link SyncProperty} can execute child-specific code of
		 * the concrete visitor implementation. This is great to omit
		 * <code>instancof</code> or <code>getClass()</code> by having the jvm
		 * doing the dispatching
		 * 
		 * @param visitor
		 */
		public abstract void accept(SyncPropertyVisitor visitor);

		/**
		 * Milliseconds passed since the last {@link InputProperty} changed
		 */
		public long timeDelta;

		/**
		 * A struct to store the three axis values of the accelerometer in.
		 * 
		 * @author Lukas Böhm
		 * 
		 */
		public static class Accelerometer extends SyncProperty {
			public float accelerometerX;
			public float accelerometerY;
			public float accelerometerZ;

			@Override
			public void accept(SyncPropertyVisitor visitor) {
				visitor.visitAccelerometer(this);
			}
		}

		/**
		 * A struct storing all information there is regarding a device's
		 * orientation. This includes the three gyroscope values as well as the
		 * screen orientation and the orientation matrix.
		 * 
		 * @author Lukas Böhm
		 * 
		 */
		public static class Orientation extends SyncProperty {
			public float roll;
			public float pitch;
			public float azimuth;
			public int orientation;
			public float[] rotationMatrix = new float[16];

			@Override
			public void accept(SyncPropertyVisitor visitor) {
				visitor.visitOrientation(this);
			}

		}

		/**
		 * A class indicating that a key was either pressed or has stopped being
		 * pressed.
		 * 
		 * @author Lukas Böhm
		 * 
		 */
		public static class KeyPressed extends SyncProperty {
			/**
			 * Describes whether a key press has begun or finished.
			 * 
			 * @author Lukas Böhm
			 * 
			 */
			public enum Type {
				PRESS, RELEASE
			}

			public Type type;
			public int keyCode;

			@Override
			public void accept(SyncPropertyVisitor visitor) {
				visitor.visitKeyPressed(this);
			}

		}

		/**
		 * Similar to {@link KeyPressed}, but closer to the high-level key
		 * events of the platforms libGdx targets.
		 * 
		 * @author Lukas Böhm
		 * 
		 */
		public static class KeyEvent extends SyncProperty {
			/**
			 * Description of the exact KeyEvent type, as libGdx' target
			 * platform events use them.
			 * 
			 * @author Lukas Böhm
			 * 
			 */
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
			public void accept(SyncPropertyVisitor visitor) {
				visitor.visitKeyEvent(this);
			}
		}

		/**
		 * Event whose implementation closely resembles the platform specific
		 * mouse events of libGdx' target platforms.
		 * 
		 * @author Lukas Böhm
		 * 
		 */
		public static class PointerEvent extends SyncProperty {
			/**
			 * Superset of all types of pointer events (called "touch" in
			 * libGdx) that the different target platforms involve.
			 * 
			 * @author Lukas Böhm
			 * 
			 */
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
			public float x;
			public float y;
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
			public void accept(SyncPropertyVisitor visitor) {
				visitor.visitPointerEvent(this);
			}
		}

		/**
		 * A class to aggregate all position information that is emitted by the
		 * use of a single touch pointer or mouse, not considering buttons or
		 * touch states.
		 * 
		 * @author Lukas Böhm
		 * 
		 */
		public static class Pointer extends SyncProperty {
			public int pointer;
			public float x;
			public float y;
			public float deltaX;
			public float deltaY;

			@Override
			public void accept(SyncPropertyVisitor visitor) {
				visitor.visitPointer(this);
			}
		}

		/**
		 * Class to store the state of the three possible buttons of a mouse.
		 * 
		 * @author Lukas Böhm
		 * 
		 */
		public static class Button extends SyncProperty {
			public boolean button0;
			public boolean button1;
			public boolean button2;

			@Override
			public void accept(SyncPropertyVisitor visitor) {
				visitor.visitButton(this);
			}
		}
	}

	/**
	 * An interface to enable the visitor pattern on {@link SyncProperty}s. This
	 * means, it helps to have the JVM do the dispatching of the concrete
	 * underlying {@link SyncProperty} type of an instance. Using this interface
	 * also adds compiler support for adding new types, since extending the
	 * interface automatically breaks concrete implementations that are not
	 * adapted for the newly added types.
	 * 
	 */
	public static interface SyncPropertyVisitor {
		void visitAccelerometer(Accelerometer accelerometer);

		void visitKeyPressed(KeyPressed keyPressed);

		void visitPointerEvent(PointerEvent pointerEvent);

		void visitKeyEvent(KeyEvent keyEvent);

		void visitOrientation(
				com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty.Orientation orientation);

		void visitPointer(Pointer pointer);

		void visitButton(Button button);
	}

	/**
	 * Abstract class parenting all classes of input, which can not be read
	 * directly from {@link Input Gdx.input} but instead are supplied to the
	 * program using specified callbacks.
	 * 
	 * @author Lukas Böhm
	 * 
	 */
	public static abstract class AsyncProperty {
		/**
		 * Method to accept {@link AsyncPropertyVisitor}s, so that concrete
		 * child classes of {@link AsyncProperty} can execute child-specific
		 * code of the concrete visitor implementation. This is great to omit
		 * <code>instancof</code> or <code>getClass()</code> by having the jvm
		 * doing the dispatching
		 * 
		 * @param visitor
		 */
		public abstract void accept(AsyncPropertyVisitor visitor);

		/**
		 * Description of the outcome of a
		 * {@link Input#getTextInput(com.badlogic.gdx.Input.TextInputListener, String, String)
		 * getText} request. If the outcome is null it means that the request
		 * was canceled.
		 * 
		 * @author Lukas Böhm
		 * 
		 */
		public static class Text extends AsyncProperty {
			public Text(String text) {
				input = text;
			}

			public Text() {

			}

			public String input;

			@Override
			public void accept(AsyncPropertyVisitor visitor) {
				visitor.visitText(this);
			}
		}

		/**
		 * Description of the outcome of a
		 * {@link Input#getPlaceholderTextInput(com.badlogic.gdx.Input.TextInputListener, String, String)
		 * getPlaceholderText} request. If the outcome is null it means that the
		 * request was canceled.
		 * 
		 * @author Lukas Böhm
		 * 
		 */
		public static class PlaceholderText extends AsyncProperty {
			public PlaceholderText(String text) {
				input = text;
			}

			public PlaceholderText() {

			}

			public String input;

			@Override
			public void accept(AsyncPropertyVisitor visitor) {
				visitor.visitPlaceholderText(this);
			}
		}
	}

	/**
	 * An interface to enable the visitor pattern on {@link AsyncProperty}s.
	 * This means, it helps to have the JVM do the dispatching of the concrete
	 * underlying {@link AsyncProperty} type of an instance. Using this
	 * interface also adds compiler support for adding new types, since
	 * extending the interface automatically breaks concrete implementations
	 * that are not adapted for the newly added types.
	 * 
	 */
	public static interface AsyncPropertyVisitor {
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
	public static class StaticProperties extends InputProperty {
		public boolean accelerometerAvailable;
		public boolean compassAvailable;
		public boolean keyboardAvailable;
		public boolean onscreenKeyboard;
		public boolean vibrator;
		public boolean hasMultitouch;
		public Orientation nativeOrientation;

		public void set(StaticProperties other) {
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
	 * Copies the {@link StaticProperties} from the current {@link Gdx libGdx}
	 * environment.
	 * 
	 * @return the {@link StaticProperties} of the current environment
	 */
	public static StaticProperties getCurrentStaticValues() {
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
