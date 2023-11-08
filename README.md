[![GPL LICENSE][license-shield]][license-url]
[![GitHub contributors][contributors-shield]][contributors-url]

# Translation And Serialization Toolkit
A toolkit made to help with converting between input files and Java Objects. This will herein be reffered to as Taskit.

## What is Taskit?
Taskit is a library that was created to faciliate converting from various input files into Java Objects. This is espcially useful for Simulation Models such as [GCM](https://github.com/HHS/ASPR-8).

Currently there is only 1 supported serialzation format, and that is protobuf. Other formats such as binary will follow in the future.

## Overview
Currently Taskit is composed of a Core library and a Protobuf library.

### Core
[Core](core) is the base taskit engine and contains the root level functionality that drives the translation and serialization.
This library contains the base TranslationController class, the base TranslationSpec class and the base TranslationEngine class.

#### Translation Controller
The TranslationController class handles the delgation of reading/writing from/to input/output files.

#### TranslationSpec
The TranslationSpec class is an abstract class that must be impleneted to define how to convert between two Java Types, generally the input Java type and the application Java Type.

#### TranslationEngine
The TranslationEngine class delgates the converting of one type to another as well as doing the actual reading/writing as delegated by the TranslationController.

### Protobuf
[Protobuf](protobuf) is a version of taskit made specifically to be used with protobuf.
This library builds on the Core library described above and adds a distinct TranslationEngine and TranslationSpecs needed to fully support protobuf.

#### Supported types
This library supports the following proto message types:
- all message types
- enums
- Any
- basic types (int32, double, etc)
- Date

See [TestObject](protobuf/src/main/proto/gov/hhs/aspr/ms/taskit/protobuf/testobject.proto) for an example proto file.

## Requirements
- Maven 3.8.x
- Java 17
- Favorite IDE for Java development
- Modeling Util located [here](https://github.com/HHS/ASPR-ms-util)

## Building
To build, navigate into the root directory and run the command ```mvn clean install```

## Documentation
Documentation has yet to be created. In the interim, the code is mostly commented and the javadocs do provide good detail with regards to method and class expectations. 

## License
Distributed under the GPLv3 License. See [LICENSE](LICENSE) for more information.


<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/HHS/ASPR-ms-taskit
[contributors-url]: https://github.com/HHS/ASPR-ms-taskit/graphs/contributors
[tag-shield]: https://img.shields.io/github/v/tag/HHS/ASPR-ms-util
[tag-url]: https://github.com/HHS/ASPR-ms-taskit/releases/latest
[license-shield]: https://img.shields.io/github/license/HHS/ASPR-ms-taskit
[license-url]: LICENSE
