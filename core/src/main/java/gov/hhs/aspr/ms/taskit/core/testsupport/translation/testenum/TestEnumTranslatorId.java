package gov.hhs.aspr.ms.taskit.core.testsupport.translation.testenum;

import gov.hhs.aspr.ms.taskit.core.translation.TranslatorId;

/**
 * TranslatorId for the TestEnumTranslator
 */
public class TestEnumTranslatorId implements TranslatorId {
    public final static TranslatorId TRANSLATOR_ID = new TestEnumTranslatorId();

    private TestEnumTranslatorId() {
    }
}
