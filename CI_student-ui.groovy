pipeline {
    agent any

    stages {
        stage('clone') {
            steps {
               git 'https://github.com/atiwaskar/dockerfile.git'
              }
            }
          stage('docker-build') {
            steps {
                sh 'docker build -t my-app:latest .'
            }
        }
        stage('push-img_ECR') {
            steps {
                sh '''
                aws ecr get-login-password --region ap-northeast-1 | docker login --username AWS --password-stdin 805797752511.dkr.ecr.ap-northeast-1.amazonaws.com
                docker tag my-app:latest 805797752511.dkr.ecr.ap-northeast-1.amazonaws.com/my-app:latest
                docker push 805797752511.dkr.ecr.ap-northeast-1.amazonaws.com/my-app:latest
                '''
            }
        }
        stage('clone-menifest') {
            steps {
              checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/atiwaskar/jenkin-eks.git']])
              }
            }
            stage('replace_new-image') {
            steps {
                  sh 'sed -i "s/latest/$BUILD_ID/" k8s_jenkins.yml'
              }
            }
            stage('push to github') {
            steps {
                withCredentials([gitUsernamePassword(credentialsId: 'git-cred', gitToolName: 'Default')]) {
                  sh '''
                  git init 
                  git config --global user.email "tiwaskarakshay92@gamail.com"
                  git config --global user.name "atiwaskar"
                  git add .
                  git commit -m "update k8s-jenkines.yml"
                  git checkout -b main
                  git push origin main
                    '''
                }
            }
        
    }
}
}
