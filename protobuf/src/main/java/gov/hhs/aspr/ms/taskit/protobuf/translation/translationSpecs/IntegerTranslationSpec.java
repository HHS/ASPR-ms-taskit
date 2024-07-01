package gov.hhs.aspr.ms.taskit.protobuf.translation.translationSpecs;

import com.google.protobuf.Int32Value;

import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;

/**
 * TranslationSpec that defines how to translate from any Java Integer to a
 * Protobuf {@link Int32Value} type and vice versa
 */
public class IntegerTranslationSpec extends ProtobufTranslationSpec<Int32Value, Integer> {

    @Override
    protected Integer translateInputObject(Int32Value inputObject) {
        return inputObject.getValue();
    }

    @Override
    protected Int32Value translateAppObject(Integer appObject) {
        return Int32Value.of(appObject);
    }

    @Override
    public Class<Integer> getAppObjectClass() {
        return Integer.class;
    }

    @Override
    public Class<Int32Value> getInputObjectClass() {
        return Int32Value.class;
    }
}
