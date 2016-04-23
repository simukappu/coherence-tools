package test.com.simukappu.coherence.entity;

import java.io.IOException;
import java.io.Serializable;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

/**
 * Father class as test entity class which has many child class.
 */
public class TestEntity implements Serializable, Cloneable, PortableObject {

	private static final long serialVersionUID = 1L;

	int intId;
	double doubleId;
	String stringId;

	/**
	 * Default constructor.
	 */
	public TestEntity() {
		super();
	}

	/**
	 * Constructor using fields except children.
	 * 
	 * @param intId
	 *            Integer id
	 * @param doubleId
	 *            Double id
	 * @param stringId
	 *            String id
	 */
	public TestEntity(int intId, double doubleId, String stringId) {
		super();
		this.intId = intId;
		this.doubleId = doubleId;
		this.stringId = stringId;
	}

	/**
	 * Overridden public clone method
	 */
	@Override
	public TestEntity clone() {
		try {
			return (TestEntity) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

	public int getIntId() {
		return intId;
	}

	public void setIntId(int intId) {
		this.intId = intId;
	}

	public double getDoubleId() {
		return doubleId;
	}

	public void setDoubleId(double doubleId) {
		this.doubleId = doubleId;
	}

	public String getStringId() {
		return stringId;
	}

	public void setStringId(String stringId) {
		this.stringId = stringId;
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
		long temp;
		temp = Double.doubleToLongBits(doubleId);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + intId;
		result = prime * result + ((stringId == null) ? 0 : stringId.hashCode());
		result &= 0x7FFFFFFF;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestEntity other = (TestEntity) obj;
		if (Double.doubleToLongBits(doubleId) != Double.doubleToLongBits(other.doubleId))
			return false;
		if (intId != other.intId)
			return false;
		if (stringId == null) {
			if (other.stringId != null)
				return false;
		} else if (!stringId.equals(other.stringId))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TestEntity [intId=");
		builder.append(intId);
		builder.append(", doubleId=");
		builder.append(doubleId);
		builder.append(", stringId=");
		builder.append(stringId);
		builder.append("]");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tangosol.io.pof.PortableObject#readExternal(com.tangosol.io.pof.
	 * PofReader)
	 */
	@Override
	public void readExternal(PofReader reader) throws IOException {
		intId = reader.readInt(0);
		doubleId = reader.readDouble(1);
		stringId = reader.readString(2);
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
		writer.writeInt(0, intId);
		writer.writeDouble(1, doubleId);
		writer.writeString(2, stringId);
	}

}
