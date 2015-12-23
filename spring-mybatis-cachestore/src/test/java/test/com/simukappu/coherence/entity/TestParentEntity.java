package test.com.simukappu.coherence.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Father class as test entity class which has many child class.
 */
public class TestParentEntity implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	int id;
	String name;
	int age;
	List<TestChildEntity> children = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public TestParentEntity() {
		super();
	}

	/**
	 * Constructor using fields except children.
	 * 
	 * @param id
	 *            Parent's id
	 * @param name
	 *            Parent's name
	 * @param age
	 *            Parent's age
	 */
	public TestParentEntity(int id, String name, int age) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
	}

	/**
	 * Constructor using fields including children.
	 * 
	 * @param id
	 *            Parent's id
	 * @param name
	 *            Parent's name
	 * @param age
	 *            Parent's age
	 * @param childlen
	 *            The list of children
	 */
	public TestParentEntity(int id, String name, int age,
			List<TestChildEntity> childlen) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.children = childlen;
	}

	/**
	 * Add child entity to children list
	 * 
	 * @param child
	 *            Child entity to add to the list of children
	 */
	public void addChild(TestChildEntity child) {
		if (!(children instanceof ArrayList)) {
			children = new ArrayList<>(children);
		}
		children.add(child);
	}

	/**
	 * Clear children list
	 */
	public void clearChildren() {
		children = new ArrayList<>();
	}

	@Override
	/**
	 * Overridden public clone method
	 */
	public TestParentEntity clone() {
		try {
			TestParentEntity cloneParent = (TestParentEntity) super.clone();
			cloneParent.clearChildren();
			for (TestChildEntity child : children) {
				cloneParent.addChild(child.clone());
			}
			return cloneParent;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		for (TestChildEntity child : children) {
			child.setParentId(id);
		}
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

	public List<TestChildEntity> getChildren() {
		return children;
	}

	public void setChildren(List<TestChildEntity> childlen) {
		if (childlen == null) {
			this.children.clear();
		} else {
			this.children = childlen;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
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
		TestParentEntity other = (TestParentEntity) obj;
		if (age != other.age)
			return false;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.containsAll(other.children))
			return false;
		if (id != other.id)
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
		builder.append("TestParentEntity [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", age=");
		builder.append(age);
		builder.append(", children=");
		builder.append(new HashSet<TestChildEntity>(children));
		builder.append("]");
		return builder.toString();
	}

}
