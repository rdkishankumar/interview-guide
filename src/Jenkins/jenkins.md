# **Parameterized** jenkins
1.pipeline {
agent any

    parameters {
        string(
            name: 'FirstName',
            defaultValue: 'kishan',
            description: 'Parameter job config'
        )
    }

    stages {
        stage('Parameter Job') {
            steps {
                echo "My name is ${params.FirstName}"
            }
        }
    }
} 
# Build Multi Job
3. pipeline {
   agent any

   stages {
   stage('Upstream Job') {
   steps {
   echo 'This is Upstream Job'
   }
   }

        stage('Trigger Downstream') {
            steps {
                build job: 'Down-stream-job'
            }
        }
    }
   }
# Run Post job
  ```groovy
  pipeline {
    agent any

    stages {
        stage("First-Stage") {
            steps {
                echo "Hello World"
            }
        }
    }

    post {
        always {
            echo "Job Completed"
        }

        success {
            echo "It's success"
        }

        failure {
            echo "It's failed"
        }
    }
}
  ```