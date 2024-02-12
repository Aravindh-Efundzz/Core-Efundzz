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
//    @Id
//    @GenericGenerator(name = "lead_id_gen", strategy = "com.efundzz.utils.LeadIdGenerator")
//    @GeneratedValue(generator = "lead_id_gen")
//    @Column(name = "lead_id")
    private String Id;

//    @Column(name = "name")
    private String name;

//    @Column(name = "mobile_number")
    private String mobileNumber;

//    @Column(name = "emailid")
    private String emailId;

//    @Column(name = "loan_type")
    private String typeOfLoan;

//    @Column(name = "city")
    private String city;

//    @Column(name = "pincode")
    private String currentPincode;

//    @Column(name = "utm_params")
//    @Type(type = "jsonb")
    private Map<String, String> utmParameters;

//    @Column(name = "created_dt")
//    @Temporal(TemporalType.TIMESTAMP)
//    @CreationTimestamp
    private Date createdDT;

//    @Column(name = "brand")
    private String brand;

//    @Column(name = "additional_params")
//    @Type(type = "jsonb")
    private Map<String, String> additionalParameters;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "source")
    private LeadSource source;

//    @Column(name = "org_id")
    private String orgId;

}

