Publisher Confirms解决MQ丢消息
=======

一、分析

依据RabbitMQ和Spring AMQP参考文档，事务(Transactional)或发布确认(Publisher Confirms / aka Publisher Acknowledgements)机制可保证消息被正确投递，即从理论上来说MQ不会丢消息。
注意这两种机制不能共存。事务机制是重量级的，同步的，会带来大量开销；发布确认机制则是轻量级的，异步的。
对于发布确认机制，(1) 需置CachingConnectionFactory的publisherConfirms属性为true；(2) 生产者需调用setConfirmCallback(ConfirmCallback callback)，Confirms就会回调给生产者；(3) 消费者需考虑消息去重处理。
这里需要注意的是，一个RabbitTemplate只能支持一个ConfirmCallback。

二、具体实施项
生产者需修改XML配置和代码，使用RabbitTemplate的带CorrelationData 参数版本的convertAndSend()或send()方法发送消息；消费者则需要增加消息去重处理。

<!-- 配置RabbitMQ连接工厂：开启发布确认机制 -->
<rabbit:connection-factory id="connectionFactoryConfirm" ... publisher-confirms="true" />
<rabbit:admin connection-factory="connectionFactoryConfirm" />
<!-- 声明队列、Exchange以及binding -->
<rabbit:queue name="Queue名" durable="true" auto-delete="false" exclusive="false" />
<!-- Direct Exchange -->
<rabbit:direct-exchange name="Exchange名">
<rabbit:bindings>
<rabbit:binding queue="Queue名" />
</rabbit:bindings>
</rabbit:direct-exchange>

<!-- 生产者配置RabbitTemplate。如果通过代码创建就不需要 -->
<rabbit:template id="rabbitTemplateConfirm" connection-factory="connectionFactoryConfirm" />

<!-- 消费者配置 -->
<!-- 声明bean -->
<bean id="" class=" " />
<!-- 声明监听器容器和监听器 -->
<rabbit:listener-container connection-factory="connectionFactoryConfirm">
<rabbit:listener ref="" method="" queue-names="Queue名" />
</rabbit:listener-container>

// 生产者代码：
// RabbitTemplate对象可通过模板bean注入：
@Autowired
public Class(RabbitTemplate rabbitTemplate) {
this.rabbitTemplate = rabbitTemplate;
}
// 或者创建默认策略和设置的RabbitTemplate对象：
@Autowired
@Qualifier("connectionFactoryConfirm")
private ConnectionFactory connectionFactoryConfirm;
// ...
{
rabbitTemplate = new RabbitTemplate(connectionFactoryConfirm);
}
 
// 初始化，安装ConfirmCallback
rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
@Override
public void confirm(CorrelationData correlationData, boolean ack, String cause) {
if (ack) {
// 处理ack
} else {
// 处理nack, 此时cause包含nack的原因。
// 如当发送消息给一个不存在的Exchange。这种情况Broker会关闭Channel；
// 当Broker关闭或发生网络故障时，需要重新发送消息。
// 暂时先日志记录，包括correlationData, cause等。
}
}
});
// 注意生产者在发送消息时还需提供CorrelationData对象。
rabbitTemplate.convertAndSend("Exchange名", "Routing Key", obj implements Serializable, newCorrelationData("自定义ID，待讨论确定"));
 
// 消费者代码：
// 注意开启发布确认机制后，消息可能会被重复传递，消费者应做去重处理。



————————————————
参考文献：
[1] Spring AMQP reference - Publisher Confirms. (http://docs.spring.io/spring-amqp/docs/1.5.6.RELEASE/reference/html/_reference.html#template-confirms)
[2] RabbitMQ - Confirms(http://www.rabbitmq.com/confirms.html)