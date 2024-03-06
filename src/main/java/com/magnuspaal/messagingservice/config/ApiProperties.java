package com.magnuspaal.messagingservice.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

@ConfigurationProperties(prefix = "application.api")
@Getter
public class ApiProperties {
  private final List<String> allowedOrigins;

  @ConstructorBinding
  public ApiProperties(List<String> allowedOrigins) {
    this.allowedOrigins = allowedOrigins;
  }
}
