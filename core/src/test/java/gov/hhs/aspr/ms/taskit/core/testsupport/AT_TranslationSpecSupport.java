package gov.hhs.aspr.ms.taskit.core.testsupport;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.translationSpecs.TestObjectTranslationSpec;

public class AT_TranslationSpecSupport {

    @Test
    public void testTestGetTranslationSpecs() throws ClassNotFoundException {
        List<TranslationSpec<?, ?>> translationSpecs = new ArrayList<>();

        translationSpecs.add(new TestObjectTranslationSpec());

        Set<String> missingSpecs = TranslationSpecSupport.testGetTranslationSpecs(TestObjectTranslator.class, translationSpecs);

        assertTrue(missingSpecs.isEmpty());

        missingSpecs = TranslationSpecSupport.testGetTranslationSpecs(TestObjectTranslator.class, new ArrayList<>());

        assertTrue(missingSpecs.size() == 1);
        assertTrue(missingSpecs.contains(TestObjectTranslationSpec.class.getSimpleName()));
    }
}
