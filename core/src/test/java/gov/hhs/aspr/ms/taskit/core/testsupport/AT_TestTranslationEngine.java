package gov.hhs.aspr.ms.taskit.core.testsupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.TranslationEngineTestHelper;
import gov.hhs.aspr.ms.taskit.core.TranslationEngineType;
import gov.hhs.aspr.ms.taskit.core.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.Translator;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.translationSpecs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.input.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.translationSpecs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_TestTranslationEngine {
    Path basePath = ResourceHelper.getResourceDir(this.getClass());
    Path filePath = ResourceHelper.createDirectory(basePath, "test-output");

    @Test
    @UnitTestMethod(target = TestTranslationEngine.class, name = "writeOutput", args = { Path.class, Object.class,
            Optional.class })
    public void testWriteOutput() throws IOException {
        String fileName = "writeOutputFromEngine_1-testOutput.json";
        String fileName2 = "writeOutputFromEngine_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(complexObjectTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        testTranslationEngine.writeOutput(filePath.resolve(fileName), expectedAppObject, Optional.empty());
        TestAppObject actualAppObject = testTranslationEngine.readInput(filePath.resolve(fileName), TestInputObject.class);
        assertEquals(expectedAppObject, actualAppObject);

        testTranslationEngine.writeOutput(filePath.resolve(fileName2), TestObjectUtil.getChildAppFromApp(expectedAppObject),
                Optional.of(TestAppObject.class));
        TestAppObject actualAppChildObject = testTranslationEngine.readInput(filePath.resolve(fileName2), TestInputObject.class);
        assertEquals(expectedAppObject, actualAppChildObject);
    }

    @Test
    @UnitTestMethod(target = TestTranslationEngine.class, name = "readInput", args = { Path.class, Class.class })
    public void testReadInput() throws IOException {
        String fileName = "readInputFromEngine_1-testOutput.json";
        String fileName2 = "readInputFromEngine_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(complexObjectTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        testTranslationEngine.writeOutput(filePath.resolve(fileName), expectedAppObject, Optional.empty());
        TestAppObject actualAppObject = testTranslationEngine.readInput(filePath.resolve(fileName), TestInputObject.class);
        assertEquals(expectedAppObject, actualAppObject);

        testTranslationEngine.writeOutput(filePath.resolve(fileName2), TestObjectUtil.getChildAppFromApp(expectedAppObject),
                Optional.of(TestAppObject.class));
        TestAppObject actualAppChildObject = testTranslationEngine.readInput(filePath.resolve(fileName2), TestInputObject.class);
        assertEquals(expectedAppObject, actualAppChildObject);
    }

    @Test
    @UnitTestMethod(target = TestTranslationEngine.class, name = "builder", args = {})
    public void testBuilder() {
        assertNotNull(TestTranslationEngine.builder());
    }

    @Test
    @UnitTestMethod(target = TestTranslationEngine.Builder.class, name = "build", args = {})
    public void testBuild() {
        assertNotNull(TestTranslationEngine.builder().build());
    }

    @Test
    @UnitTestMethod(target = TestTranslationEngine.Builder.class, name = "buildWithNoTranslatorInit", args = {})
    public void testBuildWithNoTranslatorInit() {
        assertNotNull(TestTranslationEngine.builder().buildWithNoTranslatorInit());
    }

    @Test
    @UnitTestMethod(target = TestTranslationEngine.Builder.class, name = "buildWithUnknownType", args = {})
    public void testBuildWithUnknownType() {
        assertNotNull(TestTranslationEngine.builder().buildWithUnknownType());

        assertEquals(TranslationEngineType.UNKNOWN, TestTranslationEngine.builder().buildWithUnknownType().getTranslationEngineType());
    }

    @Test
    @UnitTestMethod(target = TestTranslationEngine.Builder.class, name = "buildWithoutSpecInit", args = {})
    public void testBuildWithoutSpecInit() {
        assertNotNull(TestTranslationEngine.builder().buildWithoutSpecInit());
    }

    @Test
    @UnitTestMethod(target = TestTranslationEngine.Builder.class, name = "addTranslationSpec", args = {
            TranslationSpec.class })
    public void testAddTranslationSpec() {
        TranslationEngineTestHelper.testAddTranslationSpec(TestTranslationEngine.builder());
    }

    @Test
    @UnitTestMethod(target = TestTranslationEngine.Builder.class, name = "addTranslator", args = { Translator.class })
    public void testAddTranslator() {
        TranslationEngineTestHelper.testAddTranslator(TestTranslationEngine.builder());
    }

    @Test
    @UnitTestMethod(target = TestTranslationEngine.Builder.class, name = "addParentChildClassRelationship", args = {
            Class.class, Class.class })
    public void testAddParentChildClassRelationship() {
        TranslationEngineTestHelper.testAddParentChildClassRelationship(TestTranslationEngine.builder());
    }
    
    @Test
    @UnitTestMethod(target = TestTranslationEngine.class, name = "hashCode", args = {})
    public void testHashCode() {
        // covered by AT_TranslationEngine#testHashCode
    }

    @Test
    @UnitTestMethod(target = TestTranslationEngine.class, name = "equals", args = { Object.class })
    public void testEquals() {
        // covered by AT_TranslationEngine#testEquals
    }
}
