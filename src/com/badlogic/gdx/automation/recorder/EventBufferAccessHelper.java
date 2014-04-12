package com.badlogic.gdx.automation.recorder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.automation.recorder.InputProperty.SyncProperty;
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
	static final ArrayList<KeyEvent> keyEvents = new ArrayList<KeyEvent>();
	static final ArrayList<PointerEvent> pointerEvents = new ArrayList<PointerEvent>();
	// TODO SparseArray is shit. Replace with 256 byte ByteBuffer like in lwjgl
	private static final SparseArray<Boolean> pressedKeys = new SparseArray<Boolean>();
	private static List<Object> inputKeyEvents = null;
	private static List<Object> inputPointerEvents = null;
	private static IntMap<Object> keysPressedAndroid = null;
	private static java.nio.ByteBuffer keysPressedDesktop = null;
	private static Input keyPressedFrom = null;
	private static Input pointerEventsFrom = null;
	private static Input keyEventsFrom = null;

	static class KeyEvent {
		public KeyEvent() {
		}

		public KeyEvent(SyncProperty.KeyEvent event) {
			keyChar = event.keyChar;
			keyCode = event.keyCode;
			type = event.type;
		}

		long timeStamp;
		SyncProperty.KeyEvent.Type type;
		int keyCode;
		char keyChar;
	}

	static class PointerEvent {
		public PointerEvent() {
		}

		public PointerEvent(SyncProperty.PointerEvent event) {
			button = event.button;
			pointer = event.pointer;
			scrollAmount = event.scrollAmount;
			type = event.type;
			x = (int) event.x;
			y = (int) event.y;
		}

		long timeStamp;
		SyncProperty.PointerEvent.Type type;
		int x;
		int y;
		int scrollAmount;
		int button;
		int pointer;
	}

	private EventBufferAccessHelper() {
	}

	public static List<KeyEvent> accessKeyEvents(Input input) {
		return accessKeyEvents(input, true);
	}

	/**
	 * 
	 * @param input
	 * @param update
	 *            if LWJGL events should be pulled before accessing the events.
	 *            This is necessary if you do not access the events between a
	 *            call to {@link LwjglInput#update update} and the end of
	 *            {@link LwjglInput#processEvents processEvents}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static List<KeyEvent> accessKeyEvents(Input input, boolean update) {
		while (input instanceof InputProxy) {
			input = ((InputProxy) input).getProxiedInput();
		}
		if (update && Gdx.app.getType() == ApplicationType.Desktop) {
			callMethod("updateKeyboard", input, null, null);
		}
		if (inputKeyEvents == null || keyEventsFrom != input) {
			inputKeyEvents = (List<Object>) accessField(
					getField(input.getClass(), "keyEvents"), input);
			keyEventsFrom = input;
		}
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
					e.type = SyncProperty.KeyEvent.Type
							.mapAndroid((Integer) accessField(
									getField(event.getClass(), "type"), event));
				} else if (Gdx.app.getType() == ApplicationType.Desktop) {
					e.type = SyncProperty.KeyEvent.Type
							.mapDesktop((Integer) accessField(
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

	public static List<PointerEvent> accessPointerEvents(Input input) {
		return accessPointerEvents(input, true);
	}

	/**
	 * 
	 * @param input
	 * @param update
	 *            if LWJGL events should be pulled before accessing the events.
	 *            This is necessary if you do not access the events between a
	 *            call to {@link LwjglInput#update update} and the end of
	 *            {@link LwjglInput#processEvents processEvents}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static List<PointerEvent> accessPointerEvents(Input input, boolean update) {
		while (input instanceof InputProxy) {
			input = ((InputProxy) input).getProxiedInput();
		}
		if (update && Gdx.app.getType() == ApplicationType.Desktop) {
			callMethod("updateMouse", input, null, null);
		}
		if (inputPointerEvents == null || pointerEventsFrom != input) {
			inputPointerEvents = (List<Object>) accessField(
					getField(input.getClass(), "touchEvents"), input);
			pointerEventsFrom = input;
		}
		synchronized (pointerEvents) {
			pointerEvents.clear();
			for (Object event : inputPointerEvents) {
				PointerEvent e = new PointerEvent();
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
					e.type = SyncProperty.PointerEvent.Type
							.mapDesktop((Integer) accessField(
									getField(event.getClass(), "type"), event));
				} else {
					e.type = SyncProperty.PointerEvent.Type
							.mapAndroid((Integer) accessField(
									getField(event.getClass(), "type"), event));
				}
				pointerEvents.add(e);
			}
		}
		return pointerEvents;
	}

	public static void copyKeyEvents(Input input, List<KeyEvent> copyInto,
			boolean update) {
		copyInto.clear();
		synchronized (keyEvents) {
			copyInto.addAll(accessKeyEvents(input, update));
		}
	}

	public static void copyPointerEvents(Input input,
			List<PointerEvent> copyInto, boolean update) {
		copyInto.clear();
		synchronized (pointerEvents) {
			copyInto.addAll(accessPointerEvents(input, update));
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

	@SuppressWarnings("unchecked")
	private static void copyPressedKeys(Input input) {
		// TODO no actual input type check. Just assuming on app type
		while (input instanceof InputProxy) {
			input = ((InputProxy) input).getProxiedInput();
		}
		if (Gdx.app.getType() == ApplicationType.Android) {
			if (keysPressedAndroid == null || keyPressedFrom != input) {
				keysPressedAndroid = (IntMap<Object>) accessField(
						getField(input.getClass(), "keys"), input);
				keyPressedFrom = input;
			}
			Keys keysKeys = keysPressedAndroid.keys();
			while (keysKeys.hasNext) {
				pressedKeys.append(keysKeys.next(), Boolean.TRUE);
			}
		} else if (Gdx.app.getType() == ApplicationType.Desktop) {
			if (keysPressedDesktop == null || keyPressedFrom != input) {
				keysPressedDesktop = (ByteBuffer) accessField(
						getField(getClass("org.lwjgl.input.Keyboard"),
								"keyDownBuffer"), null);
				keyPressedFrom = input;
			}
			for (int i = 0; i < keysPressedDesktop.capacity(); i++) {
				if (keysPressedDesktop.get(i) != 0) {
					pressedKeys.put(i, Boolean.TRUE);
				}
			}
		} else {
			throw new IllegalStateException("Unsupported application type: "
					+ Gdx.app.getType());
		}
	}

	private static Object getKeySynchronizer(Input input) {
		while (input instanceof InputProxy) {
			input = ((InputProxy) input).getProxiedInput();
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
