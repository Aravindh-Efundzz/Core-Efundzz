package com.efundzz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.Map;

//@Entity
//@Table(name = "loans")
@Data
//@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Loan {

  //  @Id
    //@GenericGenerator(name = "loan_id_gen", strategy = "com.efundzz.utils.LoanIdGenerator")
    //@GeneratedValue(generator = "loan_id_gen")
    //@Column(name = "loan_id")
    private String id;

//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "custom_business_key_generator")
//    @SequenceGenerator(name = "custom_business_key_generator", sequenceName = "custom_business_key_seq", allocationSize = 1)
//    private Long customBusinessKeySequence;
//
//    @Column(unique = true)
//    private String customBusinessKey;

    //@Column(name = "user_id")
    private String userId;

    // Other loan details

    //@Column(name = "status")
    private String status;

    //@Column(name = "process_instance_id")
    private String processInstanceId;

   // @Column(name = "loan_type")
    private String loanType;

    //@Column(name = "loan_sub_type")
    private String loanSubType;

   // @Column(name = "brand")
    private String brand;

    //@Column(name = "params", columnDefinition = "jsonb")
    //@Type(type = "jsonb")
    private Map<String, Object> params;

    //@Column(name = "referral_number")
    private String referralNumber;

    private String subBrand;

   // @Column(name = "created_at")
    private LocalDateTime createdAt;
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
    // Getters and setters
}
