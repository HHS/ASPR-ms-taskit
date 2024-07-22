package gov.hhs.aspr.ms.taskit.core.testsupport.objects;

import java.util.Objects;

/**
 * compliment class to {@link TestAppObject}
 * <p>
 * note this class does not have an enum variable
 * <p>
 * <b>Should NOT</b> be used outside of testing
 */
public class TestInputObject {
    private int integer;
    private boolean bool;
    private String string;
    private TestComplexInputObject testComplexInputObject;

    /**
     * @return value of the integer variable
     */
    public int getInteger() {
        return integer;
    }

    /**
     * sets the value of the integer variable
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
     * sets the value of the bool variable
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
     * sets the value of the string variable
     * 
     * @param string the value to set
     */
    public void setString(String string) {
        this.string = string;
    }

    /**
     * @return the value of the complexInputObject variable
     */
    public TestComplexInputObject getTestComplexInputObject() {
        return testComplexInputObject;
    }

    /**
     * sets the value of the complexInputObject variable
     * 
     * @param testComplexInputObject the value to set
     */
    public void setTestComplexInputObject(TestComplexInputObject testComplexInputObject) {
        this.testComplexInputObject = testComplexInputObject;
    }

    @Override
    public int hashCode() {
        return Objects.hash(integer, bool, string, testComplexInputObject);
    }

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
        TestInputObject other = (TestInputObject) obj;
        return integer == other.integer && bool == other.bool && Objects.equals(string, other.string)
                && Objects.equals(testComplexInputObject, other.testComplexInputObject);
    }

}
