package com.simukappu.coherence.writequeue;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map.Entry;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

/**
 * Custom java.util.Map.Entry implementation class supporting POF serializer
 * 
 * @author Shota Yamazaki
 *
 * @param <K>
 *            Key class
 * @param <V>
 *            Value class
 */
public class PortableEntry<K, V> implements Entry<K, V>, Serializable, PortableObject {

	/**
	 * Serial version used in Serializable interface
	 */
	private static final long serialVersionUID = 421283175624112384L;

	private K key;
	private V value;

	/**
	 * Default constructor
	 */
	public PortableEntry() {
		super();
	}

	/**
	 * Constructor using fields
	 * 
	 * @param key
	 *            Key of the entry
	 * @param value
	 *            Value of the entry
	 */
	public PortableEntry(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map.Entry#getKey()
	 */
	@Override
	public K getKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map.Entry#getValue()
	 */
	@Override
	public V getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map.Entry#setValue(java.lang.Object)
	 */
	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tangosol.io.pof.PortableObject#readExternal(com.tangosol.io.pof.
	 * PofReader)
	 */
	@Override
	public void readExternal(PofReader reader) throws IOException {
		key = reader.readObject(0);
		value = reader.readObject(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tangosol.io.pof.PortableObject#writeExternal(com.tangosol.io.pof.
	 * PofWriter)
	 */
	@Override
	public void writeExternal(PofWriter writer) throws IOException {
		writer.writeObject(0, key);
		writer.writeObject(1, value);
	}

}
