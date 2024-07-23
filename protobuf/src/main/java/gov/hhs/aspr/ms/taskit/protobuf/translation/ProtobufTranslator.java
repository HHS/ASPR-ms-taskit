package gov.hhs.aspr.ms.taskit.protobuf.translation;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Any;

import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.protobuf.engine.IProtobufTaskitEngineBuilder;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.AnyTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.BooleanTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.DateTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.DoubleTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.EnumTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.FloatTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.IntegerTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.LongTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.StringTranslationSpec;

/**
 * Translator for the primitive Protobuf Translation Specs
 * <p>
 * this is added to every {@link ProtobufTaskitEngine} and the specs are
 * specifically used in conjunction with the {@link Any} type
 */
public class ProtobufTranslator {
    private ProtobufTranslator() {
    }

    protected static List<ITranslationSpec> getTranslationSpecs() {
        List<ITranslationSpec> list = new ArrayList<>();

        list.add(new BooleanTranslationSpec());
        list.add(new IntegerTranslationSpec());
        list.add(new LongTranslationSpec());
        list.add(new StringTranslationSpec());
        list.add(new FloatTranslationSpec());
        list.add(new DoubleTranslationSpec());
        list.add(new DateTranslationSpec());
        list.add(new EnumTranslationSpec());
        list.add(new AnyTranslationSpec());

        return list;
    }

    private static Translator.Builder builder() {
        Translator.Builder builder = Translator.builder()
                .setTranslatorId(ProtobufTranslatorId.TRANSLATOR_ID)
                .setInitializer((translatorContext) -> {
                    IProtobufTaskitEngineBuilder translationEngineBuilder = translatorContext
                            .getTaskitEngineBuilder(IProtobufTaskitEngineBuilder.class);

                    for (ITranslationSpec translationSpec : getTranslationSpecs()) {
                        translationEngineBuilder.addTranslationSpec(translationSpec);
                    }

                });

        return builder;
    }

    /**
     * Returns a Translator that includes primitive TranslationSpecs for the
     * ProtobufTaskitEngine that are used for packing/unpacking {@link Any}s
     */
    public static Translator getTranslator() {
        return builder().build();
    }
}
