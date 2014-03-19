package com.badlogic.gdx.input.recorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

class SparseArray<T> implements Map<Integer, T>, Iterable<T> {
	private final MapEntry<Integer, T> finder;
	private final Comparator<Map.Entry<Integer, T>> comp;
	private final LinkedList<Map.Entry<Integer, T>> l;
	private boolean valid = false;
	private Map.Entry<Integer, T>[] entries;

	public SparseArray() {
		comp = new Comparator<Map.Entry<Integer, T>>() {
			@Override
			public int compare(Map.Entry<Integer, T> lhs,
					Map.Entry<Integer, T> rhs) {
				return lhs.getKey() - rhs.getKey();
			}
		};
		l = new LinkedList<Map.Entry<Integer, T>>();
		finder = new MapEntry<Integer, T>();
	}

	public void invalidate() {
		valid = false;
	}

	@SuppressWarnings("unchecked")
	public void validate() {
		if (!valid) {
			valid = true;
			entries = l.toArray(new MapEntry[l.size()]);
		}
	}

	@Override
	public void clear() {
		l.clear();
		invalidate();
	}

	public T put(int key, T val) {
		int pos = findKeyInList(key);
		if (pos < 0) {
			// insert
			invalidate();
			pos = -pos - 1;
			l.add(pos, new MapEntry<Integer, T>(key, val));
			return null;
		} else {
			// replace
			Map.Entry<Integer, T> elm = l.get(pos);
			T previous = elm.getValue();
			elm.setValue(val);
			if (previous != val) {
				invalidate();
			}
			return previous;
		}
	}

	public T append(int key, T val) {
		// TODO improve
		return put(key, val);
	}

	public T get(int key) {
		int pos = findKey(key);
		if (pos < 0) {
			return null;
		}
		return l.get(pos).getValue();
	}

	public int keyAt(int i) {
		validate();
		return entries[i].getKey();
	}

	public T valueAt(int i) {
		validate();
		return entries[i].getValue();
	}

	@Override
	public int size() {
		return l.size();
	}

	private int findKeyInList(int key) {
		finder.setKey(key);
		int result = Collections.binarySearch(l, finder, comp);
		return result;
	}

	private int findKey(int key) {
		validate();
		finder.setKey(key);
		int result = Arrays.binarySearch(entries, finder, comp);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((l == null) ? 0 : l.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		SparseArray other = (SparseArray) obj;
		if (l == null) {
			if (other.l != null) {
				return false;
			}
		} else if (!l.equals(other.l)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean containsKey(Object key) {
		return findKey((Integer) key) >= 0;
	}

	@Override
	public boolean containsValue(Object value) {
		for (Map.Entry<? extends Integer, ? extends T> entry : l) {
			if (entry.getValue().equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<Integer, T>> entrySet() {
		return new LinkedHashSet<Map.Entry<Integer, T>>(l);
	}

	@Override
	public T get(Object key) {
		return get((int) ((Integer) key));
	}

	@Override
	public boolean isEmpty() {
		return l.isEmpty();
	}

	@Override
	public Set<Integer> keySet() {
		validate();
		Set<Integer> result = new LinkedHashSet<Integer>();
		for (Map.Entry<Integer, T> entry : entries) {
			result.add(entry.getKey());
		}
		return result;
	}

	@Override
	public T put(Integer key, T value) {
		return put((int) key, value);
	}

	@Override
	public void putAll(Map<? extends Integer, ? extends T> m) {
		invalidate();
		for (Map.Entry<? extends Integer, ? extends T> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public T remove(Object key) {
		int keyInt = ((Integer) key);
		Map.Entry<? extends Integer, ? extends T> entry;
		for (int i = 0; i < l.size(); i++) {
			entry = l.get(i);
			if (entry.getKey() == keyInt) {
				invalidate();
				return l.remove(i).getValue();
			}
		}
		return null;
	}

	@Override
	public Collection<T> values() {
		validate();
		ArrayList<T> values = new ArrayList<T>(l.size());
		for (Map.Entry<Integer, T> entry : entries) {
			values.add(entry.getValue());
		}
		return values;
	}

	@Override
	public Iterator<T> iterator() {
		return new SparseIterator();
	}

	private class SparseIterator implements Iterator<T> {
		private int i = 0;
		private final int entryCount;

		public SparseIterator() {
			SparseArray.this.validate();
			entryCount = entries.length;
		}

		@Override
		public boolean hasNext() {
			return i < entryCount;
		}

		@Override
		public T next() {
			if (entries.length != entryCount) {
				throw new ConcurrentModificationException();
			}
			if (hasNext()) {
				return entries[i++].getValue();
			} else {
				throw new NoSuchElementException();
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
