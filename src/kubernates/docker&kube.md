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