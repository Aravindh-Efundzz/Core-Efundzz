package com.efundzz.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReferenceDataDTO {
    private String name;
    private String image;
    private String id;
    private Boolean enabled;
    private Integer order;
    private String ref_key1;
    private String ref_key2;
    private String ref_key3;
    private String ref_key4;
}
