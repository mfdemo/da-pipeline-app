pipeline {
    agent any

    environment {
        GIT_REPO = "https://github.com/mfdemo/da-pipeline-app.git"
		APP_NAME = "DA Pipeline App"
		APP_VER = "1.0"
		COMPONENT_NAME = "da-pipeline-app"
		DA_SITE_NAME = "octane.mfdemouk.com"
    }

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "M3"
    }

    stages {
        stage('Build') {
            steps {
                // Get some code from a GitHub repository
                git "${env.GIT_REPO}"

                script {
                    // Get Git commit details
                    //sh "git rev-parse HEAD > .git\commit-id"
                    bat(/git rev-parse HEAD > .git\commit-id/)
                    env.GIT_COMMIT_ID = readFile('.git/commit-id').trim()
                    env.GIT_COMMIT_AUTHOR = bat(script: "git log -1 --pretty=%%an ${env.GIT_COMMIT_ID}", returnStdout: true).trim()
                    env.GIT_COMMIT_MSG = bat(script: "git log -1 --pretty=%%B ${env.GIT_COMMIT_ID}", returnStdout: true).trim()
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
                    junit "**/target/surefire-reports/TEST-*.xml"
                    archiveArtifacts "target/${env.COMPONENT_NAME}.jar"
                }
            }

        }

        stage('Publish') {
            steps {
                // Upload artefacts into DA
                daPublish siteName: "${env.DA_SITE_NAME}",
                    component: "${env.COMPONENT_NAME}",
                    baseDir: "${WORKSPACE}",
                    directoryOffset: "target",
                    versionName: "${env.APP_VER}-${BUILD_NUMBER}",
                    fileIncludePatterns: "${env.COMPONENT_NAME}.jar",
                    fileExcludePatterns: """**/*tmp*\n**/.git""",
                    versionProps: """job.url=${env.BUILD_URL}
jenkins.url=${env.JENKINS_URL}
git.commit.id=${env.GIT_COMMIT_ID}
issueIds=""",
                    skip: false,
                    addStatus: true,
                    statusName: "BUILT",
                    deploy: false,
                    deployIf: '',
                    deployUpdateJobStatus: false,
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
