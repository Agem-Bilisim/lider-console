#!groovy

node {
   // Mark the code checkout 'stage'....
   stage 'Checkout'

   // Checkout code from repository
   checkout scm

   // Get the maven tool.
   // ** NOTE: This 'M3' maven tool must be configured
   // **       in the global configuration.
   //def mvnHome = tool 'M3'

   // Mark the code build 'stage'....
   stage 'Build'

   def mvnHome = "/usr/share/maven/bin"
   def workspace = pwd()   

   // Generate third party dependencies
   sh "${mvnHome}/mvn -f ${workspace}/lider-console-dependencies/pom.xml clean p2:site"
   sh "/bin/bash ${mvnHome}/mvn -f ${workspace}/lider-console-dependencies/ jetty:run & ; ${mvnHome}/mvn clean install -DskipTests"
   //sleep 20
   
   // Build project
   //sh "${mvnHome}/mvn clean install -DskipTests"
   
   // Invoke SonarQube
   sh "/usr/share/maven/bin/mvn clean verify sonar:sonar"

   // Run the maven build
   //sh "${mvnHome}/bin/mvn clean verify sonar:sonar"
   //sh "/usr/share/maven/bin/mvn clean verify sonar:sonar"
}
