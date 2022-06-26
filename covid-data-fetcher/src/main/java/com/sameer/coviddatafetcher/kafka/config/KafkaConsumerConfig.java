package com.sameer.coviddatafetcher.kafka.config;


import com.sameer.coviddatafetcher.kafka.CustomDeserializer;
import com.sameer.coviddatafetcher.pdf.PdfGenerationObject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    public Map<String, Object> consumerConfig() {
        HashMap<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group-id");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomDeserializer.class);
        return config;
    }

    @Bean
    public ConsumerFactory<String, PdfGenerationObject> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }


    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, PdfGenerationObject>> factory() {
        ConcurrentKafkaListenerContainerFactory<String, PdfGenerationObject> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
