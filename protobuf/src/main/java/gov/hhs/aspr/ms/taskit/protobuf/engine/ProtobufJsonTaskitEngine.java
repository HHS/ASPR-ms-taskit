package gov.hhs.aspr.ms.taskit.protobuf.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Parser;
import com.google.protobuf.util.JsonFormat.Printer;
import com.google.protobuf.util.JsonFormat.TypeRegistry;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineData;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorContext;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslator;
import gov.hhs.aspr.ms.util.errors.ContractException;

/**
 * Protobuf TaskitEngine that reads/writes from/to JSON files into Protobuf
 * {@link Message} types
 */
public final class ProtobufJsonTaskitEngine extends ProtobufTaskitEngine {
	private final Data data;

	private ProtobufJsonTaskitEngine(Data data, Map<String, Class<?>> typeUrlToClassMap,
			TaskitEngineData taskitEngineData) {
		super(typeUrlToClassMap, taskitEngineData, ProtobufTaskitEngineId.JSON_ENGINE_ID);
		this.data = data;
	}

	private final static class Data {
		// these two fields are used for reading and writing Protobuf Messages to/from
		// JSON
		private Parser jsonParser;
		private Printer jsonPrinter;

		private Data() {
		}
	}

	/**
	 * Builder for the ProtobufJsonTaskitEngine.
	 */
	public final static class Builder implements IProtobufTaskitEngineBuilder {
		private Data data;
		private Set<Descriptor> descriptorSet = new LinkedHashSet<>();
		private final Set<FieldDescriptor> defaultValueFieldsToPrint = new LinkedHashSet<>();
		private boolean ignoringUnknownFields = true;
		private boolean includingDefaultValueFields = false;

		// this is used specifically for Any message types to pack and unpack them
		private final Map<String, Class<?>> typeUrlToClassMap = new LinkedHashMap<>();

		private TaskitEngineData.Builder taskitEngineDataBuilder = TaskitEngineData.builder();

		private Builder(ProtobufJsonTaskitEngine.Data data) {
			this.data = data;
		}

		/**
		 * Returns a new instance of a ProtobufTaskitEngine that has a jsonParser and
		 * jsonWriter that include all the typeUrls for all added TranslationSpecs and
		 * their respective Protobuf Message types.
		 * 
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
		public ProtobufJsonTaskitEngine build() {

			this.addTranslator(ProtobufTranslator.getTranslator());

			TypeRegistry.Builder typeRegistryBuilder = TypeRegistry.newBuilder();

			this.descriptorSet.forEach((descriptor) -> {
				typeRegistryBuilder.add(descriptor);
			});

			TypeRegistry registry = typeRegistryBuilder.build();

			Parser parser = JsonFormat.parser().usingTypeRegistry(registry);

			if (this.ignoringUnknownFields) {
				parser = parser.ignoringUnknownFields();
			}

			this.data.jsonParser = parser;

			Printer printer = JsonFormat.printer().usingTypeRegistry(registry);

			if (!this.defaultValueFieldsToPrint.isEmpty()) {
				printer = printer.includingDefaultValueFields(this.defaultValueFieldsToPrint);
			}

			if (this.includingDefaultValueFields) {
				printer = printer.includingDefaultValueFields();
			}

			this.data.jsonPrinter = printer;

			ProtobufJsonTaskitEngine protoJsonTaskitEngine = new ProtobufJsonTaskitEngine(this.data,
					this.typeUrlToClassMap, this.taskitEngineDataBuilder.build());

			protoJsonTaskitEngine.init();

			return protoJsonTaskitEngine;
		}

		/**
		 * Sets the flag indicating whether the jsonParser should ignore fields in the
		 * JSON file that don't exist in the associated Proto Message.
		 * <p>
		 * Defaults to true.
		 * </p>
		 * 
		 * @param ignoringUnknownFields the flag
		 * @return the builder instance
		 */
		public Builder setIgnoringUnknownFields(boolean ignoringUnknownFields) {
			this.ignoringUnknownFields = ignoringUnknownFields;
			return this;
		}

		/**
		 * Sets the flag indicating whether the jsonWrite should print all default
		 * values.
		 * <p>
		 * The default values can be found here:
		 * https://protobuf.dev/programming-guides/proto3/#default
		 * <p>
		 * Defaults to false.
		 * </p>
		 * 
		 * @param includingDefaultValueFields the flag
		 * @return the builder instance
		 */
		public Builder setIncludingDefaultValueFields(boolean includingDefaultValueFields) {
			this.includingDefaultValueFields = includingDefaultValueFields;
			return this;
		}

		/**
		 * Contrary to {@link Builder#setIncludingDefaultValueFields(boolean)} which
		 * will set the flag globally for all default values, this will set the flag for
		 * a specific default field.
		 * <p>
		 * All fields default to false.
		 * </p>
		 * 
		 * @param fieldDescriptor the descriptor of the field to print the default value
		 *                        for
		 * @return the builder instance
		 * 
		 * @throws ContractException {@link ProtobufTaskitError#NULL_FIELD_DESCRIPTOR}
		 *                           if the provided field descriptor is null
		 */
		public Builder addFieldToIncludeDefaultValue(FieldDescriptor fieldDescriptor) {
			if (fieldDescriptor == null) {
				throw new ContractException(ProtobufTaskitError.NULL_FIELD_DESCRIPTOR);
			}

			this.defaultValueFieldsToPrint.add(fieldDescriptor);

			return this;
		}

		/**
		 * Adds the given {@link ITranslationSpec} to the TaskitEngine.
		 * <p>
		 * Additionally will populate typeUrls and field descriptors associated with the
		 * Protobuf types on the given TranslationSpec.
		 * </p>
		 * 
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
		 *                           <li>{@link ProtobufTaskitError#INVALID_TRANSLATION_SPEC}
		 *                           if the given translation spec is not assignable
		 *                           from {@linkplain ProtobufTranslationSpec}</li>
		 *                           <li>{@link ProtobufTaskitError#INVALID_TRANSLATION_SPEC_INPUT_CLASS}
		 *                           if the given inputClassRef is not assignable from
		 *                           {@linkplain Message} nor
		 *                           {@linkplain ProtocolMessageEnum}</li>
		 *                           </ul>
		 */
		@Override
		public Builder addTranslationSpec(ITranslationSpec translationSpec) {
			this.taskitEngineDataBuilder.addTranslationSpec(translationSpec);

			if (!ProtobufTranslationSpec.class.isAssignableFrom(translationSpec.getClass())) {
				throw new ContractException(ProtobufTaskitError.INVALID_TRANSLATION_SPEC);
			}

			ProtobufTranslationSpec<?, ?> protobufTranslationSpec = ProtobufTranslationSpec.class.cast(translationSpec);

			populate(protobufTranslationSpec.getInputObjectClass());
			return this;
		}

		/**
		 * Adds a {@link Translator}.
		 * 
		 * @throws ContractException {@linkplain TaskitError#NULL_TRANSLATOR} if
		 *                           translator is null
		 */
		@Override
		public Builder addTranslator(Translator translator) {
			this.taskitEngineDataBuilder.addTranslator(translator);

			translator.initialize(new TranslatorContext(this));

			return this;
		}

		/**
		 * Checks the class to determine if it is a ProtocolMessageEnum or a Message.
		 * <p>
		 * If it is a Message, gets the Descriptor (which is akin to a class but for a
		 * Protobuf
		 * Message) for it to get the full name and add the typeUrl to the internal
		 * descriptorMap and typeUrlToClassMap.
		 * </p>
		 * <p>
		 * Package access for testing.
		 * </p>
		 * 
		 * @param <U>      the type of the classRef
		 * @param classRef the classRef to use
		 * 
		 * @throws ContractException {@link ProtobufTaskitError#INVALID_TRANSLATION_SPEC_INPUT_CLASS}
		 *                           if the given inputClassRef is not assignable from
		 *                           {@linkplain Message} nor
		 *                           {@linkplain ProtocolMessageEnum}
		 * 
		 * @throws RuntimeException  if there is any issue using reflection to invoke
		 *                           either the 'getDefaultInstance' method on a
		 *                           {@link Message} type or invoking the 'forNumber(0)'
		 *                           method on the {@link ProtocolMessageEnum} type
		 */
		<U> void populate(Class<U> classRef) {
			String typeUrl;
			if (ProtocolMessageEnum.class.isAssignableFrom(classRef) && ProtocolMessageEnum.class != classRef) {
				typeUrl = ProtobufTaskitEngineHelper.getDefaultEnum(classRef.asSubclass(ProtocolMessageEnum.class))
						.getDescriptorForType().getFullName();
				this.typeUrlToClassMap.putIfAbsent(typeUrl, classRef);
				return;
			}

			if (Message.class.isAssignableFrom(classRef) && Message.class != classRef) {
				Message message = ProtobufTaskitEngineHelper.getDefaultMessage(classRef.asSubclass(Message.class));
				typeUrl = message.getDescriptorForType().getFullName();

				this.typeUrlToClassMap.putIfAbsent(typeUrl, classRef);
				this.descriptorSet.add(message.getDescriptorForType());
				return;
			}

			throw new ContractException(ProtobufTaskitError.INVALID_TRANSLATION_SPEC_INPUT_CLASS);
		}

	}

	/**
	 * Returns a new builder for the Protobuf Json Taskit engine.
	 */
	public static Builder builder() {
		return new Builder(new Data());
	}

	/**
	 * Package access for testing.
	 * 
	 * @return the JSONParser instance for this engine
	 */
	Parser getJsonParser() {
		return this.data.jsonParser;
	}

	/**
	 * Package access for testing.
	 * 
	 * @return the JSONPrinter instance for this engine
	 */
	Printer getJsonPrinter() {
		return this.data.jsonPrinter;
	}

	@Override
	protected void writeToFile(File file, Message message) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		this.data.jsonPrinter.appendTo(message, writer);

		writer.flush();
	}

	@Override
	protected Message readFile(File file, Message.Builder builder) throws IOException {
		Reader buffReader = new BufferedReader(new FileReader(file));

		this.data.jsonParser.merge(buffReader, builder);

		return builder.build();
	}

}
