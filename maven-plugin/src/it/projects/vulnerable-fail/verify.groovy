File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('Failed to execute goal org.sonatype.ossindex.maven:ossindex-maven-plugin')
assert buildLog.text.contains('Detected 1 vulnerable components')
