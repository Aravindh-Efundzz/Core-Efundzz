package com.efundzz.DTO.Leads;

import com.efundzz.enums.TypeOfLoan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadsModel {
    private String id;
    private String name;
    private String mobileNumber;
    private String emailId;
    private TypeOfLoan typeOfLoan;
    private String city;
    private String currentPincode;
    private UtmParameters utmParameters;
    private String brand;
    private Map<String, String> additionalParameters;
}
