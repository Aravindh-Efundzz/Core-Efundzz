package com.efundzz.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

//@Entity
//@Builder
//@Table(name = "step_data")
//@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StepData {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(name = "application_id", nullable = false)
    private String applicationId;

    //@Column(name = "step_name", nullable = false)
    private String stepName;

//    @Column(name = "data", columnDefinition = "jsonb")
//    @Type(type = "jsonb")
    private Map<String, Object> data;

//    @Column(name = "created_dt")
//    @Temporal(TemporalType.TIMESTAMP)
//    @CreationTimestamp
//    private Date createdDT;
}
