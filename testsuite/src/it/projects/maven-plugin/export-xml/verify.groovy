File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('[WARNING] Detected 1 vulnerable components')

File report = new File(basedir, 'target/report.xml')
assert report.exists()