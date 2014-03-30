package com.badlogic.demos.automation;

import junit.framework.TestCase;

import org.junit.Test;

public class RingBufferTest extends TestCase {
	@Test
	public void testIterator() {
		testIterator(7, 7, 7);
		testIterator(5, 7, 7);
		testIterator(7, 5, 5);
		testIterator(5, 7, 5);
		testIterator(5, 7, 3);
		testIterator(7, 12, 367);
	}

	private void testIterator(int capacity, int objects, int fill) {
		RingBuffer<Integer> r = new RingBuffer<Integer>(capacity);
		Integer[] os = new Integer[objects];
		// initialize integers
		for (int i = 0; i < objects; i++) {
			Integer o = new Integer(i);
			os[i] = o;
		}

		for (int i = 0; i < fill; i++) {
			r.push(os[i % objects]);
		}

		int lastIn = fill % objects;
		int firstIn;
		if (fill < capacity) {
			firstIn = (lastIn - fill + objects) % objects;
		} else {
			firstIn = (lastIn - capacity + objects) % objects;
		}
		int i = firstIn;
		for (Object o : r) {
			assertSame("Access " + (i - firstIn) + ":", os[i % objects], o);
			i++;
		}
		assertEquals(capacity > fill ? fill : capacity, i - firstIn);
	}

	public void testGet() {
		RingBuffer<Object> r = new RingBuffer<Object>(5);
		Object[] os = new Object[7];
		for (int i = 0; i < 7; i++) {
			Object o = new Object();
			r.push(o);
			os[i] = o;
		}
		assertSame(os[2], r.get(0));
	}
}
