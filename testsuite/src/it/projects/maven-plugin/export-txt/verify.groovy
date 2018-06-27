File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('[WARNING] Detected 1 vulnerable components')

File report = new File(basedir, 'target/report.txt')
assert report.exists()

String text = report.text
assert text.contains('Detected 1 vulnerable components:')
assert text.contains('commons-fileupload:commons-fileupload:jar:1.3:compile;')