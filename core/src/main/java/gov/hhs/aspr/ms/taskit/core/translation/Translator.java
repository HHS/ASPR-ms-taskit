package gov.hhs.aspr.ms.taskit.core.translation;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitCoreError;
import gov.hhs.aspr.ms.util.errors.ContractException;

/**
 * The Translator class serves as a wrapper around one or more
 * {@link ITranslationSpec}(s) and assists in adding those translationSpecs
 * to the {@link TaskitEngine}
 */
public final class Translator {
    private final Data data;
    private boolean initialized = false;

    private Translator(Data data) {
        this.data = data;
    }

    // package access for testing
    final static class Data {
        private TranslatorId translatorId;
        private Consumer<TranslatorContext> initializer;
        private final Set<TranslatorId> dependencies = new LinkedHashSet<>();

        Data() {
        }

        @Override
        public int hashCode() {
            return Objects.hash(translatorId, dependencies);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Data other = (Data) obj;
            return Objects.equals(translatorId, other.translatorId) && Objects.equals(dependencies, other.dependencies);
        }

    }

    /**
     * Builder for the Translator
     */
    public final static class Builder {
        private Data data;

        private Builder(Data data) {
            this.data = data;
        }

        private void validate() {
            if (this.data.translatorId == null) {
                throw new ContractException(TaskitCoreError.NULL_TRANSLATOR_ID);
            }
            if (this.data.initializer == null) {
                throw new ContractException(TaskitCoreError.NULL_INIT_CONSUMER);
            }
        }

        /**
         * Builds the Translator
         * 
         * @return the built translator
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitCoreError#NULL_TRANSLATOR_ID}
         *                           if the translatorId was not set</li>
         *                           <li>{@linkplain TaskitCoreError#NULL_INIT_CONSUMER}
         *                           if the initConsumer was not set</li>
         *                           </ul>
         */
        public Translator build() {
            validate();

            return new Translator(data);
        }

        /**
         * Sets the translatorId for this Translator
         * 
         * @param translatorId the translatorId to set
         * @return the builder instance
         * @throws ContractException {@linkplain TaskitCoreError#NULL_TRANSLATOR_ID}
         *                           if the translatorId is null
         */
        public Builder setTranslatorId(TranslatorId translatorId) {
            if (translatorId == null) {
                throw new ContractException(TaskitCoreError.NULL_TRANSLATOR_ID);
            }

            this.data.translatorId = translatorId;

            return this;
        }

        /**
         * Sets the initialization consumer for this translator
         * 
         * @param initConsumer the consumer to use for initialization
         * @return the builder instance
         * @throws ContractException {@linkplain TaskitCoreError#NULL_INIT_CONSUMER}
         *                           if the initConsumer is null
         */
        public Builder setInitializer(Consumer<TranslatorContext> initConsumer) {
            if (initConsumer == null) {
                throw new ContractException(TaskitCoreError.NULL_INIT_CONSUMER);
            }

            this.data.initializer = initConsumer;

            return this;
        }

        /**
         * Adds a dependency for this Translator
         * 
         * @param dependency the translatorId of the translator this translator should
         *                   depend on
         * @return the builder instance
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitCoreError#NULL_DEPENDENCY}
         *                           if the dependency is null</li>
         *                           <li>{@linkplain TaskitCoreError#DUPLICATE_DEPENDENCY}
         *                           if the dependency has already been added</li>
         *                           </ul>
         */
        public Builder addDependency(TranslatorId dependency) {
            if (dependency == null) {
                throw new ContractException(TaskitCoreError.NULL_DEPENDENCY);
            }

            if (this.data.dependencies.contains(dependency)) {
                throw new ContractException(TaskitCoreError.DUPLICATE_DEPENDENCY);
            }

            this.data.dependencies.add(dependency);

            return this;
        }

    }

    /**
     * Creates a new Builder for a Translator
     * 
     * @return a new Builder for a Translator
     */
    public static Builder builder() {
        return new Builder(new Data());
    }

    /**
     * package access for testing
     */
    Consumer<TranslatorContext> getInitializer() {
        return this.data.initializer;
    }

    /**
     * @return the TranslatorId for this Translator
     */
    public TranslatorId getTranslatorId() {
        return this.data.translatorId;
    }

    /**
     * @return the set of dependencies for this Translator
     */
    public Set<TranslatorId> getTranslatorDependencies() {
        return this.data.dependencies;
    }

    /**
     * Initializes this translator using its initialization consumer
     * 
     * @param translatorContext the translator context to pass to the initialization
     *                          consumer
     */
    public void initialize(TranslatorContext translatorContext) {
        this.data.initializer.accept(translatorContext);
        this.initialized = true;
    }

    /**
     * @return the initialized flag of this translator
     */
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Translator other = (Translator) obj;
        // Objects.equals will automatically return if a == b, so not need for check
        return Objects.equals(data, other.data);
    }

}
