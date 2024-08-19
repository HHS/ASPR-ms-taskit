[![GPL LICENSE][license-shield]][license-url]
[![GitHub tag (with filter)][tag-shield]][tag-url]
[![GitHub contributors][contributors-shield]][contributors-url]
[![GitHub Workflow Status (with event)][dev-build-shield]][dev-build-url]
[![GitHub Workflow Status (with event)][build-shield]][build-url]

# Translation And Serialization Toolkit
The Translation and Serialization Toolkit (Taskit) is a library that was created to facilitate converting from various input files into Java Objects. This is especially useful for Simulation Models such as [GCM](https://github.com/HHS/ASPR-8).

Currently there is only 1 supported serialization format, and that is protobuf. Other formats such as binary will follow in the future.

As of v3.2.0, this project is in Maven Central.

## License
Distributed under the GPLv3 License. See [LICENSE](LICENSE) for more information.

Please read the [HHS vulnerability disclosure](https://www.hhs.gov/vulnerability-disclosure-policy/index.html).

## Usage 
To use this project in your project, simply add the following dependency to your `dependencies` section of your pom.xml file.
```
<dependency>
    <groupId>gov.hhs.aspr.ms.taskit</groupId>
    <artifactId>core</artifactId>
    <version>5.0.0</version>
</dependency>
```

To use the protobuf library of taskit, simply add the following dependency to your `dependencies` section of your pom.xml file.
```
<dependency>
    <groupId>gov.hhs.aspr.ms.taskit</groupId>
    <artifactId>protobuf</artifactId>
    <version>5.0.0</version>
</dependency>
```

## Overview
Currently Taskit is composed of a Core library and a Protobuf library.

### Core
[Core](core) contains TranslationSpec, TaskitEngine, TaskitEngineId, TaskitEngineManager, and Translator. These classes define the root level functionality of Taskit.

#### TranslationSpec
An abstract class that defines how to translate between two different Java Types. Implementers of this class must define the intricate details on the translation, while the abstract class strictly handles initialization and determining which internal translate method to call based on the given object that needs to be translated.

#### TaskitEngine
An abstract class that contains a mapping of classes to TranslationSpecs. It has the sole responsibility of translating/reading/writing. For translating, it will determine which TranslationSpec to use based on the class of the given object to translate. Implementers of this class must define how to read/write files, as that process can vary between serialization libraries.

#### TaskitEngineId
An identifier for a TaskitEngine, for use in the TaskitEngineManager.

#### TaskitEngineManager
The TaskitEngineManager can handle multiple TaskitEngines and allows the user to read/write/translate using the TaskitEngineId to determine which TaskitEngine to use. Contains the same methods as TaskitEngine, with an additional parameter for the TaskitEngineId.

#### Translator
A Translator is simply a class that can wrap a group of TranslationSpecs that should/will often be used together. It also contains a dependency mechanism that allows for Translators to depend on other Translators, which is useful if a given TranslationSpec requires another TranslationSpec that is not provided by the encompassing Translator. Translators must follow a DAG pattern; there cannot be duplicate Translators, missing Translators nor cyclic Translator dependencies.

### Protobuf
[Protobuf](protobuf) is a version of taskit made specifically to be used with protobuf.
This library builds on the Core library described above and adds a distinct TaskitEngine and TranslationSpecs needed to fully support protobuf.

#### Supported types
This library supports the following proto message types:
- messages
- enums
- Any
- basic types (int32, double, etc)
- Date

See [TestObject](protobuf/src/main/proto/gov/hhs/aspr/ms/taskit/protobuf/testobject.proto) for an example proto file.

## Building from Source

### Requirements
- Maven 3.8.x
- Java 17
- Favorite IDE for Java development
- Modeling Utilities located [here](https://github.com/HHS/ASPR-ms-util)
    
*Note that Modeling Utilities is in Maven Central, so there is no need to clone and build it.

### Building
To build this project:
- Clone the repo
- open a command line terminal
- navigate to the root folder of this project
- run the command: `mvn clean install`

## Documentation
Documentation has yet to be created. In the interim, the code is mostly commented and the javadocs do provide good detail with regards to method and class expectations. 

<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/HHS/ASPR-ms-taskit
[contributors-url]: https://github.com/HHS/ASPR-ms-taskit/graphs/contributors
[tag-shield]: https://img.shields.io/github/v/tag/HHS/ASPR-ms-taskit
[tag-url]: https://github.com/HHS/ASPR-ms-taskit/releases/latest
[license-shield]: https://img.shields.io/github/license/HHS/ASPR-ms-taskit
[license-url]: LICENSE
[dev-build-shield]: https://img.shields.io/github/actions/workflow/status/HHS/ASPR-ms-taskit/dev_build.yml?label=dev-build
[dev-build-url]: https://github.com/HHS/ASPR-ms-taskit/actions/workflows/dev_build.yml
[build-shield]: https://img.shields.io/github/actions/workflow/status/HHS/ASPR-ms-taskit/release_build.yml?label=release-build
[build-url]: https://github.com/HHS/ASPR-ms-taskit/actions/workflows/release_build.yml.yml
