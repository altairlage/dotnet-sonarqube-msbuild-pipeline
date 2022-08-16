#!/usr/bin/env groovy

// Build-Library is a sample library to be imported
// In the real world, it would be a groovy library containing re-usable code for pipelines
@Library('Build-library')

// to be imported relative to Build-library
import org.altabuild.helper

// Initialize all objects
def helper = new Helper() // org.altabuild.helper.Helper

// Initialize all variables
def gitHubUser = "jenkins-gh"
def teamName = "MyTeam"
def repoName = "sample-c-project-in-visual-studio"
def gitHubUrl = "https://my-github-server.com/${teamName}/${repoName}.git"
def artifactoryPath = "https://my-artifactory-server.com/artifactory/sample-c-project-in-visual-studio"
def msbuildPath = "\"C:\\Program Files (x86)\\Microsoft Visual Studio\\2017\\Enterprise\\MSBuild\\15.0\\Bin\\MSBuild.exe\""

// Get Jenkins job properties. Also sets initial job run defaults for properties
this.props()

node("my-windows-jenkins-node") {
   try {
       
        // clean the workspace
        deleteDir()

        stage('Checkout') {
            dir('files'){
                helper.checkOut(gitHubUrl, build_branch, gitHubUser)
            }
        }

        stage('Build Info'){
            artifact_version="${version}.$BUILD_NUMBER"
            
            if(version_tag == "")
                displayed_version="${version}"
            else
                displayed_version="\"${version} - ${version_tag}\""

            bat("echo \"artifact_version=${artifact_version}\" >> $WORKSPACE/build.properties")
            println "--- ****************** build parameters ****************** ---"
            println "---    build_number:               $BUILD_NUMBER           ---"
            println "---    build_branch:               ${build_branch}         ---"
            println "---    version:                    ${version}              ---"
            println "---    version_tag:                ${version_tag}         ---"
            println "---    displayed_version:          ${displayed_version}    ---"
            println "---    artifact_version:           ${artifact_version}     ---"
            println "---    configuration:              ${configuration}        ---"
            println "--- ****************************************************** ---"
        }

        stage('Sonar and build'){
            def scannerHome = tool 'SonarQubeMSBuild'
            commonProperties = "/d:sonar.branch.name=${build_branch} /name:sample-c-project-in-visual-studio /key:sample-c-project-in-visual-studio /version:${version}"
            
            dir("files/sample-c-project-in-visual-studio"){
                
                withSonarQubeEnv('SonarQubeMSBuild') {
                    
                    // bat("dotnet ${scannerHome} begin ${commonProperties}")
                    println("\n\n\n--- env.SONAR_HOST_URL: ${env.SONAR_HOST_URL}\n\n\n")
                    
                    println("\n\n\n--- scannerHome:")
                    bat("dir ${scannerHome}")
                    println("\n\n\n")
                    
                    println("--- Running command build-wrapper-win-x86-64.exe --out-dir  bw_output MSBuild.exe /t:Rebuild /nodeReuse:False:")
                    bat("build-wrapper-win-x86-64.exe --out-dir  bw_output ${msbuildPath} /t:Rebuild /nodeReuse:False")
                    println("\n\n\n")
                    
                    println("--- Running command ${scannerHome}\\SonarScanner.MSBuild.exe begin ${commonProperties} /d:sonar.cfamily.build-wrapper-output=bw_output")
                    bat("${scannerHome}\\SonarScanner.MSBuild.exe begin ${commonProperties} /d:sonar.cfamily.build-wrapper-output=bw_output")
                    println("\n\n\n")
                    
                    println("--- Running actual build commands from original pipeline:")
                    bat("${msbuildPath} /p:Configuration=${configuration} /p:Platform=x64 /t:clean /m ${repoName}.sln")
                    bat("${msbuildPath} /p:Configuration=${configuration} /p:Platform=x64 /t:clean /m ${repoName}.sln")
                    bat("${msbuildPath} /p:Configuration=${configuration} /p:Platform=x64 /p:WL_VERSION=${displayed_version} /t:rebuild /m ${repoName}.sln")
                    bat("${msbuildPath} /p:Configuration=${configuration} /p:Platform=x86 /p:WL_VERSION=${displayed_version} /t:rebuild /m ${repoName}.sln")
                    
                    println("--- Running command ${scannerHome}\\SonarScanner.MSBuild.exe end")
                    bat("${scannerHome}\\SonarScanner.MSBuild.exe end")
                    bat("dir")
                }
            }
        }

    } catch (e) {
        println("something failed")
        throw e

    }
}


// Get/Set Jenkins job properties. Set property defaults on job first-run.
def props() {
    properties(
        [
            [
                $class:'ParametersDefinitionProperty',
                parameterDefinitions: [
                    [
                        $class: 'StringParameterDefinition',
                        name: 'version',
                        description: 'DLLs version',
                        defaultValue: '1.0.0'
                    ],
                    [
                        $class: 'StringParameterDefinition',
                        name: 'version_tag',
                        description: 'String to append after the version, on About screen',
                        defaultValue: ''
                    ],
                    [
                        $class: 'ChoiceParameterDefinition',
                        name: 'configuration',
                        choices: 'Debug\nRelease',
                        description: '.Net project build configuration',
                        defaultValue: 'Release'
                    ],
                    [
                        $class: 'StringParameterDefinition',
                        name: 'build_branch',
                        description: 'Branch to be used to build the DLLs',
                        defaultValue: 'master'
                    ],
                ]
            ]
        ]
    )

    try {
        build_branch = "$build_branch"
    } catch (e) {
        println "--- Error setting build_branch parameter value. Setting it to default 'master' ---"
        build_branch = 'master'
    }
}
