# Enforcer Rules

Audit a project dependencies using [Sonatype OSS Index](https://ossindex.sonatype.org) invoked via
[Apache Maven Enforcer Plugin](https://maven.apache.org/enforcer/maven-enforcer-plugin/).

## Ban Vulnerable Dependencies

To ban vulnerables dependencies from being consumed by builds:

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
              <id>checks</id>
              <phase>validate</phase>
              <goals>
                <goal>enforce</goal>
              </goals>
              <configuration>
                <rules>
                  <banVunerableDependencies
                      implementation="org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies">
                  </banVunerableDependencies>
                </rules>
              </configuration>
            </execution>
          </executions>
        </plugin>
      </plugins>
    </build>
