package in.neuw.mocks.controllers;

import com.fasterxml.uuid.Generators;
import in.neuw.mocks.models.Oauth2TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Oauth2TokenMockController {

    @PostMapping("/oauth2/token")
    public Oauth2TokenResponse getOauth2Token(@ModelAttribute(name = "client_id") String clientId,
                                              @ModelAttribute("client_secret") String clientSecret,
                                              @ModelAttribute("grant_type") String grant,
                                              HttpServletRequest request) {
        log.info("input client id - {}, secret - {}, grant - {} on port - {}", clientId, clientSecret, grant, request.getServerPort());
        Oauth2TokenResponse oauth2TokenResponse = new Oauth2TokenResponse();
        return oauth2TokenResponse
                .setAccessToken(Generators.timeBasedEpochGenerator().generate().toString());
    }

}
