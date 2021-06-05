package com.sameer.coviddatafetcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailRequest {
  @JsonProperty
  private final String userEmail;
  @JsonProperty
  private final String message;
  @JsonProperty
  private final String userName;

  public EmailRequest(String userEmail, String message, String userName) {
    this.userEmail = userEmail;
    this.message = message;
    this.userName = userName;
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
        "userEmail='" + userEmail + '\'' +
        ", message='" + message + '\'' +
        ", userName='" + userName + '\'' +
        '}';
  }
}
