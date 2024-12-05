package in.neuw.mocks.controllers;

import in.neuw.mocks.models.HealthStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

    @GetMapping("/ping")
    public HealthStatus ping() {
        return new HealthStatus().setStatus("pong");
    }

}
