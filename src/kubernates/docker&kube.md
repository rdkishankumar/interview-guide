| Category | Command | Description |
| --- | --- | --- |
| **Container Lifecycle** | `docker run -d -p <host>:<container> --name <name> <image>` | Create and run a container in detached mode with port mapping |
|  | `docker run -it <image> /bin/bash` | Run an interactive container with a pseudo-TTY shell |
|  | `docker start <container>` | Start a stopped container |
|  | `docker stop <container>` | Gracefully stop a running container |
|  | `docker restart <container>` | Restart a running or stopped container |
|  | `docker rm <container>` | Remove a stopped container |
|  | `docker rm -f <container>` | Force-remove a running container |
| **Inspection & Monitoring** | `docker ps` | List all actively running containers |
|  | `docker ps -a` | List all containers (running, stopped, exited) |
|  | `docker logs -f <container>` | Stream container logs in real time |
|  | `docker exec -it <container> /bin/bash` | Execute an interactive shell inside a running container |
|  | `docker stats` | Show live CPU, memory, and I/O usage of running containers |
|  | `docker inspect <object>` | Display low-level JSON configuration of a container/image/network |
| **Image Management** | `docker images` *(or `docker image ls`)* | List all locally cached images |
|  | `docker pull <image>:<tag>` | Download an image from a registry (e.g., Docker Hub) |
|  | `docker build -t <name>:<tag> .` | Build an image from a `Dockerfile` in the current directory |
|  | `docker push <repo>/<image>:<tag>` | Upload an image to a remote registry |
|  | `docker rmi <image>` | Remove a local image |
| **Volumes & Storage** | `docker volume ls` | List all existing persistent volumes |
|  | `docker volume create <volume_name>` | Create a new named volume |
|  | `docker volume rm <volume_name>` | Remove a specific volume |
|  | `docker cp <container>:<path> <host_path>` | Copy files/folders between a container and the local host |
| **Networking** | `docker network ls` | List all available Docker networks |
|  | `docker network create <network_name>` | Create a custom user-defined network |
|  | `docker network connect <net> <container>` | Connect a running container to a network |
| **System Cleanup** | `docker system prune` | Remove stopped containers, unused networks, and dangling images |
|  | `docker system prune -a --volumes` | Remove all unused containers, images, networks, and persistent volumes |
|  | `docker system df` | Display disk usage of all Docker objects |

---
Here are comprehensive, structured study and revision notes compiled from the transcript of the crash course.

---

# Comprehensive Notes: Docker & Kubernetes Crash Course (Ashok IT)

---

## Part 1: Application Architecture & The Problem Space

### 1. Typical 3-Tier Application Architecture

Every standard modern software project consists of three core layers:

* **Front-End (UI):** What the user interacts with in the browser (e.g., Angular, React, Vue.js).
* **Back-End (Business Logic):** Core processing layer (e.g., Java, Python, Node.js, .NET, PHP).
* **Database (Storage):** Data persistence layer (e.g., MySQL, Oracle, PostgreSQL, MongoDB).

### 2. Multi-Environment Deployment Lifecycle

In real-world enterprise workflows, software is deployed across multiple environments before reaching users:

* **DEV (Development):** Used by developers for integration testing (merging code from Git/Bitbucket).
* **SIT (System Integration Testing):** Dedicated environment for QA/testing teams.
* **UAT (User Acceptance Testing):** Used by clients/business stakeholders to approve functionality.
* **Pilot / Pre-Prod (Staging):** Final mirror of production used for dry-run verification.
* **PROD (Production):** Live environment accessed by actual end users.

### 3. Core Deployment Challenges (Without Docker)

* **Repetitive Manual Environment Setup:** Every machine (physical or virtual) across all environments requires manual installation of underlying software (e.g., specific versions of OS, Angular, Java, Tomcat, MySQL).
* **Software Version Conflicts:** Discrepancies between environments (e.g., DEV running Java 17 while SIT running Java 11 by mistake) break functionality.
* **High Upgrade & Maintenance Overhead:** Upgrading dependencies across 10–20 environments requires manual uninstallation, reinstallation, and reconfiguration on every server.
* **The "Works on My Machine" Problem:** Code executing cleanly on a developer's machine fails on testing or production servers due to environmental and dependency differences.

---

## Part 2: Docker & Containerization Deep-Dive

### 1. What is Docker?

* A free, open-source **containerization platform**.
* **Containerization Definition:** Packaging application source code together with all of its required runtime dependencies, libraries, binaries, and configurations into a single, portable unit of execution called a **Docker Image**.

### 2. Docker Architecture Components

* **`Dockerfile`:** A plain-text configuration file containing a sequence of instructions to define dependencies and package the application.
* **Docker Image:** The read-only executable artifact/package containing application code + dependencies.
* **Docker Registry:** A central storage repository for storing and sharing Docker images (e.g., **Docker Hub**, AWS ECR, JFrog Artifactory, Sonatype Nexus).
* **Docker Container:** A running, isolated runtime instance of a Docker image.

### 3. Docker Internal Mechanism: OS-Level Virtualization

* Containers run on top of the host operating system via the **Docker Engine / Daemon**.
* Each container functions as an isolated, lightweight Linux environment containing only the dependencies required by that specific app.
* **Port Mapping (`-p <host_port>:<container_port>`):** Containers run isolated in private internal networks. Port mapping links the host machine's public port to the container’s internal listening port so external traffic can reach the application.
* **Detached Mode (`-d`):** Runs the container in the background, freeing the terminal for additional commands.

### 4. Essential Docker CLI Commands

| Command | Purpose / Function |
| --- | --- |
| `docker images` | Lists all local Docker images |
| `docker ps` | Lists all actively running containers |
| `docker ps -a` | Lists all containers (running, stopped, and exited) |
| `docker pull <image_name>` | Downloads an image from the registry (e.g., Docker Hub) |
| `docker run <image_name>` | Creates and starts a container from an image |
| `docker run -d -p <host>:<cont> <img_name>` | Runs a container in the background with port forwarding |
| `docker logs <container_id>` | Displays the standard output/logs of a container |
| `docker rm <container_id>` | Deletes a stopped container |
| `docker rmi <image_id/name>` | Deletes a local Docker image |
| `docker system prune -a` | Deletes all stopped containers, unused networks, and dangling images |

---

## Part 3: Kubernetes (K8s) & Container Orchestration

### 1. Why Do We Need Kubernetes?

While Docker handles **containerization**, enterprise microservices (e.g., E-commerce with 50+ services) need automated management:

* Manual scaling across hundreds of containers is unfeasible.
* Handling traffic spikes requires dynamic auto-scaling.
* Damaged containers must be detected and recreated automatically.
* **Kubernetes Definition:** An open-source **Container Orchestration Platform** originally developed by Google and now maintained by the Cloud Native Computing Foundation (CNCF).

### 2. Core Capabilities & Advantages of Kubernetes

* **Container Orchestration:** Automated deployment and lifecycle management of containers.
* **Auto-Scalability:** Dynamically increases (scale-up) or decreases (scale-down) container count based on traffic load.
* **Self-Healing:** Automatically replaces or restarts failed or crashed containers/pods to maintain the desired state.
* **Load Balancing:** Automatically distributes incoming network traffic across healthy pods in a round-robin manner.

---

## Part 4: Kubernetes Architecture

Kubernetes operates as a **Cluster** (a group of coordinated physical or virtual servers).

```text
               +---------------------------------------------+
               |                CONTROL PLANE                |
               |                                             |
               |   [API Server] <----> [etcd Database]       |
               |         ^                                   |
               |         |                                   |
               |   [Scheduler]       [Controller Manager]    |
               +---------+--------------------+--------------+
                         |                    |
        +----------------+                    +----------------+
        |                                                      |
        v                                                      v
+-----------------------+                              +-----------------------+
|      WORKER NODE 1    |                              |      WORKER NODE 2    |
|                       |                              |                       |
| [kubelet] [kube-proxy]|                              | [kubelet] [kube-proxy]|
|                       |                              |                       |
|   +-----------------+ |                              |   +-----------------+ |
|   | Pod (Containers)| |                              |   | Pod (Containers)| |
|   +-----------------+ |                              |   +-----------------+ |
|      [Docker Engine]  |                              |      [Docker Engine]  |
+-----------------------+                              +-----------------------+

```

### 1. Control Plane (Master Node)

Manages cluster state, scheduling, and administrative operations:

* **`kube-apiserver`:** The cluster's central entry point. Receives incoming user and CLI requests and communicates with all internal components.
* **`etcd`:** Consistent, distributed key-value store acting as the cluster's internal database for configuration and state.
* **`kube-scheduler`:** Inspects pending tasks/pods in `etcd`, queries node health, and assigns pods to available worker nodes.
* **`kube-controller-manager`:** Continuously tracks cluster state and reconciles discrepancies (e.g., replacing dead pods, maintaining replica counts).

### 2. Worker Nodes (Compute/Slave Nodes)

The actual servers that execute container workloads:

* **`kubelet`:** An agent running on each worker node acting as a communication bridge to the Control Plane. Monitors pod health and reports node status.
* **`kube-proxy`:** Manages network routing and IP packet filtering to allow intra-cluster and external communication.
* **Container Runtime (e.g., Docker Engine):** Software responsible for running the containers inside pods.
* **Pod:** The **smallest deployable computing unit** in Kubernetes. Represents a running process hosting one or more containers sharing storage and network resources.

### 3. Cluster Interaction Tools

* **`kubectl`:** The official command-line utility used by engineers to send instructions to the Kubernetes API Server.
* **Kubernetes Dashboard:** Web-based graphical interface for cluster monitoring and management.

---

## Part 5: Kubernetes Setup Options

* **Self-Managed:**
* **Minikube:** Single-node cluster running both control plane and worker components on one machine (strictly for local learning/practice).
* **`kubeadm`:** Tool used to manually build and bootstrap multi-node production-grade clusters from raw Linux VMs.


* **Provider-Managed (Cloud Services):**
* **AWS EKS (Elastic Kubernetes Service):** Fully managed Kubernetes control plane on AWS.
* **Azure AKS:** Azure Kubernetes Service.
* **GCP GKE:** Google Kubernetes Engine.



---

## Part 6: Kubernetes Core Concepts & Manifest Configurations

### 1. Pods vs. Services

* **Pod:**
* Smallest deployable unit; represents application runtime instances.
* Ephemeral: each pod receives a dynamic private IP that changes upon recreation.
* Pods cannot be accessed reliably from outside the cluster by default.


* **Service:**
* An abstraction that defines a stable logical endpoint and access policy across a set of dynamic pods (targeted using `labels` and `selectors`).



### 2. Service Types

* **ClusterIP (Default):** Exposes pods only on an internal cluster IP (ideal for internal databases or backend services).
* **NodePort:** Exposes the service on each Worker Node's IP at a static port.
* **LoadBalancer:** Provisions an external cloud load balancer (e.g., AWS Classic/Application Load Balancer) to route public traffic across worker nodes.

---

### 3. Complete Kubernetes Manifest YAML Breakdown

```yaml
# ==========================================
# 1. DEPLOYMENT MANIFEST (Manages Pods)
# ==========================================
apiVersion: apps/v1
kind: Deployment
metadata:
  name: java-web-app-deployment
spec:
  replicas: 2
  strategy:
    type: RollingUpdate
  selector:
    matchLabels:
      app: java-web-app
  template:
    metadata:
      name: java-web-app-pod
      labels:
        app: java-web-app
    spec:
      containers:
        - name: java-web-app-container
          image: ashokit/java-web-app:latest
          ports:
            - containerPort: 8080
---
# ==========================================
# 2. SERVICE MANIFEST (Exposes Pods via AWS LB)
# ==========================================
apiVersion: v1
kind: Service
metadata:
  name: java-web-app-service
spec:
  type: LoadBalancer
  selector:
    app: java-web-app
  ports:
    - port: 80           # External Load Balancer listening port
      targetPort: 8080   # Internal Pod/Container destination port

```

---

### 4. Essential `kubectl` CLI Commands

| Command | Purpose / Function |
| --- | --- |
| `kubectl get nodes` | Checks status and readiness of worker nodes in the cluster |
| `kubectl get pods` | Lists all pods running in the default namespace |
| `kubectl get pods -o wide` | Displays pods along with allocated Pod IPs and target Node IPs |
| `kubectl get services` (or `get svc`) | Lists active Kubernetes services and external LoadBalancer DNS endpoints |
| `kubectl get deployment` | Lists active deployments and replica status |
| `kubectl get all` | Fetches status of all resources (pods, services, deployments, replicasets) |
| `kubectl apply -f <filename.yml>` | Creates or updates cluster resources defined in the manifest YAML |
| `kubectl logs <pod_name>` | Fetches console output and application logs of a specific pod |
| `kubectl delete pod <pod_name>` | Deletes a pod (triggers self-healing if managed by a Deployment) |
| `kubectl delete all --all` | Cleans up and deletes all user-created resources in the current namespace |

---

## Part 7: Enterprise CI/CD Pipeline Context

In modern DevOps workflows, manual execution of `kubectl` commands is automated via Continuous Integration / Continuous Deployment (CI/CD) pipelines:

```text
[Developer] 
    │ (git push)
    ▼
[Source Code Repository (GitHub / Bitbucket)]
    │ (webhook trigger)
    ▼
[Jenkins CI/CD Pipeline]
    ├─► 1. Pull latest source code
    ├─► 2. Build & Package code via Maven (.jar / .war)
    ├─► 3. Build Docker Image using Dockerfile
    ├─► 4. Push Docker Image to Registry (Docker Hub / AWS ECR)
    └─► 5. Deploy / Update Kubernetes Cluster Manifests using kubectl
            │
            ▼
[Production Kubernetes Cluster (AWS EKS)]

```
---
### kube architecture
Here is a concise, structured **interview answer**:

> **Kubernetes architecture is mainly divided into two parts: the Control Plane and Worker Nodes.**
>
> The **Control Plane** is responsible for managing the cluster. It contains four main components:
>
> **First, the API Server**, which is the entry point for all Kubernetes requests. For example, when we run `kubectl apply`, the request first goes to the API Server. It handles authentication, authorization, validation, and communicates with `etcd`.
>
> **Second, etcd**, which is the key-value database of Kubernetes. It stores the cluster's configuration and desired state.
>
> **Third, the Controller Manager**, which continuously compares the desired state with the actual state. If there is any difference, it tries to correct it. For example, if we want three replicas but one Pod crashes, the controller creates another Pod to maintain three replicas.
>
> **Fourth, the Scheduler**, which is responsible for selecting the most suitable Worker Node for a newly created Pod based on available CPU, memory, affinity rules, taints and tolerations, and other scheduling requirements.
>
> On the **Worker Node**, we mainly have three components:
>
> **Kubelet**, which runs on every Worker Node. It watches for Pods assigned to its node and instructs the container runtime to start and manage the containers.
>
> **The Container Runtime**, such as `containerd` or CRI-O, which actually pulls container images and runs the containers.
>
> **Kube-proxy**, which helps manage network communication and routing for Kubernetes Services.
>
> **So, the overall flow is:** when we apply a Deployment, the request goes to the API Server, the desired state is stored in etcd, the Controller Manager creates the required Pods, the Scheduler assigns those Pods to Worker Nodes, and finally the Kubelet instructs the Container Runtime to start the containers.
>
> **The key concept of Kubernetes is that it continuously tries to make the actual state of the cluster match the desired state defined by the user.**

### What happens if etcd goes down?
> **If etcd goes down completely or loses Raft quorum, the Kubernetes Control Plane cannot properly manage the cluster because etcd is the main persistent data store for the cluster state.**
>
> However, the **existing Pods on Worker Nodes generally continue running**, because the kubelet and container runtime already have those containers running locally and don't need to continuously communicate with etcd for the containers to keep executing.
>
> Existing networking and Service routing can also continue working because the required networking rules are already configured on the Worker Nodes.
>
> But Kubernetes management operations are affected. For example, we cannot reliably create new resources, deploy updates, scale applications, schedule new Pods, or perform normal self-healing.
>
> For example, if a Pod or an entire Worker Node fails while etcd is unavailable, Kubernetes cannot reliably create and schedule replacement Pods because the Control Plane cannot access and persist the required cluster state.
>
> **In production, we avoid a single etcd failure by running an odd number of etcd nodes, usually 3 or 5, using Raft quorum. A 3-node cluster can tolerate one failure, while a 5-node cluster can tolerate two failures. We also take regular etcd snapshots for disaster recovery.**
>
> **In simple terms: if etcd goes down, existing workloads may continue running, but Kubernetes loses its ability to effectively manage, update, scale, and self-heal the cluster until etcd is restored.**
