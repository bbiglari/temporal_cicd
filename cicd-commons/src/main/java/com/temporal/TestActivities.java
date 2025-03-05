package com.temporal;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface TestActivities {

    @ActivityMethod
    boolean runIntegrationTests(String artifactPath);

    @ActivityMethod
    boolean runSmokeTests(String artifactPath);

    @ActivityMethod
    boolean runStressTests(String artifactPath);

    @ActivityMethod
    boolean runChaosMonkeyTests(String artifactPath);
}
