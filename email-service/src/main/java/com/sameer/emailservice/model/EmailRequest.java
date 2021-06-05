package com.sameer.emailservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailRequest {
  @JsonProperty
  private final String userEmail;
  @JsonProperty
  private final String message;

  public EmailRequest(String userEmail, String message) {
    this.userEmail = userEmail;
    this.message = message;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "EmailRequest{" +
        "email='" + userEmail + '\'' +
        ", message='" + message + '\'' +
        '}';
  }
}
