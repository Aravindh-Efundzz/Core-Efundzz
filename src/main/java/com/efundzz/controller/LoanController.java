package com.efundzz.controller;

import com.efundzz.DTO.*;
import com.efundzz.enums.TypeOfLoan;
import com.efundzz.servcie.LoanService;
import com.efundzz.servcie.S3Service;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/loan")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;
    private final S3Service s3Service;
    @PostMapping("/application/start/{loanType}")
    public ResponseEntity<TaskDTO> startLoanProcess(Authentication authentication,
                                                    @PathVariable String loanType,
                                                    @RequestParam(defaultValue = "") String loanSubType,
                                                    @RequestBody(required = false) Map<String, Object> params,
                                                    @RequestParam(name = "referralNumber",required = false,defaultValue = "") String referralNumber,
                                                    @RequestParam(name = "subBrand", required = false, defaultValue = "") String subBrand,
                                                    @RequestHeader(value = "X-Brand", required = false, defaultValue = "EF") String brand) {
        return loanService.startLoanProcess(authentication, loanType, loanSubType, params, referralNumber, subBrand, brand);
    }
   @PostMapping("/application/{id}/complete/{taskKey}")
    public ResponseEntity<?> completeTask(@PathVariable String id, @PathVariable String taskKey, @RequestBody Map<String, Object> variables) {
            return ResponseEntity.ok(loanService.completeTask(id, taskKey, variables));
   }
    @GetMapping("/offers/{id}")
    public ResponseEntity<List<Offer>> getOffers(@PathVariable String id) {
        try {
            return ResponseEntity.ok(loanService.getOffers(id));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/user/applications/aggregate")
    public ResponseEntity<Map<String, Long>> getAggregatedLoans(Authentication authentication, @RequestHeader(value = "X-Brand", required = false, defaultValue = "EF") String brand) {
        return ResponseEntity.ok(loanService.getAggregatedLoans(authentication, brand));
    }
    @GetMapping("/user/applications")
    public ResponseEntity<List<Object>> getUserLoans(Authentication authentication, @RequestHeader(value = "X-Brand", required = false, defaultValue = "EF") String brand) {
        return ResponseEntity.ok(loanService.getUserLoans(authentication, brand).getBody());
    }
    @PostMapping("/application/{id}/editVariables/{taskKey}")
    public ResponseEntity<?> editData(@PathVariable String id, @PathVariable String taskKey, @RequestBody Map<String, Object> variables) {
        return ResponseEntity.ok(loanService.editData(id, taskKey, variables));
    }
    @PostMapping("/application/{id}/statusByCreditScore")
    public ResponseEntity<?> changeStatus(@PathVariable String id, @RequestBody Map<String, Object> variables){
        return ResponseEntity.ok(loanService.changeStatus(id, variables));
    }
    @GetMapping("/application/{id}/current-task")
    public ResponseEntity<?> getCurrentTask(Authentication authentication, @PathVariable String id){
        return ResponseEntity.ok(loanService.getCurrentTask(authentication, id));
    }
    @PostMapping("/application/{id}/getTaskKeys/{taskKey}")
    public ResponseEntity<TaskKeysDTO> getTaskKeys(@PathVariable String id, @PathVariable String taskKey, @RequestBody Map<String, Object> variables) {
        return ResponseEntity.ok(loanService.getTaskKeys(id, taskKey, variables).getBody());
    }
    @GetMapping("/application/{id}/details")
    public ResponseEntity<?> getLoanDetails(@PathVariable String id) {
        return ResponseEntity.ok(loanService.getLoanDetails(id));
    }
    @GetMapping("/application/{id}/steps")
    public ResponseEntity<?> getSteps(@PathVariable String id){
        return ResponseEntity.ok(loanService.getSteps(id));
    }
    @GetMapping("/application/{id}/data")
    public ResponseEntity<?> getApplicationData(Authentication authentication, @PathVariable String id){
        return ResponseEntity.ok(loanService.getApplicationData(authentication, id));
    }
    @GetMapping("/application/{id}/all-variables")
    public ResponseEntity<?> getAllVariables(Authentication authentication, @PathVariable String id){
        return ResponseEntity.ok(loanService.getAllVariables(authentication, id));
    }
    @GetMapping("/crm/applications")
    public ResponseEntity<List<CRMAppliacationResponseDTO>> getApplications() {
        return ResponseEntity.ok(loanService.getAllLoanDataWithMergedStepData());

    }
    @GetMapping("/application/close/{loanType}")
    public ResponseEntity<List<String>> closeLoan(@RequestParam(defaultValue = "") String phoneNumber) throws FirebaseAuthException {
        return ResponseEntity.ok(loanService.closeLoan(phoneNumber));
    }
    @PostMapping("/delete-all-process-instances")
    public ResponseEntity<?> deleteAllProcessInstances() {
       return ResponseEntity.ok(loanService.deleteAllProcessInstances());
    }

    @GetMapping("/get-s3-url")
    public ResponseEntity<String> getS3Url(@RequestParam(name = "operation") String operation, @RequestParam(name = "fileName") String fileName) {
        return ResponseEntity.ok(Optional.ofNullable(s3Service.getPresignedUrl(operation, fileName)).map(URL::toString).orElse(""));
    }

    @GetMapping("/{loanType}/getUploadDocDetails")
    public ResponseEntity<List<UploadDocumentDTO>> getPLUploadDocDetails(@PathVariable TypeOfLoan loanType) {
        return ResponseEntity.ok(loanService.getUploadDocDetails(loanType));
    }
    @GetMapping("/{loanType}/getNewUploadDocDetails")
    public ResponseEntity<List<UploadDocumentDTO>> getNewPLUploadDocDetails(@PathVariable TypeOfLoan loanType) {
        return ResponseEntity.ok(loanService.getUploadDocDetails1(loanType));
    }
    @GetMapping("/application/getPAN/{applicationId}")
    public Object getPAN(@PathVariable String id) {
          return ResponseEntity.ok(loanService.getPAN(id));
    }
}
