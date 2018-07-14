---
title: "Enforcer Rules: Excludes"
glyph: fas fa-wrench

draft: false

menu:
  topnav:
    parent: Enforcer-Rules

categories:
  - maven
  - enforcer-rules
tags:
  - ossindex-maven-enforcer-rules
---
# Excludes

Sometimes `BanVulnerableDependencies` may detect vulnerabilities which are not relevant.
In those cases *exclusions* can be configured to prevent from vulnerabilities from matching.

## Exclude Specific Vulnerabilities

Specific vulnerabilities can be excluded by *vulnerability-id*.

For example to exclude [39d74cc8-457a-4e57-89ef-a258420138c5](https://ossindex.sonatype.org/vuln/39d74cc8-457a-4e57-89ef-a258420138c5):

{{< highlight "xml" >}}
<configuration>
  <rules>
    <banVulnerable implementation="org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies">
      <excludeVulnerabilityIds>
        <exclude>39d74cc8-457a-4e57-89ef-a258420138c5</exclude>
      </excludeVulnerabilityIds>
    </banVulnerable>
  </rules>
</configuration>
{{< /highlight >}}

## Exclude Specific Components

Specific components can be excluded by coordinates.

For example to exclude [commons-fileupload:commons-fileupload:1.3](https://ossindex.sonatype.org/component/maven:commons-fileupload/commons-fileupload@1.3):

{{< highlight "xml" >}}
<configuration>
  <rules>
    <banVulnerable implementation="org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies">
      <excludeCoordinates>
        <exclude>
          <groupId>commons-fileupload</groupId>
          <artifactId>commons-fileupload</artifactId>
          <version>1.3</version>
        </exclude>
      </excludeCoordinates>
    </banVulnerable>
  </rules>
</configuration>
{{< /highlight >}}

**NOTE:** Only exact coordinate matches are excluded.