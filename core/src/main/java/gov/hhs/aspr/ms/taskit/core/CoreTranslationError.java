package gov.hhs.aspr.ms.taskit.core;

import gov.hhs.aspr.ms.util.errors.ContractError;

public enum CoreTranslationError implements ContractError {

    CIRCULAR_TRANSLATOR_DEPENDENCIES("Circular translator dependencies: "),
    DUPLICATE_CLASSREF("Duplicate ClassRef"),
    DUPLICATE_CLASSREF_SCENARIO_PAIR("Duplicate ClassRef and Scenario Pair"),
    DUPLICATE_DEPENDENCY("Duplicate Dependency"),
    DUPLICATE_INPUT_PATH("Duplicate Input Path"),
    DUPLICATE_OUTPUT_PATH("Duplicate Output Path"),
    DUPLICATE_TRANSLATOR("Duplicate Translator"),
    DUPLICATE_TRANSLATION_SPEC("Duplicate TranslationSpec"),
    INVALID_TRANSLATION_ENGINE_CLASS_REF(
            "The given Translation Engine classRef does not match the class of the actual Translation Engine"),
    INVALID_TRANSLATION_ENGINE_BUILDER_CLASS_REF(
            "The given Translation Engine Builder classRef does not match the class of the actual Translation Engine Builder"),
    INVALID_OUTPUT_CLASSREF("The given class does not have a output file path associated with it."),
    INVALID_OUTPUT_PATH(
            "The given output file path does not exist. While the file will be created on write, the directory will not."),
    INVALID_INPUT_PATH("The given input file path does not exist"),
    MISSING_TRANSLATOR("Missing Translator: "),
    NO_TRANSLATION_ENGINES("There are no translation engines added to this controller."),
    NULL_TRANSLATOR_ID("Null TranslatorId"),
    NULL_TRANSLATOR("Null Translator"),
    NULL_TRANSLATION_ENGINE_BUILDER("Null Translation Engine Builder"),
    NULL_TRANSLATION_ENGINE("Null Translation Engine"),
    NULL_OBJECT_FOR_TRANSLATION("The object to be translated was null"),
    NULL_INIT_CONSUMER("Null Initilizer Consumer"),
    NULL_DEPENDENCY("Null dependency"),
    NULL_PATH("Null Path"),
    NULL_CLASS_REF("Null Class Ref"),
    NULL_TRANSLATION_SPEC("Null TranslationSpec"),
    NULL_TRANSLATION_SPEC_APP_CLASS("Null TranslationSpec App Class"),
    NULL_TRANSLATION_SPEC_INPUT_CLASS("Null TranslationSpec Input Class"),
    UNKNOWN_TRANSLATION_SPEC("No translation spec was provided for the given class"),
    UNITIALIZED_TRANSLATION_SPEC("TranslationSpec not initialized"),
    UNINITIALIZED_TRANSLATORS(
            "Translators were added to the builder but were not initialized. Make sure to call super.initTranslators() during your custom engine build method"),
    UNKNOWN_OBJECT("Object is not Translatable by this TranslationSpec"),
    UNKNWON_TRANSLATION_ENGINE_TYPE("Translation Engine Type was not set"),
    UNKNOWN_CLASSREF("No object has been read in with the specified classRef");

    private final String description;

    private CoreTranslationError(final String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
