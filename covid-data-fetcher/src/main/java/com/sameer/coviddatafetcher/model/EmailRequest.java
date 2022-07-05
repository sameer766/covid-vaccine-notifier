package com.sameer.coviddatafetcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailRequest {
  @JsonProperty
  private final int requestId;
  @JsonProperty
  private final String userName;
  @JsonProperty
  private final String userEmail;
  @JsonProperty
  private final String message;


  public EmailRequest(Integer requestId, String userName, String userEmail, String message) {
    this.requestId=requestId;
    this.userName = userName;
    this.userEmail = userEmail;
    this.message = message;
  }

  public String getUserName() {
    return userName;
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
            ", userName=" + userName +
            ", userEmail='" + userEmail + '\'' +
            ", message='" + message + '\'' +
            '}';
  }
}
