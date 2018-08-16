File buildLog = new File(basedir, 'build.log')
assert buildLog.text.contains('[WARNING] Excluding vulnerability ID: f20ae256-036e-4b0b-a2b1-ef80d904b245')
