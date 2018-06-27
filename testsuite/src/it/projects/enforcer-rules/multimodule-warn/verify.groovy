File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies warned with message')