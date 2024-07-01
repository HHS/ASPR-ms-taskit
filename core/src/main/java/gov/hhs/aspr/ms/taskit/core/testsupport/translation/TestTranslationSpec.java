package gov.hhs.aspr.ms.taskit.core.testsupport.translation;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;

public abstract class TestTranslationSpec<I, A> extends TranslationSpec<I, A> {
    protected TestTaskitEngine taskitEngine;

    public void init(ITaskitEngine taskitEngine) {
        super.init(taskitEngine);
        this.taskitEngine = (TestTaskitEngine) taskitEngine;
    }
}