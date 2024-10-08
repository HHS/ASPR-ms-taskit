package gov.hhs.aspr.ms.taskit.core.testsupport.translation;

import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;

/**
 * Test implementation of {@link TranslationSpec} that specifically uses a
 * {@link TestTaskitEngine}
 */
public abstract class TestTranslationSpec<I, A> extends TranslationSpec<I, A, TestTaskitEngine> {

    protected TestTranslationSpec() {
        super(TestTaskitEngine.class);
    }
}
