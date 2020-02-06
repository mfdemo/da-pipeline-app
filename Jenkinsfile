pipeline {
    agent any

    environment {
		appName = "DA Pipeline App"
		appVersion = "1.0"
		daSitename = "octane.mfdemouk.com"
		daComponentName = "da-pipeline-app"
        gitCommitId = ""
		issuesIds = ""
		
    }

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "M3"
    }

    stages {
        stage('Build') {
            steps {
                // Get some code from a GitHub repository
                git 'https://github.com/mfdemo/simple-secure-app.git'

                // Run Maven on a Unix agent.
                //sh "mvn -Dmaven.com.failure.ignore=true clean package"

                // To run Maven on a Windows agent, use
                bat "mvn -Dmaven.com.failure.ignore=true clean package"
            }

            post {
                // If Maven was able to run the tests, even if some of the com
                // failed, record the com results and archive the jar file.
                success {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.war'
					
					// Set component version propeties
					def verProperties =
					"""job.url=${env.BUILD_URL}
					jenkins.url=${env.JENKINS_URL}
					git.commit.id=${gitCommitId}
					issueIds=${issueIds}"""
		
					// Upload artefacts into DA
					daPublish siteName: "${daSitename}",
						component: "${daComponentName}", 
						baseDir: "${WORKSPACE}", 
						directoryOffset: 'target', 
						versionName: '${appVersion}-${BUILD_NUMBER}', 
						fileIncludePatterns: '${daComponentName}.jar',
						fileExcludePatterns: '''**/*tmp*
							**/.git''',
						versionProps: "${verProperties}",
						skip: false,
						addStatus: false, 
						statusName: 'BUILT',
						deploy: false, 
						deployIf: ''
						deployUpdateJobStatus: true,
						deployApp: '', 
						deployEnv: '', , 
						deployProc: '', 
						deployProps: '' 
                }
            }

        }

        stage('Integration') {
            steps {
                println "Deploying to Integration"
            }
        }
		
    }
}
