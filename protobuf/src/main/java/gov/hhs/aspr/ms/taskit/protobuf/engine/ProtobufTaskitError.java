package gov.hhs.aspr.ms.taskit.protobuf.engine;

import gov.hhs.aspr.ms.util.errors.ContractError;

/**
 * Contract Exception Errors exclusive to ProtobufTaskit.
 */
public enum ProtobufTaskitError implements ContractError {
    INVALID_TRANSLATION_SPEC_INPUT_CLASS("The input class is neither a Protobuf Message, nor a Protobuf Enum"),
    INVALID_INPUT_CLASS("The inputClassRef is not of the parent type: Message.class"),
    INVALID_TRANSLATION_SPEC("Added Translation Specs need to be of parent type Protobuf TranslationSpecs"),
    NULL_FIELD_DESCRIPTOR("The provided field descriptor is null"),
    UNKNOWN_TYPE_URL(
            "The given type url does not have a corresponding classRef. Either the typeUrl was never provided, or the typeUrl is malformed."),
    MALFORMED_TASKIT_OBJECT("The given taskit object does not have any fields set on it"),
    ;

    private final String description;

    private ProtobufTaskitError(final String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
