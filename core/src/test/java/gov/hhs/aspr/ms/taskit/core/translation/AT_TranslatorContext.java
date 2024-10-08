
package gov.hhs.aspr.ms.taskit.core.translation;	

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngineBuilder;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;	

public class AT_TranslatorContext {	

    @Test	
    @UnitTestConstructor(target = TranslatorContext.class, args = { ITaskitEngineBuilder.class })	
    public void testConstructor() {	
        TranslatorContext translatorContext = new TranslatorContext(TestTaskitEngine.builder());	

        assertNotNull(translatorContext);	
    }	

    private class BadEngineBuilder implements ITaskitEngineBuilder {

        @Override
        public ITaskitEngineBuilder addTranslationSpec(ITranslationSpec translationSpec) {
            return this;
        }

        @Override
        public ITaskitEngineBuilder addTranslator(Translator translator) {
            return this;
        }

    }

    @Test	
    @UnitTestMethod(target = TranslatorContext.class, name = "getTaskitEngineBuilder", args = { Class.class })	
    public void testGetTaskitEngineBuilder() {	
        TestTaskitEngine.Builder expectedBuilder = TestTaskitEngine.builder();	

        TranslatorContext translatorContext = new TranslatorContext(expectedBuilder);	

        TestTaskitEngine.Builder actualBuilder = translatorContext	
                .getTaskitEngineBuilder(TestTaskitEngine.Builder.class);	
        assertTrue(expectedBuilder == actualBuilder);	

        // preconditions	
        // invalid class ref	
        ContractException contractException = assertThrows(ContractException.class, () -> {	
            translatorContext.getTaskitEngineBuilder(BadEngineBuilder.class);	
        });	

        assertEquals(TaskitError.INVALID_TASKIT_ENGINE_BUILDER_CLASS_REF, contractException.getErrorType());	
    }	

}
