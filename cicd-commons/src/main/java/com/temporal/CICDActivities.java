package com.temporal;

import java.util.Map;

import com.temporal.CICDWorkflow.Environment;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface CICDActivities {

    @ActivityMethod
    String buildProject(String gitRepoPath);

    @ActivityMethod
    String containerize(String buildArtifactPath);

    @ActivityMethod
    void pushToRegistry(String image);

    @ActivityMethod
    String deploy(Environment environment, Map<String,String> deploymentConfig);
}
