package gov.hhs.aspr.ms.taskit.core.testsupport.objects;

import java.util.Objects;

/**
 * Test class representing a class that wraps another object in it.
 * <p>
 * <b>Should NOT</b> be used outside of testing.
 * <p>
 */
public class TestObjectWrapper {
	private Object wrappedObject;

	/**
	 * @return the wrappedObject value
	 */
	public Object getWrappedObject() {
		return wrappedObject;
	}

	/**
	 * Sets the value of the wrappedObject variable.
	 * 
	 * @param wrappedObject the value to set
	 * 
	 * @throws RuntimeException if the wrappedObject is equal to this instance or is
	 *                          itself an instance of this class. This is to prevent
	 *                          circular referencing
	 */
	public void setWrappedObject(Object wrappedObject) {
		if (wrappedObject == this || wrappedObject instanceof TestObjectWrapper) {
			throw new RuntimeException("Cant set the wrapped object to an instance of itself");
		}
		this.wrappedObject = wrappedObject;
	}

	/**
	 * Hash code implementation consistent with equals().
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(wrappedObject);
	}

	/**
	 * Two {@link TestObjectWrapper}s are equal if and only if their wrappedObjects
	 * are equal.
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
		TestObjectWrapper other = (TestObjectWrapper) obj;
		return Objects.equals(wrappedObject, other.wrappedObject);
	}

}
