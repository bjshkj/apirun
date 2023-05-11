pipeline {
    agent {
        node {
            label 'metersphere'
        }
    }
    options { quietPeriod(600) }
    environment { 
        IMAGE_NAME = 'jmeter-master'
        IMAGE_PREFIX = 'registry.cn-qingdao.aliyuncs.com/metersphere'
    }
    stages {
        // stage('Docker build & push Base Image') {
        //     steps {
        //         sh "docker build -t jmeter-base:latest ./jmeter-base/"
        //         sh "docker tag jmeter-base:latest ${IMAGE_FREFIX}/jmeter-base:latest"
        //         sh "docker push ${IMAGE_FREFIX}/jmeter-base:latest"
        //     }
        // }
        stage('Docker build & push') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:\${TAG_NAME:-\$BRANCH_NAME} ./jmeter-master/"
                sh "docker tag ${IMAGE_NAME}:\${TAG_NAME:-\$BRANCH_NAME} ${IMAGE_PREFIX}/${IMAGE_NAME}:\${TAG_NAME:-\$BRANCH_NAME}"
                sh "docker push ${IMAGE_PREFIX}/${IMAGE_NAME}:\${TAG_NAME:-\$BRANCH_NAME}"
            }
        }
    }
    post('Notification') {
        always {
            sh "echo \$WEBHOOK\n"
            withCredentials([string(credentialsId: 'wechat-bot-webhook', variable: 'WEBHOOK')]) {
                qyWechatNotification failSend: true, mentionedId: '', mentionedMobile: '', webhookUrl: "$WEBHOOK"
            }
        }
    }
}
