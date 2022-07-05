package com.sameer.coviddatafetcher.pdf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PdfGenerationObject {
    private String username;
    private String pincode;
    private int age;
    private String vaccineName;
}
