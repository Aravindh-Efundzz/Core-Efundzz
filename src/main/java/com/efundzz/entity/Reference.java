package com.efundzz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
//@Table(name = "reference_data")
@Data
//@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reference {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String refKey1;

    private String refKey2;

    private String refKey3;

    private String refKey4;

    private String key;

    private String value;

    private String image;

    private String isEnabled;

    private Integer order;
}
