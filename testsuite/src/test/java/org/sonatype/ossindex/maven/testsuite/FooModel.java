package org.sonatype.ossindex.maven.testsuite;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FooModel
{
  private String foo;

  public String getFoo() {
    return foo;
  }

  public void setFoo(final String foo) {
    this.foo = foo;
  }
}
