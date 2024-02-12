package com.efundzz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
//@Table(name = "partner_entities")
@Data
//@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerEntity{

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String imageUrl;

    private String interest;

    private String code;

    private String utm_link;

    private String loanProcess;

}
