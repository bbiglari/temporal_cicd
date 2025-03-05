package com.temporal.service;

import com.temporal.CICDWorkflow;
import com.temporal.CICDWorkflow.Environment;
import com.temporal.TemporalProperties;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class CICDClientService {

    private static final Logger logger = LoggerFactory.getLogger(CICDClientService.class);

    public static void main(String[] args) {
        SpringApplication.run(CICDClientService.class, args);
    }
}

@RestController
@RequestMapping("/api/v1/cicd")
class CICDController {

    private static final Logger logger = LoggerFactory.getLogger(CICDController.class);

    @PostMapping("/pipeline")
    public String startCICDPipeline(@RequestBody CICDStartEvent cicdStartEvent) {
        logger.info("Received Git event: {}", cicdStartEvent);

        // Start the Temporal workflow
        startTemporalWorkflow(cicdStartEvent);

        return "CICD pipeline started successfully!\n";
    }

    private void startTemporalWorkflow(CICDStartEvent cicdStartEvent) {
        // Set up connection to the Temporal service
        WorkflowServiceStubsOptions options = WorkflowServiceStubsOptions.newBuilder()
                .setTarget(TemporalProperties.TEMPORAL_SERVER_HOST)
                .build();

        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(options);
        WorkflowClient client = WorkflowClient.newInstance(service,
                WorkflowClientOptions.newBuilder().setNamespace(cicdStartEvent.getNamespace()).build());

        // Create a Workflow client stub for each workflow
        CICDWorkflow workflow = client.newWorkflowStub(
                CICDWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TemporalProperties.TASK_QUEUE)
                        .build());
        // Create arguments of the workflow

        
        // Start the Workflow
        workflow.execute(cicdStartEvent.getRepository().getClone_url(), getEnvironmentConfigs());
        logger.info("Temporal workflow started for gitRepoPath: {}", cicdStartEvent.getRepository().getClone_url());
    }

    private Map<Environment,Map<String,String>> getEnvironmentConfigs(){
        Map<Environment,Map<String,String>> configs = new HashMap<>();
        configs.put(Environment.STAGING, new HashMap<>());
        configs.get(Environment.STAGING).put("app", "my-app");
        configs.get(Environment.STAGING).put("namespace", "west namespace");

    
        configs.put(Environment.PRODUCTION,new HashMap<>());
        configs.get(Environment.PRODUCTION).put("app", "my-app");
        configs.get(Environment.PRODUCTION).put("namespace", "west namespace");

        return configs;
    }
}

// Simple POJO to represent a Git webhook event (you'll need to expand this)
class CICDStartEvent {
    private Repository repository;
    private String namespace;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public String toString() {
        return "CICDStartEvent{" +
                "namespace='" + namespace + '\'' + "," +
                "repository=" + repository +
                '}';
    }
}

class Repository {
    private String clone_url;

    public String getClone_url() {
        return clone_url;
    }

    public void setClone_url(String clone_url) {
        this.clone_url = clone_url;
    }

    @Override
    public String toString() {
        return "Repository{" +
                "clone_url='" + clone_url + '\'' +
                '}';
    }
}
