package com.efundzz.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StepData {

    private Long id;
    private String applicationId;
    private String stepName;
    private Map<String, Object> data;
   private Date createdDT;
}
