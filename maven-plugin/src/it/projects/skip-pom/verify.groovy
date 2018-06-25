File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('Skipping; POM module')
