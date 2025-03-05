package com.temporal;


import com.temporal.CICDWorkflow.Environment;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface TestWorkflow {

    @WorkflowMethod
    void execute(Environment environment, String image);
}
