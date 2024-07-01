package gov.hhs.aspr.ms.taskit.core.engine;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_TaskitCoreError {

    @Test
    @UnitTestMethod(target = TaskitCoreError.class, name = "getDescription", args = {})
    public void testGetDescription() {
        // show that each ErrorType has a non-null, non-empty description
        for (TaskitCoreError coreTranslationError : TaskitCoreError.values()) {
            assertNotNull(coreTranslationError.getDescription());
            assertTrue(coreTranslationError.getDescription().length() > 0);
        }

        // show that each description is unique (ignoring case as well)
        Set<String> descriptions = new LinkedHashSet<>();
        for (TaskitCoreError coreTranslationError : TaskitCoreError.values()) {
            boolean isUnique = descriptions.add(coreTranslationError.getDescription().toLowerCase());
            assertTrue(isUnique, coreTranslationError + " duplicates the description of another member");
        }
    }
}
