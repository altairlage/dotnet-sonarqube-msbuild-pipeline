# dotnet-sonarqube-msbuild-pipeline
Sample project used to build a C/.net project and send information to sonarqube server.


##Jenkins configuration:
Jenkins must have SonarQube server and SonarScanner for MSBuild configured to be able to run SonarQube scans and send reports to the server.


### SonarQube MSBuild server
This step must be done in every Jenkins Controller that will use this node to run builds and send reports to SonarQube server.

1. Navigate to Jenkins/<Controller>/Manage Jenkins/Configure system
2. Under 'SonarQube servers' click 'Add SonarQube' to add a new entry called 'SonarQubeMSBuild' with our production SonarQube server info
2.1. Name: SonarQubeMSBuild
2.2. Server URL: https://<Sonar server URL>
2.3. Server authentication token: Select the credential which allows jenkins in sonarqube (Sonarqube user)
2.4. Example: ![image](https://user-images.githubusercontent.com/1898233/184916494-cfd9611e-ffcd-4fcb-a4c8-d2c1935597d8.png)
3. Save


### SonarScanner for MSBuild Global tool
This step must be done in every Jenkins Controller that will use this node to run builds and send reports to SonarQube server.

1. Navigate to Jenkins/<Controller>/Manage Jenkins/Global Tool Configuration
2. Under 'SonarScanner for MSBuild' Click 'Add SonarScanner for MSBuild' to add a new installation
2.1. Name: SonarQubeMSBuild
2.2. Check 'Install automatically' option
2.3. Version: Pick the latest version
2.4. Example: ![image](https://user-images.githubusercontent.com/1898233/184916655-6c9206c9-6b68-4fd7-a59e-81a12b728582.png)
3. Save


### Install build-wrapper-win-x86-64.exe
Install build-wrapper:
1. Download it from https://<SonarQube Server URL>/static/cpp/build-wrapper-win-x86.zip
2. Unzip it to the desired folder
3. Add it to the PATH environment variable


### Installation files
After the first Jenkins pipeline run, Jenkins will install the SonarScanner for MSBuild Global files under: <Jenkins installation>/tools.
Example: ![image](https://user-images.githubusercontent.com/1898233/184917273-b0363b07-de6f-4603-af2e-2c3fa068ac94.png)


## References:
https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-msbuild/
https://docs.sonarqube.org/latest/analysis/languages/cfamily/
https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-jenkins/
https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-msbuild/ 
https://stackoverflow.com/questions/58558449/sonarscanner-msbuild-tool-is-not-running-in-pipeline-jenkins
https://community.sonarsource.com/t/sonarscanner-msbuild-exe-and-build-wrapper-win-x86-64-exe/25460
https://community.sonarsource.com/t/sonarcloud-with-msbuild-build-wrapper-on-github-actions/35607
https://community.sonarsource.com/t/jenkins-declarative-pipeline-with-sonarcube-and-msbuild/36964
