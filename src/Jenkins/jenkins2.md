Here is a clean, well-formatted transcription and reference guide based on the transcript provided.

---

## Executive Summary

This transcript demonstrates how to set up and configure a **Jenkins Multi-Branch Pipeline**.

Key highlights covered in the video:

1. **Automatic Branch Discovery:** Jenkins scans your Git repository and automatically creates a sub-pipeline job for every branch containing a valid `Jenkinsfile`.
2. **Behavior Without `Jenkinsfile`:** If a branch does not contain a `Jenkinsfile`, the scanner skips creating a job for that branch until one is added.
3. **Environment-Aware Pipelines:** Using build-in Jenkins environment variables (such as `${env.BRANCH_NAME}`), a single pipeline configuration can dynamically adapt execution steps based on the target branch (e.g., `main`, `prod`, `UAT`).

---

## Technical Summary & Pipeline Reference

### Sample `Jenkinsfile` Used

```groovy
pipeline {
    agent any

    stages {
        stage('Branch Test') {
            steps {
                echo "Current Branch: ${env.BRANCH_NAME}"
            }
        }
    }
}

```

---

## Workflow Step-by-Step

```
[Create Git Repository] ---> [Add Branches (main, prod, uat, test)]
                                        |
                                        v
                            [Create Jenkinsfile in Repo]
                                        |
                                        v
                          [Create Multi-branch Pipeline Job]
                                        |
                                        v
                           [Configure SCM / Repository URL]
                                        |
                                        v
                          [Run Multi-Branch Branch Indexing]
                                        |
                                        +---> [Branch with Jenkinsfile] ---> Pipeline Job Created & Executed
                                        |
                                        +---> [Branch without Jenkinsfile] ---> Skipped (Not Indexed)

```

1. **Repository Setup:**
* Create a Git repository (e.g., `multi-branch-test`).
* Add a `Jenkinsfile` containing your pipeline declaration to the main branch.
* Create additional branches (e.g., `uat`, `prod`).


2. **Jenkins Job Configuration:**
* In Jenkins, click **New Item** $\rightarrow$ Enter Job Name $\rightarrow$ Select **Multibranch Pipeline**.
* Under **Branch Sources**, add **Git** (or GitHub) and provide your repository URL.
* Under **Discover Branches**, configure your discovery/filtering rules if needed.
* Save the job.


3. **Scanning & Indexing:**
* Jenkins triggers the **Multibranch Scan Log**.
* Branches containing a valid `Jenkinsfile` are indexed and queued for building.
* Branches missing a `Jenkinsfile` are logged as *Not Found* and skipped until a valid `Jenkinsfile` is committed.



---