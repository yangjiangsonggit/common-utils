/**
	 * redis pub & sub
     */
    @Bean
	public JedisConnectionFactory jedisConnectionFactory(RedisStorageFactory redisStorageFactory) {

		GenericObjectPoolConfig poolConfig = redisStorageFactory.getPoolConfig();

		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setTestOnBorrow(poolConfig.getTestOnBorrow());
		jedisPoolConfig.setTestOnReturn(poolConfig.getTestOnReturn());
		jedisPoolConfig.setTestWhileIdle(poolConfig.getTestWhileIdle());
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(poolConfig.getTimeBetweenEvictionRunsMillis());
		jedisPoolConfig.setMaxWaitMillis(poolConfig.getMaxWaitMillis());
		jedisPoolConfig.setMaxTotal(poolConfig.getMaxTotal());
		jedisPoolConfig.setMaxIdle(poolConfig.getMaxIdle());
		jedisPoolConfig.setMinIdle(poolConfig.getMinIdle());


		Map<String, Object> doveValue = redisStorageFactory.getDoveValue();
		if (doveValue == null || doveValue.size() <= 0) {
			throw new BusinessException("Please configure redis host and port");
		}
		ArrayList<Object> objects = Lists.newArrayList(doveValue.values());
		if (CollectionUtils.isEmpty(objects)) {
			throw new BusinessException("Please configure redis host and port");
		}
		Map<String, String> val = (Map)objects.get(0);
		String masterVal = val.get("master");
		List<String> result = Splitter.on(":").trimResults().splitToList(masterVal);

		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
		jedisConnectionFactory.setHostName(result.get(0).trim());
		jedisConnectionFactory.setPort(Integer.parseInt(result.get(1).trim()));

//		RedisStandaloneConfiguration redisStandaloneConfiguration =
//				new RedisStandaloneConfiguration();
//				redisStandaloneConfiguration.setHostName(result.get(0).trim());
//		redisStandaloneConfiguration.setPort(Integer.parseInt(result.get(1).trim()));
//		redisStandaloneConfiguration.setDatabase(redisStorageFactory.getIndexDb());
//		JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jpcb =
//				(JedisClientConfiguration.JedisPoolingClientConfigurationBuilder)JedisClientConfiguration.builder();
//		jpcb.poolConfig(jedisPoolConfig);
//		JedisClientConfiguration jedisClientConfiguration = jpcb.build();
//		return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
		return jedisConnectionFactory;
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
		stringRedisTemplate.setConnectionFactory(jedisConnectionFactory);
		return stringRedisTemplate;
	}

	@Bean
	public ChannelTopic channelTopic() {
		return new ChannelTopic(REFRESH_DROOLS);
	}

	@Bean(destroyMethod="destroy")
	public RedisMessageListenerContainer redisMessageListenerContainer(JedisConnectionFactory jedisConnectionFactory,
			ChannelTopic channelTopic, SubServiceImpl subService) {
		RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
		Set<ChannelTopic> topics = Sets.newHashSetWithExpectedSize(1);
		topics.add(channelTopic);
		HashMap<MessageListener, Collection<? extends Topic>> map = Maps.newHashMapWithExpectedSize(1);
		map.put(subService,topics);
		redisMessageListenerContainer.setConnectionFactory(jedisConnectionFactory);
		redisMessageListenerContainer.setMessageListeners(map);
		return redisMessageListenerContainer;
	}
	
	
	package com.jumei.shippingfee.core.redis;
    
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.redis.core.StringRedisTemplate;
    import org.springframework.data.redis.listener.ChannelTopic;
    import org.springframework.stereotype.Component;
    
    /**
     * create by jiangsongy on 2019/1/5
     */
    @Component("pubServiceImpl")
    public class PubServiceImpl {
    
    	@Autowired
    	private StringRedisTemplate stringRedisTemplate;
    
    	@Autowired
    	private ChannelTopic channelTopic;
    
    	public void publish(String message) {
    		stringRedisTemplate.convertAndSend(channelTopic.getTopic(), message);
    	}
    }
    
    
    
    package com.jumei.shippingfee.core.redis;
    
    import com.jumei.shippingfee.service.ShippingFeePublishService;
    import com.jumei.shippingfee.utils.DateUtils;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.redis.connection.Message;
    import org.springframework.data.redis.connection.MessageListener;
    import org.springframework.data.redis.listener.ChannelTopic;
    import org.springframework.stereotype.Component;
    
    import java.util.Date;
    
    /**
     * create by jiangsongy on 2019/1/5
     */
    @Component
    public class SubServiceImpl implements MessageListener {
    	private static final Logger log = LoggerFactory.getLogger(SubServiceImpl.class);
    
    	@Autowired
    	private ChannelTopic channelTopic;
    
    	@Autowired
    	private ShippingFeePublishService shippingFeePublishService;
    
    	private MessageRecordContainer messageContainer = new MessageRecordContainer();
    
    	@Override
    	public void onMessage(Message message, byte[] pattern) {
    		log.info("redis subscribe {} ,receive message : {}",channelTopic.getTopic(),message.toString());
    		String dateStr = DateUtils.formatDate(new Date(), DateUtils.LONG_DATE_FORMAT_STR);
    		messageContainer.put(dateStr,message.toString());
    
    		shippingFeePublishService.refreshDrools();
    	}
    
    	public MessageRecordContainer getMessageContainer() {
    		return messageContainer;
    	}
    }curator