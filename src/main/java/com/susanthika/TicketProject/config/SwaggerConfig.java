package com.susanthika.TicketProject.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {


        return new OpenAPI()
                .info(new Info()
                        .title("Ticket Management System")
                        .version("0.0.1")
                        .description("Test API Document")
                        .contact(new Contact()
                                .name("System Admin")
                                .email("systemadmin@info.com")))

                .addSecurityItem(  // Tell Swagger that security is required.
                        new SecurityRequirement() // Creates a security requirement

                                .addList("JavaInUseSecurityScheme")  // Use the security configuration named JavaInUseSecurityScheme
                )
                .components( // Add reusable security configuration to Swagger.
                        new Components()   // Creates the place where we put the security configuration (Create a container for security configuration.)
                                .addSecuritySchemes(  // Add a security configuration
                                        "JavaInUseSecurityScheme",  // This is simply the name of the security configuration.
                                        new SecurityScheme()   // Creates the actual security configuration. Now we tell Swagger what type of security we use
                                                .name("JavaInUseSecurityScheme")   // Gives the security configuration a name.
                                                .type(SecurityScheme.Type.HTTP)  // Our authentication uses HTTP authentication.
                                                .scheme("bearer")   // We send the authentication token using Bearer authentication.
                                                .bearerFormat("JWT")  // The Bearer token we use is a JWT token.
                                )
                );

//                .addSecurityItem(new SecurityRequirement().addList("JavaInUseSecurityScheme"))
//                .components(new Components().addSecuritySchemes("JavaInUseSecurityScheme", new SecurityScheme()
//                        .name("JavaInUseSecurityScheme").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));

    }
}