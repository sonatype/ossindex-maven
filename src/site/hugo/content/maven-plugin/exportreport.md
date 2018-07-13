---
title: "Maven Plugin: Export Report"
glyph: fas fa-wrench

draft: false

menu:
  topnav:
    parent: Maven-Plugin

categories:
  - maven
  - maven-plugin
tags:
  - ossindex-maven-plugin
---
The [ossindex:audit](audit-mojo.html) can optionally record a report file which contains the details about the
component reports that are used to determine if a dependency is vulnerable or not.

To enable this feature configure the [reportFile](audit-mojo.html#reportFile) parameter to the location where
the report will be written.

{{< highlight "xml" >}}
<plugin>
  <groupId>org.sonatype.ossindex.maven</groupId>
  <artifactId>ossindex-maven-plugin</artifactId>
  <configuration>
    <reportFile>${project.build.directory}/audit-report.json</reportFile>
  </configuration>
</plugin>
{{</ highlight >}}

or via CLI:

    mvn ossindex:audit -Dossindex.reportFile=target/audit-report.json

## Formats

The file extension is used to inform which format the report will be written as.

Format &nbsp; | File Extension
------ | -------------- 
JSON   | `.json`        
XML    | `.xml`         
Text   | `.txt`         
