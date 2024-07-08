package gov.hhs.aspr.ms.taskit.protobuf.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
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

import gov.hhs.aspr.ms.taskit.core.engine.TaskitCoreError;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorContext;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;
import gov.hhs.aspr.ms.util.errors.ContractException;

/**
 * Protobuf TaskitEngine that allows for conversion between POJOs and
 * Protobuf Messages, extends {@link ProtobufTaskitEngine}
 */
public final class ProtobufJsonTaskitEngine extends ProtobufTaskitEngine {
    private final Data data;

    private ProtobufJsonTaskitEngine(Data data, Map<String, Class<?>> typeUrlToClassMap, TaskitEngine taskitEngine) {
        super(typeUrlToClassMap, taskitEngine);
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

    public final static class Builder implements IProtobufTaskitEngineBuilder {
        private ProtobufJsonTaskitEngine.Data data;
        private Set<Descriptor> descriptorSet = new LinkedHashSet<>();
        private final Set<FieldDescriptor> defaultValueFieldsToPrint = new LinkedHashSet<>();
        private boolean ignoringUnknownFields = true;
        private boolean includingDefaultValueFields = false;

        // this is used specifically for Any message types to pack and unpack them
        private final Map<String, Class<?>> typeUrlToClassMap = new LinkedHashMap<>();

        private TaskitEngine taskitEngine;

        private TaskitEngine.Builder taskitEngineBuilder = TaskitEngine.builder();

        private Builder(ProtobufJsonTaskitEngine.Data data) {
            this.data = data;
        }

        /**
         * returns a new instance of a ProtobufTaskitEngine that has a jsonParser
         * and jsonWriter that include all the typeUrls for all added TranslationSpecs
         * and their respective Protobuf Message types
         */
        @Override
        public ProtobufJsonTaskitEngine build() {

            this.typeUrlToClassMap.putAll(ProtobufTaskitEngineHelper.getPrimitiveTypeUrlToClassMap());

            ProtobufTaskitEngineHelper.getPrimitiveTranslationSpecs().forEach(
                    (translationSpec) -> this.taskitEngineBuilder.addTranslationSpec(translationSpec));

            this.taskitEngineBuilder.setTaskitEngineId(ProtobufTaskitEngineId.JSON_ENGINE_ID);

            this.taskitEngine = this.taskitEngineBuilder.build();

            TypeRegistry.Builder typeRegistryBuilder = TypeRegistry.newBuilder();
            this.descriptorSet.addAll(ProtobufTaskitEngineHelper.getPrimitiveDescriptors());

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
                    this.typeUrlToClassMap, this.taskitEngine);

            this.taskitEngine.init(protoJsonTaskitEngine);

            return protoJsonTaskitEngine;
        }

        /**
         * Whether the jsonParser should ignore fields in the JSON that don't exist in
         * the Protobuf Message. defaults to true
         */
        public Builder setIgnoringUnknownFields(boolean ignoringUnknownFields) {
            this.ignoringUnknownFields = ignoringUnknownFields;
            return this;
        }

        /**
         * Whether the jsonWriter should blanket print all values that are default. The
         * default values can be found here:
         * https://protobuf.dev/programming-guides/proto3/#default defaults to false
         */
        public Builder setIncludingDefaultValueFields(boolean includingDefaultValueFields) {
            this.includingDefaultValueFields = includingDefaultValueFields;
            return this;
        }

        /**
         * Contrary to {@link Builder#setIncludingDefaultValueFields(boolean)} which
         * will either print all default values or not, this will set a specific field
         * to print the default value for
         */
        public Builder addFieldToIncludeDefaultValue(FieldDescriptor fieldDescriptor) {
            this.defaultValueFieldsToPrint.add(fieldDescriptor);

            return this;
        }

        /**
         * Calls
         * {@link TaskitEngine.Builder#addTranslationSpec(TranslationSpec)}
         * <p>
         * then populates the type urls for all Protobuf Message types that exist within
         * the translationSpec
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@link ProtobufTaskitError#INVALID_TRANSLATION_SPEC_INPUT_CLASS}
         *                           if the given inputClassRef is not assignable from
         *                           {@linkplain Message} nor
         *                           {@linkplain ProtocolMessageEnum}</li>
         *                           <li>{@link ProtobufTaskitError#INVALID_TRANSLATION_SPEC}
         *                           if the given translation spec is not assignable
         *                           from {@linkplain ProtobufTranslationSpec}</li>
         *                           </ul>
         */
        @Override
        public <APP, INPUT> Builder addTranslationSpec(TranslationSpec<APP, INPUT> translationSpec) {
            if (!ProtobufTranslationSpec.class.isAssignableFrom(translationSpec.getClass())) {
                throw new ContractException(ProtobufTaskitError.INVALID_TRANSLATION_SPEC);
            }

            this.taskitEngineBuilder.addTranslationSpec(translationSpec);

            populate(translationSpec.getInputObjectClass());
            return this;
        }

        /**
         * Calls {@link TaskitEngine.Builder#addTranslator(Translator)}
         */
        @Override
        public Builder addTranslator(Translator translator) {
            this.taskitEngineBuilder.addTranslator(translator);

            translator.initialize(new TranslatorContext(this));

            return this;
        }

        /**
         * checks the class to determine if it is a ProtocolMessageEnum or a Message and
         * if so, gets the Descriptor (which is akin to a class but for a Protobuf
         * Message) for it to get the full name and add the typeUrl to the internal
         * descriptorMap and typeUrlToClassMap
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
                        .getDescriptorForType()
                        .getFullName();
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
     * Returns a new builder
     */
    public static Builder builder() {
        return new Builder(new Data());
    }

    Parser getJsonParser() {
        return this.data.jsonParser;
    }

    Printer getJsonPrinter() {
        return this.data.jsonPrinter;
    }

    /**
     * writes the given object to the path provided.
     * 
     * @param <M> the type of the object
     * 
     * @throws ContractException {@link TaskitCoreError#INVALID_OUTPUT_CLASS} if the
     *                           given object is not assignable from
     *                           {@link Message}
     * @throws IOException       if there is an IOException during writing
     */
    @Override
    public <M> void write(Path path, M object) throws IOException {
        if (!Message.class.isAssignableFrom(object.getClass())) {
            throw new ContractException(TaskitCoreError.INVALID_OUTPUT_CLASS, Message.class.getName());
        }

        Message message = Message.class.cast(object);

        BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()));
        this.data.jsonPrinter.appendTo(message, writer);

        writer.flush();
    }

    /**
     * translates the given object as the given classRef and then writes that
     * object to the path provided
     * 
     * @param <M> the type of the object
     * @param <U> the class to translate the object as
     * 
     * @throws ContractException if the given object is not assignable from
     *                           {@link Message}
     * @throws IOException       if there is an IOException during writing
     */
    @Override
    public <U, M extends U> void translateAndWrite(Path path, M object, Class<U> classRef) throws IOException {
        write(path, translateObjectAsClassSafe(object, classRef));
    }

    /**
     * translates the given object and then writes that
     * object to the path provided
     * 
     * @param <M> the type of the object
     * 
     * @throws ContractException if the given object is not assignable from
     *                           {@link Message}
     * @throws IOException       if there is an IOException during writing
     */
    @Override
    public <M> void translateAndWrite(Path path, M object) throws IOException {
        write(path, translateObject(object));
    }

    /**
     * Reads the file at the given path into an object of the type of the classRef.
     * Then translates the resulting object into it's corresponding object using the
     * object's {@link TranslationSpec}
     * 
     * 
     * @param <U> the type of the inputClass
     * @param <T> the return type
     * @throws ContractException {@linkplain TaskitCoreError#INVALID_INPUT_CLASS}
     *                           if the given inputClassRef is not assignable
     *                           from
     *                           {@linkplain Message}
     * @throws RuntimeException
     *                           <ul>
     *                           <li>if there is an issue getting the builder
     *                           method from the inputClassRef</li>
     *                           </ul>
     * 
     * @throws IOException
     *                           <ul>
     *                           <li>if there is an IOException during reading</li>
     *                           <li>if there is an issue merging the file into the
     *                           resulting Protobuf Message builder
     *                           </li>
     *                           </ul>
     */
    @Override
    public <T, U> T readAndTranslate(Path path, Class<U> classRef) throws IOException {
        return translateObject(read(path, classRef));
    }

    /**
     * Reads the file at the given path into an object of the type of the classRef.
     * 
     * @param <U> the type of the inputClass
     * 
     * @throws ContractException {@linkplain TaskitCoreError#INVALID_INPUT_CLASS}
     *                           if the given inputClassRef is not assignable
     *                           from
     *                           {@linkplain Message}
     * @throws RuntimeException
     *                           <ul>
     *                           <li>if there is an issue getting the builder
     *                           method from the inputClassRef</li>
     *                           </ul>
     * 
     * @throws IOException
     *                           <ul>
     *                           <li>if there is an IOException during reading</li>
     *                           <li>if there is an issue merging the file into the
     *                           resulting Protobuf Message builder
     *                           </li>
     *                           </ul>
     */
    @Override
    public <U> U read(Path path, Class<U> classRef) throws IOException {
        if (!Message.class.isAssignableFrom(classRef)) {
            throw new ContractException(TaskitCoreError.INVALID_INPUT_CLASS, Message.class.getName());
        }

        Reader reader = new BufferedReader(new FileReader(path.toFile()));

        Message.Builder builder = ProtobufTaskitEngineHelper.getBuilderForMessage(classRef.asSubclass(Message.class));

        this.data.jsonParser.merge(reader, builder);

        Message message = builder.build();

        return classRef.cast(message);
    }

}
