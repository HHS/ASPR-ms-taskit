package gov.hhs.aspr.ms.taskit.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.TestTranslationEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.TestComplexObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.input.TestInputObject;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_TranslationController {

    Path basePath = ResourceHelper.getResourceDir(this.getClass());
    Path filePath = ResourceHelper.makeOutputDir(basePath, "test-output");

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
     * purpose of this test is to show that if there isnt a valid TranslationEngine
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

        ResourceHelper.createOutputFile(filePath, fileName);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        translationController.writeOutput(expectedAppObject);

        translationController.readInput();

        assertTrue(translationController.getObjects().size() == 1);

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
        String fileName = "badFilePath-testoutput.json";

        ResourceHelper.createOutputFile(filePath, fileName);
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
    @UnitTestForCoverage
    public void testGetOutputPathKey() {
        String fileName = "GetOutputPath_1-testOutput.json";
        String fileName2 = "GetOutputPath_2-testOutput.json";

        ResourceHelper.createOutputFile(filePath, fileName);
        ResourceHelper.createOutputFile(filePath, fileName2);

        TranslationController translationController = TranslationController.builder()
                .addTranslationEngine(TestTranslationEngine.builder().build())
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class,
                        TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName2), Object.class, 1,
                        TranslationEngineType.CUSTOM)
                .addParentChildClassRelationship(TestAppObject.class, Object.class).build();

        Pair<String, Optional<Class<TestAppObject>>> expectedPair1 = new Pair<>(
                TestAppObject.class.getName() + ":" + 0,
                Optional.empty());
        Pair<String, Optional<Class<Object>>> expectedPair2 = new Pair<>(Object.class.getName() + ":" + 1,
                Optional.of(Object.class));

        Pair<String, Optional<Class<TestAppObject>>> actualPair1 = translationController
                .getOutputPathKey(TestAppObject.class, 0);
        Pair<String, Optional<Class<Object>>> actualPair2 = translationController
                .getOutputPathKey(TestAppObject.class, 1);

        assertEquals(expectedPair1, actualPair1);
        assertEquals(expectedPair2, actualPair2);
        // preconditions

        // if the class scenarioId pair does not exist and there is no parent child
        // class relationship
        ContractException contractException = assertThrows(ContractException.class, () -> {
            translationController.getOutputPathKey(TestInputObject.class, 1);
        });

        assertEquals(CoreTranslationError.INVALID_OUTPUT_CLASSREF, contractException.getErrorType());

        // if the class and scenarioID pair does not exist AND there is a parent child
        // class relationship AND the parentClass scenarioId pair does not exists
        contractException = assertThrows(ContractException.class, () -> {
            translationController.getOutputPathKey(TestAppObject.class, 4);
        });

        assertEquals(CoreTranslationError.INVALID_OUTPUT_CLASSREF, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "writeOutput", args = { List.class })
    public void testWriteOutput_List() {
        String fileName = "WriteOutput_List_1-testOutput.json";
        String fileName2 = "WriteOutput_List_2-testOutput.json";

        ResourceHelper.createOutputFile(filePath, fileName);
        ResourceHelper.createOutputFile(filePath, fileName2);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class,
                        TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName2), TestComplexAppObject.class,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        List<Object> outputObjects = new ArrayList<>();

        outputObjects.add(TestObjectUtil.generateTestAppObject());
        outputObjects.add(TestObjectUtil.generateTestComplexAppObject());
        translationController.writeOutput(outputObjects);

        // preconditions
        // the runtime exception is covered by the test - testMakeFileWriter()
        // the contract exception for CoreTranslationError.INVALID_OUTPUT_CLASSREF is
        // covered by the test - testGetOutputPath()
        // the contract exception for CoreTranslationError.NULL_TRANSLATION_ENGINE is
        // covered by the test - testValidateTranslationEngine()
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "writeOutput", args = { Map.class })
    public void testWriteOutput_Map() {
        String fileName = "WriteOutput_List_1-testOutput.json";
        String fileName2 = "WriteOutput_List_2-testOutput.json";

        ResourceHelper.createOutputFile(filePath, fileName);
        ResourceHelper.createOutputFile(filePath, fileName2);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), "key1", TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName2), "key2",
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        Map<String, Object> outputObjects = new LinkedHashMap<>();

        outputObjects.put("key1", TestObjectUtil.generateTestAppObject());
        outputObjects.put("key2", TestObjectUtil.generateTestComplexAppObject());
        translationController.writeOutput(outputObjects);

        // preconditions
        // the runtime exception is covered by the test - testMakeFileWriter()
        // the contract exception for CoreTranslationError.INVALID_OUTPUT_CLASSREF is
        // covered by the test - testGetOutputPath()
        // the contract exception for CoreTranslationError.NULL_TRANSLATION_ENGINE is
        // covered by the test - testValidateTranslationEngine()
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "writeOutput", args = { List.class,
            Integer.class })
    public void testWriteOutput_List_ScenarioId() throws IOException {
        String fileName = "WriteOutput_List_ScenarioId_1-testOutput.json";
        String fileName2 = "WriteOutput_List_ScenarioId_2-testOutput.json";

        ResourceHelper.createOutputFile(filePath, fileName);
        ResourceHelper.createOutputFile(filePath, fileName2);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, 1,
                        TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName2), TestComplexAppObject.class, 1,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        List<Object> outputObjects = new ArrayList<>();

        outputObjects.add(TestObjectUtil.generateTestAppObject());
        outputObjects.add(TestObjectUtil.generateTestComplexAppObject());
        translationController.writeOutput(outputObjects, 1);

        // preconditions
        // the runtime exception is covered by the test - testMakeFileWriter()
        // the contract exception for CoreTranslationError.INVALID_OUTPUT_CLASSREF is
        // covered by the test - testGetOutputPath()
        // the contract exception for CoreTranslationError.NULL_TRANSLATION_ENGINE is
        // covered by the test - testValidateTranslationEngine()
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "writeOutput", args = { Object.class })
    public void testWriteOutput() throws IOException {
        String fileName = "writeOutput-testOutput.json";

        ResourceHelper.createOutputFile(filePath, fileName);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        translationController.writeOutput(expectedAppObject);

        // preconditions
        // the runtime exception is covered by the test - testMakeFileWriter()
        // the contract exception for CoreTranslationError.INVALID_OUTPUT_CLASSREF is
        // covered by the test - testGetOutputPath()
        // the contract exception for CoreTranslationError.NULL_TRANSLATION_ENGINE is
        // covered by the test - testValidateTranslationEngine()
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "writeOutput", args = { Object.class,
            Integer.class })
    public void testWriteOutput_ScenarioId() throws IOException {
        String fileName = "writeOutput_ScenarioId-testOutput.json";

        ResourceHelper.createOutputFile(filePath, fileName);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, 1,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        translationController.writeOutput(expectedAppObject, 1);

        // preconditions
        // the runtime exception is covered by the test - testMakeFileWriter()
        // the contract exception for CoreTranslationError.INVALID_OUTPUT_CLASSREF is
        // covered by the test - testGetOutputPath()
        // the contract exception for CoreTranslationError.NULL_TRANSLATION_ENGINE is
        // covered by the test - testValidateTranslationEngine()
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "writeOutput", args = { Object.class,
            String.class })
    public void testWriteOutput_Key() throws IOException {
        String fileName = "writeOutput_Key-testOutput.json";

        ResourceHelper.createOutputFile(filePath, fileName);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        String key = "TEST_KEY";
        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), key, TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        translationController.writeOutput(expectedAppObject, key);

        // preconditions
        // the given key is not valid
        ContractException contractException = assertThrows(ContractException.class, () -> {
            translationController.writeOutput(expectedAppObject, "BAD_KEY");
        });

        assertEquals(CoreTranslationError.INVALID_OUTPUT_CLASSREF, contractException.getErrorType());
        // the runtime exception is covered by the test - testMakeFileWriter()
        // the contract exception for CoreTranslationError.INVALID_OUTPUT_CLASSREF is
        // covered by the test - testGetOutputPath()
        // the contract exception for CoreTranslationError.NULL_TRANSLATION_ENGINE is
        // covered by the test - testValidateTranslationEngine()
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "getFirstObject", args = { Class.class })
    public void testGetFirstObject() throws IOException {
        String fileName = "getFirstObject-testOutput.json";

        ResourceHelper.createOutputFile(filePath, fileName);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        translationController.writeOutput(expectedAppObject);

        translationController.readInput();

        assertTrue(translationController.getObjects().size() == 1);

        TestAppObject actualTestAppObject = translationController.getFirstObject(TestAppObject.class);

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

        ResourceHelper.createOutputFile(filePath, fileName);
        ResourceHelper.createOutputFile(filePath, fileName2);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, 1,
                        TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName2), TestAppObject.class, 2,
                        TranslationEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName2), TestInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        List<TestAppObject> expectedObjects = TestObjectUtil.getListOfAppObjects(2);

        translationController.writeOutput(expectedObjects.get(0), 1);
        translationController.writeOutput(expectedObjects.get(1), 2);

        translationController.readInput();

        assertEquals(2, translationController.getObjects().size());

        List<TestAppObject> actualObjects = translationController.getObjects(TestAppObject.class);

        assertEquals(2, actualObjects.size());

        assertTrue(actualObjects.containsAll(expectedObjects));

        List<TestComplexAppObject> actualObjects2 = translationController
                .getObjects(TestComplexAppObject.class);
        assertTrue(actualObjects2.isEmpty());
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "getObjects", args = {})
    public void testGetObjects() throws IOException {
        String fileName = "GetObjects_1-testOutput.json";
        String fileName2 = "GetObjects_2-testOutput.json";

        ResourceHelper.createOutputFile(filePath, fileName);
        ResourceHelper.createOutputFile(filePath, fileName2);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, 1,
                        TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName2), TestAppObject.class, 2,
                        TranslationEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName2), TestInputObject.class,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        List<TestAppObject> expectedObjects = TestObjectUtil.getListOfAppObjects(2);

        translationController.writeOutput(expectedObjects.get(0), 1);
        translationController.writeOutput(expectedObjects.get(1), 2);

        translationController.readInput();

        assertEquals(2, translationController.getObjects().size());

        List<Object> actualObjects = translationController.getObjects();

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

        ResourceHelper.createOutputFile(filePath, fileName);

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
    @UnitTestMethod(target = TranslationController.Builder.class, name = "addOutputFilePath", args = { Path.class,
            Class.class, TranslationEngineType.class })
    public void testAddOutputFilePath() {
        String fileName = "addOutputFilePath1-testOutput.json";
        String fileName2 = "addOutputFilePath2-testOutput.json";

        ResourceHelper.createOutputFile(filePath, fileName);

        assertDoesNotThrow(() -> TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(TestTranslationEngine.builder().build()).build());

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addOutputFilePath(null, TestAppObject.class,
                    TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.NULL_PATH, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            Class<?> classRef = null;
            TranslationController.builder().addOutputFilePath(filePath.resolve(fileName), classRef,
                    TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.NULL_CLASS_REF, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder()
                    .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class,
                            TranslationEngineType.CUSTOM)
                    .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class,
                            TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.DUPLICATE_OUTPUT_PATH, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder()
                    .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class,
                            TranslationEngineType.CUSTOM)
                    .addOutputFilePath(filePath.resolve(fileName2), TestAppObject.class,
                            TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.DUPLICATE_OUTPUT_PATH_KEY, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addOutputFilePath(
                    filePath.resolve("badpath").resolve(fileName2),
                    TestAppObject.class, TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.INVALID_OUTPUT_PATH, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationController.Builder.class, name = "addOutputFilePath", args = { Path.class,
            Class.class, Integer.class, TranslationEngineType.class })
    public void testAddOutputFilePath_ScenarioId() {
        // Tested by testAddOutputFilePath, which internally calls
        // addOutputFilePath(path, classRef, 0, engineType)
    }

    @Test
    @UnitTestMethod(target = TranslationController.Builder.class, name = "addOutputFilePath", args = { Path.class,
            String.class, TranslationEngineType.class })
    public void testAddOutputFilePath_Key() {
        // Tested by testAddOutputFilePath, which internally calls
        // addOutputFilePath(path, classRef, 0, engineType) which calls
        // addOutputFilePath(path, "classRef.name():0", engineType)
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
    public void testAddTransationEngine() {
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
