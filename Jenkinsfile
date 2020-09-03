pipeline {
    agent any

    environment {
        GIT_REPO = "https://github.com/mfdemo/da-pipeline-app.git"
	APP_NAME = "DA Pipeline App"
	APP_VER = "1.0"
	COMPONENT_NAME = "da-pipeline-app"
	DA_PROFILE_NAME = "ara.mfdemouk.com"
	DA_DEPLOY_PROCESS = "Deploy"
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
		    if (isUnix()) {	
                        sh "git rev-parse HEAD > .git/commit-id"
		    } else {	    
                        bat(/git rev-parse HEAD > .git\commit-id/)
		    }	    
                    env.GIT_COMMIT_ID = readFile('.git/commit-id').trim()
                    env.GIT_COMMIT_AUTHOR = bat(script: "git log -1 --pretty=%%an ${env.GIT_COMMIT_ID}", returnStdout: true).trim()
                    env.GIT_COMMIT_MSG = bat(script: "git log -1 --pretty=%%B ${env.GIT_COMMIT_ID}", returnStdout: true).trim()
                }

                println "Git commit id: ${env.GIT_COMMIT_ID}"
                println "Git commit author: ${env.GIT_COMMIT_AUTHOR}"

		if (isUnix()) {
                    // Run Maven on a Unix agent.
                    sh "mvn -Dmaven.com.failure.ignore=true clean package"
		} else {
                    // To run Maven on a Windows agent, use
                    bat "mvn -Dmaven.com.failure.ignore=true clean package"
		}	
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
                daPublish siteName: "${env.DA_PROFILE_NAME}",
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
                    statusName: "BUILT", // make sure this version status exists in DA
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
                // Deploy the Application to the Integration environment using DA
                daRunApplicationProcess siteName: "${env.DA_PROFILE_NAME}",
                    applicationName: "${env.APP_NAME}",
                    applicationProcessName: "${env.DA_DEPLOY_PROCESS}",
                    componentName: "${env.COMPONENT_NAME}",
                    versionName: "${env.APP_VER}-${BUILD_NUMBER}",
                    applicationProcessProperties: """job.url=${env.BUILD_URL}
jenkins.url=${env.JENKINS_URL}
git.commit.id=${env.GIT_COMMIT_ID}
issueIds=""",
                    environmentName: 'Integration',
                    runApplicationProcessIf: '',
                    updateJobStatus: true
            }

            post {
                // If deployment successful add the version status "INTEGRATED"
                success {
                    daUpdateVersionStatus siteName: "${env.DA_PROFILE_NAME}",
                        action: '',
                        componentName: "${env.COMPONENT_NAME}",
                        versionName: "${env.APP_VER}-${BUILD_NUMBER}",
                        statusName: 'INTEGRATED' // make sure this version status exists in DA
                }
                // If deployment failed add the version status "FAILED"
                failure {
                    daUpdateVersionStatus siteName: "${env.DA_PROFILE_NAME}",
                        action: '',
                        componentName: "${env.COMPONENT_NAME}",
                        versionName: "${env.APP_VER}-${BUILD_NUMBER}",
                        statusName: 'FAILED' // make sure this version status exists in DA
                }
            }
        }
		
    }
}
