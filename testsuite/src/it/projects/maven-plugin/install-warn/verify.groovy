File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('[WARNING] Detected 2 vulnerable components')
