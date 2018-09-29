最近遇到一个问题，由于kafka接收数据进行处理所花费的时间较长，导致kafka队列中有堆积，然后就想开启很多个consumer但是不怎么会用，报了一些错误，通过一天的学习了解，终于可以让多个consumer共同消费topic中的数据了。



使用3个producer同时对一个topic写入数据，其中使用2个group组来对数据进行读取，其中topic中的partitions定为2。在每个group下又创建2个consumer进行消费数据。

在项目刚开始，我只在topic中设置了一个partitions就是只有一个消费者来消费我传入的数据，但是由于项目的变化，消费的太慢写入的太快，给kafka带来了数据堆积，于是我又加了一个consumer来进行数据的消费，由于刚开始没有给group创建ID使用默认ID，但是发现我每个consumer消费的数据是相同的，并没有达到我的需求。kafka的一条数据会被我的2个consumer同时消费，消费2次，并没有增加效率，而且还给系统带来负担，后台查询官网API发现group中是有ID的，如果没有创建就自动使用默认ID这个一定要注意。其次是一个partition对应一个consumer,如果consumer的数量大于Topic中partition的数量就会有的consumer接不到数据（设置ID不使用默认ID的情况下）。 
为了满足的我业务需求我做了一下调整： 
增加topic中partition中的数量。 
相应增加consumer的数量 consumer的数量<=partition的数量 
这里需要强调的是不同的group组之间不做任何影响，就如同我一个group做python机器学习。另一个做Spark计算，这2个group的数据都是相互不影响的，这也是kafka很好用的东西。 
下面java的代码如下需要对给group添加一个name：

consumer1



public static Properties props;
    static {
        props = new Properties();
        props.put("zookeeper.connect", "node1:2181/kafka");
        props.put("serializer.class", StringEncoder.class.getName());
        props.put("metadata.broker.list", "node1:9092");
        props.put("group.id", "group"); // group组的名字 （做group组区分）
        props.put("group.name", "1"); // 当前group组中的名字
                                        // （在相同的group组中做consumer的qufe）
    }

    public static void main(String[] args) throws InterruptedException {
        String topic = "zhu1";
        ConsumerConnector consumer = Consumer
                .createJavaConsumerConnector(new ConsumerConfig(props));
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1); // 取哪一个topic 取几条数据
        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer
                .createMessageStreams(topicCountMap);
        final KafkaStream<byte[], byte[]> kafkaStream = messageStreams.get(
                topic).get(0);
        ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
        while (iterator.hasNext()) {
            String item = new String(iterator.next().message());
            // String msg;
            // try {
            // msg = new String(item.getBytes("gbk"),"utf-8");
            System.out.println("收到消息：" + item);
            Thread.sleep(2000);
            // } catch (UnsupportedEncodingException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();1234567891011121314151617181920212223242526272829303132

consumer2:



public static Properties props;
    static {
        props = new Properties();
        props.put("zookeeper.connect", "node1:2181/kafka");
        props.put("serializer.class", StringEncoder.class.getName());
        props.put("metadata.broker.list", "node1:9092");
        props.put("group.id", "group"); // group组的名字 （做group组区分）
        props.put("group.name", "2"); // 当前group组中的名字
                                        // （在相同的group组中做consumer的qufe）
    }

    public static void main(String[] args) throws InterruptedException {
        String topic = "zhu1";
        ConsumerConnector consumer = Consumer
                .createJavaConsumerConnector(new ConsumerConfig(props));
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1); // 取哪一个topic 取几条数据
        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer
                .createMessageStreams(topicCountMap);
        final KafkaStream<byte[], byte[]> kafkaStream = messageStreams.get(
                topic).get(0);
        ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
        while (iterator.hasNext()) {
            String item = new String(iterator.next().message());
            // String msg;
            // try {
            // msg = new String(item.getBytes("gbk"),"utf-8");
            System.out.println("收到消息：" + item);
            Thread.sleep(2000);
            // } catch (UnsupportedEncodingException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();1234567891011121314151617181920212223242526272829303132

这里我的producer写入是使用python写的：



    topic = client.topics["zhu1"]
    producer = topic.get_sync_producer()
    count = 0
    print "2"
    while(1):
        producer.produce("test test"+str(count))
        time.sleep(2)
        count+=112345678

结果如下图所示： 


到了这里已经实现读入kafka多对多的消费了 ，第一次写博客希望对大家有帮助！ 
但是在python中出现了一些问题，希望有大神可以解决。 
在python中我是使用pykafka进行kafka调用的我设置好group的id后貌似并没有起作用，2个consumer读到的内容都是相同的内容，但是2个consumer在同一个group中，所以很是纠结。希望后续可以进行解决。            
						
                
		
	
	


		$(".MathJax").remove();
		if($('div.markdown_views pre.prettyprint code.hljs').length > 0 ){
				$('div.markdown_views')[0].className = 'markdown_views';
		}



	(function(){
		function setArticleH(btnReadmore,posi){
			var winH = $(window).height();
			var articleBox = $("div.article_content");
			var artH = articleBox.height();
			if(artH > winH*posi){
				articleBox.css({
					'height':winH*posi+'px',
					'overflow':'hidden'
				})
				btnReadmore.click(function(){
					articleBox.removeAttr("style");
					$(this).parent().remove();
				})
			}else{
				btnReadmore.parent().remove();
			}
		}
		var btnReadmore = $("#btn-readmore");
		if(btnReadmore.length>0){
			if(currentUserName){
				setArticleH(btnReadmore,3);
			}else{
				setArticleH(btnReadmore,1.2);
			}
		}
	})()

                

	  	

---------------------

本文来自 zhaishujie 的CSDN 博客 ，全文地址请点击：https://blog.csdn.net/zhaishujie/article/details/71713794?utm_source=copy 