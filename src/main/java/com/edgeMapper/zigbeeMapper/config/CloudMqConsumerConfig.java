package com.edgeMapper.zigbeeMapper.config;

import com.edgeMapper.zigbeeMapper.mqtt.CloudMqConsumer;
import lombok.Data;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Created by rongshuai on 2020/8/16 13:18
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "rocketmq.consumer")
public class CloudMqConsumerConfig {
    public static final Logger LOGGER = LoggerFactory.getLogger(CloudMqConsumerConfig.class);

    private String groupName;
    private String namesrvAddr;
    private String topics;
    // 消费者线程数据量
    private Integer consumeThreadMin;
    private Integer consumeThreadMax;
    private Integer consumeMessageBatchMaxSize;

    @Autowired
    private CloudMqConsumer cloudMqConsumer;

    /**
     * mq 消费者配置
     * @return
     * @throws MQClientException
     */

    @Bean
    public DefaultMQPushConsumer defaultConsumer() throws MQClientException {
        LOGGER.info("defaultConsumer 正在创建---------------------------------------");
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumeThreadMin(consumeThreadMin);
        consumer.setConsumeThreadMax(consumeThreadMax);
        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
        // 设置监听
        consumer.registerMessageListener(cloudMqConsumer);

        /**
         * 设置consumer第一次启动是从队列头部开始还是队列尾部开始
         * 如果不是第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        /**
         * 设置消费模型，集群还是广播，默认为集群
         */
//        consumer.setMessageModel(MessageModel.CLUSTERING);

        try {
            // 设置该消费者订阅的主题和tag，如果订阅该主题下的所有tag，则使用*,
            String[] topicArr = topics.split(";");
            for (String tag : topicArr) {
                String[] tagArr = tag.split(",");
                consumer.subscribe(tagArr[0], tagArr[1]);
            }
            consumer.start();
            LOGGER.info("consumer 创建成功 groupName={}, topics={}, namesrvAddr={}",groupName,topics,namesrvAddr);
        } catch (MQClientException e) {
            LOGGER.error("consumer 创建失败!");
        }
        return consumer;
    }
}
