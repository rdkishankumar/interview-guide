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