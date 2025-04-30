
pipeline {
    agent any

    tools {
        jdk 'JDK17'
    }
    environment {
        registryCredential = 'ecr:us-east-1:awscreds'
        appRegistry = "329254499416.dkr.ecr.us-east-1.amazonaws.com/peopledb-app"
        vprofileRegistry = ""
        cluster="peopledb-cluster2"
        service="peopledbservice"
    }

    stages {
        stage('Checkout clonage du dépot') {
            steps {
                echo "======== Clonage du dépôt ========"
                git branch: 'master', url: 'https://github.com/lindor-crypto/springBoot-peopledb-web.git'
		
            }
        }

        stage('Nettoyage du workspace') {
            steps {
                echo "🧹 Nettoyage du dépôt local avant build"
                sh 'git clean -xfd'
                sh 'git reset --hard'
            }
        }

        stage('Check config') {
            steps {
                echo "======== Vérification du fichier checkstyle.xml ========"
                sh 'chmod +x config/checkstyle/checkstyle.xml'
                sh 'ls -l config/checkstyle/checkstyle.xml'
            }
        }

        stage('Build avec Gradle') {
            steps {
                echo "======== Construction du projet ========"
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Tests unitaires') {
            steps {
                echo "======== Exécution des tests ========"
                sh './gradlew test'
            }
        }

        stage('Analyse Checkstyle') {
            steps {
                echo "======== Analyse Checkstyle ========"
                sh './gradlew checkstyleMain'
            }
        }

        stage('Archivage du JAR') {
            steps {
                echo "======== Archivage de l\'artefact ========"
                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
            }
        }
        stage('Analyse SonarQube') {
            steps {
             echo "======== Analyse SonarQube ========"

            withSonarQubeEnv('sonarserver') {
             sh '''
                ./gradlew sonar \
                  -Dsonar.projectKey=PeopleDB-Web \
                  -Dsonar.projectName="PeopleDB Web" \
                  -Dsonar.java.checkstyle.reportPaths=build/reports/checkstyle/main.xml
            '''
             }
            }
        }

        stage('Sonar Quality Gate') {
            steps {
        timeout(time: 1, unit: 'HOURS') {
            script {
                def qg = waitForQualityGate()
                    if (qg.status != 'OK') {
                     echo "⚠️ Quality Gate échoué : ${qg.status}, mais on continue quand même."
                    } else {
                        echo "✅ Quality Gate passé."
                      }
                 }
                }
            }
        }
        stage('Build App Image') {
          steps {
            dir('app/springboot-peopledb-web') {
            script {
                dockerImage = docker.build( appRegistry + ":$BUILD_NUMBER")
                }
            }
          }
        }

        stage('Upload App Image') {
          steps{
            script {
              docker.withRegistry( vprofileRegistry, registryCredential ) {
                dockerImage.push("$BUILD_NUMBER")
                dockerImage.push('latest')
              }
            }
          }
        }
        stage('Remove Docker Image') {
            steps {
            echo "🧹 Suppression de l'image Docker après le build..."
             sh 'docker rmi -f $(docker images -a -q)'

            }
        }
        stage('Deploy to ecs') {
          steps {
            withAWS(credentials: 'awscreds', region: 'us-east-1') {
            sh 'aws ecs update-service --cluster ${cluster} --service ${service} --force-new-deployment'
               }
          }
        }


    }

    post {
        success {
            echo "✅ Build réussi."
        }
        failure {
            echo "❌ Échec du build."
        }
    }
}
