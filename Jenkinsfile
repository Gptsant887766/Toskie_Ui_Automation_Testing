#!/usr/bin/env groovy
/**
 * Toskie QA Automation — Declarative Jenkins Pipeline
 *
 * Prerequisites (install via Jenkins Plugin Manager):
 *   - Pipeline                  (bundled)
 *   - JUnit Plugin              (bundled)
 *   - Credentials Binding       (bundled)
 *   - AnsiColor Plugin          (optional — colour in console output)
 *   - Allure Jenkins Plugin     (for allure() publisher step)
 *   - Slack Notification Plugin (for slackSend() — leave SLACK_CHANNEL empty to disable)
 *
 * Jenkins Credentials to configure (Manage Credentials → Global):
 *   Kind: Secret text, IDs:
 *     toskie-test-mobile   → test user phone number
 *     toskie-test-email    → test user email address
 *     toskie-qa-secret     → OTP bypass header secret
 *     toskie-device-id     → device ID header value
 *     toskie-fingerprint   → fingerprint header value
 *
 * Jenkins Global Tool Configuration:
 *   JDK  → name "JDK-21"    (Java 21)
 *   Maven → name "Maven-3.9" (Maven 3.9.x)
 *
 * Usage:
 *   Default trigger : Any Jenkins schedule / webhook — runs smoke on DEV
 *   Manual          : Build with Parameters → choose ENVIRONMENT, BROWSER, SUITE
 *   PR gate         : Multibranch Pipeline → smoke on DEV for every PR
 */
pipeline {

    agent {
        label 'qa-agent'    // Tag your Jenkins node(s) with this label
    }

    tools {
        jdk   'JDK-21'
        maven 'Maven-3.9'
    }

    // ─── Build Parameters ─────────────────────────────────────────────────────
    parameters {

        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'qa', 'stage', 'prod'],
            description: '''Target deployment environment.
dev   → smoke suite  | DEV URLs  | fast feedback
qa    → regression   | QA URLs   | full functional
stage → master suite | STAGE URLs | full regression + all modules
prod  → smoke only   | PROD URLs  | read-only smoke (no destructive tests)'''
        )

        choice(
            name: 'BROWSER',
            choices: ['chrome', 'chromium', 'firefox', 'webkit'],
            description: 'Browser for test execution. chrome/chromium require Chrome or Chromium installed on the agent.'
        )

        choice(
            name: 'SUITE',
            choices: ['default', 'smoke', 'sanity', 'regression', 'security', 'api', 'master'],
            description: '"default" uses the environment-specific suite. Any other value overrides it.'
        )

        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browsers in headless mode. Always true in CI — set false only for debugging on a display-enabled agent.'
        )

        booleanParam(
            name: 'GENERATE_ALLURE_REPORT',
            defaultValue: true,
            description: 'Generate the Allure HTML report from JSON results after tests complete.'
        )

        string(
            name: 'SLACK_CHANNEL',
            defaultValue: '#qa-alerts',
            description: 'Slack channel for build result notifications. Leave empty to disable.'
        )
    }

    // ─── Global Environment Variables ─────────────────────────────────────────
    environment {
        CI          = 'true'
        HEADLESS    = "${params.HEADLESS}"

        // Injected from Jenkins credential store — never echo these
        TEST_MOBILE   = credentials('toskie-test-mobile')
        TEST_EMAIL    = credentials('toskie-test-email')
        QA_SECRET     = credentials('toskie-qa-secret')
        DEVICE_ID     = credentials('toskie-device-id')
        FINGERPRINT   = credentials('toskie-fingerprint')

        ALLURE_RESULTS = "${WORKSPACE}/target/allure-results"
        REPORTS_DIR    = "${WORKSPACE}/Reports"
        SCREENSHOTS    = "${WORKSPACE}/SnapShots"
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '10'))
        timeout(time: 6, unit: 'HOURS')
        timestamps()
        disableConcurrentBuilds()
        ansiColor('xterm')
    }

    // ─── Pipeline Stages ──────────────────────────────────────────────────────
    stages {

        // ── 1. Checkout ───────────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                cleanWs()
                checkout scm
                script {
                    def commitHash = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    def commitAuthor = sh(script: "git log -1 --format='%an'", returnStdout: true).trim()
                    echo """
╔══════════════════════════════════════════════════════╗
║  TOSKIE QA — PIPELINE INITIALISED                   ║
║  Build         : #${BUILD_NUMBER}                   ║
║  Branch        : ${GIT_BRANCH}                      ║
║  Commit        : ${commitHash} by ${commitAuthor}   ║
║  Environment   : ${params.ENVIRONMENT.toUpperCase()}║
║  Browser       : ${params.BROWSER}                  ║
║  Suite         : ${params.SUITE}                    ║
║  Headless      : ${params.HEADLESS}                 ║
╚══════════════════════════════════════════════════════╝"""
                }
            }
        }

        // ── 2. Setup ──────────────────────────────────────────────────────────
        stage('Setup') {
            steps {
                sh '''
                    echo "[Setup] Tool versions:"
                    java -version
                    mvn -version

                    echo "[Setup] Creating output directories..."
                    mkdir -p "${REPORTS_DIR}" "${SCREENSHOTS}" "${ALLURE_RESULTS}"
                '''
            }
        }

        // ── 3. Compile ────────────────────────────────────────────────────────
        stage('Compile') {
            steps {
                sh "mvn compile -P${params.ENVIRONMENT} -q"
            }
        }

        // ── 4. Run Tests ──────────────────────────────────────────────────────
        stage('Run Tests') {
            steps {
                script {
                    // Resolve suite XML — 'default' uses the environment's profile suite
                    def suiteArg = ''
                    if (params.SUITE != 'default') {
                        def suiteMap = [
                            'smoke'      : 'src/test/resources/suites/smoke.xml',
                            'sanity'     : 'src/test/resources/suites/sanity.xml',
                            'regression' : 'src/test/resources/suites/regression.xml',
                            'security'   : 'src/test/resources/suites/security.xml',
                            'api'        : 'src/test/resources/suites/api.xml',
                            'master'     : 'src/test/resources/ToskieMasterSuite.xml',
                        ]
                        def path = suiteMap.get(params.SUITE, '')
                        if (path) suiteArg = "-DsuiteXmlFile=${path}"
                    }

                    sh """
                        mvn test \\
                            -P${params.ENVIRONMENT} \\
                            -Dbrowser=${params.BROWSER} \\
                            -Dheadless=${params.HEADLESS} \\
                            -DtestMobile=\${TEST_MOBILE} \\
                            -DtestEmail=\${TEST_EMAIL} \\
                            -DqaSecret=\${QA_SECRET} \\
                            -DdeviceId=\${DEVICE_ID} \\
                            -Dfingerprint=\${FINGERPRINT} \\
                            ${suiteArg} \\
                            -Dsurefire.failIfNoSpecifiedTests=false \\
                            -q
                    """
                }
            }
            post {
                always {
                    // Publish TestNG XML results to Jenkins test result view
                    junit(
                        allowEmptyResults: true,
                        testResults: 'target/surefire-reports/*.xml'
                    )
                }
            }
        }

        // ── 5. Generate Allure Report ─────────────────────────────────────────
        stage('Allure Report') {
            when {
                expression { return params.GENERATE_ALLURE_REPORT }
            }
            steps {
                sh 'mvn allure:report -q'
            }
            post {
                always {
                    allure([
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'target/allure-results']]
                    ])
                }
            }
        }

        // ── 6. Archive Artifacts ──────────────────────────────────────────────
        stage('Archive') {
            steps {
                archiveArtifacts(
                    allowEmptyArchive: true,
                    fingerprint: true,
                    artifacts: [
                        'Reports/**',
                        'SnapShots/**',
                        'target/allure-report/**',
                        'target/surefire-reports/**'
                    ].join(', ')
                )
            }
        }
    }

    // ─── Post-Build Actions ────────────────────────────────────────────────────
    post {

        always {
            echo "[Pipeline] Finished — ENV:${params.ENVIRONMENT} | SUITE:${params.SUITE} | BROWSER:${params.BROWSER}"
            cleanWs(cleanWhenNotBuilt: false, cleanWhenAborted: true,
                    cleanWhenFailure: false, cleanWhenSuccess: true,
                    deleteDirs: true, notFailBuild: true,
                    patterns: [[pattern: 'target/classes/**', type: 'INCLUDE'],
                               [pattern: 'target/test-classes/**', type: 'INCLUDE']])
        }

        success {
            script {
                if (params.SLACK_CHANNEL?.trim()) {
                    slackSend(
                        channel: params.SLACK_CHANNEL,
                        color: 'good',
                        message: """:white_check_mark: *Toskie QA — BUILD PASSED*
*Environment* : `${params.ENVIRONMENT.toUpperCase()}`  |  *Suite* : `${params.SUITE}`  |  *Browser* : `${params.BROWSER}`
*Build* : <${BUILD_URL}|#${BUILD_NUMBER}>  |  *Branch* : `${GIT_BRANCH}`
<${BUILD_URL}allure/|:bar_chart: Allure Report>  |  <${BUILD_URL}artifact/Reports/|:page_facing_up: Extent Report>"""
                    )
                }
            }
        }

        failure {
            script {
                if (params.SLACK_CHANNEL?.trim()) {
                    slackSend(
                        channel: params.SLACK_CHANNEL,
                        color: 'danger',
                        message: """:x: *Toskie QA — BUILD FAILED*
*Environment* : `${params.ENVIRONMENT.toUpperCase()}`  |  *Suite* : `${params.SUITE}`  |  *Browser* : `${params.BROWSER}`
*Build* : <${BUILD_URL}|#${BUILD_NUMBER}>  |  *Branch* : `${GIT_BRANCH}`
<${BUILD_URL}artifact/SnapShots/|:camera: Screenshots>  |  <${BUILD_URL}testReport/|:clipboard: Test Report>"""
                    )
                }
            }
        }

        unstable {
            script {
                if (params.SLACK_CHANNEL?.trim()) {
                    slackSend(
                        channel: params.SLACK_CHANNEL,
                        color: 'warning',
                        message: """:warning: *Toskie QA — BUILD UNSTABLE* (some tests failed)
*Environment* : `${params.ENVIRONMENT.toUpperCase()}`  |  *Suite* : `${params.SUITE}`  |  *Browser* : `${params.BROWSER}`
*Build* : <${BUILD_URL}|#${BUILD_NUMBER}>  |  *Branch* : `${GIT_BRANCH}`
<${BUILD_URL}testReport/|:clipboard: Failure Details>"""
                    )
                }
            }
        }
    }
}
