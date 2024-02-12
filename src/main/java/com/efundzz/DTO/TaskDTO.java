package com.efundzz.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private String id;
    private String name;
    private String assignee;
    private String processInstanceId;
    private String processDefinitionId;
    private String processDefinitionName;
    private String processDefinitionKey;
    private String taskName;
    private String taskId;
    private String prevTaskDefinitionKey;
    private String taskDefinitionKey;
    private String nextTaskDefinitionKey;
    private Map<String, Object> params;
    private String loanType;
    private String loanSubType;
    private String referralNumber;
    private String subBrand;
}
