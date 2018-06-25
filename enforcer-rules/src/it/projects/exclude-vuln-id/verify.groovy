File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies failed with message')
assert buildLog.text.contains('Excluded vulnerabilities')