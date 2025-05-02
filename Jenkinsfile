pipeline {
    agent any

    tools {
        jdk 'JDK17'
        gradle 'Grad7.5'
    }

    environment {
        JAVA_OPTS = "-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=86400"
        registryCredential = 'ecr:us-east-1:awscreds'
        appRegistry = "346296964327.dkr.ecr.us-east-1.amazonaws.com/peopledb-app"
        vprofileRegistry = ""
        cluster = "peopledb-cluster"
        service = "peopledb-service"
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
                sh './gradlew --no-daemon clean build -x test'
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
