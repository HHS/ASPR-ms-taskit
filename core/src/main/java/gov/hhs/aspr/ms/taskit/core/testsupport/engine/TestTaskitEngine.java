package gov.hhs.aspr.ms.taskit.core.testsupport.engine;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngineBuilder;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineData;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorContext;

// TODO: maybe move this class to the test code exclusively, as it's only purpose is to test the TaskitEngineManager read/write/translate methods
public final class TestTaskitEngine extends TaskitEngine {
    private final Data data;

    private TestTaskitEngine(Data data, TaskitEngineData taskitEngineData) {
        super(taskitEngineData, TestTaskitEngineId.TEST_ENGINE_ID);
        this.data = data;
    }

    protected static class Data {
        protected Gson gson = new Gson();

        protected Data() {

        }

    }

    public static class Builder implements ITaskitEngineBuilder {
        private Data data;

        private TaskitEngineData.Builder taskitEngineBuilder = TaskitEngineData.builder();

        private Builder(TestTaskitEngine.Data data) {
            this.data = data;
        }

        public TestTaskitEngine build() {

            TestTaskitEngine testTaskitEngine = new TestTaskitEngine(this.data, this.taskitEngineBuilder.build());
            testTaskitEngine.init();

            return testTaskitEngine;
        }

        public TestTaskitEngine buildWithoutInit() {
            return new TestTaskitEngine(this.data, this.taskitEngineBuilder.build());
        }

        @Override
        public <E extends TaskitEngine> Builder addTranslationSpec(ITranslationSpec<E> translationSpec) {
            this.taskitEngineBuilder.addTranslationSpec(translationSpec);

            return this;
        }

        @Override
        public Builder addTranslator(Translator translator) {
            this.taskitEngineBuilder.addTranslator(translator);

            translator.initialize(new TranslatorContext(this));

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
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(data);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof TestTaskitEngine)) {
            return false;
        }
        TestTaskitEngine other = (TestTaskitEngine) obj;
        return Objects.equals(data, other.data);
    }

}
