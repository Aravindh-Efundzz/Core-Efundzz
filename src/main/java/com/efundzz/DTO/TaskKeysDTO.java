package com.efundzz.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskKeysDTO {
    private String prevTaskDefinitionKey;
    private String currentTaskDefinitionKey;
    private String nextTaskDefinitionKey;
}
