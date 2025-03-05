package com.temporal;

import io.temporal.activity.ActivityOptions;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CICDWorkflowImpl implements CICDWorkflow {

  private static final Logger logger = LoggerFactory.getLogger(CICDWorkflowImpl.class);
  private Map<Environment, Map<String, String>> deploymentConfigs;

  private CICDActivities activities = Workflow.newActivityStub(
      CICDActivities.class,
      ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofMinutes(TemporalProperties.START_TO_CLOSE_TIMEOUT))
          .setScheduleToCloseTimeout(Duration.ofMinutes(TemporalProperties.START_TO_CLOSE_TIMEOUT))
          .build());

  @Override
  public void execute(String gitRepoPath, Map<Environment, Map<String, String>> deploymentConfigs) {

    this.deploymentConfigs = deploymentConfigs;

    logger.info("staging deployment is started.");
    String artifactPath = activities.buildProject(gitRepoPath);
    String imageName = activities.containerize(artifactPath);
    activities.pushToRegistry(imageName);
    activities.deploy(Environment.STAGING, deploymentConfigs.get(Environment.STAGING));

    logger.info("staging deployment is completed.");

    // Create a child workflow for testing
    TestWorkflow testWorkflow = Workflow.newChildWorkflowStub(
        TestWorkflow.class,
        ChildWorkflowOptions.newBuilder()
            .setWorkflowId(Workflow.getInfo().getWorkflowId() + "_TEST")
            .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON)
            .build());

    logger.info("Test Workflow is executing.");
    testWorkflow.execute(Environment.STAGING, imageName);
  }

  @Override
  public void allTestPassed(Environment environment) {
    if (environment.equals(Environment.STAGING)) {
      logger.info("Production Deployment is started.");
      activities.deploy(Environment.PRODUCTION, deploymentConfigs.get(Environment.PRODUCTION));
      logger.info("Production Deployment is completed.");
    }
  }
}
