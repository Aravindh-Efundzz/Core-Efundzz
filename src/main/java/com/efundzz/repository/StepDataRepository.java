package com.efundzz.repository;//package com.efundzz.repository;
//
//import com.efundzz.entity.StepData;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface StepDataRepository  extends JpaRepository<StepData, Long> {
//    List<StepData> findByApplicationId(String id);
//
//    StepData findTopByApplicationIdAndStepName(String id, String stepName);
//
//    @Query("SELECT s FROM StepData s WHERE s.stepName IN (:stepNames) AND s.applicationId = :id")
//    List<StepData> getStepsByStepName(@Param("id") String id, @Param("stepNames") List<String> stepNames);
//}
