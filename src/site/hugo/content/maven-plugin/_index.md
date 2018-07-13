---
title: Maven Plugin
subtitle: Apache Maven plugin for Sonatype OSS Index 
glyph: fas fa-puzzle-piece

draft: false

menu:
  topnav:
    identifier: Maven-Plugin
    weight: 10

categories:
  - maven
  - maven-plugin
tags:
  - ossindex-maven-plugin
---

Audit a project dependencies using [Sonatype OSS Index](https://ossindex.sonatype.org).

## Requirements

* [Java](http://java.oracle.com) 7+ (version 10 is **NOT** supported)
* [Apache Maven](https://maven.apache.org) 3.1+

## POM execution

Add an invocation of the [ossindex:audit](ossindex-audit/) goal via POM execution:

{{< highlight "xml" >}}
<build>
  <plugins>
    <plugin>
      <groupId>org.sonatype.ossindex.maven</groupId>
      <artifactId>ossindex-maven-plugin</artifactId>
      <executions>
        <execution>
          <id>audit-dependencies</id>
          <phase>validate</verify>
          <goals>
            <goal>audit</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  <plugins>
</build>
{{</ highlight >}}

## Command-line

If you can not change the POM, run the [ossindex:audit](ossindex-audit/) goal directly on the command-line
with a project:

    mvn org.sonatype.ossindex.maven:ossindex-maven-plugin:audit -f pom.xml
    
## Goals

Goals available for this plugin:

{{< mavenplugin-goals plugin="ossindex-maven-plugin" >}}

## Usage

You should specify the version in your project's plugin configuration:

{{< mavenplugin-usage plugin="ossindex-maven-plugin" >}}

## Features
