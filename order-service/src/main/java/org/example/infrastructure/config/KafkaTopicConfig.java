package org.example.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.example.integration.kafka.KafkaTopics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderSentForApprovalTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_SENT_FOR_APPROVAL)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderApprovalResultTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_APPROVAL_RESULT)
                .partitions(1)
                .replicas(1)
                .build();
    }
}