package com.badlogic.demos.automation;

import java.util.Iterator;

/**
 * Utility construct, which simplifies saving the last 30 steps immensely.
 */
public class RingBuffer<T> implements Iterable<T> {

	private final T[] buffer;
	private int head = 0;
	private int items = 0;

	/**
	 * Creates a ringbuffer with a given size.
	 * 
	 * @param size
	 *            the size with which the ringbuffer will be created. 30 in our
	 *            case.
	 */
	@SuppressWarnings("unchecked")
	public RingBuffer(int size) throws IllegalArgumentException {
		if (size < 1) {
			throw new IllegalArgumentException(
					"Can't create Ringbuffer with size zero or smaller");
		}
		buffer = (T[]) new Object[size];
	}

	/**
	 * Places the given object on top of the ringbuffer.
	 * 
	 * @param obj
	 *            the object which will be placed upon the ringbuffer.
	 */
	public T push(T obj) {
		if (items < buffer.length) {
			items++;
		}
		T overwritten = buffer[head];
		buffer[head] = obj;
		head = (head + 1) % buffer.length;
		return overwritten;
	}

	/**
	 * Removes the topmost object of the ringbuffer and returns it.
	 * 
	 * @return the object which used to be on top of the ringbuffer.
	 * @throws Exception
	 */
	public T pop() throws Exception {
		if (items == 0) {
			throw new Exception("");
		}
		head = (head + buffer.length - 1) % buffer.length;
		items--;
		return buffer[head];
	}

	public T get(int index) {
		if (index < 0 || index >= items) {
			return null;
		}
		int off = head - items;
		int id = index + off;
		if (id < 0) {
			return buffer[buffer.length + id];
		} else {
			return buffer[id];
		}
	}

	public int size() {
		return items;
	}

	private class It implements Iterator<T> {
		private final int orig_size;

		private int traversed = 0;
		private int pos;

		public It() {
			orig_size = items;
			pos = (head - items + buffer.length) % buffer.length;
		}

		@Override
		public boolean hasNext() {
			return traversed != orig_size;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				throw new IndexOutOfBoundsException();
			}
			T next = buffer[pos];
			pos = (pos + 1) % buffer.length;
			traversed++;
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	@Override
	public Iterator<T> iterator() {
		return new It();
	}
}
