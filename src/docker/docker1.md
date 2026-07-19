### 1. Installation & Environment Setup

Before running Docker commands, these system commands were used to install and configure the Docker engine on your Amazon Linux 2023 instance:

* `sudo dnf update -y`: Updates the package repository to ensure you get the latest secure software versions.
* `sudo dnf install docker -y`: Installs the core Docker engine packages onto the virtual machine.
* `sudo systemctl enable docker`: Configures Docker to automatically start up whenever the EC2 instance reboots.
* `sudo systemctl start docker`: Launches the active Docker background service daemon.
* `sudo usermod -aG docker ec2-user`: Adds your logged-in user to the `docker` security group, letting you run `docker` commands without prefixing them with `sudo`.

---

### 2. Core Docker Commands & Purposes

#### `docker version`

* **Purpose:** Displays the installed versions of both the Docker Client (command-line tool) and the Docker Server (daemon/engine).
* **Demo Use:** Used to verify that the Docker engine was successfully installed, active, and accessible without `sudo`.

#### `docker run <image_name>`

* **Purpose:** Downloads a specified container image (if it doesn't already exist locally), creates a new container instance from it, and spins it up.
* **Demo Use:** `docker run hello-world` fetched the test image from Docker Hub, executed its script to output the greeting message, and then shut down.

#### `docker images`

* **Purpose:** Lists all container images currently downloaded and stored locally on your machine's disk.
* **Demo Use:** Checked before the run to confirm no images existed, and checked after to verify `hello-world` was successfully downloaded.

#### `docker ps`

* **Purpose:** Lists only the containers that are **currently running** active processes.
* **Demo Use:** Returned an empty list after running `hello-world`, confirming that short-lived task containers stop automatically once their jobs finish.

#### `docker ps -a`

* **Purpose:** Lists **all** containers on the system, including those currently running and those that have stopped or exited.
* **Demo Use:** Allowed you to view the remnants of the exited `hello-world` container, along with its unique container ID and exit status code.

#### `docker ps -aq`

* **Purpose:** The `-q` (quiet) flag filters the standard container list output to display **only the numeric Container IDs**, hiding columns like names, dates, and statuses.
* **Demo Use:** Used as a helper utility to feed clean lists of IDs into cleanup commands.

#### `docker rm <container_id>`

* **Purpose:** Permanently deletes a stopped container from disk storage.
* **Demo Use:** Cleans up the system layout after testing.

#### `docker rm $(docker ps -aq)`

* **Purpose:** Evaluates `docker ps -aq` to grab every container ID on the system and passes them into `docker rm` to **wipe out all stopped containers at once**.
* **Demo Use:** Purged the environment of the used `hello-world` container instance in one quick step.

#### `docker images -q`

* **Purpose:** Filters your local image list to output **only the unique Image IDs**, hiding tags, sizes, and repository details.
* **Demo Use:** Used as a helper function to bulk-delete local image downloads.

#### `docker rmi <image_id>`

* **Purpose:** Deletes a specific base container image from your local drive.
* **Demo Use:** Used to wipe out the cached `hello-world` image.

#### `docker rmi $(docker images -q)`

* **Purpose:** Evaluates the list of all local Image IDs and passes them to `docker rmi` to **force-delete every downloaded image on the system**.
* **Demo Use:** Cleared out the `hello-world` base files to leave your EC2 storage completely pristine before jumping into the retail store app demo.

---
### 1. Interacting & Executing Inside Containers

#### `docker exec -it <container_name_or_id> /bin/sh`

* **Purpose:** Opens an interactive (`-i`) pseudo-TTY (`-t`) terminal session inside a currently running container, executing the specified shell (`/bin/sh` or `/bin/bash`).
* **Demo Use:** Used to log into the `my-app-1` container to run diagnosis commands (like checking the OS release, `java -version`, `whoami`, `pwd`, and checking directory contents for `app.jar`).

#### `docker exec -it <container_name_or_id> <command>`

* **Purpose:** Executes a single command directly inside a running container from your host terminal without opening a full interactive shell session.
* **Demo Use:** Used to run `ls`, `curl http://localhost:8080`, and `env` directly inside the `my-app-1` container to read environmental variables (such as verifying the host name matches the short container ID) without leaving the EC2 host.

---

### 2. Container Lifecycle & State Management

#### `docker stop <container_name_or_id>`

* **Purpose:** Gracefully halts a running container by sending a `SIGTERM` signal to its primary process, followed by a `SIGKILL` if it doesn't stop within a grace period.
* **Demo Use:** Used to stop the `my-app-1` retail store app, transitioning its status to "Exited" and cutting off access to the web page over the network.

#### `docker start <container_name_or_id>`

* **Purpose:** Re-starts a previously stopped or exited container, preserving its original settings, container ID, and data changes.
* **Demo Use:** Used to bring the `my-app-1` container back online, instantly restoring access to the application via the web browser.

---

### 3. Cleanup & Resource Deletion

#### `docker rm <container_name>`

* **Purpose:** Deletes a specific stopped container by explicitly using its assigned string name instead of its numeric ID.
* **Demo Use:** Used to target and delete the `my-app-1` container after it was stopped.

#### `docker rmi <image_id>`

* **Purpose:** Deletes a specific base image from the local storage cache by targeting its unique Image ID directly.
* **Demo Use:** Used to completely erase the downloaded retail application image from the EC2 instance, concluding the demo with a clean workspace.