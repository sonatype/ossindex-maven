File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('[WARNING] Excluding vulnerability ID: 43e6c5a5-b586-4b31-9244-b62b6e36f2d0')
