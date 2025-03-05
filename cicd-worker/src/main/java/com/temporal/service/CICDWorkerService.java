package com.temporal.service;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.temporal.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class CICDWorkerService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CICDWorkerService.class);

    private WorkerFactory factory;

    public static void main(String[] args) {
        SpringApplication.run(CICDWorkerService.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing Temporal Worker...");

        try {
            // Set up connection to the Temporal service
            WorkflowServiceStubsOptions options = WorkflowServiceStubsOptions.newBuilder()
                    .setTarget(TemporalProperties.TEMPORAL_SERVER_HOST)
                    .build();
            WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(options);
            WorkflowClient client = WorkflowClient.newInstance(service);

            // Create a worker factory with the Temporal client
            factory = WorkerFactory.newInstance(client);
            // Create the worker for the specified task queue
            Worker worker = factory.newWorker(TemporalProperties.TASK_QUEUE);

            // Register the workflow implementation with the worker
            worker.registerWorkflowImplementationTypes(CICDWorkflowImpl.class, TestWorkflowImp.class);

            // Register activities with the worker
            worker.registerActivitiesImplementations(new CICDActivitiesImpl(), new TestActivitiesImpl());

            // Start the worker to start processing workflow tasks
            factory.start();
            logger.info("Worker started successfully.");

        } catch (Exception e) {
            logger.error("Error during worker setup: {}", e.getMessage(), e);
        }
    }
}
