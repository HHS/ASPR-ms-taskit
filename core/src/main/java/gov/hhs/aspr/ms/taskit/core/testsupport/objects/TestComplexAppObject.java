package gov.hhs.aspr.ms.taskit.core.testsupport.objects;

import java.util.Objects;

/**
 * Test class that is similar to the TestAppObject class, except that it is a
 * class that is the type of one of the variables of the TestAppObject class.
 * <p>
 * <b>Should NOT</b> be used outside of testing
 */
public class TestComplexAppObject {
    private String testString;
    private double startTime;
    private int numEntities;

    /**
     * @return the value of the string variable
     */
    public String getTestString() {
        return testString;
    }

    /**
     * sets the value of the string variable
     * 
     * @param testString the value to set
     */
    public void setTestString(String testString) {
        this.testString = testString;
    }

    /**
     * @return the value of the startTime variable
     */
    public double getStartTime() {
        return startTime;
    }

    /**
     * sets the value of the startTime variable
     * 
     * @param startTime the value to set
     */
    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the value of the numEntities variable
     */
    public int getNumEntities() {
        return numEntities;
    }

    /**
     * sets the value of the numEntities variable
     * 
     * @param numEntities the value to set
     */
    public void setNumEntities(int numEntities) {
        this.numEntities = numEntities;
    }

    @Override
    public int hashCode() {
        return Objects.hash(testString, startTime, numEntities);
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
        TestComplexAppObject other = (TestComplexAppObject) obj;
        return Objects.equals(testString, other.testString)
                && Double.doubleToLongBits(startTime) == Double.doubleToLongBits(other.startTime)
                && numEntities == other.numEntities;
    }
}
