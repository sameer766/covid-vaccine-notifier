package com.sameer.coviddatafetcher.queue;


import com.sameer.coviddatafetcher.model.VaccineRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueRequest {
    private Integer requestId;
    private VaccineRequest vaccineRequest;
    private String message;
    private String source;
}
