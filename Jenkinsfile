String jdkVersion = 'Java 8'

String mavenVersion = 'Maven 3.5.x'
String mavenSettings = 'public-settings.xml'
String mavenRepo = '.repo'
String mavenOptions = '-V -B -e'
String buildOptions = '-Dit -Dbuild.notes="b:${BRANCH_NAME}, j:${JOB_NAME}, n:#${BUILD_NUMBER}"'

String deployBranch = 'main'

pipeline {
  options {
    buildDiscarder(
        logRotator(numToKeepStr: '100', daysToKeepStr: '14',  artifactNumToKeepStr: '20', artifactDaysToKeepStr: '10')
    )
    disableConcurrentBuilds()
    timestamps()
  }

  agent {
    label 'ubuntu-zion'
  }

  triggers {
    pollSCM('*/15 * * * *')
  }

  tools {
    maven mavenVersion
    jdk jdkVersion
  }

  stages {
    stage('Build') {
      when {
        not {
          branch deployBranch
        }
      }
      steps {
        withMaven(maven: mavenVersion, jdk: jdkVersion, mavenSettingsConfig: mavenSettings, mavenLocalRepo: mavenRepo,
            // disable automatic artifact publisher
            options: [ artifactsPublisher(disabled: true) ]) {
          sh "mvn $mavenOptions clean install $buildOptions"
        }
      }
    }

    stage('Deploy') {
      when {
        branch deployBranch
      }
      steps {
        withMaven(maven: mavenVersion, jdk: jdkVersion, mavenSettingsConfig: mavenSettings, mavenLocalRepo: mavenRepo,
            // disable automatic artifact publisher
            options: [ artifactsPublisher(disabled: true) ]) {
          sh "mvn $mavenOptions clean deploy $buildOptions"
        }
      }
    }

    stage('Evaluate Policy') {
      steps {
        nexusPolicyEvaluation iqApplication: 'ossindex-maven', iqStage: 'build',
            // HACK: bogus path here to only scan indexed modules
            iqScanPatterns: [[scanPattern: 'no-such-path/*']]
      }
    }
  }

  post {
    always {
      // capture testsuite reports and logs
      archiveArtifacts artifacts: 'testsuite/target/failsafe-reports/*,testsuite/target/it-workspace/**/build.log', fingerprint: false
    }
    cleanup {
      // purge workspace after build finishes
      deleteDir()
    }
  }
}
