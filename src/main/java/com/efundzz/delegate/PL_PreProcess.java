package com.efundzz.delegate;

//import com.efundzz.repository.StepDataRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Named;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;

@Named("PL_PreProcess")
@Service
public class PL_PreProcess implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String businessKey = execution.getProcessBusinessKey();

        Integer creditScore = (Integer) execution.getVariable("creditScore");
        if(creditScore == 0) {
            creditScore = 700;
        }

        String birthDateString = (String) execution.getVariable("dob");
        HashMap totalExperience = (HashMap) execution.getVariable("totalExperience");
        Integer amount = (Integer) execution.getVariable("loanAmount");
        Long loanAmount = amount != null ? amount.longValue() : null;
        String residentType = (String) execution.getVariable("residentType");

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                .toFormatter();

        LocalDate birthDate = LocalDate.parse(birthDateString, formatter);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthDate, currentDate);

        int age = period.getYears();

        execution.setVariable("age", age);

        execution.setVariable("creditScore", creditScore);

        execution.setVariable("experience", (int) totalExperience.get("years") * 12 + (int) totalExperience.get("months"));

        execution.setVariable("loanAmount",loanAmount);

        execution.setVariable("residentType",residentType);

        Integer takeHomeSalaryMonthly = (Integer) execution.getVariable("takeHomeSalaryMonthly");

        execution.setVariable("takeHomeSalaryMonthly",takeHomeSalaryMonthly);


        Integer totalEmi = (Integer) execution.getVariable("totalEmi");

        Integer foir = (totalEmi * 12 * 100) / (takeHomeSalaryMonthly * 12);
        System.out.println(execution.getVariables());
        execution.setVariable("foir", foir);
    }
}
