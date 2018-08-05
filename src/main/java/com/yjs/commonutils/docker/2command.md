
   Docker命令
================

        # 列出系统中的所有容器
        docker ps -a
        # 结果
        CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS                      PORTS               NAMES
        aadf299b179b        hello-world         "/hello"            20 minutes ago      Exited (0) 20 minutes ago                       gloomy_khorana
        # docker ps仅列出当前运行的容器
        docker ps
        CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
        # 运行whalesay镜像
        docker fun docker/whalesay cowsay boo
        1.1 docker run hello-world解析
        这个命令总共有三部分：
        
        docker：告诉操作系统你使用的是docker程序
        
        run：创建和运行docker容器的子命令
        
        hello-world：告诉docker将哪一个镜像加载到容器中
        
        1.2 docker run hello-world的运行过程
        image是一个文件系统，里面有运行时使用的参数。它没有状态且不能改变。容器是镜像的运行实例。命令执行时，Docker Engine会进行以下的步骤：
        
        检查hello-world软件镜像是否存在
        
        如果不存在，从Docker Hub上下载hello-world
        
        加载镜像到容器中并运行
        
        运行是根据镜像的构建过程执行的，可能一直运行，也可能执行几个命令就退出。镜像可能是非常复杂的，例如镜像可以启动一个数据库软件。
        
        2. 创建自己的docker image
        2.1 写一个Dockerfile
        $ mkdir Docker
        $ cd Docker
        $ touch Dockerfile
        $ vim Dockerfile
        在Dockerfile中加入下面的代码并保存：
        
        FROM docker/whalesay:latest
        RUN apt-get -y update && apt-get install -y fortunes
        CMD /usr/games/fortune -a | cowsay
        2.2 从Dockerfile中创建image
        # 查看Dockerfile内容
        $ cat Dockerfile
        # 构建image
        $ docker build -t docker-whale .
        2.3 创建image的过程
        首先Docker检查确保有构建的需要的东西
        Sending build context to Docker daemon 2.048 kB
        然后Docker加载whalesay镜像
        Step 1 : FROM docker/whalesay:latest
         ---> 6b362a9f73eb
        Docker开始执行apt-get命令
        Step 2 : RUN apt-get -y update && apt-get install -y fortunes
         ---> Running in 3c381fdef64a
        Ign http://archive.ubuntu.com trusty InRelease
        Get:1 http://archive.ubuntu.com trusty-updates InRelease [65.9 kB]
        Get:2 http://archive.ubuntu.com trusty-security InRelease [65.9 kB]
        Hit http://archive.ubuntu.com trusty Release.gpg
        Get:3 http://archive.ubuntu.com trusty-updates/main Sources [473 kB]
        Get:4 http://archive.ubuntu.com trusty-updates/restricted Sources [5247 B]
        Get:5 http://archive.ubuntu.com trusty-updates/universe Sources [207 kB]
        Get:6 http://archive.ubuntu.com trusty-updates/main amd64 Packages [1122 kB]
        Get:7 http://archive.ubuntu.com trusty-updates/restricted amd64 Packages [23.5 kB]
        Get:8 http://archive.ubuntu.com trusty-updates/universe amd64 Packages [483 kB]
        Hit http://archive.ubuntu.com trusty Release
        Get:9 http://archive.ubuntu.com trusty-security/main Sources [152 kB]
        Get:10 http://archive.ubuntu.com trusty-security/restricted Sources [3944 B]
        Get:11 http://archive.ubuntu.com trusty-security/universe Sources [49.4 kB]
        Get:12 http://archive.ubuntu.com trusty-security/main amd64 Packages [659 kB]
        Get:13 http://archive.ubuntu.com trusty-security/restricted amd64 Packages [20.2 kB]
        Get:14 http://archive.ubuntu.com trusty-security/universe amd64 Packages [178 kB]
        Hit http://archive.ubuntu.com trusty/main Sources
        Hit http://archive.ubuntu.com trusty/restricted Sources
        Hit http://archive.ubuntu.com trusty/universe Sources
        Hit http://archive.ubuntu.com trusty/main amd64 Packages
        Hit http://archive.ubuntu.com trusty/restricted amd64 Packages
        Hit http://archive.ubuntu.com trusty/universe amd64 Packages
        Fetched 3508 kB in 25s (139 kB/s)
        Reading package lists...
        Reading package lists...
        Building dependency tree...
        Reading state information...
        The following extra packages will be installed:
          fortune-mod fortunes-min librecode0
        Suggested packages:
          x11-utils bsdmainutils
        The following NEW packages will be installed:
          fortune-mod fortunes fortunes-min librecode0
        0 upgraded, 4 newly installed, 0 to remove and 87 not upgraded.
        Need to get 1961 kB of archives.
        After this operation, 4817 kB of additional disk space will be used.
        Get:1 http://archive.ubuntu.com/ubuntu/ trusty/main librecode0 amd64 3.6-21 [771 kB]
        Get:2 http://archive.ubuntu.com/ubuntu/ trusty/universe fortune-mod amd64 1:1.99.1-7 [39.5 kB]
        Get:3 http://archive.ubuntu.com/ubuntu/ trusty/universe fortunes-min all 1:1.99.1-7 [61.8 kB]
        Get:4 http://archive.ubuntu.com/ubuntu/ trusty/universe fortunes all 1:1.99.1-7 [1089 kB]
        debconf: unable to initialize frontend: Dialog
        debconf: (TERM is not set, so the dialog frontend is not usable.)
        debconf: falling back to frontend: Readline
        debconf: unable to initialize frontend: Readline
        debconf: (This frontend requires a controlling tty.)
        debconf: falling back to frontend: Teletype
        dpkg-preconfigure: unable to re-open stdin: 
        Fetched 1961 kB in 8s (221 kB/s)
        Selecting previously unselected package librecode0:amd64.
        (Reading database ... 13116 files and directories currently installed.)
        Preparing to unpack .../librecode0_3.6-21_amd64.deb ...
        Unpacking librecode0:amd64 (3.6-21) ...
        Selecting previously unselected package fortune-mod.
        Preparing to unpack .../fortune-mod_1%3a1.99.1-7_amd64.deb ...
        Unpacking fortune-mod (1:1.99.1-7) ...
        Selecting previously unselected package fortunes-min.
        Preparing to unpack .../fortunes-min_1%3a1.99.1-7_all.deb ...
        Unpacking fortunes-min (1:1.99.1-7) ...
        Selecting previously unselected package fortunes.
        Preparing to unpack .../fortunes_1%3a1.99.1-7_all.deb ...
        Unpacking fortunes (1:1.99.1-7) ...
        Setting up librecode0:amd64 (3.6-21) ...
        Setting up fortune-mod (1:1.99.1-7) ...
        Setting up fortunes-min (1:1.99.1-7) ...
        Setting up fortunes (1:1.99.1-7) ...
        Processing triggers for libc-bin (2.19-0ubuntu6.6) ...
         ---> 5c6dbfc7a7ea
        执行CMD命令
        Removing intermediate container 3c381fdef64a
        Step 3 : CMD /usr/games/fortune -a | cowsay
         ---> Running in b0df80e0d4c9
         ---> d1178b780ac6
        Removing intermediate container b0df80e0d4c9
        Successfully built d1178b780ac6
        2.4 测试自己创建的image
        $ docker images
        $ docker run docker-whale
        # 结果
         _________________________________________ 
        / I suppose some of the variation between \
        | Boston drivers and the rest of the      |
        | country is due to the progressive       |
        | Massachusetts Driver Education Manual   |
        | which I happen to have in my top desk   |
        | drawer. Some of the Tips for Better     |
        | Driving are worth considering, to wit:  |
        |                                         |
        | [131.16d]:                              |
        |                                         |
        | "Directional signals are generally not  |
        | used except during vehicle              |
        |                                         |
        | inspection; however, a left-turn signal |
        | is appropriate when making              |
        |                                         |
        | a U-turn on a divided highway."         |
        |                                         |
        | [96.7b]:                                |
        |                                         |
        | "When paying tolls, remember that it is |
        | necessary to release the                |
        |                                         |
        | quarter a full 3 seconds before passing |
        | the basket if you are                   |
        |                                         |
        | traveling more than 60 MPH."            |
        |                                         |
        | [110.13]:                               |
        |                                         |
        | "When traveling on a one-way street,    |
        | stay to the right, so as not            |
        |                                         |
        \ to interfere with oncoming traffic."    /
         ----------------------------------------- 
            \
             \
              \     
                            ##        .            
                      ## ## ##       ==            
                   ## ## ## ##      ===            
               /""""""""""""""""___/ ===        
          ~~~ {~~ ~~~~ ~~~ ~~~~ ~~ ~ /  ===- ~~~   
               \______ o          __/            
                \    \        __/             
                  \____\______/
        3. 在Docker Hub上创建自己的仓库，与Git类似
        略.
        
        4. Tag, push, and pull your image
        Step 1: Tag and push the image
        用docker images查看你当前的image
        REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
        docker-whale        latest              d1178b780ac6        2 hours ago         274.9 MB
        hello-world         latest              c54a2cc56cbb        11 weeks ago        1.848 kB
        docker/whalesay     latest              6b362a9f73eb        16 months ago       247 MB
        用docker tag命令和IMAGE ID给image打上tag，YOUR_DOCKERHUB_NAME为你的Docker Hub帐号，它起一个namespace的作用
        docker tag d1178b780ac6 YOUR_DOCKERHUB_NAME/docker-whale:latest
        docker images重新查看image
        REPOSITORY               TAG                 IMAGE ID            CREATED             SIZE
        docker-whale             latest              d1178b780ac6        2 hours ago         274.9 MB
        snailtyan/docker-whale   latest              d1178b780ac6        2 hours ago         274.9 MB
        hello-world              latest              c54a2cc56cbb        11 weeks ago        1.848 kB
        docker/whalesay          latest              6b362a9f73eb        16 months ago       247 MB
        使用docker login命令登录你的Docker Hub账户
        $ docker login
        Login with your Docker ID to push and pull images from Docker Hub. If you don't have a Docker ID, head over to https://hub.docker.com to create one.
        Username: ***
        Password: 
        Login Succeeded
        使用docker push将image推送到Docker Hub
        $ docker push ***/docker-whale
        The push refers to a repository [docker.io/***/docker-whale]
        122284833f25: Pushed 
        5f70bf18a086: Mounted from docker/whalesay 
        d061ee1340ec: Mounted from docker/whalesay 
        d511ed9e12e1: Mounted from docker/whalesay 
        091abc5148e4: Mounted from docker/whalesay 
        b26122d57afa: Mounted from docker/whalesay 
        37ee47034d9b: Mounted from docker/whalesay 
        528c8710fd95: Mounted from docker/whalesay 
        1154ba695078: Mounted from docker/whalesay 
        latest: digest: sha256:18a6032f5726bdd6cea7c15faa02d9dcf1f12ff591c42ca15fc95c0d83de04fc size: 2614
        你可以在你的Docker Hub上看到*/docker-whale image了
        Step 2: Pull your new image
        先删除本地的docker-whale
        # 用id删除image
        $ docker rmi -f 7d9495d03763
        # 用name删除image
        $ docker rmi -f docker-whale
        通过docker run来pull image
        $ docker run ***/docker-whale
        Unable to find image '***/docker-whale:latest' locally
        latest: Pulling from ***/docker-whale
        e190868d63f8: Already exists 
        909cd34c6fd7: Already exists 
        0b9bfabab7c1: Already exists 
        a3ed95caeb02: Already exists 
        00bf65475aba: Already exists 
        c57b6bcc83e3: Already exists 
        8978f6879e2f: Already exists 
        8eed3712d2cf: Already exists 
        2bb8a73a1829: Already exists 
        Digest: sha256:18a6032f5726bdd6cea7c15faa02d9dcf1f12ff591c42ca15fc95c0d83de04fc
        Status: Downloaded newer image for ***/docker-whale:latest
         ________________________________________ 
        / Would it help if I got out and pushed? \
        |                                        |
        \ -- Princess Leia Organa                /
         ---------------------------------------- 
            \
             \
              \     
                            ##        .            
                      ## ## ##       ==            
                   ## ## ## ##      ===            
               /""""""""""""""""___/ ===        
          ~~~ {~~ ~~~~ ~~~ ~~~~ ~~ ~ /  ===- ~~~   
               \______ o          __/            
                \    \        __/             
                  \____\______/