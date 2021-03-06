---
title: Enforcer Rules
subtitle: Apache Maven Enforcer Rules for Sonatype OSS Index
glyph: fas fa-ruler-combined

draft: false

menu:
  topnav:
    identifier: Enforcer-Rules
    weight: 20

categories:
  - maven
  - enforcer-rules
tags:
  - ossindex-maven-enforcer-rules
---

{{% tag class="lead" %}}
Audit a project dependencies using [Sonatype OSS Index](https://ossindex.sonatype.org) invoked via
[Apache Maven Enforcer Plugin](https://maven.apache.org/enforcer/maven-enforcer-plugin/).
{{% /tag %}}

## Requirements

* [Java](http://java.oracle.com) 7+ (version 10 is **NOT** supported)
* [Apache Maven](https://maven.apache.org) 3.1+
* [Apache Maven Enforcer Plugin](https://maven.apache.org/enforcer/maven-enforcer-plugin/) 3+

## Dependency

{{< maven-dependency module="ossindex-maven-enforcer-rules" skip-d="true" >}}

## Ban Vulnerable Dependencies

To ban vulnerable dependencies from being consumed by builds:

{{< highlight "xml" >}}
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-enforcer-plugin</artifactId>
      <dependencies>
        <dependency>
          <groupId>org.sonatype.ossindex.maven</groupId>
          <artifactId>ossindex-maven-enforcer-rules</artifactId>
        </dependency>
      </dependencies>
      <executions>
        <execution>
          <id>vulnerability-checks</id>
          <phase>validate</phase>
          <goals>
            <goal>enforce</goal>
          </goals>
          <configuration>
            <rules>
              <banVulnerable implementation="org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies"/>
            </rules>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
{{< /highlight >}}

### Parameters

{{% tag class="table table-striped table-header-nowrap" %}}
Parameter                   | Type                        | Description | Default Value
--------------------------- | --------------------------- | ----------- | -------------
**scope**                   | String                      | Limit [scope](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Scope) of dependency resolution. | <i class="missing-data"></i>
**transitive**              | Boolean                     | Include transitive dependencies | `true`
**cvssScoreThreshold**      | Float                       | CVSS-score threshold.  Vulnerabilities with lower scores will be excluded. | `0.0`
**excludeCoordinates**      | Set of MavenCoordinates     | Set of [coordinates](https://sonatype.github.io/ossindex-maven/maven/apidocs/org/sonatype/ossindex/maven/common/MavenCoordinates.html) to exclude from vulnerability matching. | <i class="missing-data"></i>
**excludeVulnerabilityIds** | Set of String               | Set of vulnerability identifiers to exclude from matching. | <i class="missing-data"></i>
**level**                   | String                      | Levels steering whether a rule should fail a build (`ERROR`) or just display a warning (`WARN`). | `ERROR`
**clientConfiguration**     | OssindexClientConfiguration | [Client configuration](https://sonatype.github.io/ossindex-public/maven/apidocs/org/sonatype/ossindex/service/client/OssindexClientConfiguration.html). | <i class="missing-data"></i>
**authId**                  | String                      | Set client authentication from Maven settings server configuration. | <i class="missing-data"></i>
{{% /tag %}}

## Features