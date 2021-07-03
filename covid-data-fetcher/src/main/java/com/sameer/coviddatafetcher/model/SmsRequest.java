package com.sameer.coviddatafetcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SmsRequest {
  @JsonProperty
  private final int requestId;
  @JsonProperty
  private final String phoneNumber;
  @JsonProperty
  private final String message;

  public SmsRequest(Integer requestId,String phoneNumber, String message) {
    this.phoneNumber = phoneNumber;
    this.message = message;
    this.requestId= requestId;
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
