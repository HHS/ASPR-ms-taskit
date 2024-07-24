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

/**
 * TestTaskit Engine
 * <p>
 * Implementation serves to allow for testing the TaskitEngine and <b>should
 * not</b> be used outside of testing.
 * <p>
 * Can be used as a guide for writing the read/write methods on child
 * TaskitEngines.
 */
public final class TestTaskitEngine extends TaskitEngine {
	private final Gson gson = new Gson();

	private TestTaskitEngine(TaskitEngineData taskitEngineData) {
		super(taskitEngineData, TestTaskitEngineId.TEST_ENGINE_ID);
	}

	/**
	 * Builder for the TestTaskitEngine
	 * <p>
	 * Because of the nature of the TestTaskitEngine, this builder is effectively
	 * just a wrapper around {@link TaskitEngineData.Builder}.
	 * </p>
	 */
	public static class Builder implements ITaskitEngineBuilder {

		private TaskitEngineData.Builder taskitEngineDataBuilder = TaskitEngineData.builder();

		private Builder() {
		}

		/**
		 * Builds and initializes a TestTaskitEngine
		 * 
		 * @return A initialized TestTaskitEngine
		 * @throws ContractException
		 *                           <ul>
		 *                           <li>{@link TaskitError#UNINITIALIZED_TRANSLATORS}
		 *                           if translators were added to the engine but their
		 *                           initialized flag was still set to false</li>
		 *                           <li>{@link TaskitError#DUPLICATE_TRANSLATOR} if a
		 *                           duplicate translator is found</li>
		 *                           <li>{@link TaskitError#MISSING_TRANSLATOR} if an
		 *                           added translator has a unmet dependency</li>
		 *                           <li>{@link TaskitError#CIRCULAR_TRANSLATOR_DEPENDENCIES}
		 *                           if the added translators have a circular dependency
		 *                           graph</li>
		 *                           <li>{@link TaskitError#NO_TRANSLATION_SPECS} if no
		 *                           translation specs were added to the engine</li>
		 *                           </ul>
		 */
		public TestTaskitEngine build() {
			TestTaskitEngine testTaskitEngine = new TestTaskitEngine(this.taskitEngineDataBuilder.build());
			testTaskitEngine.init();

			return testTaskitEngine;
		}

		/*
		 * package access for testing. Differs from build method by not initializing the
		 * engine
		 */
		TestTaskitEngine buildWithoutInit() {
			return new TestTaskitEngine(this.taskitEngineDataBuilder.build());
		}

		/**
		 * @throws ContractException
		 *                           <ul>
		 *                           <li>{@linkplain TaskitError#NULL_TRANSLATION_SPEC}
		 *                           if the given translationSpec is null</li>
		 *                           <li>{@linkplain TaskitError#NULL_TRANSLATION_SPEC_CLASS_MAP}
		 *                           if the given translationSpec's class map is
		 *                           null</li>
		 *                           <li>{@linkplain TaskitError#EMPTY_TRANSLATION_SPEC_CLASS_MAP}
		 *                           if the given translationSpec's class map is
		 *                           empty</li>
		 *                           <li>{@linkplain TaskitError#DUPLICATE_TRANSLATION_SPEC}
		 *                           if the given translationSpec is already known</li>
		 *                           </ul>
		 */
		@Override
		public Builder addTranslationSpec(ITranslationSpec translationSpec) {
			this.taskitEngineDataBuilder.addTranslationSpec(translationSpec);

			return this;
		}

		/**
		 * @throws ContractException
		 *                           <ul>
		 *                           <li>{@linkplain TaskitError#NULL_TRANSLATOR} if
		 *                           translator is null</li>
		 *                           </ul>
		 */
		@Override
		public Builder addTranslator(Translator translator) {
			this.taskitEngineDataBuilder.addTranslator(translator);

			translator.initialize(new TranslatorContext(this));

			return this;
		}
	}

	/**
	 * @return a new builder instance for a TestTaskitEngine
	 */
	public static Builder builder() {
		return new Builder();
	}

	@Override
	public <O> void write(Path outputPath, O outputObject) throws IOException {
		String stringToWrite = this.gson.toJson(outputObject);
		FileWriter writer = new FileWriter(outputPath.toFile());
		writer.write(stringToWrite);
		writer.flush();
		writer.close();
	}

	@Override
	public <O> void translateAndWrite(Path outputPath, O outputObject) throws IOException {
		write(outputPath, translateObject(outputObject));
	}

	@Override
	public <C, O extends C> void translateAndWrite(Path outputPath, O outputObject, Class<C> outputClassRef)
			throws IOException {
		write(outputPath, translateObjectAsClassSafe(outputObject, outputClassRef));
	}

	@Override
	public <I> I read(Path inputPath, Class<I> inputClassRef) throws IOException {
		JsonObject jsonObject = JsonParser.parseReader(new JsonReader(new FileReader(inputPath.toFile())))
				.getAsJsonObject();

		return this.gson.fromJson(jsonObject.toString(), inputClassRef);
	}

	@Override
	public <T, I> T readAndTranslate(Path inputPath, Class<I> inputClassRef) throws IOException {
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
