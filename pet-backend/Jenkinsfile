pipeline {
    agent any
    
    triggers {
        githubPush()
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📦 Git Repository 체크아웃 중...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo '🔨 Gradle 빌드 시작...'
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }
        
        stage('Test') {
            steps {
                echo '🧪 단위 테스트 실행 중...'
                sh './gradlew test'
            }
            post {
                always {
                    script {
                        if (fileExists('**/build/test-results/test/*.xml')) {
                            junit '**/build/test-results/test/*.xml'
                        } else {
                            echo '⚠️ 테스트 결과 파일이 없습니다 (테스트 코드 미작성)'
                        }
                    }
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                echo '🐳 Docker 이미지 빌드 중...'
                sh '''
                    docker build -t petmediscan-backend:${BUILD_NUMBER} .
                    docker tag petmediscan-backend:${BUILD_NUMBER} petmediscan-backend:latest
                '''
            }
        }
        
        stage('Deploy') {
            steps {
                echo '🚀 Docker 컨테이너 재배포 중...'
                sh '''
                    docker stop petmediscan-backend || true
                    docker rm petmediscan-backend || true
                    
                    docker run -d \
                        --name petmediscan-backend \
                        --network petmediscan-infra_petmediscan-network \
                        --network-alias backend \
                        --restart unless-stopped \
                        petmediscan-backend:latest
                '''
            }
        }
    }
    
    post {
        success {
            echo '✅ 빌드 및 배포 성공!'
        }
        failure {
            echo '❌ 빌드 또는 배포 실패!'
        }
        always {
            echo '🧹 워크스페이스 정리 중...'
            cleanWs()
        }
    }
}
