package gov.hhs.aspr.ms.taskit.core.engine;

import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;

public interface ITaskitEngineBuilder {
    public ITaskitEngine build();

    public <I, A> ITaskitEngineBuilder addTranslationSpec(TranslationSpec<I, A> translationSpec);

    public ITaskitEngineBuilder addTranslator(Translator translator);

    public <M extends U, U> ITaskitEngineBuilder addParentChildClassRelationship(Class<M> classRef,
            Class<U> parentClassRef);
}
