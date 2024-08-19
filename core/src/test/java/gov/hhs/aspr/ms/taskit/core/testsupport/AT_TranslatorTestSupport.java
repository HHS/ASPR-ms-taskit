package gov.hhs.aspr.ms.taskit.core.testsupport;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_TranslatorTestSupport {

    @Test
    @UnitTestMethod(target = TranslatorTestSupport.class, name = "testGetTranslationSpecs", args = {Class.class, List.class})
    public void testTestGetTranslationSpecs() throws ClassNotFoundException {
        List<ITranslationSpec> translationSpecs = new ArrayList<>();

        translationSpecs.add(new TestObjectTranslationSpec());

        Set<String> missingSpecs = TranslatorTestSupport.testGetTranslationSpecs(TestObjectTranslator.class, translationSpecs);

        assertTrue(missingSpecs.isEmpty());

        missingSpecs = TranslatorTestSupport.testGetTranslationSpecs(TestObjectTranslator.class, new ArrayList<>());

        assertTrue(missingSpecs.size() == 1);
        assertTrue(missingSpecs.contains(TestObjectTranslationSpec.class.getSimpleName()));
    }
}
