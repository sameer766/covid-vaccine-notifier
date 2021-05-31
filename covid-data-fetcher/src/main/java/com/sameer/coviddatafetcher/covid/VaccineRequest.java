package com.sameer.coviddatafetcher.covid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VaccineRequest implements Serializable {
    private String pincode;
    private String userPhoneNumber;
}
