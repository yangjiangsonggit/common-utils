image运行与删除
=============
    运行image
    # 命令形式：docker run -ti --rm image
    $ docker run -ti --rm hello-world
    Hello from Docker!
    This message shows that your installation appears to be working correctly.
    To generate this message, Docker took the following steps:
     1. The Docker client contacted the Docker daemon.
     2. The Docker daemon pulled the "hello-world" image from the Docker Hub.
     3. The Docker daemon created a new container from that image which runs the
        executable that produces the output you are currently reading.
     4. The Docker daemon streamed that output to the Docker client, which sent it
        to your terminal.
    To try something more ambitious, you can run an Ubuntu container with:
     $ docker run -it ubuntu bash
    Share images, automate workflows, and more with a free Docker Hub account:
     https://hub.docker.com
    For more examples and ideas, visit:
     https://docs.docker.com/engine/userguide/
    解析：docker run命令，-i是交互模式，-t是提供一个伪终端tty，--rm是在容器退出后自动移除容器。
    
    停用全部运行中的容器:
    # 命令形式：docker stop $(docker ps -q)
    $ docker stop $(docker ps -q)
    快速删除docker中的所有容器
    # 命令形式：docker rm $(docker ps -aq)
    $ docker rm $(docker ps -aq)
    0da2369cdeb4
    961a584288a8
    91d90bb91dfc
    ece1aeec622f
    b99960c42718
    5a47d3cd4ed4
    0c70e6708f28
    ff3e7f1ee98e
    efbb943c0cdb
    835fd7facf17
    b64c8a29ed64
    aadf299b179b
    快速停止docker容器运行并删除docker中的容器
    # 命令形式：docker stop $(docker ps -q) & docker rm $(docker ps -aq)
    $ docker stop $(docker ps -q) & docker rm $(docker ps -aq)
    解析：ps -a显示所有的容器，运行的与停止的，ps -aq显示所有容器的CONTAINER ID，$(docker ps -aq)是Linux的命令替换，
    会将docker ps -aq的结果替换为参数，docker rm CONTAINER ID是删除容器命令。ps -q是显示运行中的容器的CONTAINER ID。
    
    2. 制作自己的image
    通过commit制造自己的image
    # 命令形式：docker commit -m "comment" container_id image_name
    # 查看已有的容器
    $ docker ps -qa
    9022a4eeb5d7
    # 根据容器id制作image
    $ docker commit -m "feat: make a copy" 9022a4eeb5d7 test/ubuntu
    sha256:************b50641cd172ddb6b508d5cb4df873588ab40f078663c8541
    3. 将自己的image推送到dockerhub
    # 命令形式：docker push dockerhub_username/image_name
    $ docker push test/ubuntu
    The push refers to a repository [docker.io/***/***]
    5918e60ab8d9: Pushed 
    319daea31eb9: Mounted from ***/*** 
    d41506d13d11: Mounted from ***/*** 
    83747d8dae14: Mounted from ***/*** 
    255752cc6046: Mounted from ***/*** 
    7694b371bada: Mounted from ***/*** 
    06d57711a2de: Mounted from ***/*** 
    fd8ca422aa2a: Mounted from ***/*** 
    79717468825f: Mounted from ***/*** 
    6d1cb131e81a: Mounted from ***/*** 
    374eda78f988: Mounted from ***/*** 
    cf6d1cd028ea: Mounted from ***/*** 
    3d1126efbbe3: Mounted from ***/*** 
    latest: digest: sha256:*******8c0da44f444b4c8f6b6fd37049c25eb537833ab466e366 size: 3037
    4. 挂载本地目录到容器中
    挂载本机目录到docker容器中
    # 命令形式：$ docker run -ti --rm --volume=local_dir:container_dir image_name /bin/bash
    # 进入本机目录
    $ cd CaffeDocker/
    # 创建文件
    $ touch a.txt
    # 编辑文件
    $ vim a.txt
    # 查看文件内容
    $ cat a.txt
    test
    # 挂载
    $ docker run -ti --rm --volume=$(pwd):/workspace ***/ubuntu /bin/bash
    root@c19af4388af5:/workspace# ls
    a.txt
    # 在容器中删除文件
    root@c19af4388af5:/workspace# rm -rf a.txt 
    root@c19af4388af5:/workspace# ls
    root@c19af4388af5:/workspace# exit
    exit
    # 本地文件没了
    $ ls
    解析：--volume=$(pwd):/workspace是挂载本机目录到容器中，--volume or -v是docker的挂载命令，=$(pwd):/workspace是挂载信息，
    是将$(pwd)即本机当前目录，:是挂载到哪，/workspace是容器中的目录，就是把容器中的workspace目录换成本机的当前目录，这样就可以在本机
    与容器之间进行交互了，本机当前目录可以编辑，容器中同时能看到。容器中的workspace目录的修改也直接反应到了本机上。$()是Linux中的
    命令替换，即将$()中的命令内容替换为参数，pwd是Linux查看当前目录，我的本机当前目录为CaffeDocker，--volume=$(pwd):/workspace
    就等于--volume=/Users/***/CaffeDocker:/workspace，/Users/***/CaffeDocker为pwd的执行结果，$()是将pwd的执行结果作为参数执行。
    
    5. 启动和停止容器
    启动容器
    # 命令形式：docker start
    # 命令形式：docker stop
    # 查看刚运行过的容器，-l代表last
    $ docker ps -l
    CONTAINER ID        IMAGE               COMMAND             CREATED              STATUS                      PORTS               NAMES
    f65c25d3bdfa        hello-world         "/hello"            About a minute ago   Exited (0) 55 seconds ago
    备注：Docker镜像是由多个文件系统（只读层）叠加而成。当我们启动一个容器的时候，Docker会加载只读镜像层并在其上添加一个读写层。如果
    运行中的容器修改了现有的一个已经存在的文件，那该文件将会从读写层下面的只读层复制到读写层，该文件的只读版本仍然存在，只是已经被读写层
    中该文件的副本所隐藏。当删除Docker容器，并通过该镜像重新启动时，之前的更改将会丢失。（在Docker中，只读层及在顶部的读写层的组合被
    称为Union File System，联合文件系统）。