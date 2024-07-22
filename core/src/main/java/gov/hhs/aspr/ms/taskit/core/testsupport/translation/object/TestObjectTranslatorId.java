package gov.hhs.aspr.ms.taskit.core.testsupport.translation.object;

import gov.hhs.aspr.ms.taskit.core.translation.TranslatorId;

/**
 * TranslatorId for the TestObjectTranslator
 */
public class TestObjectTranslatorId implements TranslatorId {
    public final static TranslatorId TRANSLATOR_ID = new TestObjectTranslatorId();

    private TestObjectTranslatorId() {
    }
}
