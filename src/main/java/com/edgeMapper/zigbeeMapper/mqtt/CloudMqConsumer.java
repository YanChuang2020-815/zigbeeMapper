package com.edgeMapper.zigbeeMapper.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.edgeMapper.zigbeeMapper.model.dto.DeviceDataDto;
import com.edgeMapper.zigbeeMapper.model.dto.DeviceDataRequestDto;
import com.edgeMapper.zigbeeMapper.util.RedisUtil;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by rongshuai on 2020/8/16 12:57
 */
@Component
public class CloudMqConsumer implements MessageListenerConcurrently {
    public static final Logger LOGGER = LoggerFactory.getLogger(CloudMqConsumer.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        if (CollectionUtils.isEmpty(list)) {
            LOGGER.info("MQ接收消息为空，直接返回成功");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt messageExt = list.get(0);
        LOGGER.info("MQ接收到的消息为：" + messageExt.toString());
        try {
            String topic = messageExt.getTopic();
            String tags = messageExt.getTags();
            String body = new String(messageExt.getBody(), "utf-8");

            LOGGER.info("MQ消息topic={}, tags={}, 消息内容={}", topic,tags,body);

            DeviceDataRequestDto dataRequestDto = JSONObject.parseObject(body,DeviceDataRequestDto.class);
            LOGGER.info("dataRequestDto is {}",dataRequestDto);

            DeviceDataDto deviceDataDto = (DeviceDataDto) redisUtil.get(dataRequestDto.getDeviceName());
            LOGGER.info("deviceDataDto is {}",deviceDataDto);

            Message msg = new Message("edgeDeviceData", JSONObject.toJSONString(deviceDataDto).getBytes());

            defaultMQProducer.send(msg);
        } catch (Exception e) {
            LOGGER.error("获取MQ消息内容异常{}",e);
        }
        // TODO 处理业务逻辑
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
