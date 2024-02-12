package com.efundzz.controller;//package com.efundzz.controller;
//
//import com.efundzz.DTO.ReferenceDataDTO;
//import com.efundzz.enums.RefKey;
//import com.efundzz.enums.TypeOfLoan;
//import com.efundzz.servcie.ReferenceDataService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Comparator;
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/reference")
//public class ReferenceController {
//    private final ReferenceDataService referenceDataService;
//
//    @GetMapping("/getByRefKey/{refKey}")
//    public ResponseEntity<List<ReferenceDataDTO>> getBusinessLoanSubType(@PathVariable String refKey) {
//        try {
//            return ResponseEntity.ok(referenceDataService.findDataByRefKey1(refKey));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    @GetMapping("/loanSubType/BusinessLoan")
//    public ResponseEntity<List<ReferenceDataDTO>> getBusinessLoanSubType() {
//        try {
//            return ResponseEntity
//                    .ok(referenceDataService.findDataByRefKey1AndRefKey2(TypeOfLoan.BUSINESS_LOAN.getId(), RefKey.LOAN_SUB_TYPE.name()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    @GetMapping("/PersonalLoan/EmploymentTypes")
//    public ResponseEntity<List<ReferenceDataDTO>> getPersonalLoanEmploymentTypes() {
//        try {
//            return ResponseEntity
//                    .ok(referenceDataService.findDataByRefKey1AndRefKey2(TypeOfLoan.PERSONAL_LOAN.getId(), RefKey.EMPLOYMENT_TYPE.name()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    @GetMapping("/products")
//    public ResponseEntity<List<ReferenceDataDTO>> getProducts() {
//        try {
//            List<ReferenceDataDTO> products = referenceDataService.findDataByRefKey1(RefKey.PRODUCT.name());
//            products.sort(Comparator.comparing(ReferenceDataDTO::getOrder));
//            return ResponseEntity.ok(products);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//}
