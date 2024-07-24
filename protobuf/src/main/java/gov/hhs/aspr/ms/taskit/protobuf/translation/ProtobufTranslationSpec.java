package gov.hhs.aspr.ms.taskit.protobuf.translation;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitEngine;

/**
 * Abstract implementation of {@link TranslationSpec} that sets the
 * {@link TaskitEngine} on the Spec to a {@link ProtobufTaskitEngine}.
 */
public abstract class ProtobufTranslationSpec<I, A> extends TranslationSpec<I, A, ProtobufTaskitEngine> {

    protected ProtobufTranslationSpec() {
        super(ProtobufTaskitEngine.class);
    }

}
