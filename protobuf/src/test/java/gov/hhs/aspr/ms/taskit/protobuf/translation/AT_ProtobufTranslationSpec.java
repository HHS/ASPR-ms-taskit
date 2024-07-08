package gov.hhs.aspr.ms.taskit.protobuf.translation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.protobuf.BoolValue;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufJsonTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.BooleanTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_ProtobufTranslationSpec {

    @Test
    @UnitTestMethod(target = ProtobufTranslationSpec.class, name = "init", args = { ITaskitEngine.class })
    public void testInit() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        ProtobufTranslationSpec<BoolValue, Boolean> booleanTranslationSpec = new BooleanTranslationSpec();

        booleanTranslationSpec.init(protobufTaskitEngine);

        assertTrue(booleanTranslationSpec.isInitialized());
    }

}
