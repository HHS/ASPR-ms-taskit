[![GPL LICENSE][license-shield]][license-url]
[![GitHub tag (with filter)][tag-shield]][tag-url]
[![GitHub contributors][contributors-shield]][contributors-url]
[![GitHub Workflow Status (with event)][dev-build-shield]][dev-build-url]
[![GitHub Workflow Status (with event)][build-shield]][build-url]

# Translation And Serialization Toolkit
The Translation and Serialization Tookkil (Taskit) is a library that was created to faciliate converting from various input files into Java Objects. This is espcially useful for Simulation Models such as [GCM](https://github.com/HHS/ASPR-8).

Currently there is only 1 supported serialzation format, and that is protobuf. Other formats such as binary will follow in the future.

As of v3.2.0, this project is in Maven Central.

## License
Distributed under the GPLv3 License. See [LICENSE](LICENSE) for more information.

Please read the [HHS vulnerability discloure](https://www.hhs.gov/vulnerability-disclosure-policy/index.html).

## Usage 
To use this project in your project, simply add the following dependency to your `dependencies` section of your pom.xml file.
```
<dependency>
    <groupId>gov.hhs.aspr.ms.taskit</groupId>
    <artifactId>core</artifactId>
    <version>3.2.1</version>
</dependency>
```

To use the protobuf library of taskit, simply add the following dependency to your `dependencies` section of your pom.xml file.
```
<dependency>
    <groupId>gov.hhs.aspr.ms.taskit</groupId>
    <artifactId>protobuf</artifactId>
    <version>3.2.1</version>
</dependency>
```

## Overview
Currently Taskit is composed of a Core library and a Protobuf library.

### Core
[Core](core) is the base taskit engine and contains the root level functionality that drives the translation and serialization.
This library contains the base TranslationController class, the base TranslationSpec class and the base TranslationEngine class.

#### TranslationController
The TranslationController class handles the delgation of reading/writing from/to input/output files.

#### TranslationSpec
The TranslationSpec class is an abstract class that must be impleneted to define how to convert between two Java Types, generally the input Java type and the application Java Type.

#### TranslationEngine
The TranslationEngine class delgates the converting of one type to another via TranslationSpecs as well as doing the actual reading/writing as delegated by the TranslationController.

### Protobuf
[Protobuf](protobuf) is a version of taskit made specifically to be used with protobuf.
This library builds on the Core library described above and adds a distinct TranslationEngine and TranslationSpecs needed to fully support protobuf.

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