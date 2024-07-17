package gov.hhs.aspr.ms.taskit.core.testsupport.engine;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngineBuilder;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineData;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorContext;
import gov.hhs.aspr.ms.util.errors.ContractException;

// TODO: maybe move this class to the test code exclusively, as it's only purpose is to test the TaskitEngineManager read/write/translate methods
public final class TestTaskitEngine extends TaskitEngine {
    private final Gson gson = new Gson();

    private TestTaskitEngine(TaskitEngineData taskitEngineData) {
        super(taskitEngineData, TestTaskitEngineId.TEST_ENGINE_ID);
    }

    public static class Builder implements ITaskitEngineBuilder {

        private TaskitEngineData.Builder taskitEngineDataBuilder = TaskitEngineData.builder();

        private Builder() {
        }

        public TestTaskitEngine build() {
            TestTaskitEngine testTaskitEngine = new TestTaskitEngine(this.taskitEngineDataBuilder.build());
            testTaskitEngine.init();

            return testTaskitEngine;
        }

        public TestTaskitEngine buildWithoutInit() {
            return new TestTaskitEngine(this.taskitEngineDataBuilder.build());
        }

        @Override
        public Builder addTranslationSpec(ITranslationSpec translationSpec) {
            this.taskitEngineDataBuilder.addTranslationSpec(translationSpec);

            return this;
        }

        @Override
        public Builder addTranslator(Translator translator) {
            if (translator == null) {
                throw new ContractException(TaskitError.NULL_TRANSLATOR);
            }
            translator.initialize(new TranslatorContext(this));

            this.taskitEngineDataBuilder.addTranslator(translator);

            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <M> void write(Path path, M object) throws IOException {
        String stringToWrite = this.gson.toJson(object);
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

        return this.gson.fromJson(jsonObject.toString(), inputClassRef);
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

}
