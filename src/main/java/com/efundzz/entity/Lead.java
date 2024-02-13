package com.efundzz.entity;

import com.efundzz.constants.LeadSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

//@Entity
//@Table(name = "leads")
@Data
//@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Lead  {
    private String Id;
    private String name;
    private String mobileNumber;
    private String emailId;
    private String typeOfLoan;
    private String city;
    private String currentPinCode;
    private Map<String, String> utmParameters;
    private Date createdDT;
    private String brand;
    private Map<String, String> additionalParameters;
    private LeadSource source;
    private String orgId;

}

