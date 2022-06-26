package com.sameer.coviddatafetcher.kafka.config;


import com.sameer.coviddatafetcher.kafka.CustomSerializer;
import com.sameer.coviddatafetcher.pdf.PdfGenerationObject;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    public Map<String, Object> producerConfig() {
        HashMap<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CustomSerializer.class);
        return config;
    }

    @Bean
    public ProducerFactory<String, PdfGenerationObject> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }


    @Bean
    public KafkaTemplate<String, PdfGenerationObject> kafkaTemplate(ProducerFactory<String, PdfGenerationObject> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
