File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('[WARNING] Excluding coordinates: commons-fileupload:commons-fileupload:1.3')