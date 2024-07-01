package gov.hhs.aspr.ms.taskit.core.translation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestComplexObjectTranslatorId;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestObjectTranslatorId;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;

public class AT_Translator {

    @Test
    @UnitTestMethod(target = Translator.class, name = "getInitializer", args = {})
    public void testGetInitializer() {
        Consumer<TranslatorContext> expectedInitializer = (translatorContext) -> {
        };
        Translator testTranslator = Translator.builder().setInitializer(expectedInitializer)
                .setTranslatorId(new TranslatorId() {
                }).build();

        assertEquals(expectedInitializer, testTranslator.getInitializer());
    }

    @Test
    @UnitTestMethod(target = Translator.class, name = "getTranslatorId", args = {})
    public void testGetTranslatorId() {
        TranslatorId expectedTranslatorId = new TranslatorId() {
        };
        Translator testTranslator = Translator.builder().setInitializer((translatorContext) -> {
        }).setTranslatorId(expectedTranslatorId).build();

        assertEquals(expectedTranslatorId, testTranslator.getTranslatorId());
    }

    @Test
    @UnitTestMethod(target = Translator.class, name = "getTranslatorDependencies", args = {})
    public void testGetTranslatorDependencies() {
        TranslatorId expectedTranslatorId = new TranslatorId() {
        };
        Translator testTranslator = Translator.builder().setInitializer((translatorContext) -> {
        }).setTranslatorId(expectedTranslatorId).addDependency(TestObjectTranslatorId.TRANSLATOR_ID)
                .addDependency(TestComplexObjectTranslatorId.TRANSLATOR_ID).build();

        Set<TranslatorId> expectedDependencies = new LinkedHashSet<>();
        expectedDependencies.add(TestObjectTranslatorId.TRANSLATOR_ID);
        expectedDependencies.add(TestComplexObjectTranslatorId.TRANSLATOR_ID);

        Set<TranslatorId> actualDependencies = testTranslator.getTranslatorDependencies();

        assertEquals(expectedDependencies, actualDependencies);
    }

    @Test
    @UnitTestMethod(target = Translator.class, name = "hashCode", args = {})
    public void testHashCode() {
        TranslatorId translatorIdA = new TranslatorId() {
        };
        TranslatorId translatorIdB = new TranslatorId() {
        };
        Consumer<TranslatorContext> consumerA = (translatorContext) -> {
        };

        Translator translatorA = Translator.builder().setInitializer(consumerA).setTranslatorId(translatorIdA).build();

        Translator translatorB = Translator.builder().setInitializer(consumerA).setTranslatorId(translatorIdB).build();

        Translator translatorC = Translator.builder().setInitializer(consumerA).setTranslatorId(translatorIdA)
                .addDependency(translatorIdB).build();

        Translator translatorD = Translator.builder().setInitializer(consumerA).setTranslatorId(translatorIdA)
                .addDependency(translatorIdB).build();

        // if same instance, equal
        assertEquals(translatorA.hashCode(), translatorA.hashCode());

        // if different class, not equal
        assertNotEquals(translatorA.hashCode(), new Object().hashCode());

        // if different translator id, not equal
        assertNotEquals(translatorA.hashCode(), translatorB.hashCode());

        // if same id, but different dependencies, not equal
        assertNotEquals(translatorA.hashCode(), translatorC.hashCode());

        // if same id and dependencies, equal
        assertEquals(translatorC.hashCode(), translatorD.hashCode());
    }

    @Test
    @UnitTestMethod(target = Translator.class, name = "equals", args = { Object.class })
    public void testEquals() {
        TranslatorId translatorIdA = new TranslatorId() {
        };
        TranslatorId translatorIdB = new TranslatorId() {
        };
        Consumer<TranslatorContext> consumerA = (translatorContext) -> {
        };
        Translator translatorA = Translator.builder().setInitializer(consumerA).setTranslatorId(translatorIdA).build();

        Translator translatorB = Translator.builder().setInitializer(consumerA).setTranslatorId(translatorIdB).build();

        Translator translatorC = Translator.builder().setInitializer(consumerA).setTranslatorId(translatorIdA)
                .addDependency(translatorIdB).build();

        Translator translatorD = Translator.builder().setInitializer(consumerA).setTranslatorId(translatorIdA)
                .addDependency(translatorIdB).build();

        // if same instance, equal
        assertEquals(translatorA, translatorA);

        // if null, not equal
        assertNotEquals(translatorA, null);

        // if different class, not equal
        assertNotEquals(translatorA, new Object());

        // if different translator id, not equal
        assertNotEquals(translatorA, translatorB);

        // if same id, but different dependencies, not equal
        assertNotEquals(translatorA, translatorC);

        // if same id and dependencies, equal
        assertEquals(translatorC, translatorD);

        Translator.Data data = new Translator.Data();
        assertEquals(data, data);
        assertNotEquals(data, null);
        assertNotEquals(data, new Object());
    }

    @Test
    @UnitTestMethod(target = Translator.class, name = "builder", args = {})
    public void testBuilder() {
        assertNotNull(Translator.builder());
    }

    @Test
    @UnitTestMethod(target = Translator.Builder.class, name = "build", args = {})
    public void testBuild() {
        TranslatorId translatorIdA = new TranslatorId() {
        };
        Translator translatorA = Translator.builder().setInitializer((translatorContext) -> {
        }).setTranslatorId(translatorIdA).build();

        assertNotNull(translatorA);

        // preconditions
        // null initializer
        ContractException contractException = assertThrows(ContractException.class, () -> {
            Translator.builder().setTranslatorId(translatorIdA).build();
        });

        assertEquals(TaskitError.NULL_INIT_CONSUMER, contractException.getErrorType());

        // null translatorId
        contractException = assertThrows(ContractException.class, () -> {
            Translator.builder().setInitializer((translatorContext) -> {
            }).build();
        });

        assertEquals(TaskitError.NULL_TRANSLATOR_ID, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = Translator.Builder.class, name = "setTranslatorId", args = { TranslatorId.class })
    public void testSetTranslatorId() {
        TranslatorId translatorIdA = new TranslatorId() {
        };
        Translator translatorA = Translator.builder().setInitializer((translatorContext) -> {
        }).setTranslatorId(translatorIdA).build();

        assertEquals(translatorIdA, translatorA.getTranslatorId());

        // preconditions
        // null translatorId
        ContractException contractException = assertThrows(ContractException.class, () -> {
            Translator.builder().setTranslatorId(null);
        });

        assertEquals(TaskitError.NULL_TRANSLATOR_ID, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = Translator.Builder.class, name = "setInitializer", args = { Consumer.class })
    public void testSetInitializer() {
        Consumer<TranslatorContext> expectedInitializer = (translatorContext) -> {
        };
        Translator testTranslator = Translator.builder().setInitializer(expectedInitializer)
                .setTranslatorId(new TranslatorId() {
                }).build();

        assertEquals(expectedInitializer, testTranslator.getInitializer());

        // preconditions
        // null initializer
        ContractException contractException = assertThrows(ContractException.class, () -> {
            Translator.builder().setInitializer(null);
        });

        assertEquals(TaskitError.NULL_INIT_CONSUMER, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = Translator.Builder.class, name = "addDependency", args = { TranslatorId.class })
    public void testAddDependency() {
        TranslatorId expectedTranslatorId = new TranslatorId() {
        };
        Translator testTranslator = Translator.builder().setInitializer((translatorContext) -> {
        }).setTranslatorId(expectedTranslatorId).addDependency(TestObjectTranslatorId.TRANSLATOR_ID)
                .addDependency(TestComplexObjectTranslatorId.TRANSLATOR_ID).build();

        Set<TranslatorId> expectedDependencies = new LinkedHashSet<>();
        expectedDependencies.add(TestObjectTranslatorId.TRANSLATOR_ID);
        expectedDependencies.add(TestComplexObjectTranslatorId.TRANSLATOR_ID);

        Set<TranslatorId> actualDependencies = testTranslator.getTranslatorDependencies();

        assertEquals(expectedDependencies, actualDependencies);

        // preconditions
        // null dependency
        ContractException contractException = assertThrows(ContractException.class, () -> {
            Translator.builder().addDependency(null);
        });

        assertEquals(TaskitError.NULL_DEPENDENCY, contractException.getErrorType());

        // duplicate dependency
        contractException = assertThrows(ContractException.class, () -> {
            Translator.builder().addDependency(TestObjectTranslatorId.TRANSLATOR_ID)
                    .addDependency(TestObjectTranslatorId.TRANSLATOR_ID);
        });

        assertEquals(TaskitError.DUPLICATE_DEPENDENCY, contractException.getErrorType());
    }
}
