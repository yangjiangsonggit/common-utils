1. docker安装及介绍
==========

        1.1 docker安装
        
        Mac上Docker安装，参考文档：https://docs.docker.com/docker-for-mac/
        
        1.2 docker平台介绍
        docker是一个开发，移植，运行应用的开放平台。docker能使程序和运行的基础环境进行分离，因此你可以快速交付软件。利用docker的优势，你可以快速移植，测试，部署代码，可以显著的降低代码编写与程序运行之间的延迟。docker提供了打包并在隔离环境中运行程序的能力，这个隔离环境就是容器。容器的隔离性与安全性可以使你在一台主机上同时运行许多容器。由于容器的轻量级特性，你可以在运行虚拟机的硬件上运行更多的容器。
        
        docker提供了管理容器的工具和平台：
        
        将应用封装到docker的容器中
        
        在团队中分发和移植这些容器以便于快速开发和测试
        
        在产品环境中部署应用，无论是在本地还是在云上
        
        2. 查看版本
        # docker，docker-compose，docker-machine
        docker --version
        docker-compose --version
        docker-machine --version
        3. Docker部分命令
        # 运行一个docker实例，如果没有会从Docker Hub上pull（类似于git的pull命令），--name 指定docker容器名字，nginx为image
        docker run -d -p 80:80 --name webserver nginx
        # 列出当前运行的docker container
        docker ps
        # 结果信息
        CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                         NAMES
        5af6a4c418b6        nginx               "nginx -g 'daemon off"   4 seconds ago       Up 3 seconds        0.0.0.0:80->80/tcp, 443/tcp   webserver
        # 停止运行webserver 的docker container
        docker stop webserver
        # 运行docker ps，不输出信息
        docker ps
        # docker中启动webserver，使用docker ps查看
        docker start webserver
        # 停止webserver容器并删除，不是删除了nginx image
        docker rm -f webserver
        # 列出本地所有docker image
        docker images
        # 删除本地的docker image，使用docker images查看
        # docker rmi <imageID>|<imageName>
        docker rmi nignx
        # docker卸载
        # <DockerforMacPath> --uninstall，下面的是默认路径
        /Applications/Docker.app/Contents/MacOS/Docker --uninstall
        4. Docker Engine
        docker引擎是一个client-server应用，主要有以下组件：
        
        server，一个称为daemon进程的长期运行的程序。
        
        REST API 提供交互程序，可以与daemon进行交互并告诉它做什么。
        
        CLI（command line interface） client。
        
        image
        
        5. docker作用
        5.1 快速，一致的交付你的应用
        你可以将docker集成到CI/CD流中。
        
        CI：continuous integration
        CD：continuous deployment
        
        想象这样一个场景，你有三个docker的容器，一个用来开发，一个用来测试，一个用来发布，你可以很容器的在三者之间转换，并且在测试容器中发现问题，可以立即到开发容器中修改，然后重新部署到测试容器中，没问题了，再发布到发布容器中，三个容器之间互不影响，而且都在一台电脑上就能完成，并且三者之间的部署是非常的easy。
        
        5.2 响应式的部署与扩展
        docker的容器是便携式，轻量级的，你可以将它运行在本地，物理机上，虚拟机上，云上，甚至是这些环境的混合。docker可以很容易的动态管理工作负载，进行扩展，应用拆分，根据业务需求进行服务，这些操作是接近实时的。
        
        5.3 在同样的硬件上运行更多的应用
        docker是快速轻量级的。它提供了一个虚拟机（基于hypervisor的）的替代品，而且这个替代品是可行的、划算的。docker可以让你使用更多的计算性能来达到你的业务目标。在高密集环境，使用更少资源来部署应用的情况下是非常有用的。
        
        6. docker的架构
        docker采用client-server架构。docker的client与docker的daemon进行交互，daemon负责container（容器）的building，running，distribution。docker的client和daemon可以在一个系统中运行，或者使用client链接远程的Docker daemon。client和daemon的交互是通过socket或REST API进行的。
        
        image
        
        The Docker daemon
        
        docker daemon运行在主机上，用户通过client与daemon进行交互。
        
        The Docker client
        
        用户与docker交互的主要接口，可以接收commands和configuration flags。一个client可以与多个无关的daemon交互。
        
        Inside Docker
        
        要了解docker内部的东西，需要了解images, registries, containers。
        
        Docker images
        
        docker的images是指令的只读模板，是用来创建container的。一个image可能包含带有Apache web服务器和已经安装了web应用的Ubuntu操作系统。你可以创建或更新images，可以使用其他人创建的images。一个image可能是基于其它images的，也可能是其它images的一个扩展。描述image的文本文件称为Dockerfile，里面有一些简单的、已经定义好的语法。
        
        images是docker的build组件。
        
        Docker containers
        
        docker container是docker image的运行实例。通过Docker API或CLI command你可以运行，启动、停止、移动和删除container。运行容器时，可以提供配置信息或环境变量。每个容器都是一个安全、隔离的应用平台。
        
        containers是docker的run组件
        
        Docker registries
        
        docker registry是一个image库。registry可以是公有的或私有的，可以与daemon或client放在同一个服务器上，也可以在一个完全分离的服务器上。
        
        registries是docker的distribution组件。
        
        Docker services
        
        docker service允许一群（swarm）docker结点一起工作，可以运行指定数目task副本实例，每个副本本身是一个image。你可以指定并发运行的task副本的数目，swarm管理器负责工作结点的负载均衡。对于用户而言，docker service就是一个单一应用。Docker Engine在Docker 1.12版本或更高版本支持swarm模式。
        
        services是docker的scalability组件。
        
        Docker Component
        
        Component Name	Component Feature
        image	build
        container	run
        registry	distribution
        service	scalability
        7. docker组件工作原理
        7.1 How does a Docker image work?
        Docker images是只读模板，docker containers根据images进行实例化。每个image中包含很多层（layer）。Docker使用union file systems将这些层结合到一个image中。union file systems允许独立的文件系统中的文件和目录（称为分支）进行显式的叠加，形成一个一致的文件系统。
        
        这些layers是Docker轻量级的原因之一。当更改一个image时，例如应用更新时，将会创建一个新层并将要更新的层进行替换，其它层保持不变。要发布更新，你只需要传递更新的层。Laying加速了Docker images的分发。Docker决定运行时更新哪些层。
        
        Docker images在Dockerfile中定义。每个image都是起始于一个base image，例如ubuntu，是从base Ubuntu image来的，fedora同样如此。你可以使用你自己的image作为一个新image的基，例如你可以使用Apache image作为你所有web应用image的基。dockefile中使用FROM来定义base image。
        
        注：Dock Hub是一个公有的registry，主要用来存储images。
        
        从base image创建docker image主要通过一系列简单的描述步骤的集合，称之为instructions，它是存储在一个Dockfile中的。每个instruction创建image中的一个新layer。下面是一些Dockfile instructions的例子：
        
        指定base image的名字 (FROM)
        指定maintainer (MAINTAINER)
        运行命令 (RUN)
        添加一个文件或目录 (ADD)
        创建环境变量 (ENV)
        从image启动一个container时运行哪一个进程 (CMD)
        当你创建一个image时，Docker会读取Dockerfile，执行其中的instructions，返回创建的image。
        
        7.2 How does a Docker registry work?
        Dock registry是存储Docker image的。在创建Docker image之后，你可以push（类似于git的push）到一个公共的registry例如Dock Hub或一个在防火墙之后运行私有的registry中。你也可以搜索现有的images并将它们从registry中pull（类似于git的pull）下来。
        
        Docker Hub是一个公有的Docker registry，上面有现有的images并且允许你贡献你自己的image。
        
        Docker商店允许买卖Docker images。对于image，你可以从软件供应商那购买一个包含应用的Docker image，并用这个image部署应用到你的testing，staging和production环境中，通过pull新版本的image来进行升级并重新部署到容器中。Docker商店现在在私有的beta中。
        
        7.3 How does a container work?
        container使用主机的Linux内核，当image创建时包括使用你添加的额外文件，当container运行时，使用container创建的相关元数据。image定义了container的内容，包括container启动时运行哪一个进程，还有许多其它的配置细节。Docker image是只读的。当Docker从一个image运行一个container时，它会在应用运行的image之上添加读写层（使用前面的UnionFS）。
        
        7.4 What happens when you run a container?
        当使用CLI命令docker run或等价的API时，Docker Engine client会通知Docker daemon运行一个container。下面的例子告诉Docker daemon运行一个container，使用ubuntu Docker image，保持前端的交互模式(-i)，运行/bin/bash命令。
        
        $ docker run -i -t ubuntu /bin/bash
        当执行这个命令时，Docker Engine执行下面的过程：
        
        Pulls the ubuntu image: Docker Engine 检查ubuntu image是否存在. 如果image本地存在, Docker Engine 使用它创建container。否则， Docker Engine会从Dock Hub上pull这个image.
        
        Creates a new container: Docker用这个image创建container.
        
        Allocates a filesystem and mounts a read-write layer: 在文件系统中创建container并在image中添加读写layer.
        
        Allocates a network / bridge interface: 创建网络接口，运行Docker container与本地主机通信.
        
        Sets up an IP address: 从池中添加可用的IP地址.
        
        Executes a process that you specify: 执行/bin/bash可执行程序.
        
        Captures and provides application output: 链接并打印标准输入、输出和错误信息，你可以看到你的应用运行情况，因为你请求了交互模式。
        
        8. docker基础技术
        docker是用Go语言写的并且利用了Linux内核的一些特性来交付它的功能。
        
        8.1 Namespaces
        Docker使用namespaces技术为container提供独立的工作空间。当你运行一个container时，Docker为container创建一系列namespaces。
        
        命名空间提供了独立的层。container的每个方面都运行在一个独立的命名空间中，并且有权访问那个命名空间。
        
        Docker Engine在Linux中使用下面的命名空间：
        
        pid namespace: 进程分离 (PID: Process ID).
        net namespace: 管理网络接口 (NET: Networking).
        ipc namespace: 管理IPC资源的访问权限 (IPC: InterProcess Communication).
        mnt namespace: 管理文件挂载点 (MNT: Mount).
        uts namespace: 分离内核和版本标识符. (UTS: Unix Timesharing System).
        
        8.2 Control groups
        Docker Engine在Linux上也依赖另一项技术叫做control groups (cgroups)。cgroup限定应用访问特定的资源集合。Control groups允许Docker Engine的containers共享可获得的硬件资源。例如，你可以限制特定容器的可用内存。
        
        8.3 Union file systems
        Union文件系统或UnionFS，是创建层操作的文件系统，可以使创建层快速且轻量级。 Docker Engine使用UnionFS提供containers的创建块。Docker Engine可以使用多个UnionFS变种，包括AUFS，btrfs，vfs，和DeviceMapper。
        
        8.4 Container format
        Docker Engine将namespaces，control groups和UnionFS组合进一个包装器叫做container format。默认的container format叫libcontainer。将来，Docker通过技术融合例如BSD Jails或Solaris Zones可能会支持其它的container formats。