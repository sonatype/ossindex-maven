File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('[WARNING] Excluding CVSS-score: 0.0')
assert buildLog.text.contains('[WARNING] Excluding CVSS-score: 7.5')
