package com.sameer.emailservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import lombok.Builder;

@Entity
@Table(name = "email")
@Builder
public class EmailObject {

  @GeneratedValue
  @javax.persistence.Id
  private final Integer Id;
  private final String userName;
  private final String userEmail;
  @Column(nullable = false, columnDefinition = "BOOLEAN")
  private final Boolean isEmailSent;
}
