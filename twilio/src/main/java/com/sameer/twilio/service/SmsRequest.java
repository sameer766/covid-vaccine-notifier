package com.sameer.twilio.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SmsRequest {
  @JsonProperty
  private int requestId;
  @JsonProperty
  private final String phoneNumber;
  @JsonProperty
  private final String message;

  public SmsRequest(String phoneNumber, String message) {
    this.phoneNumber = phoneNumber;
    this.message = message;
  }

  public int getRequestId() {
    return requestId;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "SmsRequest{" +
            "phoneNumber='" + phoneNumber + '\'' +
            ", message='" + message + '\'' +
            '}';
  }
}
