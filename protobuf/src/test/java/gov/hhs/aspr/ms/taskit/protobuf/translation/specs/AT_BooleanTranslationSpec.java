package gov.hhs.aspr.ms.taskit.protobuf.translation.specs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.google.protobuf.BoolValue;

import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufJsonTaskitEngine;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_BooleanTranslationSpec {

    @Test
    @UnitTestConstructor(target = BooleanTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new BooleanTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        BooleanTranslationSpec booleanTranslationSpec = new BooleanTranslationSpec();
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        booleanTranslationSpec.init(protobufTaskitEngine);

        Boolean expectedValue = false;
        BoolValue inputValue = BoolValue.of(false);

        Boolean actualValue = booleanTranslationSpec.translateInputObject(inputValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        BooleanTranslationSpec booleanTranslationSpec = new BooleanTranslationSpec();
        booleanTranslationSpec.init(protobufTaskitEngine);

        Boolean appValue = false;
        BoolValue expectedValue = BoolValue.of(false);

        BoolValue actualValue = booleanTranslationSpec.translateAppObject(appValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestMethod(target = BooleanTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        BooleanTranslationSpec booleanTranslationSpec = new BooleanTranslationSpec();

        assertEquals(Boolean.class, booleanTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = BooleanTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        BooleanTranslationSpec booleanTranslationSpec = new BooleanTranslationSpec();

        assertEquals(BoolValue.class, booleanTranslationSpec.getInputObjectClass());
    }
}