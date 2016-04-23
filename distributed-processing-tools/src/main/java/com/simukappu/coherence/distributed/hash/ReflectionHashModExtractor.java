package com.simukappu.coherence.distributed.hash;

import java.io.IOException;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.util.extractor.ReflectionExtractor;

/**
 * Extractor implementation which returns modulo of the hash code of the field
 * value extracted from the target object by parent ReflectionExtractor.
 * 
 * @author Shota Yamazaki
 *
 * @param <T>
 *            Target key class or value class to extract
 */
public class ReflectionHashModExtractor<T> extends ReflectionExtractor<T, Integer> {

	/**
	 * UID for Serializable interface
	 */
	private static final long serialVersionUID = 8350652654404321752L;

	/**
	 * Total number of distributed members (divisor of the hash value)
	 */
	private int divisor;

	/**
	 * Default constructor
	 */
	public ReflectionHashModExtractor() {
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
	public ReflectionHashModExtractor(int divisor) {
		super();
		this.divisor = divisor;
	}

	/**
	 * Constructor using fields
	 * 
	 * @param sMethod
	 *            For parent ReflectionExtractor
	 * @param divisor
	 *            Total number of distributed members (divisor of the hash
	 *            value)
	 */
	public ReflectionHashModExtractor(String sMethod, int divisor) {
		super(sMethod);
		this.divisor = divisor;
	}

	/**
	 * Constructor using fields
	 * 
	 * @param sMethod
	 *            For parent ReflectionExtractor
	 * @param aoParam
	 *            For parent ReflectionExtractor
	 * @param divisor
	 *            Total number of distributed members (divisor of the hash
	 *            value)
	 */
	public ReflectionHashModExtractor(String sMethod, Object[] aoParam, int divisor) {
		super(sMethod, aoParam);
		this.divisor = divisor;
	}

	/**
	 * Constructor using fields
	 * 
	 * @param sMethod
	 *            For parent ReflectionExtractor
	 * @param aoParam
	 *            For parent ReflectionExtractor
	 * @param nTarget
	 *            For parent ReflectionExtractor
	 * @param divisor
	 *            Total number of distributed members (divisor of the hash
	 *            value)
	 */
	public ReflectionHashModExtractor(String sMethod, Object[] aoParam, int nTarget, int divisor) {
		super(sMethod, aoParam, nTarget);
		this.divisor = divisor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tangosol.util.ValueExtractor#extract(java.lang.Object)
	 */
	@Override
	public Integer extract(T oTarget) {
		return (super.extract(oTarget).hashCode() % this.divisor);
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
		super.readExternal(reader);
		divisor = reader.readInt(5);
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
		super.writeExternal(writer);
		writer.writeInt(5, divisor);
	}

}
