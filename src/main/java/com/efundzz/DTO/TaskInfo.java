package com.efundzz.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskInfo {
    private String id;
    private String name;
    private int stepNumber;
    private String status;
}
