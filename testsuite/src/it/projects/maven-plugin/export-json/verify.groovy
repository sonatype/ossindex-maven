File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('[WARNING] Detected 2 vulnerable components')

File report = new File(basedir, 'target/report.json')
assert report.exists()

String text = report.text
assert text.contains('"commons-fileupload:commons-fileupload:jar:1.3:compile" : {')
assert text.contains('"coordinates" : "pkg:maven/commons-fileupload/commons-fileupload@1.3",')