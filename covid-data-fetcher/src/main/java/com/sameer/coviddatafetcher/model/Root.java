package com.sameer.coviddatafetcher.model;

import com.sameer.coviddatafetcher.model.Center;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  Root {
    public List<Center> centers;
}