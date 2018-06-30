File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies failed with message')

// TODO: how to easily verify http-proxy interaction?