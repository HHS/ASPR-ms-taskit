package gov.hhs.aspr.ms.taskit.protobuf.translation.specs;

import com.google.protobuf.StringValue;

import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;

/**
 * TranslationSpec that defines how to translate from any Java String to a
 * Protobuf {@link StringValue} type and vice versa
 */
public class StringTranslationSpec extends ProtobufTranslationSpec<StringValue, String> {

    @Override
    protected String translateInputObject(StringValue inputObject) {
        return inputObject.getValue();
    }

    @Override
    protected StringValue translateAppObject(String appObject) {
        return StringValue.of(appObject);
    }

    @Override
    public Class<String> getAppObjectClass() {
        return String.class;
    }

    @Override
    public Class<StringValue> getInputObjectClass() {
        return StringValue.class;
    }
}
