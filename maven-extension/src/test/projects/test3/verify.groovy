File buildLog = new File(basedir, 'build.log')
// FIXME: adjust assertion
assert buildLog.text.contains('org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies failed with message')