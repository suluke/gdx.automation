package com.badlogic.gdx.automation.recorder;

import java.util.Map;

/**
 * Generic implementation of {@link Map.Entry} used in {@link SparseArray}
 * 
 * @param <K>
 * @param <V>
 * 
 * @author Lukas Böhm
 */
class MapEntry<K, V> implements java.util.Map.Entry<K, V> {

	private K k;
	private V v;

	public MapEntry() {

	}

	/**
	 * @param key
	 * @param value
	 */
	public MapEntry(final K key, final V value) {
		k = key;
		v = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((k == null) ? 0 : k.hashCode());
		result = prime * result + ((v == null) ? 0 : v.hashCode());
		return result;
	}

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
		MapEntry other = (MapEntry) obj;
		if (k == null) {
			if (other.k != null) {
				return false;
			}
		} else if (!k.equals(other.k)) {
			return false;
		}
		if (v == null) {
			if (other.v != null) {
				return false;
			}
		} else if (!v.equals(other.v)) {
			return false;
		}
		return true;
	}

	@Override
	public K getKey() {
		return k;
	}

	@Override
	public V getValue() {
		return v;
	}

	@Override
	public V setValue(V value) {
		V oldV = v;
		v = value;
		return oldV;
	}

	public K setKey(K key) {
		K oldK = k;
		k = key;
		return oldK;
	}

}
