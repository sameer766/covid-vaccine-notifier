package com.sameer.emailservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_sent")
@Builder
public class EmailObject {

  @GeneratedValue
  @javax.persistence.Id
  private final Integer iD;
  private final String userEmail;
  @CreationTimestamp
  @Column(name="dateCreated")
  private LocalDateTime dateCreated ;
  @UpdateTimestamp
  @Column(name="dateModified")
  private LocalDateTime dateModified;
  @Column(nullable = false, columnDefinition = "BOOLEAN")
  private final Boolean isEmailSent;
}
