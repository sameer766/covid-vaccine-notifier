package com.sameer.emailservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailRequest {
  @JsonProperty
  private int requestId;
  @JsonProperty
  private final String userName;
  @JsonProperty
  private final String userEmail;
  @JsonProperty
  private final String message;

  public EmailRequest(String userName, String userEmail, String message) {
    this.userName = userName;
    this.userEmail = userEmail;
    this.message = message;
  }

  public int getRequestId() {
    return requestId;
  }

  public String getUserEmail() {
    return userEmail;
  }


  public String getMessage() {
    return message;
  }


  public String getUserName() {
    return userName;
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
