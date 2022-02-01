@Library(['private-pipeline-library', 'jenkins-shared']) _

mavenPipeline(
  mavenVersion: 'Maven 3.6.x',
  javaVersion: 'Java 8',
  mavenOptions: '-Dit -Dbuild.notes="b:${BRANCH_NAME}, j:${JOB_NAME}, n:#${BUILD_NUMBER}"',
  usePublicSettingsXmlFile: true,
  useEventSpy: false,
  runFeatureBranchPolicyEvaluations: true,
  testResults: [ '**/target/*-reports/*.xml' ]
)
