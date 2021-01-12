package io.swagger.api;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
        title = "Swagger Server", 
        version = "0.1.0", 
        description = "For easy retrieval of a publication or data, assigned with a Dutch NBN in the Netherlands, a service called <a href ='https://persistent-identifier.nl'>National Resolver</a> is available. This service is managed by <a href ='https://dans.knaw.nl'>Data Archiving and Networked Services</a> (DANS) and <a href=\"https://www.kb.nl\">KB, National Library of the Netherlands</a> (KB). Find out more about the <a href=\"https://www.kb.nl/organisatie/onderzoek-expertise/informatie-infrastructuur-diensten-voor-bibliotheken/registration-agency-nbn\">'Registration Agency NBN'</a>.",
        termsOfService = "",
        contact = @Contact(email = "harvester@dans.knaw.nl"),
        license = @License(
            name = "Apache License 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0.html"
        )
    )
)
public class Bootstrap {
}
