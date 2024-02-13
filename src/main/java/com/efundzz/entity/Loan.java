package com.efundzz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Loan {

    private String id;
    private String userId;
    private String status;
    private String processInstanceId;
    private String loanType;
    private String loanSubType;
    private String brand;
    private Map<String, Object> params;
    private String referralNumber;
    private String subBrand;
    private LocalDateTime createdAt;
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
    // Getters and setters
}
