package com.sameer.coviddatafetcher.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "cowin_static_data")
@Getter
@Setter
public class PincodeDetails implements Serializable {
    public PincodeDetails(String pincode, boolean isScheduled, String vaccineDetails) {
        this.pincode = pincode;
        this.isScheduled = isScheduled;
        this.vaccineDetails = vaccineDetails;
    }

    public PincodeDetails() {
    }

    @GeneratedValue
    @javax.persistence.Id
    private final Integer Id = null;
    @Column(name="pincode")
    private String pincode;
    @CreationTimestamp
    @Column(name="dateCreated")
    private LocalDateTime dateCreated ;
    @UpdateTimestamp
    @Column(name="dateModified")
    private LocalDateTime dateModified;
    @Column(name="isScheduled")
    private boolean isScheduled;
    @Column(name="vaccineDetails")
    private String vaccineDetails;
}
