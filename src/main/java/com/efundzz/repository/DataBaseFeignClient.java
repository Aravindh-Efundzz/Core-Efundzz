package com.efundzz.repository;

import com.efundzz.DTO.CRMAppliacationResponseDTO;
import com.efundzz.entity.Loan;
import com.efundzz.entity.PartnerEntity;
import com.efundzz.entity.Reference;
import com.efundzz.entity.StepData;
import com.efundzz.enums.TypeOfLoan;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "db-service", url = "http://localhost:8090")
public interface DataBaseFeignClient {
    @GetMapping("/api/database/getUserLoans")
    ResponseEntity<List<Loan>> getUserLoans(@RequestParam String userId,
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
    void saveStepData(@RequestBody StepData stepData);
    @GetMapping("/api/database/getPartnerEntities")
    ResponseEntity<List<PartnerEntity>> getAllPartnerEntites();
    @GetMapping("/api/database/getAggregatedLoans/{userId}/{brand}")
    ResponseEntity<List<Loan>> getAllLoansByUserIdAndBrand(@PathVariable String userId, @RequestParam String brand);
    @GetMapping("/api/database/getStepDataById/{id}")
    ResponseEntity<List<StepData>> getStepDataListByApplicationId(@PathVariable String id);
    @GetMapping("/api/database/getLoanByUserIdAndId/{id}/{userId}")
    ResponseEntity<Loan> getLoanByIdAndUserId(@PathVariable String id, @PathVariable String userId);
    @GetMapping("/api/database/getDocumentReferenceDetails/{loanType}")
    ResponseEntity<List<Reference>> getDocumentsDetailsByRefKey1AndRefKey2(@PathVariable TypeOfLoan loanType);
    @GetMapping("/api/database/getAllData")
    ResponseEntity<List<CRMAppliacationResponseDTO>> getAllStepDataWithLoanData();
    @GetMapping("/api/database/getLoansByUserId")
    ResponseEntity<List<Loan>> getLoandByUserId(@PathVariable String uid);
}
