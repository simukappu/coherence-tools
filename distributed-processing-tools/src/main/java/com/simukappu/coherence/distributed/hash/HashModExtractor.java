package com.simukappu.coherence.distributed.hash;

import java.io.IOException;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.util.extractor.AbstractExtractor;

/**
 * Extractor implementation which returns modulo of the hash code of the target
 * object.
 * 
 * @author Shota Yamazaki
 *
 * @param <T>
 *            Target key class or value class to extract
 */
public class HashModExtractor<T> extends AbstractExtractor<T, Integer> implements PortableObject {

	/**
	 * UID for Serializable interface
	 */
	private static final long serialVersionUID = -270566873961038021L;

	/**
	 * Total number of distributed members (divisor of the hash value)
	 */
	private int divisor;

	/**
	 * Default constructor
	 */
	public HashModExtractor() {
		super();
		this.divisor = 1;
	}

	/**
	 * Constructor using fields
	 * 
	 * @param divisor
	 *            Total number of distributed members (divisor of the hash
	 *            value)
	 */
	public HashModExtractor(int divisor) {
		super();
		this.divisor = divisor;
	}

	/**
	 * Constructor using fields and target
	 * 
	 * @param nTarget
	 *            For parent ReflectionExtractor
	 * @param divisor
	 *            Total number of distributed members (divisor of the hash
	 *            value)
	 */
	public HashModExtractor(int nTarget, int divisor) {
		super();
		super.m_nTarget = nTarget;
		this.divisor = divisor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tangosol.util.ValueExtractor#extract(java.lang.Object)
	 */
	@Override
	public Integer extract(T oTarget) {
		return (oTarget.hashCode() % this.divisor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tangosol.util.extractor.ReflectionExtractor#readExternal(com.tangosol
	 * .io.pof.PofReader)
	 */
	@Override
	public void readExternal(PofReader reader) throws IOException {
		m_nTarget = reader.readInt(0);
		divisor = reader.readInt(1);
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
		writer.writeInt(0, m_nTarget);
		writer.writeInt(1, divisor);
	}

}
