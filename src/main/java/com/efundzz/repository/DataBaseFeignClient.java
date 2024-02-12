package com.efundzz.repository;

import com.efundzz.entity.Loan;
import com.efundzz.entity.StepData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "db-service", url = "http://localhost:8090")
public interface DataBaseFeignClient {
    @GetMapping("/api/database/getUserLoans")
    ResponseEntity<List<Loan>> getuserLoans(@RequestParam String userId,
                                            @RequestParam String pending,
                                            @RequestParam String loanType,
                                            @RequestParam String brand);
    @PostMapping("/api/database/saveLoan")
    ResponseEntity<Loan> saveLoan(@RequestBody Loan loan);
    @GetMapping("/api/database/getLoan/{id}")
    ResponseEntity<Loan> getLoanById(@PathVariable String id);
    @GetMapping("/api/database/getStepData/{id}/{taskKey}")
    ResponseEntity<StepData> getStepDataByIdAndStepName(@PathVariable String id, @PathVariable String taskKey);
    @PostMapping("/api/database/saveStepData")
    ResponseEntity<StepData> saveStepData(@RequestBody StepData stepData);
}
