package gov.hhs.aspr.ms.taskit.core.testsupport;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngineBuilder;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineType;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;

public class TestTaskitEngine implements ITaskitEngine {
    private final Data data;

    private TestTaskitEngine(Data data) {
        this.data = data;
    }

    protected static class Data {
        protected Gson gson = new Gson();

        private TaskitEngine baseTaskitEngine;

        protected Data() {

        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

    }

    public static class Builder implements ITaskitEngineBuilder {
        private TestTaskitEngine.Data data;

        private TaskitEngine.Builder baseTaskitEngineBuilder = TaskitEngine.builder();

        private Builder(TestTaskitEngine.Data data) {
            this.data = data;
        }

        void clearBuilder() {
            this.data = new Data();
        }
        
        @Override
        public TestTaskitEngine build() {
            this.baseTaskitEngineBuilder.setTaskitEngineType(TaskitEngineType.CUSTOM);

            this.data.baseTaskitEngine = baseTaskitEngineBuilder.build();

            TestTaskitEngine taskitEngine = new TestTaskitEngine(this.data);

            this.data.baseTaskitEngine.initTranslationSpecs(taskitEngine);
            this.data.baseTaskitEngine.validateInit();
            return taskitEngine;
        }

        public TestTaskitEngine buildWithoutSpecInit() {
            this.baseTaskitEngineBuilder.setTaskitEngineType(TaskitEngineType.CUSTOM);

            this.data.baseTaskitEngine = baseTaskitEngineBuilder.build();

            return new TestTaskitEngine(this.data);
        }

        public TestTaskitEngine buildWithUnknownType() {
            this.baseTaskitEngineBuilder.setTaskitEngineType(TaskitEngineType.UNKNOWN);

            this.data.baseTaskitEngine = baseTaskitEngineBuilder.build();

            TestTaskitEngine taskitEngine = new TestTaskitEngine(this.data);

            this.data.baseTaskitEngine.initTranslationSpecs(taskitEngine);
            this.data.baseTaskitEngine.validateInit();
            return taskitEngine;
        }

        @Override
        public <I, A> Builder addTranslationSpec(TranslationSpec<I, A> translationSpec) {
            this.baseTaskitEngineBuilder.addTranslationSpec(translationSpec);

            return this;
        }

        @Override
        public Builder addTranslator(Translator translator) {
            this.baseTaskitEngineBuilder.addTranslator(translator);

            return this;
        }

        @Override
        public <M extends U, U> Builder addParentChildClassRelationship(Class<M> classRef, Class<U> markerInterface) {
            this.baseTaskitEngineBuilder.addParentChildClassRelationship(classRef, markerInterface);

            return this;
        }

        @Override
        public ITaskitEngineBuilder setTaskitEngineType(TaskitEngineType taskitEngineType) {
            this.baseTaskitEngineBuilder.setTaskitEngineType(taskitEngineType);

            return this;
        }
    }

    public static Builder builder() {
        return new Builder(new Data());
    }

    @Override
    public <U, M extends U> void write(Path outputPath, M object, Optional<Class<U>> outputClassRefOverride)
            throws IOException {
        Object outputObject;
        if (outputClassRefOverride.isPresent()) {
            outputObject = translateObjectAsClassSafe(object, outputClassRefOverride.get());
        } else {
            outputObject = translateObject(object);
        }

        String stringToWrite = this.data.gson.toJson(outputObject);
        FileWriter writer = new FileWriter(outputPath.toFile());
        writer.write(stringToWrite);
        writer.flush();
        writer.close();
    }

    @Override
    public <T, U> T read(Path inputPath, Class<U> inputClassRef) throws IOException {
        JsonObject jsonObject = JsonParser.parseReader(new JsonReader(new FileReader(inputPath.toFile())))
                .getAsJsonObject();

        return translateObject(this.data.gson.fromJson(jsonObject.toString(), inputClassRef));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public TaskitEngine getTaskitEngine() {
        return this.data.baseTaskitEngine;
    }

    @Override
    public TaskitEngineType getTaskitEngineType() {
        return this.data.baseTaskitEngine.getTaskitEngineType();
    }

    @Override
    public Set<ITranslationSpec> getTranslationSpecs() {
        return this.data.baseTaskitEngine.getTranslationSpecs();
    }

    @Override
    public <T> T translateObject(Object object) {
        return this.data.baseTaskitEngine.translateObject(object);
    }

    @Override
    public <T, M extends U, U> T translateObjectAsClassSafe(M object, Class<U> classRef) {
        return this.data.baseTaskitEngine.translateObjectAsClassSafe(object, classRef);
    }

    @Override
    public <T, M, U> T translateObjectAsClassUnsafe(M object, Class<U> classRef) {
        return this.data.baseTaskitEngine.translateObjectAsClassUnsafe(object, classRef);
    }

}
