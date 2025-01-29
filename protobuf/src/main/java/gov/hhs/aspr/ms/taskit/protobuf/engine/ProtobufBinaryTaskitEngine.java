package gov.hhs.aspr.ms.taskit.protobuf.engine;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;
import com.google.protobuf.ProtocolMessageEnum;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineData;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorContext;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslator;
import gov.hhs.aspr.ms.util.errors.ContractException;

public class ProtobufBinaryTaskitEngine extends ProtobufTaskitEngine {

    protected ProtobufBinaryTaskitEngine(Map<String, Class<?>> typeUrlToClassMap, TaskitEngineData taskitEngineData) {
        super(typeUrlToClassMap, taskitEngineData, ProtobufTaskitEngineId.BINARY_ENGINE_ID);
    }

    /**
     * Builder for the ProtobufJsonTaskitEngine.
     */
    public final static class Builder implements IProtobufTaskitEngineBuilder {
        private Set<Descriptor> descriptorSet = new LinkedHashSet<>();

        // this is used specifically for Any message types to pack and unpack them
        private final Map<String, Class<?>> typeUrlToClassMap = new LinkedHashMap<>();

        private TaskitEngineData.Builder taskitEngineDataBuilder = TaskitEngineData.builder();

        private Builder() {
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
        public ProtobufBinaryTaskitEngine build() {
            this.addTranslator(ProtobufTranslator.getTranslator());

            ProtobufBinaryTaskitEngine protoBinaryTaskitEngine = new ProtobufBinaryTaskitEngine(this.typeUrlToClassMap,
                    this.taskitEngineDataBuilder.build());

            protoBinaryTaskitEngine.init();

            return protoBinaryTaskitEngine;
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
        return new Builder();
    }

    @Override
    protected void writeToFile(File file, Message message) throws IOException {
        BufferedOutputStream bOutputStream = new BufferedOutputStream(new FileOutputStream(file));

        message.writeTo(bOutputStream);

        bOutputStream.flush();
    }

    @Override
    protected Message readFile(File file, Message.Builder builder) throws IOException {
        BufferedInputStream bInputStream = new BufferedInputStream(new FileInputStream(file));

        builder.mergeFrom(bInputStream);

        return builder.build();
    }

}
