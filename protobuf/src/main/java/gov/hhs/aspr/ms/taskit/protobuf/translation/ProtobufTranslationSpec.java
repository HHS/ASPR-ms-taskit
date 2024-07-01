package gov.hhs.aspr.ms.taskit.protobuf.translation;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitEngine;

/**
 * Abstract implementation of {@link TranslationSpec} that sets the
 * {@link TaskitEngine} on the Spec to a {@link ProtobufTaskitEngine}
 */
public abstract class ProtobufTranslationSpec<I, A> extends TranslationSpec<I, A> {
    protected ProtobufTaskitEngine taskitEngine;

    protected ProtobufTranslationSpec() {
    }

    /**
     * init implementation, sets the taskitEngine on this translationSpec.
     * calls super.init() to ensure this spec was properly initialized.
     */
    public void init(ITaskitEngine taskitEngine) {
        super.init(taskitEngine);
        this.taskitEngine = (ProtobufTaskitEngine) taskitEngine;
    }
}
