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
   
   // Generate third party dependencies
   sh "cd ./lider-console-dependencies | /usr/share/maven/bin/mvn clean p2:site | /usr/share/maven/bin/mvn jetty:run & | J_PID=\$!"
   //sh "/usr/share/maven/bin/mvn clean p2:site"
   //sh "/usr/share/maven/bin/mvn jetty:run &"
   //sh "J_PID=\$!"
   //sh "cd ../"
   
   // Build project
   sh "/usr/share/maven/bin/mvn clean install -DskipTests"
   sh "kill \$J_PID"
   
   // Invoke SonarQube
   sh "/usr/share/maven/bin/mvn clean verify sonar:sonar"

   // Run the maven build
   //sh "${mvnHome}/bin/mvn clean verify sonar:sonar"
   //sh "/usr/share/maven/bin/mvn clean verify sonar:sonar"
}
