package gov.hhs.aspr.ms.taskit.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.TestTranslationEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.TestComplexObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.input.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.input.TestInputObject;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_TranslationController {

    Path basePath = ResourceHelper.getResourceDir(this.getClass());
    Path filePath = ResourceHelper.createDirectory(basePath, "test-output");

    @Test
    @UnitTestForCoverage
    public void testValidateTranslationEngine() {
        TranslationController translationController = TranslationController.builder()
                .addTranslationEngine(TestTranslationEngine.builder().build())
                .buildWithoutInitAndChecks();

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            translationController.validateTranslationEngines();
        });

        assertEquals(CoreTranslationError.NO_TRANSLATION_ENGINES, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            for (TranslationEngine translationEngine : translationController.data.translationEngines) {
                translationController.translationEngines
                        .put(translationEngine.getTranslationEngineType(), null);
            }
            translationController.validateTranslationEngines();
        });

        assertEquals(CoreTranslationError.NULL_TRANSLATION_ENGINE, contractException.getErrorType());

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            TestTranslationEngine translationEngine2 = TestTranslationEngine.builder()
                    .buildWithoutSpecInit();

            translationController.translationEngines.put(translationEngine2.getTranslationEngineType(),
                    translationEngine2);

            translationController.validateTranslationEngines();
        });

        assertEquals("TranslationEngine has been built but has not been initialized.",
                runtimeException.getMessage());

        runtimeException = assertThrows(RuntimeException.class, () -> {
            for (TranslationEngine translationEngine : translationController.data.translationEngines) {
                translationController.translationEngines.put(
                        translationEngine.getTranslationEngineType(),
                        translationEngine);
                translationController.translationEngineClassToTypeMap.put(translationEngine.getClass(),
                        null);
            }
            translationController.validateTranslationEngines();
        });

        assertEquals(
                "Not all Translation Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.",
                runtimeException.getMessage());

        runtimeException = assertThrows(RuntimeException.class, () -> {
            for (TranslationEngine translationEngine : translationController.data.translationEngines) {
                translationController.translationEngines.put(
                        translationEngine.getTranslationEngineType(),
                        translationEngine);
            }
            translationController.validateTranslationEngines();
        });

        assertEquals(
                "Not all Translation Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.",
                runtimeException.getMessage());
    }

    @Test
    @UnitTestForCoverage
    /*
     * purpose of this test is to show that if there isn't a valid TranslationEngine
     * class -> Translation Engine Type -> Translation Engine mapping, an exception
     * is thrown
     */
    public void testValidateTranslationEngines() {
        TranslationController translationController = TranslationController.builder()
                .buildWithoutInitAndChecks();

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            translationController.validateTranslationEngines();
        });

        assertEquals(CoreTranslationError.NO_TRANSLATION_ENGINES, contractException.getErrorType());

        // class to type map not populated
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            TranslationController translationController2 = TranslationController.builder()
                    .addTranslationEngine(TestTranslationEngine.builder().build())
                    .buildWithoutInitAndChecks();
            for (TranslationEngine translationEngine : translationController2.data.translationEngines) {
                translationController2.translationEngines.put(
                        translationEngine.getTranslationEngineType(),
                        translationEngine);
            }
            translationController2.validateTranslationEngines();
        });

        assertEquals(
                "Not all Translation Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.",
                runtimeException.getMessage());

        // class to type map not fully mapped
        runtimeException = assertThrows(RuntimeException.class, () -> {
            TranslationController translationController2 = TranslationController.builder()
                    .addTranslationEngine(TestTranslationEngine.builder().build())
                    .buildWithoutInitAndChecks();
            for (TranslationEngine translationEngine : translationController2.data.translationEngines) {
                translationController2.translationEngines.put(
                        translationEngine.getTranslationEngineType(),
                        translationEngine);
                translationController2.translationEngineClassToTypeMap.put(translationEngine.getClass(),
                        null);
            }
            translationController2.validateTranslationEngines();
        });

        assertEquals(
                "Not all Translation Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.",
                runtimeException.getMessage());

    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "readInput", args = {})
    public void testReadInput() {
        String fileName = "readInput-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        translationController.writeOutput(expectedAppObject, filePath.resolve(fileName),
                TranslationEngineType.CUSTOM);

        translationController.readInput();

        assertEquals(1, translationController.getNumObjects());

        TestAppObject actualTestAppObject = translationController.getFirstObject(TestAppObject.class);

        assertEquals(expectedAppObject, actualTestAppObject);

        // preconditions

        // invalid file path
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            translationController.readInput(filePath.resolve("badPath"), TestInputObject.class,
                    TestTranslationEngine.builder().build());
        });

        assertTrue(runtimeException.getCause() instanceof IOException);
    }

    @Test
    @UnitTestForCoverage
    public void testWriteOutput_Engine() {
        String fileName = "badFilePath-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        TestTranslationEngine engine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();
        TranslationController translationController = TranslationController.builder()
                .addTranslationEngine(engine)
                .build();

        // preconditions

        // if the filePath is invalid
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            translationController.writeOutput(filePath, TestObjectUtil.generateTestAppObject(),
                    Optional.empty(),
                    engine);
        });

        assertTrue(runtimeException.getCause() instanceof IOException);
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "writeOutput", args = { Object.class,
            Path.class, TranslationEngineType.class })
    public void testWriteOutput() {
        String fileName = "writeOutput-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .addParentChildClassRelationship(TestAppChildObject.class, TestAppObject.class)
                .build();

        TranslationController translationController = TranslationController.builder()
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        translationController.writeOutput(expectedAppObject, filePath.resolve(fileName),
                TranslationEngineType.CUSTOM);

        TestAppChildObject testAppChildObject = TestObjectUtil.getChildAppFromApp(expectedAppObject);

        translationController.writeOutput(testAppChildObject, filePath.resolve(fileName),
                TranslationEngineType.CUSTOM);
        // preconditions
        // CoreTranslationError.NULL_OBJECT_FOR_TRANSLATION is tested by
        // testWriteOutput_Base()
        // CoreTranslationError#NULL_PATH is tested by testWriteOutput_Base()
        // CoreTranslationError#INVALID_OUTPUT_PATH is tested by testWriteOutput_Base()
        // CoreTranslationError#NULL_TRANSLATION_ENGINE is tested by
        // testWriteOutput_Base()
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "writeOutput", args = { Object.class, Class.class,
            Path.class, TranslationEngineType.class })
    public void testWriteOutput_ParentClass() {
        String fileName = "writeOutput_ParentClass-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject appObject = TestObjectUtil.generateTestAppObject();
        TestAppChildObject expectedAppObject = TestObjectUtil.getChildAppFromApp(appObject);

        translationController.writeOutput(expectedAppObject, TestAppObject.class, filePath.resolve(fileName),
                TranslationEngineType.CUSTOM);

        // preconditions
        // the given parent classref is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            Class<TestAppObject> badClassRef = null;
            translationController.writeOutput(expectedAppObject, badClassRef, filePath.resolve(fileName),
                    TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.NULL_CLASS_REF, contractException.getErrorType());

        // preconditions
        // CoreTranslationError.NULL_OBJECT_FOR_TRANSLATION is tested by
        // testWriteOutput_Base()
        // CoreTranslationError#NULL_PATH is tested by testWriteOutput_Base()
        // CoreTranslationError#INVALID_OUTPUT_PATH is tested by testWriteOutput_Base()
        // CoreTranslationError#NULL_TRANSLATION_ENGINE is tested by
        // testWriteOutput_Base()
    }

    @Test
    @UnitTestForCoverage
    public void testWriteOutput_Base() {
        String fileName = "writeOutput_Base-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject appObject = TestObjectUtil.generateTestAppObject();
        TestAppChildObject expectedAppObject = TestObjectUtil.getChildAppFromApp(appObject);

        // preconditions
        // the given object is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            translationController.writeOutput(null, Optional.empty(), filePath.resolve(fileName),
                    TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // the path is null
        contractException = assertThrows(ContractException.class, () -> {
            translationController.writeOutput(expectedAppObject, Optional.empty(), null,
                    TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.NULL_PATH, contractException.getErrorType());

        // if the path is invalid
        contractException = assertThrows(ContractException.class, () -> {
            translationController.writeOutput(expectedAppObject, Optional.empty(),
                    filePath.resolve("badPath").resolve(fileName),
                    TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.INVALID_OUTPUT_PATH, contractException.getErrorType());

        // if the translation engine is null
        contractException = assertThrows(ContractException.class, () -> {
            translationController.writeOutput(expectedAppObject, Optional.empty(),
                    filePath.resolve(fileName),
                    TranslationEngineType.UNKNOWN);
        });

        assertEquals(CoreTranslationError.NULL_TRANSLATION_ENGINE, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "getFirstObject", args = { Class.class })
    public void testGetFirstObject() throws IOException {
        String fileName = "getFirstObject-testOutput.json";
        String fileName2 = "getFirstObject-testOutput2.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName2), TestComplexInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestComplexAppObject expectedComplexAppObject = TestObjectUtil.generateTestComplexAppObject();

        translationController.writeOutput(expectedAppObject, filePath.resolve(fileName),
                TranslationEngineType.CUSTOM);

        translationController.writeOutput(expectedComplexAppObject, filePath.resolve(fileName2),
                TranslationEngineType.CUSTOM);

        translationController.readInput();

        assertEquals(2, translationController.getNumObjects());
        TestAppObject actualTestAppObject = translationController.getFirstObject(TestAppObject.class);
        assertEquals(1, translationController.getNumObjects());

        assertNotNull(actualTestAppObject);

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {

            translationController.getFirstObject(TestInputObject.class);
        });

        assertEquals(CoreTranslationError.UNKNOWN_CLASSREF, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "getObjects", args = { Class.class })
    public void testGetObjects_OfClass() throws IOException {
        String fileName = "GetObjects_OfClass_1-testOutput.json";
        String fileName2 = "GetObjects_OfClass_2-testOutput.json";
        String fileName3 = "GetObjects_OfClass_3-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);
        ResourceHelper.createFile(filePath, fileName3);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName2), TestInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName3), TestComplexInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        List<TestAppObject> expectedObjects = TestObjectUtil.getListOfAppObjects(2);
        TestComplexAppObject expectedComplexAppObject = TestObjectUtil.generateTestComplexAppObject();

        translationController.writeOutput(expectedObjects.get(0), filePath.resolve(fileName),
                TranslationEngineType.CUSTOM);
        translationController.writeOutput(expectedObjects.get(1), filePath.resolve(fileName2),
                TranslationEngineType.CUSTOM);
        translationController.writeOutput(expectedComplexAppObject, filePath.resolve(fileName3),
                TranslationEngineType.CUSTOM);

        translationController.readInput();

        assertEquals(3, translationController.getNumObjects());

        List<TestAppObject> actualObjects = translationController.getObjects(TestAppObject.class);
        assertEquals(1, translationController.getNumObjects());

        assertEquals(2, actualObjects.size());

        assertTrue(actualObjects.containsAll(expectedObjects));

        List<TestAppObject> actualObjects2 = translationController
                .getObjects(TestAppObject.class);
        assertTrue(actualObjects2.isEmpty());
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "getObjects", args = {})
    public void testGetObjects() throws IOException {
        String fileName = "GetObjects_1-testOutput.json";
        String fileName2 = "GetObjects_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName2), TestInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        List<TestAppObject> expectedObjects = TestObjectUtil.getListOfAppObjects(2);

        translationController.writeOutput(expectedObjects.get(0), filePath.resolve(fileName),
                TranslationEngineType.CUSTOM);
        translationController.writeOutput(expectedObjects.get(1), filePath.resolve(fileName2),
                TranslationEngineType.CUSTOM);

        translationController.readInput();

        assertEquals(2, translationController.getNumObjects());

        List<Object> actualObjects = translationController.getObjects();
        assertEquals(0, translationController.getNumObjects());
        assertEquals(2, actualObjects.size());

        assertTrue(actualObjects.containsAll(expectedObjects));
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "builder", args = {})
    public void testBuilder() {
        assertNotNull(TranslationController.builder());
    }

    @Test
    @UnitTestMethod(target = TranslationController.Builder.class, name = "build", args = {})
    public void testBuild() {
        TranslationController translationController = TranslationController.builder()
                .addTranslationEngine(TestTranslationEngine.builder().build()).build();

        assertNotNull(translationController);

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().build();
        });

        assertEquals(CoreTranslationError.NULL_TRANSLATION_ENGINE, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationController.Builder.class, name = "addInputFilePath", args = { Path.class,
            Class.class, TranslationEngineType.class })
    public void testAddInputFilePath() {
        String fileName = "addInputFilePath-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        assertDoesNotThrow(() -> TranslationController.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(TestTranslationEngine.builder().build()).build());

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addInputFilePath(null, TestInputObject.class,
                    TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.NULL_PATH, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addInputFilePath(filePath.resolve(fileName), null,
                    TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.NULL_CLASS_REF, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder()
                    .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                            TranslationEngineType.CUSTOM)
                    .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                            TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.DUPLICATE_INPUT_PATH, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addInputFilePath(filePath.resolve("badPath"),
                    TestInputObject.class,
                    TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.INVALID_INPUT_PATH, contractException.getErrorType());

    }

    @Test
    @UnitTestMethod(target = TranslationController.Builder.class, name = "addParentChildClassRelationship", args = {
            Class.class, Class.class })
    public void testAddParentChildClassRelationship() {
        TranslationController.builder().addParentChildClassRelationship(TestAppObject.class, Object.class);

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addParentChildClassRelationship(null, Object.class);
        });

        assertEquals(CoreTranslationError.NULL_CLASS_REF, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addParentChildClassRelationship(TestAppObject.class, null);
        });

        assertEquals(CoreTranslationError.NULL_CLASS_REF, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder()
                    .addParentChildClassRelationship(TestAppObject.class, Object.class)
                    .addParentChildClassRelationship(TestAppObject.class, Object.class);
        });

        assertEquals(CoreTranslationError.DUPLICATE_CLASSREF, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationController.Builder.class, name = "addTranslationEngine", args = {
            TranslationEngine.class })
    public void testAddTranslationEngine() {
        TestTranslationEngine translationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .addParentChildClassRelationship(TestAppObject.class, Object.class).build();

        TranslationController.builder().addTranslationEngine(translationEngine).build();

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addTranslationEngine(null);
        });

        assertEquals(CoreTranslationError.NULL_TRANSLATION_ENGINE, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TestTranslationEngine translationEngine2 = TestTranslationEngine.builder()
                    .addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(TestComplexObjectTranslator.getTranslator())
                    .addParentChildClassRelationship(TestAppObject.class, Object.class).build();

            TranslationController.builder()
                    .addParentChildClassRelationship(TestAppObject.class, Object.class)
                    .addTranslationEngine(translationEngine2);
        });

        assertEquals(CoreTranslationError.DUPLICATE_CLASSREF, contractException.getErrorType());
    }
}
