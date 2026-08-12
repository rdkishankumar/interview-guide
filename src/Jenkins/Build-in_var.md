# Jenkins Jobs & Pipeline Architecture: Built-in Environment Variables

---

## Built-in Environment Variables Overview

Jenkins automatically exposes a comprehensive set of predefined **Built-in Environment Variables** to every executing job and pipeline. These variables capture real-time metadata about the build run, the execution node, the SCM context, and the Jenkins controller environment without requiring any manual setup.

```
┌────────────────────────────────────────────────────────────────────────┐
│               Jenkins Built-in Environment Variables                   │
├────────────────────────────────────────────────────────────────────────┤
│  Pipeline Runtime Execution Context                                    │
│    ├── Build Metadata   ──> env.BUILD_NUMBER, env.BUILD_ID, env.BUILD_TAG│
│    ├── Job & Server Info ──> env.JOB_NAME, env.JENKINS_URL             │
│    └── Agent Environment──> env.NODE_NAME, env.WORKSPACE, env.JAVA_HOME │
└────────────────────────────────────────────────────────────────────────┘

```

---

## Key Built-in Variables Reference

The table below highlights the most commonly used built-in environment variables available natively inside any Declarative or Scripted `Jenkinsfile`:

| Variable Name | Description | Example Output / Value |
| --- | --- | --- |
| **`env.BUILD_NUMBER`** | The current sequential build number. Increments with every build run. | `1`, `42`, `108` |
| **`env.BUILD_ID`** | The exact timestamp ID assigned to the build run. | `2026-08-02_17-48-12` |
| **`env.BUILD_TAG`** | A unique string identifying the job name and build number. | `jenkins-demo-job-42` |
| **`env.BUILD_URL`** | Full canonical HTTP URL pointing to the build run page in the Jenkins UI. | `[http://jenkins.example.com:8080/job/demo/42/](http://jenkins.example.com:8080/job/demo/42/)` |
| **`env.JOB_NAME`** | Name of the project or pipeline job currently executing. | `built-in-variables-pipeline` |
| **`env.NODE_NAME`** | Name of the agent node executing the build (`built-in` / `master` for controller). | `linux-worker-01` |
| **`env.WORKSPACE`** | Absolute file system path of the workspace directory allocated to the job on the agent host. | `/var/lib/jenkins/workspace/demo-job` |
| **`env.JENKINS_URL`** | Base HTTP URL of the Jenkins controller instance. | `[http://192.168.1.100:8080/](http://192.168.1.100:8080/)` |
| **`env.EXECUTOR_NUMBER`** | The unique ID of the specific executor slot running the build on the host node. | `0`, `1` |

---

## Practical Real-World Use Case: Dynamic Docker Image Tagging

A primary real-world production application of built-in variables—specifically **`env.BUILD_NUMBER`** and **`env.JOB_NAME`**—is dynamically tagging Docker container images during continuous integration builds.

### Why It Matters

Using static tags like `docker build -t app:latest .` creates ambiguity because every new build overwrites `latest`. Tagging images with `${env.BUILD_NUMBER}` guarantees **immutable, traceable artifacts** that tie container images directly back to the exact Jenkins build log that produced them.

```groovy
pipeline {
    agent any

    environment {
        REGISTRY_HOST = 'docker.io/myorg'
    }

    stages {
        stage('Build & Tag Docker Image') {
            steps {
                echo "Building Docker image for Job: ${env.JOB_NAME}"
                
                // Dynamically tag container image using build number and commit metadata
                sh """
                    docker build -t ${env.REGISTRY_HOST}/${env.JOB_NAME}:${env.BUILD_NUMBER} .
                    docker tag ${env.REGISTRY_HOST}/${env.JOB_NAME}:${env.BUILD_NUMBER} ${env.REGISTRY_HOST}/${env.JOB_NAME}:latest
                """
            }
        }
    }
}

```

---

## Step-by-Step Practical Walkthrough

### 1. Create the Declarative Pipeline Job

1. Open **Jenkins Dashboard** $\rightarrow$ Click **New Item**.
2. Enter item name: `builtin-variables-demo`.
3. Select **Pipeline** $\rightarrow$ Click **OK**.

### 2. Configure the Pipeline Script

Paste the following Declarative Groovy DSL script into the **Pipeline script** box:

```groovy
pipeline {
    agent any

    stages {
        stage('Inspect Built-in Variables') {
            steps {
                // Double quotes enable Groovy variable expansion for ${env.VAR_NAME}
                echo "Current Build Iteration : ${env.BUILD_NUMBER}"
                echo "Running Job Name        : ${env.JOB_NAME}"
                echo "Build Timestamp ID      : ${env.BUILD_ID}"
                echo "Assigned Agent Node     : ${env.NODE_NAME}"
                echo "Agent Workspace Path    : ${env.WORKSPACE}"
                echo "Jenkins Access URL      : ${env.JENKINS_URL}"
            }
        }
    }
}

```

---

## Execution Logs & Verification

1. Click **Save** and then click **Build Now** twice to create build **#1** and build **#2**.
2. Open **Build #1** $\rightarrow$ Click **Console Output**:

```text
Started by user Admin
[Pipeline] Start of Pipeline
[Pipeline] node
Running on Jenkins in /var/lib/jenkins/workspace/builtin-variables-demo
[Pipeline] {
[Pipeline] stage
[Pipeline] { (Inspect Built-in Variables)
[Pipeline] echo
Current Build Iteration : 1
[Pipeline] echo
Running Job Name        : builtin-variables-demo
[Pipeline] echo
Build Timestamp ID      : 2026-08-02_17-48-12
[Pipeline] echo
Assigned Agent Node     : built-in
[Pipeline] echo
Agent Workspace Path    : /var/lib/jenkins/workspace/builtin-variables-demo
[Pipeline] echo
Jenkins Access URL      : http://localhost:8080/
[Pipeline] }
[Pipeline] End of Pipeline
Finished: SUCCESS

```

3. Open **Build #2** $\rightarrow$ Click **Console Output**:
   Observe that `Current Build Iteration` automatically increments to **`2`**, while `Running Job Name` remains consistent (`builtin-variables-demo`).

---

## Accessing Available Variables Endpoint in Jenkins

To inspect the full, complete list of built-in environment variables supported by your specific Jenkins installation and installed plugins, navigate directly to the following URL path on your controller:

$$\text{\texttt{http://<JENKINS\_HOST>:8080/pipeline-syntax/globals\#env}}$$

---

## Key File System Paths & Directories

| Directory / File Path | Description & Purpose |
| --- | --- |
| **`JENKINS_HOME/workspace/<JOB_NAME>/`** | File path exposed dynamically by the **`${env.WORKSPACE}`** built-in variable. |
| **`JENKINS_HOME/jobs/<JOB_NAME>/builds/<BUILD_NUMBER>/`** | Disk location where Jenkins stores build artifacts and console logs corresponding to **`${env.BUILD_NUMBER}`**. |

---

## Troubleshooting & Common Scenarios

### Problem: Variable prints literal text `${env.BUILD_NUMBER}` instead of expanding to `1`

* **Problem:** Console output log prints `Current build is: ${env.BUILD_NUMBER}` literally.
* **Cause:** The string in the `echo` or `sh` step was enclosed in single quotes (`'...'`) instead of double quotes (`"..."`). Single quotes in Apache Groovy are raw string literals.
* **Solution:** Wrap the string in double quotes (`"..."`) or triple double quotes (`"""..."""`) to enable GString interpolation.
```groovy
// Incorrect (Single quotes = Literal)
echo 'Build number is: ${env.BUILD_NUMBER}'

// Correct (Double quotes = Interpolated)
echo "Build number is: ${env.BUILD_NUMBER}"

```



---

## Quick Revision Cheat Sheet

### Built-in Variables Essentials

* **Namespace Access:** `${env.VARIABLE_NAME}`
* **`env.BUILD_NUMBER`:** Auto-incrementing integer identifier for build runs; ideal for tagging Docker images or release archives.
* **`env.JOB_NAME`:** The current project job name; useful for Slack/email alert notifications and pathing.
* **`env.WORKSPACE`:** Path to agent build working directory.
* **`env.BUILD_URL`:** Full web path to the build execution log in the Jenkins web UI.
* **Interpolation Rule:** Requires **double quotes (`"..."`)** in Groovy DSL steps.

---

## Jenkins Interview Questions & Answers

### Question 1: What are built-in environment variables in Jenkins, and how do you access them in a Declarative Jenkinsfile?

**Short Answer:**

Built-in environment variables are predefined context variables automatically provided by Jenkins (such as `BUILD_NUMBER`, `JOB_NAME`, and `WORKSPACE`). They are accessed using the `env` object namespace inside double-quoted strings (e.g., `${env.BUILD_NUMBER}`).

**Detailed Explanation:**

Jenkins injects runtime metadata into the execution context of every job run. Engineers reference these properties via `env.VARIABLE_NAME` inside steps. String interpolation requires double quotes in Groovy (e.g., `echo "Building ${env.JOB_NAME} #${env.BUILD_NUMBER}"`); single-quoted strings treat variable references as unexpanded literal text.

---

### Question 2: Give a real-world DevOps scenario where `env.BUILD_NUMBER` is used in a CI/CD pipeline.

**Short Answer:**

`env.BUILD_NUMBER` is commonly used to tag Docker container images (`my-app:105`) or name compiled release archives (`app-v105.war`), ensuring image immutability and precise traceability to Jenkins build runs.

**Detailed Explanation:**

In containerized CI/CD pipelines, overwriting image tags like `latest` makes tracking which code commit runs in production difficult. By appending `${env.BUILD_NUMBER}` to the image tag during the `docker build` stage (e.g., `docker build -t registry/app:${env.BUILD_NUMBER} .`), every build produces an immutable, uniquely versioned artifact. If a bug occurs in production, operators inspect the container tag to identify the exact Jenkins build execution and Git commit hash that introduced the issue.

---

### Question 3: Where can you find the complete list of built-in environment variables available on your Jenkins server?

**Short Answer:**

By navigating to the `/pipeline-syntax/globals#env` path in the Jenkins web interface.

**Detailed Explanation:**

Jenkins hosts an internal documentation generator accessible from any pipeline job by clicking **Pipeline Syntax** in the left sidebar and selecting **Global Variables Reference**, or navigating directly to `http://<JENKINS_HOST>:8080/pipeline-syntax/globals#env`. This page lists all core environment variables as well as custom variables exposed by active third-party plugins installed on the controller.

---