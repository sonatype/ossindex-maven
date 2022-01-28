File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('[WARNING] Excluding coordinates: junit:junit:4.12')