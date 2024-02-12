package com.efundzz.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Offer {

    private Long id;

    private String logo;

    private String amount;

    private String interestRate;

    private String tenure;

    private String processingFee;

    private Integer probability;

    private String emi;

    private String name;

    private String utm_link;

    private String loanProcess;
}
