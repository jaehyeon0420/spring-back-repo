pipeline {
    agent any
  
    environment { //전역 환경변수
        DOCKER_REGISTRY = "docker.io"
        // Docker Hub 사용자 이름
        DOCKERHUB_USERNAME = 'baejaehyeon'
        // Docker Hub에 업로드할 이미지 이름
        DOCKER_IMAGE_NAME = "spring-app"
        // Jenkins 빌드 번호로 버전 구분
        DOCKER_IMAGE_TAG = "1.0.${BUILD_NUMBER}"  
        // 젠킨스 Credential ID (Docker Hub 로그인 정보)
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
        FULL_IMAGE_NAME = "${DOCKER_REGISTRY}/${DOCKERHUB_USERNAME}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
    }

    stages {
       stage('Cleanup Workspace') {
            steps {
                // 빌드마다 이전 빌드의 모든 파일을 삭제
                cleanWs()
            }
        }
        stage('Checkout Code') {
            steps {
                // GitHub 저장소에서 코드 가져오기
                git branch: 'dev', url: 'https://github.com/jaehyeon0420/spring-back-repo.git'
            }
        }

        stage('Build') {
            steps {
                // Maven을 사용하여 프로젝트 빌드
                bat 'mvn clean package'  
 	    //Gradle bat './gradlew clean build'
            }
        }

        stage('Build Docker Image') {
            steps {
                // Dockerfile을 이용해 Docker 이미지 빌드
                // 빌드된 jar 파일은 Dockerfile에 의해 이미지에 포함.
                script {
                    def imageTag = "${env.DOCKERHUB_USERNAME}/${env.DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}"
                    bat "docker build -t ${imageTag} ."
                    env.DOCKER_IMAGE_TAG = imageTag
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                // Docker Hub 로그인 정보를 사용하여 이미지 푸시
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS_ID, usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_PASS')]) {
                    bat "docker login -u ${DOCKERHUB_USER} -p ${DOCKERHUB_PASS}"
                    bat "docker push ${env.DOCKER_IMAGE_TAG}"
                }
            }
        }
    }
}