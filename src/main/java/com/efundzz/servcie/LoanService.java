package com.efundzz.servcie;

import com.efundzz.DTO.*;
import com.efundzz.config.FirebaseConfig;
import com.efundzz.entity.Loan;
import com.efundzz.entity.PartnerEntity;
import com.efundzz.entity.Reference;
import com.efundzz.entity.StepData;
import com.efundzz.enums.Month;
import com.efundzz.enums.TypeOfLoan;
import com.efundzz.repository.*;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import static com.efundzz.utils.CommonVariables.*;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final TaskService taskService;
    private final DataBaseFeignClient dataBaseFeignClient;
    private final RepositoryService repositoryService;

    private static final Logger log = LoggerFactory.getLogger(LoanService.class);
    List<String> validBrands = Arrays.asList("EF", "BM", "HL", "IV", "KH", "NX", "RB", "RL", "VH", "WD", "YH","SQ","MH", "IM", "AZ");
    List<String> stepNames = Arrays.asList("NewLoanDetails","LoanDetails","OneTapLoanDetails", "CredilioDetails","LoanAgainstMutualFundsInfo");

    public ResponseEntity<TaskDTO>startLoanProcess(Authentication authentication, String loanType, String loanSubType, Map<String, Object> params, String referralNumber, String subBrand, String brand) {
        try {
            String userId = authentication.getName();
            if (!validBrands.contains(brand)) {
                return ResponseEntity.badRequest().body(new TaskDTO());
            }
            ResponseEntity<List<Loan>> responseEntity = dataBaseFeignClient.getUserLoans(userId, "DETAILS PENDING", loanType, brand);
            List<Loan> userLoans = responseEntity.getBody();
            if (!Objects.requireNonNull(userLoans).isEmpty()) {
                Loan loan = userLoans.get(0);
                HistoricProcessInstance historicProcessInstance = historyService
                        .createHistoricProcessInstanceQuery()
                        .processInstanceId(loan.getProcessInstanceId())
                        .singleResult();

                // If the process instance is still active, return the first task
                if (historicProcessInstance != null && historicProcessInstance.getEndTime() == null) {
                    HistoricTaskInstance historicTaskInstance = historyService
                            .createHistoricTaskInstanceQuery()
                            .processInstanceId(loan.getProcessInstanceId())
                            .unfinished()
                            .singleResult();

                    TaskDTO taskDTO = new TaskDTO();
                    taskDTO.setId(loan.getId());
                    taskDTO.setTaskId(historicTaskInstance.getId());
                    taskDTO.setTaskName(historicTaskInstance.getName());
                    taskDTO.setTaskDefinitionKey(historicTaskInstance.getTaskDefinitionKey());
                    taskDTO.setLoanType(loan.getLoanType());
                    taskDTO.setLoanSubType(loan.getLoanSubType());
                    taskDTO.setReferralNumber(loan.getReferralNumber());
                    taskDTO.setSubBrand(loan.getSubBrand());
                    taskDTO.setProcessInstanceId(loan.getProcessInstanceId());
                    taskDTO.setParams(loan.getParams());
                    return ResponseEntity.ok(taskDTO);
                }
            } else {
                // If there are no pending loans, create a new loan and start a new process instance with BusinessKey = loanId
                Loan loan = new Loan();
                loan.setUserId(userId);
                loan.setStatus("DETAILS PENDING");
                loan.setLoanSubType(loanSubType);
                loan.setLoanType(loanType);
                loan.setParams(params);
                loan.setBrand(brand);
                loan.setReferralNumber(referralNumber);
                loan.setSubBrand(subBrand);
                ResponseEntity<Loan> savedLoanResponse = dataBaseFeignClient.saveLoan(loan);
                if (savedLoanResponse.getStatusCode().is2xxSuccessful()) {
                    Loan savedLoan = savedLoanResponse.getBody();
                    String generatedId = Objects.requireNonNull(savedLoan).getId();
                    loan.setId(generatedId);
                    StringBuilder processKey = new StringBuilder();
                    processKey.append(loanType);
                    if (!loanSubType.isEmpty()) {
                        processKey.append("_");
                        processKey.append(loanSubType);
                    }

                    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey.toString(), loan.getId());

                    loan.setProcessInstanceId(processInstance.getId());

                    dataBaseFeignClient.saveLoan(loan);

                    // Get the first user task
                    Task task = taskService.createTaskQuery()
                            .processInstanceId(processInstance.getId())
                            .singleResult();
                    return ResponseEntity.ok(taskToTaskDTO(task, loan));
                } else {
                    return ResponseEntity.status(savedLoanResponse.getStatusCode()).build();
                }
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
            return null;

    }
    private TaskDTO taskToTaskDTO(Task task, Loan loan) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskId(task.getId());
        taskDTO.setTaskName(task.getName());
        taskDTO.setTaskDefinitionKey(task.getTaskDefinitionKey());
        taskDTO.setProcessInstanceId(task.getProcessInstanceId());
        taskDTO.setId(loan.getId());
        taskDTO.setParams(loan.getParams());
        taskDTO.setLoanType(loan.getLoanType());
        taskDTO.setLoanSubType(loan.getLoanSubType());
        taskDTO.setReferralNumber(loan.getReferralNumber());
        String processDefinitionId = task.getProcessDefinitionId();
        String currentTaskDefinitionKey =  task.getTaskDefinitionKey();
        taskDTO.setPrevTaskDefinitionKey(getPreviousTaskDefinitionKey(processDefinitionId, currentTaskDefinitionKey));
        taskDTO.setNextTaskDefinitionKey(getNextTaskDefinitionKey(processDefinitionId, currentTaskDefinitionKey));
        return taskDTO;
    }
    public ResponseEntity<?> completeTask(String id, String taskKey, Map<String, Object> variables) {
        Loan loan = dataBaseFeignClient.getLoanById(id).getBody();
            ProcessInstance retrievedProcessInstance =runtimeService
                    .createProcessInstanceQuery().processInstanceBusinessKey(id)
                    .singleResult();
            if (retrievedProcessInstance == null) {
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Task task ;
            if (retrievedProcessInstance != null) {
                 task = taskService.createTaskQuery()
                        .processInstanceId(retrievedProcessInstance.getId())
                        .taskDefinitionKey(taskKey)
                        .singleResult();
            } else {
                 task = taskService.createTaskQuery()
                        .processInstanceId(Objects.requireNonNull(loan).getProcessInstanceId())
                        .taskDefinitionKey(taskKey)
                        .singleResult();
            }

            if (task == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
                StepData stepData = new StepData();
                stepData.setStepName(taskKey);
                stepData.setApplicationId(id);
                stepData.setData(variables);
                dataBaseFeignClient.saveStepData(stepData);

            taskService.complete(task.getId(), variables);
            if (taskKey.equals("ReviewOffers")) {
                Objects.requireNonNull(loan).setStatus("DETAILS RECEIVED");
                dataBaseFeignClient.saveLoan(loan);
                return ResponseEntity.ok(loan);
            }
            ProcessInstance updatedProcessInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(Objects.requireNonNull(retrievedProcessInstance).getId()).singleResult();
            if (updatedProcessInstance != null) {
                Task nextTask = taskService.createTaskQuery()
                        .processInstanceId(retrievedProcessInstance.getId())
                        .singleResult();
                if (nextTask != null){
                    return ResponseEntity.ok(taskToTaskDTO(task, Objects.requireNonNull(loan)));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } else {
                HistoricVariableInstance resultVariable = historyService.createHistoricVariableInstanceQuery()
                        .processInstanceId(retrievedProcessInstance.getId())
                        .variableName("result")
                        .singleResult();
                if (resultVariable != null) {
                    Object result =resultVariable.getValue();
                    return ResponseEntity.ok(Collections.singletonMap("result", result));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
    }

    public List<Offer> getOffers(String id) {
        List<String> offers;
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(id)
                .singleResult();
        List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(historicProcessInstance.getId())
                .list();
        Map<String, Object> variables = historicVariableInstances.stream()
                .filter(hvi -> hvi.getValue() != null)
                .collect(Collectors.toMap(HistoricVariableInstance::getName, HistoricVariableInstance::getValue, (oldValue, newValue) -> oldValue));
        if (id.contains("HL") || id.contains("CC") || id.contains("GL") || id.contains("AL") || id.contains("ML") || id.contains("LP")) {
            offers = new ArrayList<>();
            offers.add("WERIZE");
            offers.add("MYSHUBLIFE");
            offers.add("IDFC");
            offers.add("CASHE");
            offers.add("SBI");
            offers.add("BOB");
        } else {
            offers = ((List<Map<String, String>>) Optional.ofNullable(variables.get("offers"))
                    .orElse(Collections.emptyList())).stream()
                    .flatMap(entity -> Optional.ofNullable(entity.get("entity")).stream())
                    .collect(Collectors.toList());
        }

        List<PartnerEntity> partnerEntities = dataBaseFeignClient.getAllPartnerEntites().getBody();

        List<String> finalOffers = offers;
        finalOffers.replaceAll(String::toUpperCase);
        List<PartnerEntity> filteredOffers = Objects.requireNonNull(partnerEntities).stream()
                .filter(partnerEntity -> finalOffers.contains(partnerEntity.getCode()))
                .toList();


        return filteredOffers.stream()
                .map(partnerEntity -> {
                    Offer offer = new Offer();
                    offer.setId(partnerEntity.getId());
                    offer.setInterestRate(partnerEntity.getInterest());
                    offer.setName(partnerEntity.getName());
                    offer.setLogo(partnerEntity.getImageUrl());
                    offer.setAmount(variables.get("loanAmount").toString());
                    offer.setEmi("");
                    offer.setProbability(80);
                    offer.setEmi("");
                    offer.setProcessingFee("");
                    offer.setLoanProcess(partnerEntity.getLoanProcess());
                    offer.setUtm_link(partnerEntity.getUtm_link());
                    return offer;
                })
                .collect(Collectors.toList());
    }
    public Map<String, Long> getAggregatedLoans(Authentication authentication, String brand) {
        String userId = authentication.getName();
        List<Loan> loans = dataBaseFeignClient.getAllLoansByUserIdAndBrand(userId, brand).getBody();
        long submittedCount = Objects.requireNonNull(loans).stream()
                .filter(loan -> loan.getStatus().equals("DETAILS RECEIVED"))
                .count();
        long draftCount = loans.stream()
                .filter(loan -> loan.getStatus().equals("DETAILS PENDING"))
                .count();
        Map<String, Long> counts = new HashMap<>();
        counts.put("Submitted", submittedCount);
        counts.put("Draft", draftCount);
        counts.put("Evaluation", 0L);
        counts.put("Approval", 0L);
        counts.put("Disbursed", 0L);
        return counts;
    }
    public ResponseEntity<List<Object>> getUserLoans(Authentication authentication, String brand) {
        String userId = authentication.getName();
        if (!validBrands.contains(brand)) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }

        List<Loan> loans = dataBaseFeignClient.getAllLoansByUserIdAndBrand(userId, brand).getBody();
        List<Object> userLoans = new ArrayList<>();

        for (Loan loan : Objects.requireNonNull(loans)) {
            List<StepData> stepDataList = dataBaseFeignClient.getStepDataListByApplicationId(loan.getId()).getBody();
            StepData stepDataResponse = null;
            if (!Objects.requireNonNull(stepDataList).isEmpty()) {
                for (String step : stepNames) {
                    Optional<StepData> loanDetailsStepData = stepDataList.stream()
                            .filter(stepData -> step.equals(stepData.getStepName()))
                            .findFirst();

                    if (loanDetailsStepData.isPresent()) {
                        stepDataResponse = loanDetailsStepData.get();
                        break; // Break the loop once stepData is found
                    }
                }
            }

            // Construct loanMap with loan information
            Map<String, Object> loanMap = new HashMap<>();
            loanMap.put("id", loan.getId());
            loanMap.put("status", loan.getStatus());
            loanMap.put("createdAt", loan.getCreatedAt());
            loanMap.put("type", loan.getLoanType());
            loanMap.put("loanSubtype", loan.getLoanSubType());
            loanMap.put("referralNumber",loan.getReferralNumber());
            loanMap.put("params", loan.getParams());
            if (stepDataResponse != null && stepDataResponse.getData().containsKey("loanAmount")) {
                String loanAmount = stepDataResponse.getData().get("loanAmount").toString();
                loanMap.put("loanAmount", loanAmount);
            } else {
                loanMap.put("loanAmount", "");
            }

            userLoans.add(loanMap);
        }

        return ResponseEntity.ok(userLoans);
    }
    public EditedDTO editData(String id, String taskKey, Map<String, Object> variables) {
        String processInstanceId = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(id)
                .singleResult()
                .getId();
        StepData existingStepData = dataBaseFeignClient.getStepDataByIdAndStepName(id, taskKey).getBody();
        if (existingStepData != null) {
            Map<String, Object> updatingData = existingStepData.getData();
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String fieldName = entry.getKey();
                Object updatedValue = entry.getValue();
                if (updatingData.containsKey(fieldName)) {
                    updatingData.put(fieldName, updatedValue);
                }
            }
            existingStepData.setData(updatingData);
            dataBaseFeignClient.saveStepData(existingStepData);
            runtimeService.setVariables(processInstanceId, updatingData);
        } else {
            return null;
        }

        EditedDTO editedDTO = new EditedDTO();
        editedDTO.setEditedData(variables);
        ProcessInstance retreivedProcessInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(id)
                .singleResult();
        editedDTO.setTaskDefinitionKey(getNextTaskDefinitionKey(retreivedProcessInstance.getProcessDefinitionId(), taskKey));

        return editedDTO;
    }
    public Loan changeStatus(String id, Map<String, Object> variables) {
        Loan loan = dataBaseFeignClient.getLoanById(id).getBody();
        Integer creditScore = (Integer) variables.get("creditScore");
        if (creditScore >= 1 && creditScore <= 550) {
            Objects.requireNonNull(loan).setStatus("REJECTED");
            StepData stepData = new StepData();
            stepData.setStepName("CreditScore");
            stepData.setData(variables);
            stepData.setApplicationId(id);
            dataBaseFeignClient.saveStepData(stepData);
        }
        return loan;
    }
    public ResponseEntity<TaskDTO> getCurrentTask(Authentication authentication, String id){
        String userId = authentication.getName();
        Loan loan = dataBaseFeignClient.getLoanByIdAndUserId(id, userId).getBody();

        if(loan == null){
            return new ResponseEntity<>(new TaskDTO(), HttpStatus.UNAUTHORIZED);
        }
        ProcessInstance retrievedProcessInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceBusinessKey(id).singleResult();


        if (retrievedProcessInstance == null) {
            return ResponseEntity.notFound().build();
        }

        Task currentTask = taskService.createTaskQuery()
                .processInstanceId(retrievedProcessInstance.getId())
                .singleResult();

        if (currentTask == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(taskToTaskDTO(currentTask, loan));
    }
    public String getPreviousTaskDefinitionKey(String processDefinitionId, String currentTaskDefinitionKey) {
        BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
        UserTask currentTask = modelInstance.getModelElementById(currentTaskDefinitionKey);
        Collection<SequenceFlow> incomingFLows = findIncomingSequenceFlows(modelInstance, currentTask);
        if (!incomingFLows.isEmpty()) {
            FlowNode sourceElement = incomingFLows.iterator().next().getSource();
            if (sourceElement != null) {
                return sourceElement.getId();
            }
        }
        return null;
    }
    private Collection<SequenceFlow> findIncomingSequenceFlows(BpmnModelInstance modelInstance, UserTask userTask) {
        return modelInstance.getModelElementsByType(SequenceFlow.class)
                .stream()
                .filter(sequenceFlow -> sequenceFlow.getTarget().equals(userTask))
                .collect(Collectors.toList());
    }

    public String getNextTaskDefinitionKey(String processDefinitionId, String currentTaskDefinitionKey) {
        BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
        UserTask currentTask = modelInstance.getModelElementById(currentTaskDefinitionKey);
        Collection<SequenceFlow> outgoingFlows = findOutgoingSequenceFlows(modelInstance, currentTask);
        if (!outgoingFlows.isEmpty()) {
            FlowNode targetElement = outgoingFlows.iterator().next().getTarget();
            if (targetElement != null) {
                return targetElement.getId();
            }
        }
        return null;
    }
    private Collection<SequenceFlow> findOutgoingSequenceFlows(BpmnModelInstance modelInstance, UserTask userTask) {
        return modelInstance.getModelElementsByType(SequenceFlow.class)
                .stream()
                .filter(sequenceFlow -> sequenceFlow.getSource().equals(userTask))
                .collect(Collectors.toList());
    }
    public ResponseEntity<TaskKeysDTO> getTaskKeys(String id, String taskKey, Map<String, Object> variables) {
        ProcessInstance retreivedProcessInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(id)
                .singleResult();
        String processInstanceId = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(id)
                .singleResult()
                .getId();
        if (retreivedProcessInstance == null){
            return ResponseEntity.notFound().build();
        }
        StepData existingStepData = dataBaseFeignClient.getStepDataByIdAndStepName(id, taskKey).getBody();
        if (existingStepData != null) {
            Map<String, Object> updatingData = existingStepData.getData();
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String fieldName = entry.getKey();
                Object updatedValue = entry.getValue();
                if (updatingData.containsKey(fieldName)) {
                    updatingData.put(fieldName, updatedValue);
                }
            }
            existingStepData.setData(updatingData);
            dataBaseFeignClient.saveStepData(existingStepData);
            runtimeService.setVariables(processInstanceId, updatingData);
        } else {
            String processDefinitionId = retreivedProcessInstance.getProcessDefinitionId();
            TaskKeysDTO taskKeysDTO = new TaskKeysDTO();
            taskKeysDTO.setCurrentTaskDefinitionKey(taskKey);
            taskKeysDTO.setPrevTaskDefinitionKey(getPreviousTaskDefinitionKey(processDefinitionId, taskKey));
            taskKeysDTO.setNextTaskDefinitionKey(getNextTaskDefinitionKey(processDefinitionId, taskKey));
            taskKeysDTO.setEditedData(variables);
            return ResponseEntity.ok(taskKeysDTO);
        }
        String processDefinitionId = retreivedProcessInstance.getProcessDefinitionId();
        TaskKeysDTO taskKeysDTO = new TaskKeysDTO();
        taskKeysDTO.setCurrentTaskDefinitionKey(taskKey);
        taskKeysDTO.setPrevTaskDefinitionKey(getPreviousTaskDefinitionKey(processDefinitionId, taskKey));
        taskKeysDTO.setNextTaskDefinitionKey(getNextTaskDefinitionKey(processDefinitionId, taskKey));
        taskKeysDTO.setEditedData(variables);
        return ResponseEntity.ok(taskKeysDTO);
    }
    public ResponseEntity<Map<String, Object>> getLoanDetails(String id) {

        //retrieve process instance by business key
        ProcessInstance retrievedProcessInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceBusinessKey(id).singleResult();

        if (retrievedProcessInstance == null) {
            return ResponseEntity.notFound().build();
        }

        String currentTask = taskService.createTaskQuery()
                .processInstanceId(retrievedProcessInstance.getId()).singleResult().getName();


        // Fetch the variables for the process instance
        Map<String, Object> variables = runtimeService.getVariables(retrievedProcessInstance.getId());
        variables.put("currentTask", currentTask);

        return ResponseEntity.ok(variables);
    }
    public static Map<String, Object> maskData(Map<String, Object> input, Map<String, List<Integer>> maskRanges) {
        Map<String, Object> output = new HashMap<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                String maskedValue = maskString((String) value, maskRanges.getOrDefault(key, Collections.emptyList()));
                output.put(key, maskedValue);
            } else if (value instanceof Map) {
                Map<String, Object> maskedSubMap = maskData((Map<String, Object>) value, maskRanges);
                output.put(key, maskedSubMap);
            } else {
                output.put(key, value);
            }
        }
        return output;
    }

    public static String maskString(String input, List<Integer> maskRanges) {
        char[] chars = input.toCharArray();
        for (int i : maskRanges) {
            if (i >= 0 && i < chars.length) {
                chars[i] = '*';
            }
        }
        return new String(chars);
    }
    public ResponseEntity<Map<String, Object>> getAllVariables(Authentication authentication, String id) {
        // Check if the process instance exists (either running or finished)
        String userId = authentication.getName();
        List<StepData> stepDataList = dataBaseFeignClient.getStepDataListByApplicationId(id).getBody();

        // return the response  as json with each step as the key and data as the value

        Loan loan = dataBaseFeignClient.getLoanByIdAndUserId(id, userId).getBody();

        if(loan == null){
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.UNAUTHORIZED);
        }


        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(id)
                .singleResult();

        if (historicProcessInstance == null) {
            return ResponseEntity.notFound().build();
        }

        // Fetch all the historic variables for the process instance
        List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(historicProcessInstance.getId())
                .list();

        // Convert the list of historic variable instances to a map
        Map<String, Object> variables = historicVariableInstances.stream()
                .filter(hvi -> hvi.getValue() != null)
                .collect(Collectors.toMap(HistoricVariableInstance::getName, HistoricVariableInstance::getValue, (oldValue, newValue) -> oldValue));


        Map<String, List<Integer>> maskRanges = new HashMap<>();
        maskRanges.put("pan", Arrays.asList(0, 1, 2, 3, 4, 5));
        maskRanges.put("adhaar", Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));

        Map<String, Object> maskedData = maskData(variables, maskRanges);
        maskedData.put("loanType", loan.getLoanType());
        maskedData.put("loanSubtype", loan.getLoanSubType());
        maskedData.put("params", loan.getParams());


        return ResponseEntity.ok(maskedData);
    }


    public FlowNode findSourceFlowNode(UserTask userTask, BpmnModelInstance bpmnModelInstance) {
        SequenceFlow incomingSequenceFlow = userTask.getIncoming().iterator().next();
        return bpmnModelInstance.getModelElementById(incomingSequenceFlow.getSource().getId());
    }

    public void traverseFlowNodes(FlowNode flowNode, List<UserTask> orderedTasks) {
        if (flowNode instanceof UserTask) {
            orderedTasks.add((UserTask) flowNode);
        }

        Collection<SequenceFlow> outgoingSequenceFlows = flowNode.getOutgoing();
        for (SequenceFlow outgoingSequenceFlow : outgoingSequenceFlows) {
            FlowNode nextFlowNode = outgoingSequenceFlow.getTarget();
            traverseFlowNodes(nextFlowNode, orderedTasks);
        }
    }

    public List<TaskInfo> getAllTasks(ProcessDefinition processDefinition, BpmnModelInstance bpmnModelInstance, List<String> completedTaskNames) {
        // Find the start event
        StartEvent startEvent = bpmnModelInstance.getModelElementsByType(StartEvent.class).iterator().next();

        // Traverse the process model to retrieve user tasks in order
        List<UserTask> orderedTasks = new ArrayList<>();
        traverseFlowNodes(startEvent, orderedTasks);

        List<TaskInfo> taskInfos = new ArrayList<>();
        for (int i = 0; i < orderedTasks.size(); i++) {
            UserTask userTask = orderedTasks.get(i);
            String status = completedTaskNames.contains(userTask.getName()) ? "completed" : "pending";
            taskInfos.add(new TaskInfo(userTask.getId(), userTask.getName(), i + 1, status));
        }

        return taskInfos;
    }

    public List<TaskInfo> getAllTasksOld(BpmnModelInstance bpmnModelInstance, List<String> completedTaskNames) {
        // Get all user tasks
        Collection<UserTask> userTasks = bpmnModelInstance.getModelElementsByType(UserTask.class);

        // Sort user tasks based on their incoming flow nodes
        List<UserTask> sortedUserTasks = userTasks.stream()
                .sorted(Comparator.comparing(userTask -> findSourceFlowNode(userTask, bpmnModelInstance).getId()))
                .toList();

        List<TaskInfo> taskInfos = new ArrayList<>();
        for (int i = 0; i < sortedUserTasks.size(); i++) {
            UserTask userTask = sortedUserTasks.get(i);
            String status = completedTaskNames.contains(userTask.getName()) ? "completed" : "pending";
            taskInfos.add(new TaskInfo(userTask.getId(), userTask.getName(), i + 1, status));
        }

        return taskInfos;
    }

    public ResponseEntity<List<TaskInfo>> getSteps(String id) {
        // Get the process instance to find the process definition
        ProcessInstance retrievedProcessInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(id).singleResult();

        if (retrievedProcessInstance == null) {
            return ResponseEntity.notFound().build();
        }

        // Get the process definition
        ProcessDefinition processDefinition = repositoryService.getProcessDefinition(retrievedProcessInstance.getProcessDefinitionId());

        // Get the process model and collect all user task names
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinition.getId());

        // Get the completed tasks for the process instance
        List<HistoricTaskInstance> completedTasks = historyService.createHistoricTaskInstanceQuery().processInstanceId(retrievedProcessInstance.getId()).finished().list();
        List<String> completedTaskNames = completedTasks.stream().map(HistoricTaskInstance::getName).collect(Collectors.toList());

        // Get all tasks with their status
        List<TaskInfo> allTaskInfos = getAllTasks(processDefinition, bpmnModelInstance, completedTaskNames);

        // Filter out the pending tasks
        //List<TaskInfo> futureTasks = allTaskInfos.stream().filter(taskInfo -> taskInfo.getStatus().equals("pending")).collect(Collectors.toList());

        return ResponseEntity.ok(allTaskInfos);
    }


    private String maskString(String data, int start, int end, char maskChar) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        StringBuilder maskedData = new StringBuilder(data);
        for (int i = start; i < end && i < maskedData.length(); i++) {
            maskedData.setCharAt(i, maskChar);
        }
        return maskedData.toString();
    }
    public ResponseEntity<Map<String, StepData>> getApplicationData(Authentication authentication, String id) {
        String userId = authentication.getName();
        // Get the step data from stepDataRepository where application_id is id
        List<StepData> stepDataList = dataBaseFeignClient.getStepDataListByApplicationId(id).getBody();

        // return the response  as json with each step as the key and data as the value

        Loan loan = dataBaseFeignClient.getLoanByIdAndUserId(id, userId).getBody();

        if(loan == null){
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> loanData = new HashMap<>();
        loanData.put("loanType", loan.getLoanType());
        loanData.put("loanSubtype", loan.getLoanSubType());
        loanData.put("referralNumber",loan.getReferralNumber());
        loanData.put("params", loan.getParams());
        loanData.put("status", loan.getStatus());

        StepData loanDataStep = new StepData();

        loanDataStep.setStepName("LoanMetaData");
        loanDataStep.setData(loanData);

        Map<String, StepData> groupedStepData = Objects.requireNonNull(stepDataList).stream()
                .collect(Collectors.toMap(StepData::getStepName, stepData -> {
                    if (stepData.getStepName().equals("BasicAADHARVerification")) {
                        Map<String, Object> data = stepData.getData();
                        String adhaar = (String) data.get("adhaar");
                        data.put("adhaar", maskString(adhaar, 0, 8, '*'));
                    }
                    if ("LoanDetails".equals(stepData.getStepName())) {
                        // Create a new map with masked pan field
                        Map<String, Object> data = stepData.getData();
                        String pan = (String) data.get("pan");
                        data.put("pan", maskString(pan, 0, 6, '*'));
                    }
                    if ("PartnerDetails".equals(stepData.getStepName())) {
                        Map<String, Object> data = stepData.getData();
                        List<Map<String, String>> partners = (List<Map<String, String>>) data.get("partners");

                        partners.forEach(partner -> {
                            String pan = partner.get("pan");
                            String adhaar = partner.get("aadhaar");
                            partner.put("pan", maskString(pan, 0, 6, '*'));
                            partner.put("aadhaar", maskString(adhaar, 0, 8, '*'));
                        });
                    }
                    if ("PanAadharCreditVerification".equals(stepData.getStepName())){
                        Map<String, Object> data =stepData.getData();
                        String adhaar = (String) data.get("adhaar");
                        String pan = (String) data.get("pan");
                        data.put("pan", maskString(pan, 0, 6, '*'));
                        data.put("adhaar", maskString(adhaar, 0, 8, '*'));

                    }
                    return stepData;
                }));

        groupedStepData.put("LoanMetaData", loanDataStep);

        return ResponseEntity.ok(groupedStepData);
    }
    public ResponseEntity<Void> deleteAllProcessInstances() {
        // Call the method to delete all process instances
        // Get a list of all process instances
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().list();

        // Iterate through the list and delete each process instance
        for (ProcessInstance processInstance : processInstances) {
            runtimeService.deleteProcessInstance(processInstance.getId(), "Deleted all process instances");
        }

        // Return a response with an appropriate status
        return ResponseEntity.noContent().build();
    }
    public List<CRMAppliacationResponseDTO> getAllLoanDataWithMergedStepData() {
        List<CRMAppliacationResponseDTO> fetchedData = dataBaseFeignClient.getAllStepDataWithLoanData().getBody();

        Map<String, CRMAppliacationResponseDTO> resultMap = new LinkedHashMap<>();

        for (CRMAppliacationResponseDTO item : Objects.requireNonNull(fetchedData)) {
            if (!resultMap.containsKey(item.getId())) {
                resultMap.put(item.getId(), item);
            } else {
                CRMAppliacationResponseDTO existingItem = resultMap.get(item.getId());
                existingItem.getData().putAll(item.getData());
            }
        }

        return new ArrayList<>(resultMap.values());
    }
    public List<String> closeLoan(String phoneNumber) throws FirebaseAuthException {
        String uid = getUidFromPhoneNumber(phoneNumber);
        List<String> closedLoans = new ArrayList<>();

        if(StringUtils.isNotBlank(uid)) {
            Objects.requireNonNull(dataBaseFeignClient.getLoandByUserId(uid).getBody()).forEach(loan -> {
                if (!loan.getStatus().equalsIgnoreCase("DETAILS RECEIVED")) {
                    loan.setStatus("DETAILS RECEIVED");
                    dataBaseFeignClient.saveLoan(loan);
                    closedLoans.add(loan.getId());
                }
            });
        }

        return closedLoans;
    }
//
    public String getUidFromPhoneNumber(String phoneNumber) throws FirebaseAuthException {
        UserRecord user = FirebaseConfig.getFirebaseAuthInstance().getUserByPhoneNumber(phoneNumber);
        return user.getUid();
    }
    public List<UploadDocumentDTO> getUploadDocDetails(TypeOfLoan loanType) {
        LocalDate currentDate = LocalDate.now();
        List<UploadDocumentDTO> response = new ArrayList<>();
        Objects.requireNonNull(dataBaseFeignClient.getDocumentsDetailsByRefKey1AndRefKey2(loanType).getBody()).forEach(ref -> {
            switch (ref.getKey()) {
                case SALARY1:
                    String firstPayslipTitle = getPrevPayslipName(currentDate.getMonthValue(), currentDate.getYear(), 1);
                    response.add(getUploadDocumentDTO(SALARY1, firstPayslipTitle, ref));
                    break;
                case SALARY2:
                    String secondPayslipTitle = getPrevPayslipName(currentDate.getMonthValue(), currentDate.getYear(), 2);
                    response.add(getUploadDocumentDTO(SALARY2, secondPayslipTitle, ref));
                    break;
                case SALARY3:
                    String thirdPayslipTitle = getPrevPayslipName(currentDate.getMonthValue(), currentDate.getYear(), 3);
                    response.add(getUploadDocumentDTO(SALARY3, thirdPayslipTitle, ref));
                    break;
                case FINANCIAL_STATEMENT1:
                    String financialStatement1 = getPrevFinancialYear(FINANCIAL_STMT_UPLOAD, currentDate.getYear(), 1);
                    response.add(getUploadDocumentDTO(FINANCIAL_STATEMENT1, financialStatement1, ref));
                    break;
                case FINANCIAL_STATEMENT2:
                    String financialStatement2 = getPrevFinancialYear(FINANCIAL_STMT_UPLOAD, currentDate.getYear(), 2);
                    response.add(getUploadDocumentDTO(FINANCIAL_STATEMENT2, financialStatement2, ref));
                    break;
                case FINANCIAL_STATEMENT3:
                    String financialStatement3 = getPrevFinancialYear(FINANCIAL_STMT_UPLOAD, currentDate.getYear(), 3);
                    response.add(getUploadDocumentDTO(FINANCIAL_STATEMENT3, financialStatement3, ref));
                    break;
                case ITR1:
                    String itr1 = getPrevFinancialYear(ITR_UPLOAD, currentDate.getYear(), 1);
                    response.add(getUploadDocumentDTO(ITR1, itr1, ref));
                    break;
                case ITR2:
                    String itr2 = getPrevFinancialYear(ITR_UPLOAD, currentDate.getYear(), 2);
                    response.add(getUploadDocumentDTO(ITR2, itr2, ref));
                    break;
                case ITR3:
                    String itr3 = getPrevFinancialYear(ITR_UPLOAD, currentDate.getYear(), 3);
                    response.add(getUploadDocumentDTO(ITR3, itr3, ref));
                    break;
                case BANK_STATEMENT:
                    String bankStatement = MessageFormatter.format(BANK_STATEMENT_UPLOAD, Month.getMonthByNumber(currentDate.getMonthValue()).getShortName(), (currentDate.getYear()-1)).getMessage();
                    response.add(getUploadDocumentDTO(BANK_STATEMENT, bankStatement, ref));
                    break;
                case OTHERS:
                    response.add(getUploadDocumentDTO(OTHERS, OTHERS_UPLOAD, ref));
                    break;
                case INCORPORATION_DOCUMENT:
                    response.add(getUploadDocumentDTO(INCORPORATION_DOCUMENT, INCORPORATION_DOC_UPLOAD, ref));
                    break;
                case TRADE_LICENSE:
                    response.add(getUploadDocumentDTO(TRADE_LICENSE, TRADE_LICENSE_UPLOAD, ref));
                    break;
                case PARTNERSHIP_DEED:
                    response.add(getUploadDocumentDTO(PARTNERSHIP_DEED, PARTNERSHIP_DEED_UPLOAD, ref));
                    break;
            }
        });

        response.sort(Comparator.comparing(UploadDocumentDTO::getOrder));

        return response;
    }
    public List<UploadDocumentDTO> getUploadDocDetails1(TypeOfLoan loanType){
        LocalDate currentDate = LocalDate.now();
        List<UploadDocumentDTO> response = new ArrayList<>();
       Objects.requireNonNull(dataBaseFeignClient.getDocumentsDetailsByRefKey1AndRefKey2(loanType).getBody()).forEach(ref -> {
            switch (ref.getKey()) {
                case SALARY1:
                    String firstPayslipTitle = getPrevPayslipName1(currentDate.getMonthValue(), currentDate.getYear(),1);
                    response.add(getUploadDocumentDTO1(SALARY1, firstPayslipTitle, ref));
                    break;
                case SALARY2:
                    String secondPayslipTitle = getPrevPayslipName1(currentDate.getMonthValue(), currentDate.getYear(),2);
                    response.add(getUploadDocumentDTO1(SALARY2, secondPayslipTitle, ref));
                    break;
                case SALARY3:
                    String thirdPayslipTitle = getPrevPayslipName1(currentDate.getMonthValue(), currentDate.getYear(),3);
                    response.add(getUploadDocumentDTO1(SALARY3, thirdPayslipTitle, ref));
                    break;
                case BANK_STATEMENT:
                    String bankStatement = MessageFormatter.format(BANK_STATEMENT_UPLOAD, Month.getMonthByNumber((currentDate.minusMonths(6).getMonthValue())).getMonthName(), (currentDate.getMonthValue()< 7) ? (currentDate.getYear()-1): currentDate.getYear()).getMessage();
                    response.add(getUploadDocumentDTO1(BANK_STATEMENT, bankStatement, ref));
                    break;
                case OTHERS:
                    response.add(getUploadDocumentDTO1(OTHERS, OTHERS_UPLOAD, ref));
                    break;
            }
        });
        response.sort(Comparator.comparing(UploadDocumentDTO::getOrder));

        return response;
    }

    private UploadDocumentDTO getUploadDocumentDTO(String docType, String docTitle, Reference ref) {
        return UploadDocumentDTO.builder().docType(docType).docTitle(docTitle).isMandatory(ref.getValue().equalsIgnoreCase(Y)).order(ref.getOrder()).build();
    }
    private UploadDocumentDTO getUploadDocumentDTO1(String docType, String docTitle, Reference ref) {
        return UploadDocumentDTO.builder().docType(docType).docTitle(docTitle).isMandatory(ref.getValue().equalsIgnoreCase(Y)).order(ref.getOrder()).build();
    }

    private String getPrevPayslipName(int currentMonth, int currentYear, int i) {
        return MessageFormatter.format(PAYSLIP_UPLOAD, Month.getMonthByNumber(currentMonth - i).getShortName(), (currentYear + ((currentMonth-i)<=0 ? -1 : 0))).getMessage();
    }
    private String getPrevPayslipName1(int currentMonth, int currentYear, int i){
        return MessageFormatter.format(PAYSLIP_UPLOAD1, Month.getMonthByNumber(currentMonth - i).getMonthName(), (currentYear + ((currentMonth-i)<=0 ? -1 : 0))).getMessage();
    }

    private String getPrevFinancialYear(String message, int currentYear, int i) {
        return MessageFormatter.format(message, (currentYear-i), (currentYear-i+1)).getMessage();
    }
    public Object getPAN(String id) {
        try {
            StepData stepData = dataBaseFeignClient.getStepDataByIdAndStepName(id, "LoanDetails").getBody();
            if (Objects.requireNonNull(stepData).getData().get("pan") == null) {
                return ResponseEntity.ok(Collections.emptyMap());
            } else {
                return ResponseEntity.ok(
                        Collections.singletonMap("pan", stepData.getData().get("pan").toString()));
            }
        } catch (Exception e) {
            return ResponseEntity.notFound();
        }
    }
}
