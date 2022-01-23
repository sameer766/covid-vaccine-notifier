package com.sameer.coviddatafetcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailRequest {
  @JsonProperty
  private final int requestId;
  @JsonProperty
  private final String userEmail;
  @JsonProperty
  private final String message;


  public EmailRequest(Integer requestId,String userEmail, String message) {
    this.requestId=requestId;
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
        "requestId=" + requestId +
        ", userEmail='" + userEmail + '\'' +
        ", message='" + message + '\'' +
        '}';
  }
}
