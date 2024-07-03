package gov.hhs.aspr.ms.taskit.protobuf.translation.specs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.google.protobuf.UInt64Value;

import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufJsonTaskitEngine;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_ULongTranslationSpec {

    @Test
    @UnitTestConstructor(target = ULongTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new ULongTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        ULongTranslationSpec booleanTranslationSpec = new ULongTranslationSpec();
        booleanTranslationSpec.init(protobufTaskitEngine);

        Long expectedValue = 100L;
        UInt64Value inputValue = UInt64Value.of(expectedValue);

        Long actualValue = booleanTranslationSpec.translateInputObject(inputValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        ULongTranslationSpec booleanTranslationSpec = new ULongTranslationSpec();
        booleanTranslationSpec.init(protobufTaskitEngine);

        Long appValue = 100L;
        UInt64Value expectedValue = UInt64Value.of(appValue);

        UInt64Value actualValue = booleanTranslationSpec.translateAppObject(appValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestMethod(target = ULongTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        ULongTranslationSpec booleanTranslationSpec = new ULongTranslationSpec();

        assertEquals(Long.class, booleanTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = ULongTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        ULongTranslationSpec booleanTranslationSpec = new ULongTranslationSpec();

        assertEquals(UInt64Value.class, booleanTranslationSpec.getInputObjectClass());
    }
}