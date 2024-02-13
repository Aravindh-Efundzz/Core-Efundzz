package com.efundzz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartnerEntity{

    private Long id;

    private String name;

    private String imageUrl;

    private String interest;

    private String code;

    private String utm_link;

    private String loanProcess;

}
