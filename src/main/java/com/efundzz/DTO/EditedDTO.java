
package com.efundzz.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditedDTO {
    private Map<String, Object> editedData;
    private String taskDefinitionKey;
}
