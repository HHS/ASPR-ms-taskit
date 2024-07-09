package gov.hhs.aspr.ms.taskit.core.engine;

import java.io.IOException;
import java.nio.file.Path;

import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;

/**
 * Interface for a {@link TaskitEngine} that applies to TaskitEngine and any
 * derivatives thereof
 */
public interface ITaskitEngine {

    /**
     * @return the {@link TaskitEngine} associated with the given TaskitEngine.
     *         <p>
     *         Note that for {@link TaskitEngine} this method returns itself.
     */
    public TaskitEngine getTaskitEngine();

    /**
     * @return the {@link TaskitEngineId} associated with the TaskitEngine
     */
    public TaskitEngineId getTaskitEngineId();

    /**
     * Writes the object to the file referenced by the Path
     * 
     * @param <O>    the type of the object to write
     * @param path   the path of the file to write to
     * @param object the object to write
     * @throws IOException if there is an issue writing the file
     */
    public <O> void write(Path path, O object) throws IOException;

    /**
     * Translates the object and then writes the translated object to the file
     * reference by the Path
     * 
     * @param <O>    the type of the object to write
     * @param path   the path of the file to write to
     * @param object the object to write
     * @throws IOException if there is an issue writing the file
     */
    public <O> void translateAndWrite(Path path, O object) throws IOException;

    /**
     * Translates the object as the provided class and then writes the translated
     * object to the file referenced by the Path
     * <p>
     * This method call is safe in the sense that the type parameters ensure that
     * the passed in object is actually a child of the passed in classRef type
     * 
     * @param <C>      the type to translate the object as
     * @param <O>      the type of the object
     * @param path     the path of the file to write to
     * @param object   the object to write
     * @param classRef the class to translate the object as
     * @throws IOException if there is an issue writing the file
     */
    public <C, O extends C> void translateAndWrite(Path path, O object, Class<C> classRef)
            throws IOException;

    /**
     * Reads the given path into the provided class type
     * 
     * @param <I>      the input type
     * @param path     the path of the file to read
     * @param classRef the class to read the file as
     * @return the resulting object from reading the file as the class
     * @throws IOException if there is an issue reading the file
     */
    public <I> I read(Path path, Class<I> classRef) throws IOException;

    /**
     * Reads the given path into the provided class type and then translates it to
     * the corresponding app type associated with the input type
     * 
     * @param <T>      the translated type
     * @param <I>      the input type
     * @param path     the path of the file to read
     * @param classRef the class to read the file as
     * @return the resulting translated read in object
     * @throws IOException if there is an issue reading the file
     */
    public <T, I> T readAndTranslate(Path path, Class<I> classRef) throws IOException;

    /**
     * Given an object, uses the class of the object to obtain the translationSpec
     * and then calls {@link TranslationSpec#translate(Object)} to translate the
     * object
     * <p>
     * this conversion method will be used approx ~90% of the time
     * </p>
     * 
     * @param <T>    the translated type
     * @param object the object to translate
     * @return the translated object
     */
    public <T> T translateObject(Object object);

    /**
     * Given an object, uses the given classRef to obtain the
     * translationSpec and then calls {@link TranslationSpec#translate(Object)}
     * <p>
     * This method call is safe in the sense that the type parameters ensure that
     * the passed in object is actually a child of the passed in classRef type
     * </p>
     * <p>
     * this conversion method will be used approx ~7% of the time
     * </p>
     * 
     * @param <T>      the translated type
     * @param <O>      the type of the object
     * @param <C>      the type to translate the object as
     * @param object   the object to translate
     * @param classRef the classRef of the type to translate the object as
     * @return the translated object
     */
    public <T, O extends C, C> T translateObjectAsClassSafe(O object, Class<C> classRef);

    /**
     * Given an object, uses the given classRef to obtain the
     * translationSpec and then calls {@link TranslationSpec#translate(Object)}
     * <p>
     * There is no type safety with this method unlike the
     * {@link ITaskitEngine#translateObjectAsClassSafe(Object, Class)} method.
     * Therefore it is on the caller of this method to ensure that the given object
     * can be translated using the given classRef.
     * <p>
     * A conventional use case for this would be when you want to wrap an object
     * into another object type where there is no correlation between the wrapping
     * object and the object being wrapped.
     * </p>
     * <p>
     * this conversion method will be used approx ~3% of the time
     * </p>
     * 
     * @param <T>      the corresponding type
     * @param <O>      the type of the object
     * @param <C>      the type to translate the object as
     * @param object   the object to translate
     * @param classRef the classRef of the type to translate the object as
     * @return the translated object
     */
    public <T, O, C> T translateObjectAsClassUnsafe(O object, Class<C> classRef);
}
