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

import com.google.protobuf.Any;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Parser;
import com.google.protobuf.util.JsonFormat.Printer;
import com.google.protobuf.util.JsonFormat.TypeRegistry;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngineBuilder;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineType;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.AnyTranslationSpec;
import gov.hhs.aspr.ms.util.errors.ContractException;

/**
 * Protobuf TaskitEngine that allows for conversion between POJOs and
 * Protobuf Messages, extends {@link TaskitEngine}
 */
public final class ProtobufTaskitEngine implements ITaskitEngine {
    private final Data data;

    private ProtobufTaskitEngine(Data data) {
        this.data = data;
    }

    private final static class Data {
        // this is used specifically for Any message types to pack and unpack them
        private final Map<String, Class<?>> typeUrlToClassMap = new LinkedHashMap<>();

        private TaskitEngine taskitEngine;

        // these two fields are used for reading and writing Protobuf Messages to/from
        // JSON
        private Parser jsonParser;
        private Printer jsonPrinter;

        private Data() {
        }
    }

    public final static class Builder implements ITaskitEngineBuilder {
        private ProtobufTaskitEngine.Data data;
        private Set<Descriptor> descriptorSet = new LinkedHashSet<>();
        private final Set<FieldDescriptor> defaultValueFieldsToPrint = new LinkedHashSet<>();
        private boolean ignoringUnknownFields = true;
        private boolean includingDefaultValueFields = false;

        private TaskitEngine.Builder taskitEngineBuilder = TaskitEngine.builder();

        private Builder(ProtobufTaskitEngine.Data data) {
            this.data = data;
        }

        /**
         * returns a new instance of a ProtobufTaskitEngine that has a jsonParser
         * and jsonWriter that include all the typeUrls for all added TranslationSpecs
         * and their respective Protobuf Message types
         */
        @Override
        public ProtobufTaskitEngine build() {
            ProtobufTaskitEngineHelper primitiveTranslationSpecs = new ProtobufTaskitEngineHelper();

            this.data.typeUrlToClassMap.putAll(primitiveTranslationSpecs.getPrimitiveTypeUrlToClassMap());

            primitiveTranslationSpecs.getPrimitiveInputTranslatorSpecMap().values().forEach(
                    (translationSpec) -> this.taskitEngineBuilder.addTranslationSpec(translationSpec));

            this.taskitEngineBuilder.setTaskitEngineType(TaskitEngineType.PROTOBUF);

            this.data.taskitEngine = this.taskitEngineBuilder.build();

            TypeRegistry.Builder typeRegistryBuilder = TypeRegistry.newBuilder();
            this.descriptorSet.addAll(new ProtobufTaskitEngineHelper().getPrimitiveDescriptors());

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

            ProtobufTaskitEngine taskitEngine = new ProtobufTaskitEngine(this.data);

            this.data.taskitEngine.initTranslationSpecs(taskitEngine);

            return taskitEngine;
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

            return this;
        }

        /**
         * Calls
         * {@link TaskitEngine.Builder#addParentChildClassRelationship(Class, Class)}
         */
        @Override
        public <M extends U, U> Builder addParentChildClassRelationship(Class<M> classRef, Class<U> parentClassRef) {
            this.taskitEngineBuilder.addParentChildClassRelationship(classRef, parentClassRef);

            return this;
        }

        /**
         * Calls
         * {@link TaskitEngine.Builder#setTaskitEngineType(TaskitEngineType)}
         */
        @Override
        public ITaskitEngineBuilder setTaskitEngineType(TaskitEngineType taskitEngineType) {
            this.taskitEngineBuilder.setTaskitEngineType(taskitEngineType);

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
                this.data.typeUrlToClassMap.putIfAbsent(typeUrl, classRef);
                return;
            }

            if (Message.class.isAssignableFrom(classRef) && Message.class != classRef) {
                Message message = ProtobufTaskitEngineHelper.getDefaultMessage(classRef.asSubclass(Message.class));
                typeUrl = message.getDescriptorForType().getFullName();

                this.data.typeUrlToClassMap.putIfAbsent(typeUrl, classRef);
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
     * @throws ContractException if the given object is not assignable from
     *                           {@link Message}
     * @throws IOException       if there is an IOException during writing
     */
    @Override
    public <M> void write(Path path, M object) throws IOException {
        if (!Message.class.isAssignableFrom(object.getClass())) {
            throw new ContractException(TaskitError.INVALID_OUTPUT_CLASS, Message.class.getName());
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
     * @throws ContractException {@linkplain TaskitError#INVALID_INPUT_CLASS}
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
     * @throws ContractException {@linkplain TaskitError#INVALID_INPUT_CLASS}
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
            throw new ContractException(TaskitError.INVALID_INPUT_CLASS, Message.class.getName());
        }

        Reader reader = new BufferedReader(new FileReader(path.toFile()));

        Message.Builder builder = ProtobufTaskitEngineHelper.getBuilderForMessage(classRef.asSubclass(Message.class));

        this.data.jsonParser.merge(reader, builder);

        Message message = builder.build();

        return classRef.cast(message);
    }

    /**
     * Given an object of type {@link Any}, will translate it to the resulting
     * object
     * <p>
     * Will ultimately use the {@link AnyTranslationSpec} to accomplish this
     * </p>
     * 
     * @param <T> the return type
     */
    public <T> T getObjectFromAny(Any anyValue) {
        return translateObject(anyValue);
    }

    /**
     * Given an object , will translate it to an {@link Any} type
     * <p>
     * Will use the {@link AnyTranslationSpec} to accomplish this
     * </p>
     */
    public Any getAnyFromObject(Object object) {
        return translateObjectAsClassUnsafe(object, Any.class);
    }

    /**
     * Given an object , will translate it to an {@link Any} type
     * <p>
     * This method call differs from {@link #getAnyFromObject(Object)} in that it
     * will first translate the object using the safe parent class by calling
     * {@link #translateObjectAsClassSafe(Object, Class)} and will then use the
     * {@link AnyTranslationSpec} to wrap the resulting translated object in an
     * {@link Any}
     * </p>
     * 
     * @param <U> the parent Class
     * @param <M> the object class
     * @throws ContractException {@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           parentClassRef
     */
    public <U, M extends U> Any getAnyFromObjectAsClassSafe(M object, Class<U> parentClassRef) {
        U translatedObject = translateObjectAsClassSafe(object, parentClassRef);

        return translateObjectAsClassUnsafe(translatedObject, Any.class);
    }

    /**
     * Given an object, uses the class of the object to obtain the translationSpec
     * and then calls {@link TranslationSpec#translate(Object)}
     * <p>
     * this conversion method will be used approx ~90% of the time
     * </p>
     * 
     * @param <T> the return type after translating
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           <li>{@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           objects class</li>
     *                           </ul>
     */
    @Override
    public <T> T translateObject(Object object) {
        return this.data.taskitEngine.translateObject(object);
    }

    /**
     * Given an object, uses the parent class of the object to obtain the
     * translationSpec and then calls {@link TranslationSpec#translate(Object)}
     * <p>
     * This method call is safe in the sense that the type parameters ensure that
     * the passed in object is actually a child of the passed in parentClassRef
     * </p>
     * <p>
     * this conversion method will be used approx ~7% of the time
     * </p>
     * 
     * @param <T> the return type after translating
     * @param <M> the type of the object; extends U
     * @param <U> the parent type of the object and the class for which
     *            translationSpec you want to use
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           <li>{@linkplain TaskitError#NULL_CLASS_REF}
     *                           if the passed in parentClassRef is null</li>
     *                           <li>{@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           objects class</li>
     *                           </ul>
     */
    @Override
    public <T, M extends U, U> T translateObjectAsClassSafe(M object, Class<U> classRef) {
        return this.data.taskitEngine.translateObjectAsClassSafe(object, classRef);
    }

    /**
     * Given an object, uses the passed in class to obtain the translationSpec and
     * then calls {@link TranslationSpec#translate(Object)}
     * <p>
     * This method call is unsafe in the sense that the type parameters do not
     * ensure any relationship between the passed in object and the passed in
     * classRef.
     * </p>
     * <p>
     * A common use case for using this conversion method would be to call a
     * translationSpec that will wrap the given object in another object.
     * </p>
     * <p>
     * this conversion method will be used approx ~3% of the time
     * </p>
     * 
     * @param <T> the return type after translating
     * @param <M> the type of the object
     * @param <U> the type of the class for which translationSpec you want to use
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           <li>{@linkplain TaskitError#NULL_CLASS_REF}
     *                           if the passed in objectClassRef is null</li>
     *                           <li>{@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           objects class</li>
     *                           </ul>
     */
    @Override
    public <T, M, U> T translateObjectAsClassUnsafe(M object, Class<U> classRef) {
        return this.data.taskitEngine.translateObjectAsClassUnsafe(object, classRef);
    }

    /**
     * Given a typeUrl, returns the associated Protobuf Message type Class, if it
     * has been previously provided
     * 
     * @throws ContractException {@linkplain ProtobufTaskitError#UNKNOWN_TYPE_URL}
     *                           if the given type url does not exist. This could be
     *                           because the type url was never provided or the type
     *                           url itself is malformed
     */
    public Class<?> getClassFromTypeUrl(String typeUrl) {
        if (this.data.typeUrlToClassMap.containsKey(typeUrl)) {
            return this.data.typeUrlToClassMap.get(typeUrl);
        }

        throw new ContractException(ProtobufTaskitError.UNKNOWN_TYPE_URL,
                "Unable to find corresponding class for: " + typeUrl);
    }

    /**
     * Returns an instance of the BaseTaskitEngine for this translation engine
     */
    @Override
    public TaskitEngine getTaskitEngine() {
        return this.data.taskitEngine;
    }

    /**
     * returns the {@link TaskitEngineType} of this TaskitEngine
     * 
     * guaranteed to NOT be {@link TaskitEngineType#UNKNOWN}
     */
    @Override
    public TaskitEngineType getTaskitEngineType() {
        return this.data.taskitEngine.getTaskitEngineType();
    }

    /**
     * Returns a set of all {@link TranslationSpec}s associated with this
     * TaskitEngine
     */
    @Override
    public Set<ITranslationSpec> getTranslationSpecs() {
        return this.data.taskitEngine.getTranslationSpecs();
    }

}
