package com.efundzz.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.util.ObjectUtils.isEmpty;

@Named("SendEmail")
@Service
public class SendEmail implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String businessKey = execution.getProcessBusinessKey();

            Map<String, Object> data = new HashMap<>();
            String sendTo = (String) execution.getVariable("email");
            String fullName = Optional.ofNullable(execution.getVariable("fullName"))
                    .orElse("").toString();
            String nameOfTheCard = Optional.ofNullable(
                    execution.getVariable("nameOfTheCard")).orElse("").toString();

            data.put("referenceNumber", businessKey);
            data.put("fullName", isEmpty(fullName) ? nameOfTheCard : fullName);
    }
}
