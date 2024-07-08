package gov.hhs.aspr.ms.taskit.core.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.TestComplexObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.TestComplexObjectTranslatorId;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.TestObjectTranslatorId;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorId;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.graph.MutableGraph;

public class AT_TaskitEngineHelper {

    @Test
    @UnitTestForCoverage
    public void testGetOrderedTranslators() {

        List<Translator> translators = new ArrayList<>();

        translators.add(TestObjectTranslator.getTranslator());
        translators.add(TestComplexObjectTranslator.getTranslator());

        TaskitEngineHelper taskitEngineHelper = new TaskitEngineHelper(translators);

        List<Translator> expectedList = new ArrayList<>();
        expectedList.add(TestComplexObjectTranslator.getTranslator());
        expectedList.add(TestObjectTranslator.getTranslator());

        List<Translator> actualList = taskitEngineHelper.getOrderedTranslators();

        assertEquals(expectedList, actualList);

        // preconditions

        // duplicate translator in the graph

        ContractException contractException = assertThrows(ContractException.class,
                () -> {
                    MutableGraph<TranslatorId, Object> mutableGraph = new MutableGraph<>();
                    Map<TranslatorId, Translator> translatorMap = new LinkedHashMap<>();
                    mutableGraph.addNode(TestObjectTranslatorId.TRANSLATOR_ID);
                    taskitEngineHelper.addNodes(mutableGraph, translatorMap);
                });

        assertEquals(TaskitCoreError.DUPLICATE_TRANSLATOR,
                contractException.getErrorType());

        // missing translator
        contractException = assertThrows(ContractException.class, () -> {
            MutableGraph<TranslatorId, Object> mutableGraph = new MutableGraph<>();
            Map<TranslatorId, Translator> translatorMap = new LinkedHashMap<>();

            // call normally
            taskitEngineHelper.getOrderedTranslators(mutableGraph, translatorMap);
            // remove a mapping
            translatorMap.remove(TestComplexObjectTranslatorId.TRANSLATOR_ID);
            TranslatorId thirdId = new TranslatorId() {
            };
            mutableGraph.addNode(thirdId);
            mutableGraph.addEdge(new Object(), thirdId,
                    TestComplexObjectTranslatorId.TRANSLATOR_ID);
            taskitEngineHelper.checkForMissingTranslators(mutableGraph, translatorMap);
        });

        assertEquals(TaskitCoreError.MISSING_TRANSLATOR,
                contractException.getErrorType());

        // cyclic graph
        contractException = assertThrows(ContractException.class, () -> {
            MutableGraph<TranslatorId, Object> mutableGraph = new MutableGraph<>();
            Map<TranslatorId, Translator> translatorMap = new LinkedHashMap<>();

            // call normally
            taskitEngineHelper.getOrderedTranslators(mutableGraph, translatorMap);
            mutableGraph.addEdge(new Object(),
                    TestComplexObjectTranslatorId.TRANSLATOR_ID,
                    TestObjectTranslatorId.TRANSLATOR_ID);
            TranslatorId thirdId = new TranslatorId() {
            };
            TranslatorId fourthId = new TranslatorId() {
            };
            mutableGraph.addNode(thirdId);
            mutableGraph.addNode(fourthId);
            mutableGraph.addEdge(new Object(), thirdId, fourthId);
            mutableGraph.addEdge(new Object(), fourthId, thirdId);
            taskitEngineHelper.checkForCyclicGraph(mutableGraph);
        });

        assertEquals(TaskitCoreError.CIRCULAR_TRANSLATOR_DEPENDENCIES,
                contractException.getErrorType());
    }
}
