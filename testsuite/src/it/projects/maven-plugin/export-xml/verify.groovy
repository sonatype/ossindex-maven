File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('[WARNING] Detected 1 vulnerable components')

File report = new File(basedir, 'target/report.xml')
assert report.exists()

String text = report.text
assert text.contains('<key>commons-fileupload:commons-fileupload:jar:1.3:compile</key>')
assert text.contains('<coordinates>pkg:maven/commons-fileupload/commons-fileupload@1.3</coordinates>')