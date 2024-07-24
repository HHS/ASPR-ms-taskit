package gov.hhs.aspr.ms.taskit.core.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceError;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

/**
 * The TaskitEngineManager allows {@link TaskitEngine}s to be added to it, and
 * acts as a wrapper around the TaskitEngine read/write/translate methods, using
 * the {@link TaskitEngineId} to select which engine to use for the given
 * operation
 */
public final class TaskitEngineManager {
	private final Data data;

	private TaskitEngineManager(Data data) {
		this.data = data;
	}

	private final static class Data {
		private final Map<TaskitEngineId, TaskitEngine> taskitEngineIdToEngineMap = new LinkedHashMap<>();

		Data() {
		}
	}

	/**
	 * Builder for the TaskitEngineManager
	 */
	public final static class Builder {
		Data data;

		private Builder(Data data) {
			this.data = data;
		}

		private void validateTaskitEngine(TaskitEngine taskitEngine) {
			if (taskitEngine == null) {
				throw new ContractException(TaskitError.NULL_TASKIT_ENGINE);
			}

			if (!taskitEngine.isInitialized()) {
				throw new ContractException(TaskitError.UNINITIALIZED_TASKIT_ENGINE);
			}
		}

		private void validateTaskitEngineAdded() {
			if (this.data.taskitEngineIdToEngineMap.isEmpty()) {
				throw new ContractException(TaskitError.NO_TASKIT_ENGINES);
			}
		}

		/**
		 * Builds the TaskitEngineManager.
		 * 
		 * @throws ContractException
		 *                           <ul>
		 *                           <li>{@linkplain TaskitError#NO_TASKIT_ENGINES} if
		 *                           no TaskitEngines were added</li>
		 *                           </ul>
		 */
		public TaskitEngineManager build() {
			// at least 1 engine must be added
			validateTaskitEngineAdded();

			return new TaskitEngineManager(this.data);
		}

		/**
		 * Adds a {@link TaskitEngine} to this TaskitEngineManager
		 * 
		 * @param taskitEngine the Taskit engine to add
		 * @return the builder instance
		 * 
		 * @throws ContractException
		 *                           <ul>
		 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE} if
		 *                           taskitEngine is null</li>
		 *                           <li>{@linkplain TaskitError#UNINITIALIZED_TASKIT_ENGINE}
		 *                           if the Taskit engine was not initialized prior to
		 *                           adding it to the manager</li>
		 *                           </ul>
		 */
		public Builder addTaskitEngine(TaskitEngine taskitEngine) {
			validateTaskitEngine(taskitEngine);

			this.data.taskitEngineIdToEngineMap.put(taskitEngine.getTaskitEngineId(), taskitEngine);

			return this;
		}
	}

	/**
	 * Returns a new instance of TaskitEngineManager Builder
	 */
	public static Builder builder() {
		return new Builder(new Data());
	}

	private void validateTaskitEngine(TaskitEngine taskitEngine) {
		if (taskitEngine == null) {
			throw new ContractException(TaskitError.NULL_TASKIT_ENGINE);
		}
	}

	private void validatePath(Path path) {
		if (path == null) {
			throw new ContractException(TaskitError.NULL_PATH);
		}

		ResourceHelper.validateFilePath(path);
	}

	private void validateClass(Class<?> classRef) {
		if (classRef == null) {
			throw new ContractException(TaskitError.NULL_CLASS_REF);
		}
	}

	private void validateObject(Object object) {
		if (object == null) {
			throw new ContractException(TaskitError.NULL_OBJECT_FOR_TRANSLATION);
		}
	}

	private void validateTaskitEngineId(TaskitEngineId taskitEngineId) {
		if (taskitEngineId == null) {
			throw new ContractException(TaskitError.NULL_TASKIT_ENGINE_ID);
		}
	}

	private TaskitEngine getTaskitEngine(TaskitEngineId taskitEngineId) {
		validateTaskitEngineId(taskitEngineId);

		TaskitEngine taskitEngine = this.data.taskitEngineIdToEngineMap.get(taskitEngineId);

		validateTaskitEngine(taskitEngine);

		return taskitEngine;
	}

	/**
	 * Using the given {@link TaskitEngineId}'s associated {@link TaskitEngine},
	 * reads the given file into the provided class type
	 * 
	 * @param <I>            the input type
	 * @param inputPath      the path of the file to read
	 * @param inputClassRef  the to read the file as
	 * @param taskitEngineId the taskitEngineId to use to read the file
	 * @return the resulting object from reading the file as the class
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_PATH} if the path
	 *                           is null</li>
	 *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
	 *                           if the path points to a directory instead of a
	 *                           file</li>
	 *                           <li>{@linkplain TaskitError#NULL_CLASS_REF} if the
	 *                           classRef is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_ID}
	 *                           if taskitEngineId is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE} if
	 *                           taskitEngine is null</li>
	 *                           </ul>
	 * @throws RuntimeException  if the reading of the file encounters an
	 *                           IOException
	 */
	public <I> I read(Path inputPath, Class<I> inputClassRef, TaskitEngineId taskitEngineId) {
		// TODO: validateFile not filePath
		validatePath(inputPath);
		validateClass(inputClassRef);

		TaskitEngine taskitEngine = getTaskitEngine(taskitEngineId);

		try {
			return taskitEngine.read(inputPath, inputClassRef);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Using the given {@link TaskitEngineId}'s associated {@link TaskitEngine},
	 * reads the given file into the provided class type and then translates it to
	 * the corresponding type associated with the input type.
	 * 
	 * @param <I>            the input type
	 * @param <T>            the translated type
	 * @param inputPath      the path of the file to read
	 * @param inputClassRef  the to read the file as
	 * @param taskitEngineId the taskitEngineId to use to read the file
	 * @return the resulting object from reading the file as the class
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_PATH} if the path
	 *                           is null</li>
	 *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
	 *                           if the path points to a directory instead of a
	 *                           file</li>
	 *                           <li>{@linkplain TaskitError#NULL_CLASS_REF} if the
	 *                           classRef is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_ID}
	 *                           if taskitEngineId is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE} if
	 *                           taskitEngine is null</li>
	 *                           </ul>
	 * @throws RuntimeException  if the reading of the file encounters an
	 *                           IOException
	 */
	public <I, T> T readAndTranslate(Path inputPath, Class<I> inputClassRef, TaskitEngineId taskitEngineId) {
		validatePath(inputPath);
		validateClass(inputClassRef);

		TaskitEngine taskitEngine = getTaskitEngine(taskitEngineId);

		try {
			return taskitEngine.readAndTranslate(inputPath, inputClassRef);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Using the given {@link TaskitEngineId}'s associated {@link TaskitEngine},
	 * writes the object to the file referenced by the Path.
	 * 
	 * @param <O>            the type of the object to write
	 * @param outputPath     the path of the file to write to
	 * @param outputObject   the object to write
	 * @param taskitEngineId the taskitEngineId to use to write the object
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_PATH} if the path
	 *                           is null</li>
	 *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
	 *                           if the path points to a directory instead of a
	 *                           file</li>
	 *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
	 *                           if the object is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_ID}
	 *                           if taskitEngineId is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE} if
	 *                           taskitEngine is null</li>
	 *                           </ul>
	 * @throws RuntimeException  if the writing of the file encounters an
	 *                           IOException
	 */
	public <O> void write(Path outputPath, O outputObject, TaskitEngineId taskitEngineId) {
		write(outputPath, outputObject, Optional.empty(), taskitEngineId, false);
	}

	/**
	 * Using the given {@link TaskitEngineId}'s associated {@link TaskitEngine},
	 * translates the object and then writes the translated object to the file
	 * reference by the Path.
	 * 
	 * @param <O>            the type of the object to write
	 * @param outputPath     the path of the file to write to
	 * @param outputObject   the object to write
	 * @param taskitEngineId the taskitEngineId to use to write the object
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_PATH} if the path
	 *                           is null</li>
	 *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
	 *                           if the path points to a directory instead of a
	 *                           file</li>
	 *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
	 *                           if the object is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_ID}
	 *                           if taskitEngineId is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE} if
	 *                           taskitEngine is null</li>
	 *                           </ul>
	 * @throws RuntimeException  if the writing of the file encounters an
	 *                           IOException
	 */
	public <O> void translateAndWrite(Path outputPath, O outputObject, TaskitEngineId taskitEngineId) {
		write(outputPath, outputObject, Optional.empty(), taskitEngineId, true);
	}

	/**
	 * Using the given {@link TaskitEngineId}'s associated {@link TaskitEngine},
	 * translates the object as the provided class and then writes the translated
	 * object to the file referenced by the Path.
	 * <p>
	 * The type params ensure that the object can be written as the provided class.
	 * </p>
	 * 
	 * @param <C>            the type to translate the object as
	 * @param <O>            the type of the object
	 * @param outputPath     the path of the file to write to
	 * @param outputObject   the object to write
	 * @param outputClassRef the class to translate the object as
	 * @param taskitEngineId the taskitEngineId to use to write the object
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_CLASS_REF} if the
	 *                           output classref is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_PATH} if the path
	 *                           is null</li>
	 *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
	 *                           if the path points to a directory instead of a
	 *                           file</li>
	 *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
	 *                           if the object is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_ID}
	 *                           if taskitEngineId is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE} if
	 *                           taskitEngine is null</li>
	 *                           </ul>
	 * @throws RuntimeException  if the writing of the file encounters an
	 *                           IOException
	 */
	public <C, O extends C> void translateAndWrite(Path outputPath, O outputObject, Class<C> outputClassRef,
			TaskitEngineId taskitEngineId) {
		validateClass(outputClassRef);

		write(outputPath, outputObject, Optional.of(outputClassRef), taskitEngineId, true);
	}

	/**
	 * Given an object, uses the class of the object to obtain the translationSpec
	 * and then calls {@link TranslationSpec#translate(Object)} to translate the
	 * object.
	 * <p>
	 * this conversion method will be used approximately 90% of the time.
	 * </p>
	 * 
	 * @param <T>            the translated type
	 * @param object         the object to translate
	 * @param taskitEngineId the taskitEngine to use for translate
	 * @return the translated object
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_ID}
	 *                           if taskitEngineId is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE} if
	 *                           taskitEngine is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
	 *                           if the passed in object is null</li>
	 *                           <li>{@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
	 *                           if no translationSpec was provided for the given
	 *                           objects class</li>
	 *                           </ul>
	 */
	public final <T> T translateObject(Object object, TaskitEngineId taskitEngineId) {
		TaskitEngine taskitEngine = getTaskitEngine(taskitEngineId);

		return taskitEngine.translateObject(object);
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
	 * @param taskitEngineId      the taskitEngine to use for translate
	 * @return the translated object
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_ID}
	 *                           if taskitEngineId is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE} if
	 *                           taskitEngine is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
	 *                           if the passed in object is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_CLASS_REF} if the
	 *                           passed in classRef is null</li>
	 *                           <li>{@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
	 *                           if no translationSpec was provided for the given
	 *                           objects class</li>
	 *                           </ul>
	 */
	public final <T, O extends C, C> T translateObjectAsClassSafe(O object, Class<C> translateAsClassRef,
			TaskitEngineId taskitEngineId) {

		TaskitEngine taskitEngine = getTaskitEngine(taskitEngineId);

		return taskitEngine.translateObjectAsClassSafe(object, translateAsClassRef);
	}

	/**
	 * Given an object, uses the given classRef to obtain the translationSpec and
	 * then calls {@link TranslationSpec#translate(Object)}.
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
	 * This conversion method will be used approximately 3% of the time.
	 * </p>
	 * 
	 * @param <T>                 the translated type
	 * @param <O>                 the type of the object
	 * @param <C>                 the type to translate the object as
	 * @param object              the object to translate
	 * @param translateAsClassRef the classRef of the type to translate the object
	 *                            as
	 * @param taskitEngineId      the taskitEngine to use for translate
	 * @return the translated object
	 * 
	 * @throws ContractException
	 *                           <ul>
	 * 
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_ID}
	 *                           if taskitEngineId is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE} if
	 *                           taskitEngine is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
	 *                           if the passed in object is null</li>
	 *                           <li>{@linkplain TaskitError#NULL_CLASS_REF} if the
	 *                           passed in classRef is null</li>
	 *                           <li>{@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
	 *                           if no translationSpec was provided for the given
	 *                           objects class</li>
	 *                           </ul>
	 */
	public final <T, O, C> T translateObjectAsClassUnsafe(O object, Class<C> translateAsClassRef,
			TaskitEngineId taskitEngineId) {
		TaskitEngine taskitEngine = getTaskitEngine(taskitEngineId);

		return taskitEngine.translateObjectAsClassUnsafe(object, translateAsClassRef);
	}

	/*
	 * package access for testing
	 * 
	 * calls the associated TaskitEngine write method depending on whether the
	 * translate flag is set and whether there is a classRef provided to translate
	 * the object as
	 */
	<C, O extends C> void write(Path outputPath, O outputObject, Optional<Class<C>> outputClassRef,
			TaskitEngineId taskitEngineId, boolean translateBeforeWrite) {

		validatePath(outputPath);
		validateObject(outputObject);

		TaskitEngine taskitEngine = getTaskitEngine(taskitEngineId);

		try {
			if (!translateBeforeWrite) {
				taskitEngine.write(outputPath, outputObject);
				return;
			}
			if (outputClassRef.isEmpty()) {
				taskitEngine.translateAndWrite(outputPath, outputObject);
				return;
			}
			taskitEngine.translateAndWrite(outputPath, outputObject, outputClassRef.get());
			return;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
