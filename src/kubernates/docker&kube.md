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

## How does Kubernetes Scheduler decide where to place a Pod?
The **`kube-scheduler`** assigns unscheduled Pods to nodes using a two-phase cycle: **Filtering (Predicates)** to find eligible nodes, followed by **Scoring (Priorities)** to rank them. The node with the highest score wins the placement.

---

### Phase 1: Filtering (Predicates)

The scheduler checks each node in the cluster against a set of hard constraints. Any node that fails even one check is immediately filtered out.

* **Resource Availability (`NodeResourcesFit`):** Does the node have enough unallocated CPU, Memory, and Ephemeral Storage to satisfy the Pod's `spec.containers.resources.requests`?
* **Taints and Tolerations (`NodeUnschedulable` / `TaintToleration`):** Does the node have a taint (e.g., `dedicated=gpu:NoSchedule`) that the Pod does not explicitly tolerate?
* **Node Selectors & Affinity (`NodeAffinity`):** Does the node match hard rules defined in `nodeSelector` or `requiredDuringSchedulingIgnoredDuringExecution`?
* **Pod Topology & Anti-Affinity (`PodTopologySpread` / `InterPodAffinity`):** Does placing the Pod here violate co-location or anti-colocation constraints?
* **Volume Constraints (`VolumeBinding` / `NodeVolumeLimits`):** Can the node attach the requested PV, and is it in the correct availability zone/rack for the underlying storage?
* **Port Conflicts (`NodePorts`):** Is a requested `hostPort` already bound on this node?

If no nodes survive this phase, the Pod transitions to the **`Pending`** state and emits a `FailedScheduling` event.

---

### Phase 2: Scoring (Priorities)

All nodes that pass the filtering phase are ranked on a scale from 0 to 100 across several scoring plugins:

* **Resource Balancing (`NodeResourcesBalancedAllocation` & `LeastAllocated`):** Prefers nodes with balanced CPU-to-memory usage or nodes that maximize overall cluster utilization.
* **Soft Affinity Rules (`preferredDuringSchedulingIgnoredDuringExecution`):** Rewards nodes that match the Pod's preferred labels or zone preferences.
* **Image Locality (`ImageLocality`):** Favors nodes that already have the required container images cached locally to reduce pull times.
* **Topology Spread (`PodTopologySpread`):** Awards higher scores to nodes in underrepresented failure domains (racks or AZs) for high availability.

Each scoring plugin has an assigned weight. The scheduler calculates a weighted sum:

$$\text{Final Score} = \sum (\text{Plugin Score} \times \text{Plugin Weight})$$

The node with the highest total score is selected. If multiple nodes tie, one is chosen at random.

---

### Phase 3: Binding

Once the node is selected:

1. The scheduler creates a **`Binding`** object targeting the node.
2. It sends an asynchronous `POST` request to `kube-apiserver`, which writes the assignment into `etcd` (`spec.nodeName`).
3. The `kubelet` on that target node detects the binding via its watch stream and begins container creation.

---

### End-to-End Decision Flow

```text
[ Unscheduled Pod ]
        │
        ▼
┌────────────────── Phase 1: Filtering ──────────────────┐
│  • Check CPU / Memory requests                         │
│  • Match NodeAffinity / nodeSelector                   │
│  • Validate Taints & Tolerations                       │
│  • Verify Port & Volume constraints                    │
└─────────────────────────┬───────────────────────────────┘
                          │ (Eligible Nodes)
                          ▼
┌─────────────────── Phase 2: Scoring ───────────────────┐
│  • Score LeastAllocated / BalancedAllocation           │
│  • Score PreferredAffinity                             │
│  • Score ImageLocality (cached container images)       │
│  • Score PodTopologySpread (spread across AZs)         │
└─────────────────────────┬───────────────────────────────┘
                          │ (Highest Scored Node)
                          ▼
┌─────────────────── Phase 3: Binding ───────────────────┐
│  • Write spec.nodeName to etcd via API Server          │
│  • Target Node's kubelet picks up and starts container │
└────────────────────────────────────────────────────────┘

```

---

### Advanced Scheduling Mechanisms

* **Preemption & Eviction:** If a high-priority Pod (`PriorityClass`) cannot fit on any node, the scheduler can evict lower-priority Pods from a node to clear enough resources.
* **Custom Scheduling Profiles:** Kubernetes supports multiple schedulers running simultaneously or customized scheduling profiles configured via `KubeSchedulerConfiguration` (e.g., custom filter/score plugins).

##### Interview Answer
Here is how to deliver this concisely and naturally in an interview:

---

### The Interview Pitch

> "The **`kube-scheduler`** determines Pod placement using a three-phase loop: **Filtering**, **Scoring**, and **Binding**.
> 1. **Filtering (Predicates):** First, the scheduler eliminates nodes that cannot run the Pod based on hard constraints:
> * Resource capacity (`requests` for CPU/Memory).
> * Taints and tolerations.
> * Hard node affinity (`nodeSelector` or `requiredDuringScheduling`).
> * Storage zone constraints and host port conflicts.
    > *(If no nodes pass, the Pod stays in `Pending` with a `FailedScheduling` event.)*
>
>
> 2. **Scoring (Priorities):** Next, it ranks all surviving candidate nodes on a 0–100 scale using weighted scoring plugins:
> * **Resource balancing:** Balancing CPU vs. memory usage (`LeastAllocated` / `BalancedAllocation`).
> * **Soft affinity:** Honoring `preferredDuringScheduling` rules.
> * **Image locality:** Favoring nodes that already have container images cached.
> * **Topology spread:** Distributing replicas across failure domains (AZs/racks) for high availability.
    > *(The node with the highest weighted sum wins; ties are broken randomly.)*
>
>
> 3. **Binding:** Finally, the scheduler creates a `Binding` object and updates the Pod's `spec.nodeName` via the API server. The target node's `kubelet` detects this assignment via its watch stream and launches the container runtime."
>
>

---

### Senior Engineer Edge (Production Nuance to Mention)

If the interviewer probes for operational depth, add this brief closing point:

> "In production, two edge cases often come up:
> * **Preemption:** If a high-priority Pod (`PriorityClass`) cannot schedule, the scheduler triggers eviction of lower-priority Pods on a candidate node to free up capacity.
> * **Custom Frameworks:** Since K8s 1.18+, scheduling logic is fully extensible via the **Scheduling Framework**, allowing custom Go plugins across pre-filter, score, reserve, and permit extension points without maintaining a custom fork."
>
----

### Your application works inside the Pod but not from outside the cluster. Why?
Here is a clean, structured way to deliver this answer in an interview:

---

### The Interview Pitch

> "When an application works inside the container (e.g., via `curl localhost:<port>`) but cannot be reached from outside the cluster, I troubleshoot it by working down the networking path in four layers:
> 1. **Binding Address & Probes (Pod Layer):**
> * **`127.0.0.1` vs. `0.0.0.0`:** The app might be listening strictly on loopback instead of all network interfaces (`0.0.0.0`), dropping external packets at the Pod’s virtual NIC.
> * **Readiness Probes:** If the readiness probe is failing, the Pod stays in a `NotReady` state, causing Kubernetes to exclude it from the Service’s active Endpoints.
>
>
> 2. **Service & Endpoints Configuration:**
> * **Selector Mismatch:** If the Service's `spec.selector` doesn't match the Pod’s `labels`, running `kubectl get endpoints` will show `<none>`.
> * **`port` vs. `targetPort`:** The Service `targetPort` must match the exact container listen port, not the Service's internal virtual port.
>
>
> 3. **Service Type & Ingress Layer:**
> * **Service Type:** A standard `ClusterIP` isn't routable outside the cluster. It requires a `NodePort`, `LoadBalancer`, or an **Ingress / Gateway API** routing layer.
> * **Ingress Misconfigurations:** Missing `Host` headers, path-prefix mismatches, or TLS misconfigurations.
>
>
> 4. **Network Policies & Cloud Firewalls:**
> * A namespace `NetworkPolicy` might be dropping incoming traffic, or the cloud provider’s Security Groups / firewall rules might be blocking the LoadBalancer or `NodePort` (30000–32767) range."
>
>
>
>

---

### Fast Debugging Methodology (Show Your Practical Experience)

> "In production, my 30-second debug workflow is:
> 1. Run `kubectl get endpoints <svc-name>` — if empty, it’s a label selector or readiness issue.
> 2. Run `kubectl port-forward svc/<svc-name> 8080:<port>` — if this works, the Service/Pod config is healthy, and the failure is higher up in the Ingress or Cloud Load Balancer layer."

### Service has the correct selector, but there are no endpoints. What could be wrong?
If a Kubernetes Service has the exact matching label selector for your Pods but `kubectl get endpoints <service-name>` still shows `<none>` (empty), the issue usually boils down to **Pod lifecycle/health states**, **port naming mismatches**, or **namespace isolation**.

---

### Root Causes & Fixes

**1. Pods Are Failing Readiness Probes (`NotReady` State)**

* **Why:** By default, Kubernetes only registers Pods into the Endpoints/EndpointSlice object if they pass their configured `readinessProbe`. If the probe fails, the Pod is marked `NotReady` and immediately removed from the active Endpoints list.
* **Verification:**
```bash
kubectl get pods -l <selector-key>=<selector-value>

```


Look at the `READY` column (e.g., `0/1`). If it shows `0/1`, check why:
```bash
kubectl describe pod <pod-name>

```


Check the **Events** section for `Readiness probe failed`.

**2. Named `targetPort` Mismatch**

* **Why:** If the Service defines a **string name** for `targetPort` (e.g., `targetPort: http-web`), the container's `ports[].name` in the Pod spec must match that name **character-for-character**. If there is a typo or the container doesn't define that named port, no endpoints will bind.
* **Service Spec:**
```yaml
ports:
  - port: 80
    targetPort: http-web  # Must match Pod's container port name

```


* **Pod Spec:**
```yaml
containers:
  - name: app
    ports:
      - name: http-web    # If missing or misspelled -> No Endpoints
        containerPort: 8080

```



**3. Cross-Namespace Isolation**

* **Why:** Services only look for Pods residing in the **same namespace**. If the Service is in namespace `default` and the matching Pods are in namespace `production`, the Service cannot see or register those Pods.
* **Fix:** Ensure both the Service and Pods are deployed to the same namespace.

**4. Pods Are Terminating or in a Failed Phase**

* **Why:** Pods in phases other than `Running` (such as `Pending`, `CrashLoopBackOff`, `ImagePullBackOff`, `Completed`, or in a graceful `Terminating` state) are excluded from the Endpoints list.
* **Verification:**
```bash
kubectl get pods -l <selector-key>=<selector-value> -o wide

```



**5. Service or EndpointSlice Controller Stalled**

* **Why:** In rare control-plane incidents, the `kube-controller-manager`'s EndpointSlice/Endpoint controller might be degraded or lagging, failing to reconcile state changes.
* **Verification:** Check `kubectl get endpointslices` to see if EndpointSlices are synced.

---

### Interview Delivery Summary

> "If the label selector is verified to match the Pod labels, the primary culprit is almost always **failing readiness probes**—Kubernetes deliberately excludes `NotReady` Pods from the Endpoints list to protect traffic.
> The other common reasons are:
> 1. **Named `targetPort` mismatches:** Using a named port on the Service that isn't defined under `ports.name` in the Pod spec.
> 2. **Namespace mismatch:** Service and Pods residing in different namespaces.
> 3. **Pod state:** Pods are stuck in `Pending`, `CrashLoopBackOff`, or `Terminating`."
>

#### interview answer: 
Here is how to deliver this answer naturally, crisply, and with senior-level authority in an interview:

---

### The Interview Pitch

> "If the label selector on the Service matches the Pod labels perfectly but `kubectl get endpoints` still returns `<none>`, the issue almost always comes down to **Pod health**, **port naming**, or **namespace scope**.
> 1. **Failing Readiness Probes (Most Common):**
> * Kubernetes intentionally excludes Pods from active Endpoints if they are in a `NotReady` state (e.g., `0/1 READY`).
> * If the container is still warming up or failing its `readinessProbe`, it won't receive traffic until the probe succeeds.
>
>
> 2. **Named `targetPort` Mismatch:**
> * When a Service references a named port (like `targetPort: http-web`), it must match the container's `ports[].name` in the Pod spec character-for-character. If there's a typo or the port name is missing on the container spec, EndpointSlice reconciliation silently drops the binding.
>
>
> 3. **Namespace Isolation:**
> * Services only discover Pods within their **own namespace**. If the Service is in `default` but the target Pods are deployed in another namespace, the selector won't find them.
>
>
> 4. **Pod Lifecycle States:**
> * Only Pods in the `Running` phase are eligible. If the Pods are stuck in `Pending`, `CrashLoopBackOff`, `ImagePullBackOff`, or are actively in a graceful `Terminating` drain cycle, they will not appear as endpoints."
>
>
>
>

---

### The 15-Second Production Debugging Flow

> "My go-to triage command is:
> ```bash
> kubectl get pods -l <selector> -o wide
> 
> ```
>
>
> * If the Pods are `0/1 READY`, I run `kubectl describe pod <name>` and look for `Readiness probe failed` events.
> * If they are `1/1 READY` and running, I verify the namespaces match and check the Service's `targetPort` against the container's port definitions."
---
## Questions: Deployment shows 10 replicas, but only 7 Pods are running. How would you investigate?
Here is a structured, production-grade troubleshooting workflow tailored for an interview:

---

### The Interview Pitch

> "When a Deployment is configured for 10 replicas but only 7 are running, I break the investigation into a top-down triage: **Deployment/ReplicaSet status $\rightarrow$ Pod lifecycle phase $\rightarrow$ Node capacity and scheduling constraints $\rightarrow$ Container execution logs.**"

---

### Step-by-Step Investigation Workflow

```text
[ 1. Check Deployment & ReplicaSet Status ]
     │  Is the ReplicaSet attempting to create 10 pods?
     ▼
[ 2. Inspect Non-Running Pods ]
     ├── Pending? ──────────> (Capacity / Quota / Scheduling constraints)
     ├── CrashLoopBackOff? ──> (OOMKilled / App runtime crash / Bad config)
     └── ImagePullBackOff? ──> (Registry auth / Missing tag)
     │
     ▼
[ 3. Deep-Dive with describe & logs ]
     ├── kubectl describe pod <pod-name> (Check Events)
     └── kubectl logs <pod-name> --previous (Check App / Exit code)

```

---

### 1. Check Deployment & ReplicaSet Alignment

First, verify if the Deployment Controller and ReplicaSet Controller actually attempted to scale:

```bash
kubectl get deployment <deployment-name>
kubectl get rs -l app=<app-label>

```

* **What to look for:** Look at `DESIRED`, `CURRENT`, and `READY`.
* **If `CURRENT < 10`:** The ReplicaSet itself hasn't created the pods. Check if there is a **Namespace ResourceQuota** blocking object creation or a rolling update stuck on `maxSurge`/`maxUnavailable` settings.
* **If `CURRENT == 10` but `READY == 7`:** The remaining 3 Pods exist but are in a non-running or not-ready state.

---

### 2. Identify the State of the 3 Missing/Failing Pods

Run:

```bash
kubectl get pods -l app=<app-label> -o wide

```

Group the 3 failing pods into their specific phase:

#### Scenario A: Pods are stuck in `Pending` (Scheduling Bottleneck)

* **Root Causes:**
* **Cluster Resource Starvation:** Nodes do not have enough unallocated CPU or Memory to satisfy `spec.containers.resources.requests`.
* **Taints / Tolerations / Affinity:** The remaining nodes have taints that the Pods don't tolerate, or anti-affinity rules prevent scheduling multiple replicas on the same node/rack/AZ.
* **PersistentVolumeBinding:** PVs/PVCs are bound to specific availability zones or nodes and cannot attach elsewhere.


* **Command:** `kubectl describe pod <pending-pod>` $\rightarrow$ check **Events** for `FailedScheduling`.

#### Scenario B: Pods are in `CrashLoopBackOff` or `Error` (Runtime Failure)

* **Root Causes:**
* **OOMKilled (Out of Memory):** The Pod exceeded its `resources.limits.memory`. (Look for `Exit Code 137` / `Reason: OOMKilled`).
* **Missing Secrets / ConfigMaps:** Application fails to boot because required environment variables or mounted volume mounts are missing.
* **Application Startup Failure:** Database connection pool exhausted, unhandled startup exceptions, or port collisions.


* **Commands:**
```bash
kubectl describe pod <crashloop-pod>   # Check 'Last State' and 'Exit Code'
kubectl logs <crashloop-pod> --previous # Check container logs before crash

```



#### Scenario C: Pods are in `ImagePullBackOff` / `ErrImagePull`

* **Root Causes:**
* Image tag typo or missing in the container registry.
* Image pull secret (`imagePullSecrets`) is missing or expired in the namespace.


* **Command:** `kubectl describe pod <pod-name>` $\rightarrow$ check **Events** for registry auth/pull failures.

#### Scenario D: Pods are `Running` but `0/1 READY`

* **Root Causes:**
* The containers started, but the **Readiness Probe** is failing (e.g., dependency warmup taking too long or failing `/healthz` checks).


* **Command:** `kubectl describe pod <pod-name>` $\rightarrow$ look for `Readiness probe failed` events.

---

### 3. Namespace & Cluster Level Constraints

If the pods are completely blocked or cluster-autoscaler is not kicking in:

* **ResourceQuota / LimitRange:** Check if the namespace hit its maximum limit for CPU, memory, or total Pod count:
```bash
kubectl get resourcequota -n <namespace>

```


* **Cluster Autoscaler Lag:** If the cluster relies on autoscaling (Karpenter or Cluster Autoscaler), check whether the node group hit max instance limits or cloud provider capacity limits (e.g., AWS `InsufficientInstanceCapacity`).

---

### 30-Second Summary for the Interviewer

> "I run `kubectl get pods -l app=<label>` to see the exact state of the 3 missing pods.
> * If they are **Pending**, it's a scheduling or resource requests/affinity issue.
> * If they are **CrashLoopBackOff**, I inspect `kubectl logs --previous` and check for `OOMKilled` (Exit Code 137).
> * If they are **0/1 Ready**, I inspect the readiness probe.
> * If the Pods were never created at all, I check the ReplicaSet events and namespace `ResourceQuota`."
>
### interview pitch : 

### The Interview Delivery

> "I approach this using a top-down triage: **ReplicaSet scale status $\rightarrow$ Pod lifecycle state $\rightarrow$ Scheduling or Runtime diagnostics.**
> **Step 1: Check the ReplicaSet**
> First, I run `kubectl get deployment <name>` and check the ReplicaSet to see if the missing pods were even created:
> * If `CURRENT < 10`, the ReplicaSet is blocked from creating pods—usually due to a namespace `ResourceQuota` or a rolling update constraint (`maxUnavailable`/`maxSurge`).
> * If `CURRENT == 10` but `READY == 7`, the pods exist, so I inspect their specific states.
>
>
> **Step 2: Triage the 3 Failing Pods**
> Running `kubectl get pods -l app=<label>` immediately categorizes the failure:
> * **`Pending`:** The scheduler cannot place them. I run `kubectl describe pod` and check events for `FailedScheduling`—typically insufficient node CPU/memory requests, anti-affinity rules, or un-tolerated node taints.
> * **`CrashLoopBackOff`:** The container is failing at runtime. I inspect the last termination state for **Exit Code 137 (`OOMKilled`)** or check `kubectl logs <pod> --previous` for missing ConfigMaps, unmounted Secrets, or failed database connection pools.
> * **`ImagePullBackOff`:** Indicates a missing/typoed image tag or an expired/missing `imagePullSecret`.
> * **`0/1 READY` (Running but unready):** The container is alive, but its **readiness probe** is failing, keeping it out of the ready count.
>
>
> **Step 3: Cluster-Level Infrastructure**
> If pods are stuck in `Pending` and should trigger autoscaling, I verify whether Karpenter or Cluster Autoscaler has hit node group limits or cloud capacity constraints."

---

### Concise Wrap-Up

> "In short: `kubectl get pods` tells me **where** the failure is happening—scheduler, registry, runtime, or health check—and `kubectl describe` plus `--previous` logs pinpoint the **why**."

---
## Question: After deploying version 2, CPU usage suddenly increases. What would you check?

When CPU usage spikes immediately after a new deployment (v2), the investigation focuses on isolating whether the cause is **code changes**, **runtime/GC thrashing**, **traffic/workload shifts**, or **infrastructure/Kubernetes misconfigurations**.

---

### Step-by-Step Triage Workflow

```text
[ Deploy v2 -> CPU Spikes ]
        │
        ├── 1. Traffic Check: Did request rate / throughput jump? (Prometheus / Ingress metrics)
        │
        ├── 2. Code Delta: What changed in v2? (Git diff / hot paths / regex / unindexed queries)
        │
        ├── 3. Thread & Profiling: Where are CPU cycles spent? (Thread dumps / Continuous Profiler / Flamegraphs)
        │
        ├── 4. Memory & GC Pressure: Is CPU burned by Garbage Collection? (JVM GC pauses / high heap churn)
        │
        └── 5. K8s / Runtime Config: CPU limits & throttling? (CFS quotas / thread pool sizing / replicas)

```

---

### 1. Code-Level Changes & Algorithmic Regressions

* **Git Diff Analysis:** Check recent commits in v2 for:
* Infinite loops, busy-waiting (`while(true)` without backoff), or tight polling loops.
* Inefficient algorithms (e.g., $O(N^2)$ data transformations or unmemoized recursive operations).
* Inefficient regular expressions (catastrophic backtracking / ReDoS).
* Expensive serialization/deserialization or recursive object parsing (e.g., deeply nested JSON/Protobuf).


* **Downstream Dependency Retries:** If a downstream service or database is slow or timing out in v2, aggressive retry loops without exponential backoff/jitter can peg the CPU.

---

### 2. Runtime & Memory Pressure (Garbage Collection Thrashing)

* High CPU is often a **symptom of memory starvation**, not pure computation:
* **JVM / Go / Node GC:** If memory allocation rate is high or the heap is near capacity, the Garbage Collector runs continuously in full stop-the-world or concurrent sweep loops, consuming near 100% CPU.


* **Verification:**
* Check GC metrics (e.g., JVM GC pause time, allocation rate, minor/major GC frequency).
* Capture a heap dump or check heap utilization graphs in Prometheus/Datadog.



---

### 3. Application Profiling & Thread Dumps (Root Cause Isolation)

* **Thread Dumps:**
* Take 3 consecutive thread dumps spaced 5–10 seconds apart (`jcmd <pid> Thread.print` or `jstack`).
* Look for threads stuck in `RUNNABLE` state executing the exact same method or stack trace across dumps.


* **CPU Profiling (Flamegraphs / Async Profiler):**
* Run an on-demand profiler (e.g., `async-profiler`, Go `pprof`, or continuous profilers like Pyroscope/Datadog).
* Identify the exact class, function, or library consuming the widest CPU slice.



---

### 4. Concurrency & Thread Pool Misconfigurations

* **Thread Contention & Spinlocks:**
* Oversized thread pools (e.g., Tomcat/Netty/Executors) causing excessive OS context-switching.
* Lock contention on shared data structures where threads spin or repeatedly wake up.


* **Background Scheduled Tasks:**
* Check if v2 introduced new cron jobs, cache-warmers, or unbounded batch processing running concurrently with user traffic.



---

### 5. Kubernetes & Resource Configuration

* **CPU Throttling (CFS Quotas):**
* Check if `resources.limits.cpu` is set too low or configured with a restrictive CFS quota:
```bash
kubectl top pod <pod-name>

```


* Check container throttling metrics (`container_cpu_cfs_throttled_seconds_total`). If throttled, response times increase, queuing more requests and causing CPU saturation.


* **Replica Count & Load Imbalance:**
* Ensure the new v2 deployment actually has the correct number of replicas running and traffic is balanced evenly across all pods.



---

### Fast Mitigation Strategy

1. **Immediate Rollback:** If production latency or error budgets (SLAs) are breached, immediately roll back to v1 using `kubectl rollout undo deployment/<name>`.
2. **Post-Incident Debugging:** Replicate the v2 workload in a staging environment, attach a CPU profiler (`async-profiler` / `pprof`), and analyze the hot call paths.

---

Here is a crisp, natural interview delivery tailored for a senior engineering discussion:

---

### The Interview Pitch

> "When CPU spikes immediately after deploying v2, my immediate priority is to assess user impact, decide on mitigation, and isolate the root cause across five distinct layers:
> **1. Fast Mitigation vs. Rollback**
> If production latency or error rate SLAs are breached, I immediately execute a rollback via `kubectl rollout undo deployment/<name>`. If the system is degraded but stable, I gather diagnostic artifacts before touching the deployment.
> **2. Triage & Root Cause Analysis:**
> * **Traffic vs. Code Delta:** First, verify via Ingress/APM metrics whether request throughput legitimately jumped, or if the per-request CPU cost increased. If throughput is steady, I inspect the v2 Git diff for algorithmic regressions ($O(N^2)$ loops, catastrophic regex backtracking, or tight polling loops without backoff).
> * **GC Thrashing (Hidden Memory Issue):** High CPU is frequently a symptom of heap pressure. If the heap is near capacity, the Garbage Collector runs in tight concurrent/full GC loops. I check GC pause frequency and allocation rates in Prometheus/Datadog.
> * **Thread Dumps & Profiling:** To find the exact hot path, I capture 3 consecutive thread dumps (via `jcmd` / `jstack`) to spot threads stuck in `RUNNABLE` executing the same stack frame. For deeper analysis, I generate a CPU flamegraph using `async-profiler` or Go `pprof`.
> * **Concurrency & Downstream Cascades:** Check for lock contention, thread pool saturation causing excessive OS context switching, or aggressive retry loops firing against slow downstream dependencies.
> * **Kubernetes Limits & Throttling:** Inspect `container_cpu_cfs_throttled_seconds_total` to see if restrictive CPU limits are causing Linux CFS quota throttling, which cascades into request queuing and CPU saturation."
>
>

---

### 15-Second Executive Summary

> "In short: rule out traffic spikes first, check if it's GC thrashing rather than pure compute, capture thread dumps or flamegraphs to isolate the hot method, and roll back immediately if SLAs are violated."
---
