运行Hello world
================
    # 运行Hello world
    $ docker run ubuntu /bin/echo 'Hello world'
    # 在容器内运行一个ubuntu的shell
    $ docker run -t -i ubuntu /bin/bash
    # 
    $ docker run -d ubuntu /bin/sh -c "while true; do echo hello world; sleep 1; done"
    $ docker ps
    CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS               NAMES
    91d90bb91dfc        ubuntu              "/bin/sh -c 'while tr"   43 seconds ago      Up 42 seconds                           pensive_jepsen
    $ docker logs pensive_jepsen
    hello world
    hello world
    # 查看版本信心
    $ docker version
    Client:
     Version:      1.12.1
     API version:  1.24
     Go version:   go1.7.1
     Git commit:   6f9534c
     Built:        Thu Sep  8 10:31:18 2016
     OS/Arch:      darwin/amd64
    Server:
     Version:      1.12.1
     API version:  1.24
     Go version:   go1.6.3
     Git commit:   23cf638
     Built:        Thu Aug 18 17:52:38 2016
     OS/Arch:      linux/amd64
    # docker帮助
    $ docker --help
    ...
    $ docker stop pensive_jepsen
    pensive_jepsen
    docker run 运行一个容器
    
    ubuntu 镜像名字
    
    -t 一个新容器内部的伪终端
    
    -i 能与容器进行交互，交互模式
    
    /bin/bash 在容器内启动Bash shell
    
    -d 后台运行
    
    docker ps 列出当前运行的容器
    
    docker logs 显示某个容器的输出
    
    docker stop 停止某个容器的运行
    
    docker version 输出docker版本信息
    
    2. 运行一个简单的web应用
    # 运行web应用
    $ docker run -d -P training/webapp python app.py
    # 查看当前运行的容器
    $ docker ps -l
    CONTAINER ID        IMAGE               COMMAND             CREATED              STATUS              PORTS                     NAMES
    6e95e02e0fad        training/webapp     "python app.py"     About a minute ago   Up About a minute   0.0.0.0:32768->5000/tcp   mad_curran
    # 指定容器的端口映射
    docker run -d -p 80:5000 training/webapp python app.py
    $ docker ps
    CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS                     NAMES
    961a584288a8        training/webapp     "python app.py"     9 seconds ago       Up 8 seconds        0.0.0.0:32769->5000/tcp   goofy_kowalevski
    ddbdae724301        training/webapp     "python app.py"     2 minutes ago       Up 2 minutes        0.0.0.0:80->5000/tcp      high_dubinsky
    $ docker port goofy_kowalevski 5000
    0.0.0.0:32769
    $ docker logs -f high_dubinsky
     * Running on http://0.0.0.0:5000/ (Press CTRL+C to quit)
    172.17.0.1 - - [22/Sep/2016 09:47:27] "GET / HTTP/1.1" 200 -
    172.17.0.1 - - [22/Sep/2016 09:47:27] "GET /favicon.ico HTTP/1.1" 404 -
    $ docker top high_dubinsky
    PID                 USER                TIME                COMMAND
    3732                root                0:00                python app.py
    $ docker inspect high_dubinsky
    [
        {
            "Id": "ddbdae724301831fa2477d9c95077c3b07fa16fad3686e429a6037f33e3f570c",
            "Created": "2016-09-22T09:37:45.008007727Z",
            "Path": "python",
            "Args": [
                "app.py"
            ],
            "State": {
                "Status": "running",
                "Running": true,
                "Paused": false,
                "Restarting": false,
                "OOMKilled": false,
                "Dead": false,
                "Pid": 3732,
                "ExitCode": 0,
                "Error": "",
                "StartedAt": "2016-09-22T09:37:45.762803488Z",
                "FinishedAt": "0001-01-01T00:00:00Z"
            },
    ...
    $ docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' high_dubinsky
    172.17.0.3
    $ docker stop high_dubinsky
    $ docker rm high_dubinsky
    -P 映射容器端口到主机端口
    
    -l 告诉docker显示最后启动的容器信息
    
    -a 显示所有容器的信息，包括已经停止运行的容器
    
    PORTS 为容器端口与主机端口之间的映射
    
    -p 80:5000 容器的5000端口映射主机的80端口
    
    docker port 容器name/id 端口号，可以查看指定容器的端口5000映射到主机的端口
    
    -f，类似linux tail -f，查看容器的标准输出
    
    docker top 用来查看容器内部运行的进程
    
    docker inspect 查看容器的配置与状态信息，返回的是JSON串
    
    docker inspect -f ，查询JSON串中的指定内容
    
    docker rm 只能移除已经停止运行的容器，因此在用之前，需要用docker stop 停止运行你想要移除的容器
    
    注：从上面可以看到主机的两个端口32769和80都映射到了容器的5000端口，可以在主机浏览器中访问http://localhost:32769/，http://localhost:80/，都能看到Hello world!页面。虽然两个容器的端口都是5000，但它们是不一样的，容器的namespace属性会将两个容器隔离开，因此它们对应的主机端口是不一样的。