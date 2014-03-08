package com.badlogic.gdx.input;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Keys;

/**
 * Helper class to get the sets of pressed keys from the different input types.
 * Uses reflection to retrieve the collections of pressed keys. Currently only
 * desktop and android supported.
 * 
 * @author Lukas Böhm
 * 
 */
class EventBufferAccessHelper {
	private static final ArrayList<KeyEvent> keyEvents = new ArrayList<KeyEvent>();
	private static final ArrayList<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	private static final SparseArray<Boolean> pressedKeys = new SparseArray<Boolean>();

	static enum KeyState {
		KEY_DOWN, KEY_UP, KEY_TYPED;
		public static KeyState mapAndroid(int i) {
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

		public static KeyState mapDesktop(int i) {
			// luckily they have the same implementation
			return mapAndroid(i);
		}
	}

	static class KeyEvent {
		long timeStamp;
		KeyState type;
		int keyCode;
		char keyChar;
	}

	static enum TouchState {
		TOUCH_DOWN, TOUCH_UP, TOUCH_DRAGGED, TOUCH_SCROLLED, TOUCH_MOVED;
		public static TouchState mapAndroid(int i) {
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

		public static TouchState mapDesktop(int i) {
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

	static class TouchEvent {
		long timeStamp;
		TouchState type;
		int x;
		int y;
		int scrollAmount;
		int button;
		int pointer;
	}

	private EventBufferAccessHelper() {
	}

	public static List<KeyEvent> accessKeyEvents(Input input) {
		if (input instanceof TextInputTracker) {
			input = ((TextInputTracker) input).getProxiedInput();
		}
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			callMethod("updateKeyboard", input, null, null);
		}
		@SuppressWarnings("unchecked")
		List<Object> inputKeyEvents = (List<Object>) accessField(
				getField(input.getClass(), "keyEvents"), input);
		synchronized (keyEvents) {
			keyEvents.clear();
			for (Object event : inputKeyEvents) {
				KeyEvent e = new KeyEvent();
				e.keyChar = (Character) accessField(
						getField(event.getClass(), "keyChar"), event);
				e.keyCode = (Integer) accessField(
						getField(event.getClass(), "keyCode"), event);
				e.timeStamp = (Long) accessField(
						getField(event.getClass(), "timeStamp"), event);
				if (Gdx.app.getType() == ApplicationType.Android) {
					e.type = KeyState.mapAndroid((Integer) accessField(
							getField(event.getClass(), "type"), event));
				} else if (Gdx.app.getType() == ApplicationType.Desktop) {
					e.type = KeyState.mapDesktop((Integer) accessField(
							getField(event.getClass(), "type"), event));
				} else {
					throw new IllegalStateException(
							"Recorder is not supporting backend "
									+ Gdx.app.getType());
				}
				keyEvents.add(e);
			}
		}
		return keyEvents;
	}

	public static List<TouchEvent> accessTouchEvents(Input input) {
		if (input instanceof TextInputTracker) {
			input = ((TextInputTracker) input).getProxiedInput();
		}
		@SuppressWarnings("unchecked")
		List<Object> inputTouchEvents = (List<Object>) accessField(
				getField(input.getClass(), "touchEvents"), input);
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			callMethod("updateMouse", input, null, null);
		}
		synchronized (touchEvents) {
			touchEvents.clear();
			for (Object event : inputTouchEvents) {
				TouchEvent e = new TouchEvent();
				e.x = (Integer) accessField(getField(event.getClass(), "x"),
						event);
				e.y = (Integer) accessField(getField(event.getClass(), "y"),
						event);
				e.timeStamp = (Long) accessField(
						getField(event.getClass(), "timeStamp"), event);
				e.pointer = (Integer) accessField(
						getField(event.getClass(), "pointer"), event);
				if (Gdx.app.getType() == ApplicationType.Desktop) {
					e.scrollAmount = (Integer) accessField(
							getField(event.getClass(), "scrollAmount"), event);
					e.button = (Integer) accessField(
							getField(event.getClass(), "button"), event);
					e.type = TouchState.mapDesktop((Integer) accessField(
							getField(event.getClass(), "type"), event));
				} else {
					e.type = TouchState.mapAndroid((Integer) accessField(
							getField(event.getClass(), "type"), event));
				}
			}
		}
		return touchEvents;
	}

	public static void copyKeyEvents(Input input, List<KeyEvent> copyInto) {
		copyInto.clear();
		synchronized (keyEvents) {
			Collections.copy(accessKeyEvents(input), copyInto);
		}
	}

	public static void copyTouchEvents(Input input, List<TouchEvent> copyInto) {
		copyInto.clear();
		synchronized (touchEvents) {
			Collections.copy(accessTouchEvents(input), copyInto);
		}
	}

	public static SparseArray<Boolean> accessPressedKeys(Input input) {
		synchronized (pressedKeys) {
			pressedKeys.clear();

			Object synchronizer = getKeySynchronizer(input);
			synchronized (synchronizer) {
				copyPressedKeys(input);
			}
		}
		return pressedKeys;
	}

	private static void copyPressedKeys(Input input) {
		// TODO no actual input type check. Just assuming on app type
		if (input instanceof TextInputTracker) {
			input = ((TextInputTracker) input).getProxiedInput();
		}
		if (Gdx.app.getType() == ApplicationType.Android) {
			@SuppressWarnings("unchecked")
			IntMap<Object> keys = (IntMap<Object>) accessField(
					getField(input.getClass(), "keys"), input);
			Keys keysKeys = keys.keys();
			while (keysKeys.hasNext) {
				pressedKeys.append(keysKeys.next(), Boolean.TRUE);
			}
		} else if (Gdx.app.getType() == ApplicationType.Desktop) {
			java.nio.ByteBuffer keyDownBuffer = (ByteBuffer) accessField(
					getField(getClass("org.lwjgl.input.Keyboard"),
							"keyDownBuffer"), null);
			for (int i = 0; i < keyDownBuffer.capacity(); i++) {
				if (keyDownBuffer.get(i) != 0) {
					pressedKeys.put(i, Boolean.TRUE);
				}
			}
		} else {
			throw new IllegalStateException("Unsupported application type: "
					+ Gdx.app.getType());
		}
	}

	private static Object getKeySynchronizer(Input input) {
		if (input instanceof TextInputTracker) {
			input = ((TextInputTracker) input).getProxiedInput();
		}
		Object synchronizer = null;
		if (Gdx.app.getType() == ApplicationType.Android) {
			// Android: The AndroidInput itself
			if (getClass("com.badlogic.gdx.backends.android.AndroidInput")
					.isAssignableFrom(input.getClass())) {
				synchronizer = input;
			} else {
				throw new IllegalStateException(
						"Unsupported or unexpected input type: "
								+ Gdx.input.getClass().getName());
			}
		} else if (Gdx.app.getType() == ApplicationType.Desktop) {
			// LWJGL: OpenGLPackageAccess.global_lock
			Class<?> openGLPackageAccess = getClass("org.lwjgl.input.OpenGLPackageAccess");
			Field global_lock = getField(openGLPackageAccess, "global_lock");
			synchronizer = accessField(global_lock, null);
		} else {
			throw new IllegalStateException("Unsupported application type: "
					+ Gdx.app.getType());
		}

		return synchronizer;
	}

	public static void copyPressedKeys(Input input,
			SparseArray<Boolean> copyInto) {
		copyInto.clear();
		synchronized (pressedKeys) {
			copyInto.putAll(accessPressedKeys(input));
		}
	}

	/*
	 * Reflection helper methods
	 */

	private static Class<?> getClass(String name) {
		Class<?> result;
		try {
			result = Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Coluld not get class: " + name);
		}
		return result;
	}

	private static Field getField(Class<?> clazz, String name) {
		Field field;
		try {
			field = clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException("Field does not exist: " + name
					+ " in class " + clazz.getName());
		} catch (SecurityException e) {
			throw e;
		}
		return field;
	}

	private static Object accessField(Field f, Object instance) {
		Object field;
		try {
			f.setAccessible(true);
			field = f.get(instance);
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("IllegalAccess: "
					+ e.getLocalizedMessage());
		}
		return field;
	}

	private static Object callMethod(String name, Object o,
			Class<?>[] parameterTypes, Object[] args) {
		try {
			Method method = o.getClass()
					.getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
			return method.invoke(o, args);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("No such method: " + name + " in "
					+ o.getClass());
		} catch (SecurityException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(
					"Method enforces Java access control: " + name + " in "
							+ o.getClass());
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw new IllegalStateException("Called method " + name + " in "
					+ o.getClass() + " threw exception");
		}
	}
}
