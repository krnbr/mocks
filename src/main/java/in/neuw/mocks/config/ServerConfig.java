package in.neuw.mocks.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

@Slf4j
@Configuration
public class ServerConfig {

    @Bean
    @ConditionalOnProperty(value = "server.second.secured", havingValue = "false")
    public ServletWebServerFactory servletContainer(@Value("${server.second.server.port}") int httpPort) {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(httpPort);

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(connector);
        return tomcat;
    }

    @Bean
    @ConditionalOnProperty(value = "server.second.secured", havingValue = "true", matchIfMissing = true)
    public ServletWebServerFactory servletContainerSecured(@Value("${server.second.server.port}") int httpsPort,
                                                           ServerProperties serverProperties) {
        try {
            Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
            connector.setPort(httpsPort);

            Http11NioProtocol protocol = (Http11NioProtocol)connector.getProtocolHandler();
            protocol.setSSLEnabled(true);
            connector.setScheme("https");
            connector.setSecure(true);

            // Validate port availability before proceeding
            try (ServerSocket serverSocket = new ServerSocket(httpsPort)) {
                // Port is available
                log.info("Server port {} was available", serverSocket.getLocalPort());
            } catch (IOException e) {
                log.error("Port {} is already in use. May need to configure a different port.", httpsPort);
                throw new IllegalStateException("Port " + httpsPort + " is already in use. May need to configure a different port.", e);
            }

            var sslConfig = new SSLHostConfig();
            var configCertificate = new SSLHostConfigCertificate(sslConfig, SSLHostConfigCertificate.Type.UNDEFINED);

            // Validate SSL configuration
            if (serverProperties.getSsl() == null) {
                throw new IllegalStateException("SSL configuration is missing in server properties");
            }

            String certFile = serverProperties.getSsl().getCertificate();
            String keyFile = serverProperties.getSsl().getCertificatePrivateKey();
            String password = serverProperties.getSsl().getKeyStorePassword();

            // Validate SSL files exist
            validateFileExists(certFile, "Certificate file");
            validateFileExists(keyFile, "Private key file");

            configCertificate.setCertificateFile(certFile);
            configCertificate.setCertificateKeyFile(keyFile);
            configCertificate.setCertificateKeystorePassword(StringUtils.hasText(password) ? password : "");

            sslConfig.addCertificate(configCertificate);
            protocol.addSslHostConfig(sslConfig);

            TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
            tomcat.addAdditionalTomcatConnectors(connector);
            tomcat.setSsl(serverProperties.getSsl());
            return tomcat;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to configure Tomcat connector: " + e.getMessage(), e);
        }
    }

    private void validateFileExists(String filePath, String fileType) {
        if (filePath == null || !new File(filePath).exists()) {
            throw new IllegalStateException(fileType + " not found at: " + filePath);
        }
    }

}
