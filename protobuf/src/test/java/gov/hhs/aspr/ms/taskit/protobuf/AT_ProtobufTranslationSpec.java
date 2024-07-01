package gov.hhs.aspr.ms.taskit.protobuf;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.protobuf.BoolValue;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.translationSpecs.BooleanTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_ProtobufTranslationSpec {

    @Test
    @UnitTestMethod(target = ProtobufTranslationSpec.class, name = "init", args = { TaskitEngine.class })
    public void testInit() {
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder().build();

        ProtobufTranslationSpec<BoolValue, Boolean> booleanTranslationSpec = new BooleanTranslationSpec();

        booleanTranslationSpec.init(protobufTaskitEngine);

        assertTrue(booleanTranslationSpec.isInitialized());
    }

}
