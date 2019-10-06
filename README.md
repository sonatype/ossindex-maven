<!--

    Copyright (c) 2018-present Sonatype, Inc. All rights reserved.

    This program is licensed to you under the Apache License Version 2.0,
    and you may not use this file except in compliance with the Apache License Version 2.0.
    You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.

    Unless required by applicable law or agreed to in writing,
    software distributed under the Apache License Version 2.0 is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.

-->
# Sonatype OSS Index - Maven Integrations

![license](https://img.shields.io/github/license/sonatype/ossindex-maven.svg)

![maven-central](https://img.shields.io/maven-central/v/org.sonatype.ossindex.maven/ossindex-maven.svg)

[Sonatype OSS Index](https://ossindex.sonatype.org/) integrations for [Apache Maven](https://maven.apache.org/).

## Usage

For information on how to use the integrations see the project documentation:

* https://sonatype.github.io/ossindex-maven/

## Building

### Requirements

* [Apache Maven](https://maven.apache.org/) 3.3+ (prefer to use included `mvnw`)
* JDK 7+ (10 is **NOT** supported)

### Build

    ./build rebuild

## Site 

### Building

    ./build site_build
    
### Publishing

    ./build site_deploy
