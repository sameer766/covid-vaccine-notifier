package com.sameer.coviddatafetcher.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VaccineResponse {
    private String date;
    private String vaccine;
    private List<String> slots;
    private boolean isAvailable;
}
