package org.projects.book.bookshop.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@OpenAPIDefinition(info = @Info(
        contact = @Contact(
          name = "Ahmed",
          email = "ahmednaserm30@gmail.com",
          url = "https://github.com/AhmedNaserm30"

        ),
        description = "OpenApi documentation for BookLib",
        title = "Bookshop API",
        version = "1.0",
      termsOfService = "terms Of Service"
),
        servers = {
        @Server(
                description = "Local ENV"
                ,url = "http://localhost:8080/api/v1"
        ),
                @Server(
                        description = "Production ENV"
                        ,url = ""
                )
        },
        security = {
        @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(name =  "bearerAuth",
description = "Jwt Authentication",
scheme = "bearer",
type = SecuritySchemeType.HTTP,
bearerFormat = "JWT",
in = SecuritySchemeIn.HEADER)
public class WebConfiguration {
}
