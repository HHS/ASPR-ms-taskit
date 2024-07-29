package gov.hhs.aspr.ms.taskit.core.engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceError;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

/**
 * TaskitEngine initializes all {@link TranslationSpec}s and maintains a mapping
 * between the translationSpec and it's respective classes. Each serialization
 * library must implement its own unique Taskit Engine that extends this engine
 */
public abstract class TaskitEngine {
	private final TaskitEngineData data;
	private final TaskitEngineId taskitEngineId;

	private boolean isInitialized = false;

	protected TaskitEngine(TaskitEngineData data, TaskitEngineId taskitEngineId) {
		this.data = data;
		this.taskitEngineId = taskitEngineId;
	}

	/**
	 * Initializes the taskitEngine by calling init on each translationSpec added in
	 * the builder
	 */
	public final void init() {
		/*
		 * Calling init on a translationSpec causes the hashCode of the translationSpec
		 * to change. Because of this, before calling init, we need to remove them from
		 * the translationSpecs Set, then initialize them, then add them back to the
		 * set. Set's aren't happy when the hash code of the objects in them change
		 */
		List<ITranslationSpec> copyOfTranslationSpecs = new ArrayList<>(this.data.translationSpecs);

		this.data.translationSpecs.clear();

		for (ITranslationSpec translationSpec : copyOfTranslationSpecs) {

			translationSpec.init(this);
			this.data.translationSpecs.add(translationSpec);
		}

		this.isInitialized = true;
	}

	/**
	 * @return the taskitEngineId for this taskitEngine
	 */
	public final TaskitEngineId getTaskitEngineId() {
		return this.taskitEngineId;
	}

	/**
	 * @return the initialized flag of the TaskitEngine
	 */
	public final boolean isInitialized() {
		return this.isInitialized;
	}

	/**
	 * @return a set of all {@link TranslationSpec}s associated with this
	 *         TaskitEngine
	 */
	public final Set<ITranslationSpec> getTranslationSpecs() {
		return this.data.translationSpecs;
	}

	/**
	 * Writes the object to the file referenced by the Path
	 * 
	 * @param <O>          the type of the object to write
	 * @param outputPath   the path of the file to write to
	 * @param outputObject the object to write
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_PATH} if the path
	 *                           is null</li>
	 *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
	 *                           if the path points to a directory instead of a
	 *                           file</li>
	 *                           <li>{@linkplain ResourceError#DIRECTORY_PATH_IS_FILE}
	 *                           if path points to a file that doesn't exist and the
	 *                           parent path points to a file</li>
	 *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
	 *                           if the passed in object is null</li>
	 *                           </ul>
	 * @throws IOException       if there is an issue writing the file
	 */
	public final <O> void write(Path outputPath, O outputObject) throws IOException {
		validateFilePath(outputPath);
		validateObject(outputObject);

		this.writeToFile(outputPath.toFile(), outputObject);
	}

	/**
	 * Translates the object and then writes the translated object to the file
	 * referenced by the Path
	 * 
	 * @param <O>          the type of the object to write
	 * @param outputPath   the path of the file to write to
	 * @param outputObject the object to write
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_PATH} if the path
	 *                           is null</li>
	 *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
	 *                           if the path points to a directory instead of a
	 *                           file</li>
	 *                           <li>{@linkplain ResourceError#DIRECTORY_PATH_IS_FILE}
	 *                           if path points to a file that doesn't exist and the
	 *                           parent path points to a file</li>
	 *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
	 *                           if the passed in object is null</li>
	 *                           <li>{@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
	 *                           if no translationSpec was provided for the given
	 *                           objects class</li>
	 *                           </ul>
	 * @throws IOException       if there is an issue writing the file
	 */
	public final <O> void translateAndWrite(Path outputPath, O outputObject) throws IOException {
		validateFilePath(outputPath);

		this.writeToFile(outputPath.toFile(), this.translateObject(outputObject));
	}

	/**
	 * Translates the object using the given classRef and then writes the translated
	 * object to the file referenced by the Path
	 * 
	 * @param <O>          the type of the object to write
	 * @param outputPath   the path of the file to write to
	 * @param outputObject the object to write
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_PATH} if the path
	 *                           is null</li>
	 *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
	 *                           if the path points to a directory instead of a
	 *                           file</li>
	 *                           <li>{@linkplain ResourceError#DIRECTORY_PATH_IS_FILE}
	 *                           if path points to a file that doesn't exist and the
	 *                           parent path points to a file</li>
	 *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
	 *                           if the passed in object is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_CLASS_REF} if the
	 *                           passed in classRef is null</li>
	 *                           <li>{@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
	 *                           if no translationSpec was provided for the given
	 *                           objects class</li>
	 *                           </ul>
	 * @throws IOException       if there is an issue writing the file
	 */
	public final <C, O extends C> void translateAndWrite(Path outputPath, O outputObject, Class<C> outputClassRef)
			throws IOException {
		validateFilePath(outputPath);

		this.writeToFile(outputPath.toFile(), this.translateObjectAsClassSafe(outputObject, outputClassRef));
	}

	/**
	 * Reads the given path into the provided class type
	 * 
	 * @param <I>           the input type
	 * @param inputPath     the path of the file to read
	 * @param inputClassRef the class to read the file as
	 * @return the resulting object from reading the file as the class
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_PATH} if the path
	 *                           is null</li>
	 *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
	 *                           if the path points to a directory instead of a
	 *                           file</li>
	 *                           <li>{@linkplain ResourceError#UNKNOWN_FILE}
	 *                           if path points to a file that doesn't exist</li>
	 *                           <li>{@linkplain TaskitError#NULL_CLASS_REF}
	 *                           if the passed in classRef is null</li>
	 *                           </ul>
	 * @throws IOException       if there is an issue reading the file
	 */
	public final <I> I read(Path inputPath, Class<I> inputClassRef) throws IOException {
		validateFile(inputPath);
		validateClass(inputClassRef);

		return this.readFile(inputPath.toFile(), inputClassRef);
	}

	/**
	 * Reads the given path into the provided class type and then translates it to
	 * the corresponding app type associated with the input type
	 * 
	 * @param <T>       the translated type
	 * @param <I>       the input type
	 * @param inputPath the path of the file to read
	 * @param classRef  the class to read the file as
	 * @return the resulting translated read in object
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_PATH} if the path
	 *                           is null</li>
	 *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
	 *                           if the path points to a directory instead of a
	 *                           file</li>
	 *                           <li>{@linkplain ResourceError#UNKNOWN_FILE}
	 *                           if path points to a file that doesn't exist</li>
	 *                           <li>{@linkplain TaskitError#NULL_CLASS_REF}
	 *                           if the passed in classRef is null</li>
	 *                           <li>{@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
	 *                           if no translationSpec was provided for the given
	 *                           objects class</li>
	 *                           </ul>
	 * 
	 * @throws IOException       if there is an issue reading the file
	 */
	public final <T, I> T readAndTranslate(Path inputPath, Class<I> inputClassRef) throws IOException {
		validateFile(inputPath);
		validateClass(inputClassRef);

		I readObj = this.readFile(inputPath.toFile(), inputClassRef);

		return this.translateObject(readObj);
	}

	/**
	 * Given an object, uses the class of the object to obtain the translationSpec
	 * and then calls {@link TranslationSpec#translate(Object)} to translate the
	 * object.
	 * <p>
	 * This conversion method will be used approximately 90% of the time.
	 * </p>
	 * 
	 * @param <T>    the translated type
	 * @param object the object to translate
	 * @return the translated object
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
	 *                           if the passed in object is null</li>
	 *                           <li>{@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
	 *                           if no translationSpec was provided for the given
	 *                           objects class</li>
	 *                           </ul>
	 */
	public final <T> T translateObject(Object object) {
		validateObject(object);
		return getTranslationSpecForClass(object.getClass()).translate(object);
	}

	/**
	 * Given an object, uses the given classRef to obtain the translationSpec and
	 * then calls {@link TranslationSpec#translate(Object)}.
	 * <p>
	 * This method call is safe in the sense that the type parameters ensure that
	 * the passed in object is actually a child of the passed in classRef type.
	 * </p>
	 * <p>
	 * This conversion method will be used approximately 7% of the time.
	 * </p>
	 * 
	 * @param <T>                 the translated type
	 * @param <O>                 the type of the object
	 * @param <C>                 the type to translate the object as
	 * @param object              the object to translate
	 * @param translateAsClassRef the classRef of the type to translate the object
	 *                            as
	 * @return the translated object
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
	 *                           if the passed in object is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_CLASS_REF} if the
	 *                           passed in classRef is null</li>
	 *                           <li>{@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
	 *                           if no translationSpec was provided for the given
	 *                           objects class</li>
	 *                           </ul>
	 */
	public final <T, O extends C, C> T translateObjectAsClassSafe(O object, Class<C> translateAsClassRef) {
		validateObject(object);

		return getTranslationSpecForClass(translateAsClassRef).translate(object);
	}

	/**
	 * Given an object, uses the given classRef to obtain the translationSpec and
	 * then calls {@link TranslationSpec#translate(Object)}
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
	 * @param <T>                 the translated type
	 * @param <O>                 the type of the object
	 * @param <C>                 the type to translate the object as
	 * @param object              the object to translate
	 * @param translateAsClassRef the classRef of the type to translate the object
	 * @return the translated object
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
	 *                           if the passed in object is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_CLASS_REF} if the
	 *                           passed in classRef is null</li>
	 *                           <li>{@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
	 *                           if no translationSpec was provided for the given
	 *                           objects class</li>
	 *                           </ul>
	 */
	public final <T, O, C> T translateObjectAsClassUnsafe(O object, Class<C> translateAsClassRef) {
		validateObject(object);

		return getTranslationSpecForClass(translateAsClassRef).translate(object);
	}

	/**
	 * Package access for testing.
	 * 
	 * @param <T>      the type of the classRef
	 * @param classRef the classRef to find a translation spec for
	 * @return the translation spec for the given classRef, if found
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_CLASS_REF} if the
	 *                           passed in classRef is null</li>
	 *                           <li>
	 *                           {@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
	 *                           if no translationSpec for the given class was
	 *                           found</li>
	 *                           </ul>
	 */
	public final <T> ITranslationSpec getTranslationSpecForClass(Class<T> classRef) {
		validateClass(classRef);

		if (this.data.classToTranslationSpecMap.containsKey(classRef)) {
			return this.data.classToTranslationSpecMap.get(classRef);
		}

		throw new ContractException(TaskitError.UNKNOWN_TRANSLATION_SPEC, classRef.getName());
	}

	/**
	 * A hash code implementation consistent with equals().
	 */
	@Override
	public int hashCode() {
		return Objects.hash(data, taskitEngineId, isInitialized);
	}

	/**
	 * Two {@link TaskitEngine}s are equal if and only if they have the same engine
	 * id, contain the same {@link TranslationSpec}s, and have the same
	 * initialization state.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof TaskitEngine)) {
			return false;
		}

		TaskitEngine other = (TaskitEngine) obj;

		return Objects.equals(taskitEngineId, other.taskitEngineId) && isInitialized == other.isInitialized
				&& Objects.equals(data, other.data);
	}

	protected abstract <O> void writeToFile(File file, O outputObject) throws IOException;

	protected abstract <I> I readFile(File file, Class<I> inputClassRef) throws IOException;

	private void validateObject(Object object) {
		if (object == null) {
			throw new ContractException(TaskitError.NULL_OBJECT_FOR_TRANSLATION);
		}
	}

	private void validateClass(Class<?> classRef) {
		if (classRef == null) {
			throw new ContractException(TaskitError.NULL_CLASS_REF);
		}
	}

	private void validateFilePath(Path path) {
		if (path == null) {
			throw new ContractException(TaskitError.NULL_PATH);
		}

		ResourceHelper.validateFilePath(path);
	}

	private void validateFile(Path path) {
		if (path == null) {
			throw new ContractException(TaskitError.NULL_PATH);
		}

		ResourceHelper.validateFile(path);
	}

}
