package gov.hhs.aspr.ms.taskit.protobuf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import gov.hhs.aspr.ms.taskit.core.translation.BaseTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.protobuf.translationSpecs.AnyTranslationSpec;
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

        private TaskitEngine baseTaskitEngine;

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

        private TaskitEngine.Builder baseTaskitEngineBuilder = TaskitEngine.builder();

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
            PrimitiveTranslationSpecs primitiveTranslationSpecs = new PrimitiveTranslationSpecs();

            this.data.typeUrlToClassMap.putAll(primitiveTranslationSpecs.getPrimitiveTypeUrlToClassMap());

            primitiveTranslationSpecs.getPrimitiveInputTranslatorSpecMap().values().forEach(
                    (translationSpec) -> this.baseTaskitEngineBuilder.addTranslationSpec(translationSpec));

            this.baseTaskitEngineBuilder.setTaskitEngineType(TaskitEngineType.PROTOBUF);

            this.data.baseTaskitEngine = this.baseTaskitEngineBuilder.build();

            TypeRegistry.Builder typeRegistryBuilder = TypeRegistry.newBuilder();
            this.descriptorSet.addAll(new PrimitiveTranslationSpecs().getPrimitiveDescriptors());

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

            this.data.baseTaskitEngine.initTranslationSpecs(taskitEngine);

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
         *                           <li>{@link ProtobufCoreTranslationError#INVALID_INPUT_CLASS}
         *                           if the given inputClassRef is not assignable from
         *                           {@linkplain Message} nor
         *                           {@linkplain ProtocolMessageEnum}</li>
         *                           <li>{@link ProtobufCoreTranslationError#INVALID_TRANSLATION_SPEC}
         *                           if the given translation spec is not assignable
         *                           from {@linkplain ProtobufTranslationSpec}</li>
         *                           </ul>
         */
        @Override
        public <APP, INPUT> Builder addTranslationSpec(TranslationSpec<APP, INPUT> translationSpec) {
            this.baseTaskitEngineBuilder.addTranslationSpec(translationSpec);

            if (!ProtobufTranslationSpec.class.isAssignableFrom(translationSpec.getClass())) {
                throw new ContractException(ProtobufCoreTranslationError.INVALID_TRANSLATION_SPEC);
            }

            populate(translationSpec.getInputObjectClass());
            return this;
        }

        /**
         * Calls {@link TaskitEngine.Builder#addTranslator(Translator)}
         */
        @Override
        public Builder addTranslator(Translator translator) {
            this.baseTaskitEngineBuilder.addTranslator(translator);

            return this;
        }

        /**
         * Calls
         * {@link TaskitEngine.Builder#addParentChildClassRelationship(Class, Class)}
         */
        @Override
        public <M extends U, U> Builder addParentChildClassRelationship(Class<M> classRef, Class<U> markerInterface) {
            this.baseTaskitEngineBuilder.addParentChildClassRelationship(classRef, markerInterface);

            return this;
        }

        /**
         * Calls
         * {@link TaskitEngine.Builder#setTaskitEngineType(TaskitEngineType)}
         */
        @Override
        public ITaskitEngineBuilder setTaskitEngineType(TaskitEngineType taskitEngineType) {
            this.baseTaskitEngineBuilder.setTaskitEngineType(taskitEngineType);

            return this;
        }

        /**
         * checks the class to determine if it is a ProtocolMessageEnum or a Message and
         * if so, gets the Descriptor (which is akin to a class but for a Protobuf
         * Message) for it to get the full name and add the typeUrl to the internal
         * descriptorMap and typeUrlToClassMap
         * 
         * @throws ContractException {@link ProtobufCoreTranslationError#INVALID_INPUT_CLASS}
         *                           if the given inputClassRef is not assignable from
         *                           {@linkplain Message} nor
         *                           {@linkplain ProtocolMessageEnum}
         */
        <U> void populate(Class<U> classRef) {
            String typeUrl;
            if (ProtocolMessageEnum.class.isAssignableFrom(classRef) && ProtocolMessageEnum.class != classRef) {
                typeUrl = getDefaultEnum(classRef.asSubclass(ProtocolMessageEnum.class)).getDescriptorForType()
                        .getFullName();
                this.data.typeUrlToClassMap.putIfAbsent(typeUrl, classRef);
                return;
            }

            if (Message.class.isAssignableFrom(classRef) && Message.class != classRef) {
                Message message = getDefaultMessage(classRef.asSubclass(Message.class));
                typeUrl = message.getDescriptorForType().getFullName();

                this.data.typeUrlToClassMap.putIfAbsent(typeUrl, classRef);
                this.descriptorSet.add(message.getDescriptorForType());
                return;
            }

            throw new ContractException(ProtobufCoreTranslationError.INVALID_INPUT_CLASS);
        }

        /**
         * given a Class ref to a Protobuf Message, get the defaultInstance of it
         */
        <U extends Message> U getDefaultMessage(Class<U> classRef) {
            try {
                Method method = classRef.getMethod("getDefaultInstance");
                Object obj = method.invoke(null);
                return classRef.cast(obj);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * given a Class ref to a ProtocolMessageEnum, get the default value for it,
         * enum number 0 within the proto enum
         */
        <U extends ProtocolMessageEnum> U getDefaultEnum(Class<U> classRef) {
            try {
                Method method = classRef.getMethod("forNumber", int.class);
                Object obj = method.invoke(null, 0);
                return classRef.cast(obj);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
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
     * write output implementation
     * <p>
     * Will first convert the object, if needed, and then use the jsonPrinter to
     * take
     * the the converted object and write it to an output file using a
     * BufferedWriter wrapping a FileWriter
     * 
     * @param <U> the type of the optional parent class of the appObject
     * @param <M> the type of the appObject
     * @throws RuntimeException if there is an IOException during writing
     */
    public <U, M extends U> void write(Path outputPath, M outputObject, Optional<Class<U>> outputClassRefOverride)
            throws IOException {
        Message message;
        if (Message.class.isAssignableFrom(outputObject.getClass())) {
            message = Message.class.cast(outputObject);
        } else if (outputClassRefOverride.isPresent()) {
            message = convertObjectAsSafeClass(outputObject, outputClassRefOverride.get());
        } else {
            message = convertObject(outputObject);
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()));
        this.data.jsonPrinter.appendTo(message, writer);

        writer.flush();
    }

    /**
     * Given a reader and a classRef, will read the JSON from the reader, parse it
     * into a JSON Object, merge the resulting JSON Object into the equivalent
     * Protobuf Message and then convert that Protobuf Message to the equivalent
     * AppObject
     * <p>
     * if debug is set on this class, will also print the resulting read in Protobuf
     * Message to console
     * </p>
     * 
     * @param <U> the type of the inputClass
     * @param <T> the return type
     * @throws FileNotFoundException
     * @throws RuntimeException
     *                               <ul>
     *                               <li>if there is an issue getting the builder
     *                               method
     *                               from the inputClassRef</li>
     *                               <li>if there is an issue merging the read in
     *                               JSON
     *                               object into the resulting Protobuf Message
     *                               builder
     *                               </li>
     *                               </ul>
     * @throws ContractException     {@linkplain ProtobufCoreTranslationError#INVALID_READ_INPUT_CLASS_REF}
     *                               if the given inputClassRef is not assignable
     *                               from
     *                               {@linkplain Message}
     */
    public <T, U> T read(Path path, Class<U> inputClassRef) throws IOException {
        if (!Message.class.isAssignableFrom(inputClassRef)) {
            throw new ContractException(ProtobufCoreTranslationError.INVALID_READ_INPUT_CLASS_REF);
        }

        Reader reader = new BufferedReader(new FileReader(path.toFile()));

        Message.Builder builder = getBuilderForMessage(inputClassRef.asSubclass(Message.class));

        this.data.jsonParser.merge(reader, builder);

        Message message = builder.build();

        return convertObject(message);
    }

    <U> Message.Builder getBuilderForMessage(Class<U> messageClass) {

        Method[] messageMethods = messageClass.getDeclaredMethods();

        List<Method> newBuilderMethods = new ArrayList<>();
        for (Method method : messageMethods) {
            if (method.getName().equals("newBuilder")) {
                newBuilderMethods.add(method);
            }
        }

        if (newBuilderMethods.isEmpty()) {
            throw new RuntimeException("The method \"newBuilder\" does not exist");
        }

        for (Method method : newBuilderMethods) {
            if (method.getParameterCount() == 0) {
                try {
                    return (com.google.protobuf.Message.Builder) method.invoke(null);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        throw new RuntimeException(
                "\"newBuilder\" method exists, but it requires arguments, when it is expected to require 0 arguments");
    }

    /**
     * Given an object of type {@link Any}, will convert it to the resulting object
     * <p>
     * Will ultimately use the {@link AnyTranslationSpec} to accomplish this
     * </p>
     * 
     * @param <T> the return type
     */
    public <T> T getObjectFromAny(Any anyValue) {
        return convertObject(anyValue);
    }

    /**
     * Given an object , will convert it to an {@link Any} type
     * <p>
     * Will use the {@link AnyTranslationSpec} to accomplish this
     * </p>
     */
    public Any getAnyFromObject(Object object) {
        return convertObjectAsUnsafeClass(object, Any.class);
    }

    /**
     * Given an object , will convert it to an {@link Any} type
     * <p>
     * This method call differs from {@link #getAnyFromObject(Object)} in that it
     * will first convert the object using the safe parent class by calling
     * {@link #convertObjectAsSafeClass(Object, Class)} and will then use the
     * {@link AnyTranslationSpec} to wrap the resulting converted object in an
     * {@link Any}
     * </p>
     * 
     * @param <U> the parent Class
     * @param <M> the object class
     * @throws ContractException {@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           parentClassRef
     */
    public <U, M extends U> Any getAnyFromObjectAsSafeClass(M object, Class<U> parentClassRef) {
        U convertedObject = convertObjectAsSafeClass(object, parentClassRef);

        return convertObjectAsUnsafeClass(convertedObject, Any.class);
    }

    /**
     * Given an object, uses the class of the object to obtain the translationSpec
     * and then calls {@link TranslationSpec#convert(Object)}
     * <p>
     * this conversion method will be used approx ~90% of the time
     * </p>
     * 
     * @param <T> the return type after converting
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
    public <T> T convertObject(Object object) {
        return this.data.baseTaskitEngine.convertObject(object);
    }

    /**
     * Given an object, uses the parent class of the object to obtain the
     * translationSpec and then calls {@link TranslationSpec#convert(Object)}
     * <p>
     * This method call is safe in the sense that the type parameters ensure that
     * the passed in object is actually a child of the passed in parentClassRef
     * </p>
     * <p>
     * this conversion method will be used approx ~7% of the time
     * </p>
     * 
     * @param <T> the return type after converting
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
    public <T, M extends U, U> T convertObjectAsSafeClass(M object, Class<U> classRef) {
        return this.data.baseTaskitEngine.convertObjectAsSafeClass(object, classRef);
    }

    /**
     * Given an object, uses the passed in class to obtain the translationSpec and
     * then calls {@link TranslationSpec#convert(Object)}
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
     * @param <T> the return type after converting
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
    public <T, M, U> T convertObjectAsUnsafeClass(M object, Class<U> classRef) {
        return this.data.baseTaskitEngine.convertObjectAsUnsafeClass(object, classRef);
    }

    /**
     * Given a typeUrl, returns the associated Protobuf Message type Class, if it
     * has been previously provided
     * 
     * @throws ContractException {@linkplain ProtobufCoreTranslationError#UNKNOWN_TYPE_URL}
     *                           if the given type url does not exist. This could be
     *                           because the type url was never provided or the type
     *                           url itself is malformed
     */
    public Class<?> getClassFromTypeUrl(String typeUrl) {
        if (this.data.typeUrlToClassMap.containsKey(typeUrl)) {
            return this.data.typeUrlToClassMap.get(typeUrl);
        }

        throw new ContractException(ProtobufCoreTranslationError.UNKNOWN_TYPE_URL,
                "Unable to find corresponding class for: " + typeUrl);
    }

    /**
     * Returns an instance of the BaseTaskitEngine for this translation engine
     */
    @Override
    public TaskitEngine getBaseTaskitEngine() {
        return this.data.baseTaskitEngine;
    }

    /**
     * returns the {@link TaskitEngineType} of this TaskitEngine
     * 
     * guaranteed to NOT be {@link TaskitEngineType#UNKNOWN}
     */
    @Override
    public TaskitEngineType getTaskitEngineType() {
        return this.data.baseTaskitEngine.getTaskitEngineType();
    }

    /**
     * Returns a set of all {@link TranslationSpec}s associated with this
     * TaskitEngine
     */
    @Override
    public Set<BaseTranslationSpec> getTranslationSpecs() {
        return this.data.baseTaskitEngine.getTranslationSpecs();
    }

}
