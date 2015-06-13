package test.tool.coherence.entity;

import java.io.Serializable;

/**
 * Child class as test entity class which belongs to father class.
 */
public class TestChildEntity implements Serializable, Cloneable {

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result + parentId;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

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

}
