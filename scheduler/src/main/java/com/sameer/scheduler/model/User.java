package com.sameer.scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private String pincode;
    private String userPhoneNumber;
    private String userEmail;
    private String userName;
    private Integer age;
}
