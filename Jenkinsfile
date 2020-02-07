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

                script {}
                    // Get Git commit details
                    //sh "git rev-parse HEAD > .git\commit-id"
                    bat(/git rev-parse HEAD > .git\commit-id/)
                    env.GIT_COMMIT_ID = readFile('.git/commit-id').trim()
                    env.GIT_COMMIT_AUTHOR = bat(script: "git log -1 --pretty=%%an ${gitCommitId}", returnStdout: true).trim()
                    env.GIT_COMMIT_MSG = bat(script: "git log -1 --pretty=%%B ${gitCommitId}", returnStdout: true).trim()
                }

                println "Git commit id: ${env.GIT_COMMIT_ID}"
                println "Git commit author: ${env.GIT_COMMIT_AUTHOR}"

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

                }
            }

        }

        stage('Publish') {
            steps {
                // Upload artefacts into DA
                daPublish siteName: "${daSitename}",
                    component: "${daComponentName}",
                    baseDir: "${WORKSPACE}",
                    directoryOffset: "target",
                    versionName: "${appVersion}-${BUILD_NUMBER}",
                    fileIncludePatterns: "${daComponentName}.jar",
                    fileExcludePatterns: """**/*tmp*
                        **/.git""",
                    versionProps: """job.url=${env.BUILD_URL}
                        jenkins.url=${env.JENKINS_URL}
                        git.commit.id=${env.GIT_COMMIT_ID}
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

        stage('Integration') {
            steps {
                println "Deploying to Integration"
            }
        }
		
    }
}
