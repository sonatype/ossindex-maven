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

## Goals

Goals available for this plugin:

{{< mavenplugin-goals plugin="ossindex-maven-plugin" >}}

## Usage

You should specify the version in your project's plugin configuration:

{{< mavenplugin-usage plugin="ossindex-maven-plugin" >}}

## Features
