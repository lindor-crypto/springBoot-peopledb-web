pipeline {
    agent any

    tools {
        jdk 'JDK17'
        gradle 'Grad7.5'
    }

    environment {
        registryCredential = 'ecr:us-east-1:awscreds'
        appRegistry = "346296964327.dkr.ecr.us-east-1.amazonaws.com/peopledb-app"
        vprofileRegistry = ""
        cluster = "peopledb-cluster"
        service = "peopledb-service"
    }

    stages {
        stage('Clonage du dépôt') {
            steps {
                echo "📥 Clonage du dépôt"
                git branch: 'master', url: 'https://github.com/lindor-crypto/springBoot-peopledb-web.git'
            }
        }

        stage('Préparation') {
            steps {
                echo "🧹 Nettoyage du dépôt local"
                sh 'git clean -xfd'
                sh 'git reset --hard'
                sh 'chmod +x gradlew'
            }
        }

        stage('Check config') {
            steps {
                echo "🔍 Vérification du fichier checkstyle.xml"
                sh 'ls -l config/checkstyle/checkstyle.xml || true'
            }
        }

        stage('Build & Tests') {
            steps {
                echo "🏗️ Build du projet et tests unitaires"
                sh './gradlew clean build'
            }
        }

        stage('Analyse Checkstyle') {
            steps {
                echo "🔎 Analyse du code avec Checkstyle"
                sh './gradlew checkstyleMain'
            }
        }

        stage('Archivage du JAR') {
            steps {
                echo "📦 Archivage de l’artefact"
                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
            }
        }

        stage('Build App Image') {
            steps {
                script {
                    echo "🐳 Construction de l’image Docker"
                    dockerImage = docker.build("${appRegistry}:${BUILD_NUMBER}")
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    echo "☁️ Envoi de l’image vers ECR"
                    docker.withRegistry(vprofileRegistry, registryCredential) {
                        dockerImage.push("${BUILD_NUMBER}")
                        dockerImage.push("latest")
                    }
                }
            }
        }

        stage('Nettoyage Docker') {
            steps {
                echo "🧹 Suppression des images locales"
                sh 'docker rmi -f $(docker images -a -q) || true'
            }
        }

        stage('Déploiement ECS') {
            steps {
                echo "🚀 Déploiement sur ECS"
                withAWS(credentials: 'awscreds', region: 'us-east-1') {
                    sh 'aws ecs update-service --cluster ${cluster} --service ${service} --force-new-deployment'
                }
            }
        }
    }

    post {
        success {
            echo "✅ Build terminé avec succès."
        }
        failure {
            echo "❌ Le build a échoué."
        }
    }
}
