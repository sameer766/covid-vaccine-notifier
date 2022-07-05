package com.sameer.coviddatafetcher.kafka.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.sameer.coviddatafetcher.util.Util.KAFKA_TOPIC;

@Configuration
public class KafkaTopicConfig {

    @Bean
   public NewTopic newTopic(){
       return TopicBuilder.name(KAFKA_TOPIC)
               .build();
   }
}
