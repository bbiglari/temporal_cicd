package com.temporal;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

import com.temporal.CICDWorkflow.Environment;

public class TestWorkflowImp implements TestWorkflow {

    private final TestActivities testActivities = Workflow.newActivityStub(
            TestActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .build());

    @Override
    public void execute(Environment environment, String artifactPath) {
        if (!testActivities.runIntegrationTests(artifactPath))
            throw new RuntimeException("Integration Test failed");

        if (!testActivities.runSmokeTests(artifactPath))
            throw new RuntimeException("Smoke Test failed");
        
        if (!testActivities.runStressTests(artifactPath))
            throw new RuntimeException("Stress Test failed");

        if (!testActivities.runChaosMonkeyTests(artifactPath))
            throw new RuntimeException("Chaos Monkey Test failed");

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String parentWorkflowID = Workflow.getInfo().getParentWorkflowId().get();
        CICDWorkflow parent = Workflow.newExternalWorkflowStub(CICDWorkflow.class, parentWorkflowID);
        parent.allTestPassed(environment);
    }
}
