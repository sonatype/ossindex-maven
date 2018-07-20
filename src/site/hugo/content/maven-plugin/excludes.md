---
title: "Maven Plugin: Excludes"
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
{{% tag class="lead" %}}
Sometimes audit may detect vulnerabilities which are not relevant.
{{% /tag %}}

In those cases audit can be configured to *exclude* vulnerabilities from matching.

## Exclude Specific Vulnerabilities

Specific vulnerabilities can be excluded by *vulnerability-id*.

For example to exclude [39d74cc8-457a-4e57-89ef-a258420138c5](https://ossindex.sonatype.org/vuln/39d74cc8-457a-4e57-89ef-a258420138c5):

{{< highlight "xml" >}}
<plugin>
  <groupId>org.sonatype.ossindex.maven</groupId>
  <artifactId>ossindex-maven-plugin</artifactId>
  <configuration>
    <excludeVulnerabilityIds>
      <exclude>39d74cc8-457a-4e57-89ef-a258420138c5</exclude>
    </excludeVulnerabilityIds>
  </configuration>
</plugin>
{{< /highlight >}}

This exclusion can also be configured via CLI property as a comma-separated list of ids:

{{< command >}}
mvn ossindex:audit -Dossindex.excludeVulnerabilityIds=39d74cc8-457a-4e57-89ef-a258420138c5
{{< /command >}}

## Exclude Specific Components

Specific components can be excluded by coordinates.

For example to exclude [commons-fileupload:commons-fileupload:1.3](https://ossindex.sonatype.org/component/maven:commons-fileupload/commons-fileupload@1.3):

{{< highlight "xml" >}}
<plugin>
  <groupId>org.sonatype.ossindex.maven</groupId>
  <artifactId>ossindex-maven-plugin</artifactId>
  <configuration>
    <excludeCoordinates>
      <exclude>
        <groupId>commons-fileupload</groupId>
        <artifactId>commons-fileupload</artifactId>
        <version>1.3</version>
      </exclude>
    </excludeCoordinates>
  </configuration>
</plugin>
{{< /highlight >}}

This exclusion can also be configured via CLI property as a comma-separated list of coordinates:

{{< command >}}
mvn ossindex:audit -Dossindex.excludeCoordinates=commons-fileupload:commons-fileupload:1.3
{{< /command >}}

**NOTE:** Only exact coordinate matches are excluded.