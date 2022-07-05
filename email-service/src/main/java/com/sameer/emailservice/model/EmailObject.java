package com.sameer.emailservice.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_log")
public class EmailObject {

    @GeneratedValue
    @javax.persistence.Id
    private Integer iD;
    private String userEmail;
    @CreationTimestamp
    @Column(name = "dateCreated")
    private LocalDateTime dateCreated;
    @UpdateTimestamp
    @Column(name = "dateModified")
    private LocalDateTime dateModified;
    @Column(nullable = false, columnDefinition = "BOOLEAN")
    private Boolean isEmailSent;

    public EmailObject() {
    }

    public Integer getiD() {
        return iD;
    }

    public void setiD(Integer iD) {
        this.iD = iD;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified = dateModified;
    }

    public Boolean getEmailSent() {
        return isEmailSent;
    }

    public void setEmailSent(Boolean emailSent) {
        isEmailSent = emailSent;
    }
}
