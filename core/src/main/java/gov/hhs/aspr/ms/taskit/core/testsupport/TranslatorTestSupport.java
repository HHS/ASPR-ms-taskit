package gov.hhs.aspr.ms.taskit.core.testsupport;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

/**
 * Class to help test Translators and their internal list of translation specs
 */
public class TranslatorTestSupport {

    private TranslatorTestSupport() {
    }

    /**
     * This method is used to ensure that every translationSpec that is supposed to be
     * tied to a Translator is defined in its list of translationSpecs. If a
     * translationSpec is added and not subsequently added to the list in the
     * Translator, then this test will fail and provide the name of the missing
     * TranslationSpec
     * 
     * @param <T>                the type of the translator
     * @param translatorClassRef the classRef of the translator
     * @param translationSpecs   the list of translation specs defined in the
     *                           translator
     * @return a set containing any missing translationSpecs defined in the
     *         translation.spec package for which the translator is located
     * @throws ClassNotFoundException if the class loader cannot load a class
     * 
     */
    public static <T> Set<String> testGetTranslationSpecs(Class<T> translatorClassRef,
            List<ITranslationSpec> translationSpecs) throws ClassNotFoundException {
        Set<String> missingTranslationSpecs = new LinkedHashSet<>();
        List<Class<?>> translationSpecClasses = new ArrayList<>();

        // create a list with the translation spec class names
        for (ITranslationSpec translationSpec : translationSpecs) {
            translationSpecClasses.add(translationSpec.getClass());
        }

        // get the package of the translator class to get its translationSpecs package
        // path
        String packageName = translatorClassRef.getPackageName() + ".specs";
        String packagePath = packageName.replaceAll("[.]", "/");

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        Path path = ResourceHelper.getResourceDir(translatorClassRef)
                .getParent()
                .resolve("classes")
                .resolve(packagePath);

        // the path from above will be referencing the test-classes compile folder. We
        // want the classes folder, else it will fail because the test classes for
        // translationSpecs are prefixed with AT_
        File[] files = path.toFile().listFiles();

        // loop over all the files in the directory, for every file that ends in .class,
        // construct the full qualified class name. use the classLoader to load the
        // class and assert that the provided list of translationSpecs contains that
        // class
        for (File file : files) {
            String className = file.getName();
            if (className.endsWith(".class")) {
                // note the substring here is to eliminate the .class suffix of the filename
                className = packageName + "." + className.substring(0, className.length() - 6);
                Class<?> classRef = classLoader.loadClass(className);

                if (!translationSpecClasses.contains(classRef)) {
                    missingTranslationSpecs.add(classRef.getSimpleName());
                }
            }
        }

        return missingTranslationSpecs;
    }
}
