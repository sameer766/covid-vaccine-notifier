package com.sameer.coviddatafetcher.cache;

import com.hazelcast.config.*;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class HazelcastConfiguration {

  public HazelcastConfiguration() {
  }

  @Bean
  public Config hazelCastConfig() {
    Config config = new Config();
    config.setInstanceName("hazelcast-instance")
        .addMapConfig(
            new MapConfig()
                .setName("configuration")
                .setEvictionConfig(new EvictionConfig().setEvictionPolicy((EvictionPolicy.LRU))
                                       .setMaxSizePolicy(MaxSizePolicy.FREE_HEAP_SIZE))
                .setTimeToLiveSeconds(-1));//  less then 0 means never expired.
    return config;
  }
}