package gov.hhs.aspr.ms.taskit.core.translation;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;

public class TranslationSpecContext<E extends TaskitEngine> {
    private final E taskitEngine;

    public TranslationSpecContext(E taskitEngine) {
        this.taskitEngine = taskitEngine;
    }

    public E getTaskitEngine() {
        return this.taskitEngine;
    }
}
