package gov.hhs.aspr.ms.taskit.protobuf.translation.specs;

import com.google.protobuf.BoolValue;

import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;

/**
 * TranslationSpec that defines how to translate from any Java Boolean to a
 * Protobuf {@link BoolValue} type and vice versa.
 */
public class BooleanTranslationSpec extends ProtobufTranslationSpec<BoolValue, Boolean> {

    @Override
    protected Boolean translateInputObject(BoolValue inputObject) {
        return inputObject.getValue();
    }

    @Override
    protected BoolValue translateAppObject(Boolean appObject) {
        return BoolValue.of(appObject);
    }

    @Override
    public Class<Boolean> getAppObjectClass() {
        return Boolean.class;
    }

    @Override
    public Class<BoolValue> getInputObjectClass() {
        return BoolValue.class;
    }
}