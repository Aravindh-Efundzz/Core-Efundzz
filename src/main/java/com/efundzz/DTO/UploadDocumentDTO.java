package com.efundzz.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadDocumentDTO {
    private String docType;
    private String docTitle;
    private boolean isMandatory;
    private Integer order;
}
