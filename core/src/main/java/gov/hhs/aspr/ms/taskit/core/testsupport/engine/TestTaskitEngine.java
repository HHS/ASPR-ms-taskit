package gov.hhs.aspr.ms.taskit.core.testsupport.engine;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngineBuilder;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineId;
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

        private TaskitEngine taskitEngine;

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

        private TaskitEngine.Builder taskitEngineBuilder = TaskitEngine.builder();

        private Builder(TestTaskitEngine.Data data) {
            this.data = data;
        }

        void clearBuilder() {
            this.data = new Data();
        }

        @Override
        public TestTaskitEngine build() {
            this.taskitEngineBuilder.setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID);

            this.data.taskitEngine = taskitEngineBuilder.build();

            TestTaskitEngine taskitEngine = new TestTaskitEngine(this.data);

            this.data.taskitEngine.initTranslationSpecs(taskitEngine);
            this.data.taskitEngine.validateInit();
            return taskitEngine;
        }

        public TestTaskitEngine buildWithoutSpecInit() {
            this.taskitEngineBuilder.setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID);

            this.data.taskitEngine = taskitEngineBuilder.build();

            return new TestTaskitEngine(this.data);
        }

        public TestTaskitEngine buildWithUnknownType() {
            this.taskitEngineBuilder.setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID);

            this.data.taskitEngine = taskitEngineBuilder.build();

            TestTaskitEngine taskitEngine = new TestTaskitEngine(this.data);

            this.data.taskitEngine.initTranslationSpecs(taskitEngine);
            this.data.taskitEngine.validateInit();
            return taskitEngine;
        }

        @Override
        public <I, A> Builder addTranslationSpec(TranslationSpec<I, A> translationSpec) {
            this.taskitEngineBuilder.addTranslationSpec(translationSpec);

            return this;
        }

        @Override
        public Builder addTranslator(Translator translator) {
            this.taskitEngineBuilder.addTranslator(translator);

            return this;
        }

        @Override
        public <M extends U, U> Builder addParentChildClassRelationship(Class<M> classRef, Class<U> markerInterface) {
            this.taskitEngineBuilder.addParentChildClassRelationship(classRef, markerInterface);

            return this;
        }
    }

    public static Builder builder() {
        return new Builder(new Data());
    }

    @Override
    public <M> void write(Path path, M object) throws IOException {
        String stringToWrite = this.data.gson.toJson(object);
        FileWriter writer = new FileWriter(path.toFile());
        writer.write(stringToWrite);
        writer.flush();
        writer.close();
    }

    @Override
    public <M> void translateAndWrite(Path path, M object) throws IOException {
        write(path, translateObject(object));
    }

    @Override
    public <U, M extends U> void translateAndWrite(Path path, M object, Class<U> classRef) throws IOException {
        write(path, translateObjectAsClassSafe(object, classRef));
    }

    @Override
    public <U> U read(Path inputPath, Class<U> inputClassRef) throws IOException {
        JsonObject jsonObject = JsonParser.parseReader(new JsonReader(new FileReader(inputPath.toFile())))
                .getAsJsonObject();

        return this.data.gson.fromJson(jsonObject.toString(), inputClassRef);
    }

    @Override
    public <T, U> T readAndTranslate(Path inputPath, Class<U> inputClassRef) throws IOException {
        return translateObject(read(inputPath, inputClassRef));
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
        return this.data.taskitEngine;
    }

    @Override
    public TaskitEngineId getTaskitEngineId() {
        return this.data.taskitEngine.getTaskitEngineId();
    }

    @Override
    public Set<ITranslationSpec> getTranslationSpecs() {
        return this.data.taskitEngine.getTranslationSpecs();
    }

    @Override
    public <T> T translateObject(Object object) {
        return this.data.taskitEngine.translateObject(object);
    }

    @Override
    public <T, M extends U, U> T translateObjectAsClassSafe(M object, Class<U> classRef) {
        return this.data.taskitEngine.translateObjectAsClassSafe(object, classRef);
    }

    @Override
    public <T, M, U> T translateObjectAsClassUnsafe(M object, Class<U> classRef) {
        return this.data.taskitEngine.translateObjectAsClassUnsafe(object, classRef);
    }

}
