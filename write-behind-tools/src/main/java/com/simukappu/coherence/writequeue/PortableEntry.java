package com.simukappu.coherence.writequeue;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map.Entry;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

public class PortableEntry<K, V> implements Entry<K, V>, Serializable, PortableObject {

	/**
	 * Serial version used in Serializable interface
	 */
	private static final long serialVersionUID = 421283175624112384L;

	private K key;
	private V value;

	public PortableEntry() {
		super();
	}

	public PortableEntry(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}

	@Override
	public void readExternal(PofReader reader) throws IOException {
		key = reader.readObject(0);
		value = reader.readObject(1);
	}

	@Override
	public void writeExternal(PofWriter writer) throws IOException {
		writer.writeObject(0, key);
		writer.writeObject(1, value);
	}

}
