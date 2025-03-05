package com.temporal;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temporal.CICDWorkflow.Environment;


public class CICDActivitiesImpl implements CICDActivities {

    private static final Logger logger = LoggerFactory.getLogger(CICDActivitiesImpl.class);

    @Override
    public String buildProject(String gitRepoPath) {
        // Implement logic to build your project (e.g., using Maven, Gradle)
        // ...
        logger.info("buildProject");
        return "build_artifact_path";
    }

    @Override
    public String containerize(String buildArtifactPath) {
        // Implement logic to containerize your application (e.g., using Docker)
        // ...
        logger.info("containerize");
        return "image_name";
    }

    @Override
    public void pushToRegistry(String image) {
        // Implement logic to push the Docker image to ECR
        // ...
        logger.info("pushToRegistry");
    }

    @Override
    public String deploy(Environment environment, Map<String, String> deploymentConfig) {
        logger.info("Deployment for environment = " + environment + "is started.");
        logger.info("Deployment for environment = " + environment + "is Completed.");
        return "http://my-" + environment + "-service-url" + System.currentTimeMillis();
    }
}
