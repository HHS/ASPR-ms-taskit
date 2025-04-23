package gov.hhs.aspr.ms.taskit.core.testsupport.objects;

import java.util.Objects;

/**
 * Test class representing a class with various variables, including a Complex
 * class.
 * <p>
 * <b>Should NOT</b> be used outside of testing.
 * </p>
 */
public class TestAppObject {
	private int integer;
	private boolean bool;
	private String string;
	private TestComplexAppObject testComplexAppObject;
	private TestAppEnum testAppEnum;

	/**
	 * @return value of the integer variable
	 */
	public int getInteger() {
		return integer;
	}

	/**
	 * Sets the value of the integer variable.
	 * 
	 * @param integer the value to set
	 */
	public void setInteger(int integer) {
		this.integer = integer;
	}

	/**
	 * @return value of the bool variable
	 */
	public boolean isBool() {
		return bool;
	}

	/**
	 * Sets the value of the bool variable.
	 * 
	 * @param bool the value to set
	 */
	public void setBool(boolean bool) {
		this.bool = bool;
	}

	/**
	 * @return the value of the string variable
	 */
	public String getString() {
		return string;
	}

	/**
	 * Sets the value of the string variable.
	 * 
	 * @param string the value to set
	 */
	public void setString(String string) {
		this.string = string;
	}

	/**
	 * @return the value of the complexAppObject variable
	 */
	public TestComplexAppObject getTestComplexAppObject() {
		return testComplexAppObject;
	}

	/**
	 * Sets the value of the complexAppObject variable.
	 * 
	 * @param testComplexAppObject the value to set
	 */
	public void setTestComplexAppObject(TestComplexAppObject testComplexAppObject) {
		this.testComplexAppObject = testComplexAppObject;
	}

	/**
	 * @return the value of the enum variable
	 */
	public TestAppEnum getTestAppEnum() {
		return this.testAppEnum;
	}

	/**
	 * Sets the value of the enum variable.
	 * 
	 * @param testAppEnum the value to set
	 */
	public void setTestAppEnum(TestAppEnum testAppEnum) {
		this.testAppEnum = testAppEnum;
	}

	/**
	 * Standard implementation consistent with the {@link #equals(Object)} method
	 */
	@Override
	public int hashCode() {
		return Objects.hash(integer, bool, string, testComplexAppObject);
	}

	/**
	 * Two {@link TestAppObject}s are equal if and only they contain identical
	 * internal values.
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
		TestAppObject other = (TestAppObject) obj;
		return integer == other.integer && bool == other.bool && Objects.equals(string, other.string)
				&& Objects.equals(testComplexAppObject, other.testComplexAppObject);
	}
}
