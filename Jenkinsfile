pipeline {
    agent any

    environment {
        jenkinsUrl = "env.JENKINS_URL"
        buildUrl = "env.BUILD_URL"
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
                git 'https://github.com/mfdemo/da-pipeline-app.git'

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
                    archiveArtifacts 'target/*.jar'

					// Upload artefacts into DA
					daPublish siteName: "${daSitename}",
						component: "${daComponentName}", 
						baseDir: "${WORKSPACE}", 
						directoryOffset: "target",
						versionName: "${appVersion}-${BUILD_NUMBER}",
						fileIncludePatterns: "${daComponentName}.jar",
						fileExcludePatterns: """**/*tmp*
							**/.git""",
						versionProps: """job.url=${buildUrl}
                            jenkins.url=${jenkinsUrl}
                            git.commit.id=
                            issueIds=""",
						skip: false,
						addStatus: false, 
						statusName: "BUILT",
						deploy: false, 
						deployIf: '',
						deployUpdateJobStatus: true,
						deployApp: '', 
						deployEnv: '',
						deployProc: '', 
						deployProps: '',
                        runProcess: false,
						processIf: '',
						processName: '',
						processProps: '',
						processUpdateJobStatus: false,
						resourceName: ''
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
