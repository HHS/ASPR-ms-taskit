package gov.hhs.aspr.ms.taskit.core.engine;

import gov.hhs.aspr.ms.util.errors.ContractError;

/**
 * Errors that describe contract exceptions within Taskit
 */
public enum TaskitError implements ContractError {

        CIRCULAR_TRANSLATOR_DEPENDENCIES("Circular translator dependencies: "),
        DUPLICATE_CLASSREF("Duplicate ClassRef"),
        DUPLICATE_DEPENDENCY("Duplicate Dependency"),
        DUPLICATE_TRANSLATOR("Duplicate Translator"),
        DUPLICATE_TRANSLATION_SPEC("Duplicate TranslationSpec"),
        DOUBLE_TRANSLATION_SPEC_INIT("Called init more than once on a translation spec"),
        EMPTY_TRANSLATION_SPEC_CLASS_MAP(
                        "The provided translation spec class map was empty. it must contain at least 1 class to translation spec mapping"),
        INVALID_INPUT_CLASS("The given input class is not of the expected type."),
        INVALID_OUTPUT_CLASS("The given output class is not of the expected type."),
        INVALID_PARENT_OUTPUT_CLASS(
                        "The given output class is not known to be a parent of the given objects class. You need to specify this in the TaskitEngine Builder, otherwise this check will fail."),
        INVALID_OUTPUT_PATH(
                        "The given output file path does not exist. While the file will be created on write, the directory will not."),
        INVALID_TASKIT_ENGINE_BUILDER_CLASS_REF(
                        "The given Taskit Engine Builder classRef does not match the class of the actual Taskit Engine Builder"),
        INVALID_TASKIT_ENGINE(
                        "Init was called on a translationSpec with a TaskitEngine that was not of the expected type."),
        MISSING_TRANSLATOR("Missing Translator: "),
        NO_TASKIT_ENGINES("There are no TaskitEngines added to this TaskitEngineManager."),
        NO_TRANSLATION_SPECS("There are no translation specs added to this TaskitEngine."),
        NULL_CLASS_REF("Null Class Ref"),
        NULL_DEPENDENCY("Null dependency"),
        NULL_INIT_CONSUMER("Null Initializer Consumer"),
        NULL_OBJECT_FOR_TRANSLATION("The object to be translated was null"),
        NULL_PATH("Null Path"),
        NULL_TASKIT_ENGINE("Null Taskit Engine"),
        NULL_TASKIT_ENGINE_BUILDER("Null Taskit Engine Builder"),
        NULL_TASKIT_ENGINE_ID("Null Taskit Engine Type"),
        NULL_TRANSLATION_SPEC("Null TranslationSpec"),
        NULL_TRANSLATION_SPEC_CLASS_MAP("The provided translation spec to class map was null"),
        NULL_TRANSLATION_SPEC_APP_CLASS("Null TranslationSpec App Class"),
        NULL_TRANSLATION_SPEC_INPUT_CLASS("Null TranslationSpec Input Class"),
        NULL_TRANSLATOR("Null Translator"),
        NULL_TRANSLATOR_ID("Null TranslatorId"),
        TRANSLATORS_WERE_FOUND_WHEN_NOT_EXPECTED(
                        "Translators were added to the Taskit Engine but the normal build method was called. Translators that are added must be"),
        UNINITIALIZED_TRANSLATION_SPEC("TranslationSpec not initialized"),
        UNINITIALIZED_TRANSLATORS(
                        "Translators were added to the builder but were not initialized. Make sure to call super.initTranslators() during your custom engine build method"),
        UNINITIALIZED_TASKIT_ENGINE("The TaskitEngine was not initialized. Be sure to call taskitEngine.init()"),
        UNKNOWN_CLASSREF("No object has been read in with the specified classRef"),
        UNKNOWN_OBJECT("Object is not Translatable by this TranslationSpec"),
        UNKNOWN_TASKIT_ENGINE_ID("Taskit Engine ID was not set"),
        UNKNOWN_TRANSLATION_SPEC("No translation spec was provided for the given class"),
        UNSUPPORTED_VERSION("The given version is not supported");

        private final String description;

        private TaskitError(final String description) {
                this.description = description;
        }

        @Override
        public String getDescription() {
                return description;
        }
}
