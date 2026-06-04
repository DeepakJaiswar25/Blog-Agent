# Introduction

Spring AI is a framework for integrating predictive and generative models into Spring applications. This guide shows a minimal, practical setup to get a simple AI-backed REST endpoint running. It focuses on current platform recommendations and practical details for a first project.

# Prerequisites

- Java 17 or later (Java 17+ is recommended; Spring Boot 3.x requires Java 17+)
- Maven or Gradle
- An IDE such as IntelliJ IDEA or Eclipse
- An internet connection to download dependencies

# Set up a new project

Use Spring Initializr (https://start.spring.io) to bootstrap the project. Select:

- Project type: Maven or Gradle (your preference)
- Language: Java
- Spring Boot: 3.x (or the latest 3.x release)
- Dependencies: Spring Web

You can add the Spring AI dependency manually to the build file after generation (see the dependency example below). Always check the Spring AI project's documentation for the correct artifact id and latest version.

# Add the Spring AI dependency

Add the library dependency to your pom.xml (example; confirm the latest groupId/artifactId/version in the project docs):

```xml
<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-core</artifactId>
  <version>0.1.0</version> <!-- replace with the current version -->
</dependency>
```

If you use Gradle, add the equivalent to build.gradle or build.gradle.kts.

# Implement a predictive service

The library you use will typically provide a model client or an interface. For clarity, this example shows a small PredictiveModel interface and a service that delegates to it. Replace or adapt the interface with the real API from your chosen AI library.

```java
// Example: replace with the real model interface provided by your AI library
public interface PredictiveModel {
    String predict(String input);
}

import org.springframework.stereotype.Service;

@Service
public class PredictionService {
    private final PredictiveModel model;

    public PredictionService(PredictiveModel model) {
        this.model = model;
    }

    public String predict(String input) {
        // Add input validation, logging, and error handling as needed
        return model.predict(input);
    }
}
```

If your AI library provides a client that needs configuration (API keys, endpoints, etc.), configure that client as a @Configuration class or bind properties via application.yml/application.properties.

# Create a controller

Expose the prediction service with a clear API contract. This example accepts plain text and returns plain text. You can switch to JSON or another format as required.

```java
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/predict")
public class PredictionController {
    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getPrediction(@RequestBody String input) {
        String result = predictionService.predict(input);
        return ResponseEntity.ok(result);
    }
}
```

Notes:
- If you expect JSON input, change consumes/produces to MediaType.APPLICATION_JSON_VALUE and use a DTO for request/response payloads.
- Add error handling (ControllerAdvice) and input validation for production readiness.

# Running the application

From the project directory run:

```
./mvnw spring-boot:run
```

(or `mvn spring-boot:run` if you use your system Maven). The app will start on http://localhost:8080 by default.

# Testing the endpoint

Example using curl for plain text input (include the Content-Type header):

```
curl -X POST http://localhost:8080/api/predict \
  -H "Content-Type: text/plain" \
  --data "your input"
```

If your controller uses JSON, send and receive JSON and use `-H "Content-Type: application/json"` with a JSON body.

# Production considerations

- Secure and manage any API keys or credentials used by model clients using Spring Boot configuration and secrets management.
- Observe rate limits of external model providers and implement retries/backoff where appropriate.
- Add logging, metrics, and tracing (Micrometer/OpenTelemetry) for observability.
- Validate input and sanitize outputs to avoid unexpected model behavior.

# Conclusion

You now have a minimal Spring application that delegates predictions to a model. From here, integrate the actual AI client from the Spring AI project or another provider, add configuration, validation, and operational concerns, and iterate on the model and API design.

For detailed, up-to-date usage and configuration, consult the official Spring AI documentation or the project's repo.

Happy coding!