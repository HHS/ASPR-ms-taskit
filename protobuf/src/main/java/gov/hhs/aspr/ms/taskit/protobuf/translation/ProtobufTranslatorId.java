package gov.hhs.aspr.ms.taskit.protobuf.translation;

import gov.hhs.aspr.ms.taskit.core.translation.TranslatorId;

public class ProtobufTranslatorId implements TranslatorId {
    public final static TranslatorId TRANSLATOR_ID = new ProtobufTranslatorId();

    private ProtobufTranslatorId() {
    }
}