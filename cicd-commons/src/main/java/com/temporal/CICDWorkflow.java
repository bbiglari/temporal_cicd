package com.temporal;

import java.util.Map;


import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface CICDWorkflow {

    public static enum Environment {
        STAGING,
        PRODUCTION
    }

    @WorkflowMethod
    void execute(String gitRepoPath, Map<Environment, Map<String, String>> deployementConfigs);

    @SignalMethod
    void allTestPassed(Environment environment);
}
