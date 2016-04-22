package test.com.simukappu.coherence.entity;

import java.io.IOException;
import java.io.Serializable;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

/**
 * Child class as test entity class which belongs to father class.
 */
public class TestChildEntity implements Serializable, Cloneable, PortableObject {

	private static final long serialVersionUID = 1L;

	int parentId;
	String name;
	int age;

	/**
	 * Default constructor.
	 */
	public TestChildEntity() {
		super();
	}

	/**
	 * Constructor using fields.
	 * 
	 * @param parentId
	 *            Parent's id
	 * @param name
	 *            Child's name
	 * @param age
	 *            Child's age
	 */
	public TestChildEntity(int parentId, String name, int age) {
		super();
		this.parentId = parentId;
		this.name = name;
		this.age = age;
	}

	@Override
	/**
	 * Overridden public clone method
	 */
	public TestChildEntity clone() {
		try {
			return (TestChildEntity) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
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
		result = prime * result + age;
		result = prime * result + parentId;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		TestChildEntity other = (TestChildEntity) obj;
		if (age != other.age)
			return false;
		if (parentId != other.parentId)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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
		builder.append("TestChildEntity [parentId=");
		builder.append(parentId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", age=");
		builder.append(age);
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
		parentId = reader.readInt(0);
		name = reader.readString(1);
		age = reader.readInt(2);
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
		writer.writeInt(0, parentId);
		writer.writeString(1, name);
		writer.writeInt(2, age);
	}

}
