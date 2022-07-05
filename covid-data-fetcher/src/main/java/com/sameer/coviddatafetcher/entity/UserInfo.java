package com.sameer.coviddatafetcher.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserInfo implements Serializable {
    @GeneratedValue
    @javax.persistence.Id
    private final Integer Id = null;
    @Column(name="pincode")
    private String pincode;
    @Column(name="phone_number")
    private String userPhoneNumber;
    @Column(name="email",unique = true)
    private String userEmail;
    @Column(name="name")
    private String userName;
    @Column(name="age")
    private Integer age;

}
