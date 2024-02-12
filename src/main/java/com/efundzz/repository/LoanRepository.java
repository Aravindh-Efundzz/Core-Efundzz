package com.efundzz.repository;//package com.efundzz.repository;
//
//import com.efundzz.DTO.CRMAppliacationResponseDTO;
//import com.efundzz.entity.Loan;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.io.Serializable;
//import java.util.List;
//
//public interface LoanRepository extends JpaRepository<Loan, Serializable> {
//    List<Loan> findByUserId(String userId);
//
//    List<Loan> findByUserIdAndStatusAndLoanTypeAndBrand(String userId, String pending, String loanType, String brand);
//
//    List<Loan> findByIdAndUserId(String id, String userId);
//    Loan findTopByIdAndUserId(String id, String userId);
//
//    List<Loan> findByUserIdAndBrand(String userId, String brand);
//
//    Loan findTopByUserIdAndStatusAndLoanTypeAndBrand(String userId, String pending, String loanType, String brand);
//
//    @Query("SELECT new com.efundzz.DTO.CRMAppliacationResponseDTO(" +
//            "l.id, l.userId, l.status, l.loanType, s.data) " +
//            "FROM Loan l " +
//            "JOIN StepData s ON l.id = s.applicationId")
//    List<CRMAppliacationResponseDTO> findAllWithStepData();
//
//    @Query("SELECT new com.efundzz.DTO.CRMAppliacationResponseDTO(" +
//            "l.id, l.userId, l.status, l.loanType, s.data) " +
//            "FROM Loan l " +
//            "JOIN StepData s ON l.id = s.applicationId " +
//            "WHERE l.id = :applicationId")
//    List<CRMAppliacationResponseDTO> findByApplicationIdWithStepData(@Param("applicationId") String applicationId);
//
//}
