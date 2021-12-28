package com.geekshirt.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
//Importar una clase
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    // Un Docket te ayuda a describir objetos y documentarlos
    // Un Bean son objetos que maneja el contenedor de Spring (si no agrego la etiqueta entonces no lo administra Spring)

    /* Para ver la documentacion que genero Swagger sobre las apis hay que
    mirar el puerto que se levanto, y en la terminal informa la ruta http://localhost:8080/api/v1/v2/api-docs
    ek api/v1 se agrega porque se agrego en el application.properties como default.
    */

    /* En el build.gradle esta la dependencia implementation group: 'io.springfox', name: 'springfox-swagger-ui', version: '2.9.2'
    el UI te levanta la informacion json de Swagger y te crea una pagina html con la documentacion de la Api.

     */
    @Bean
    public Docket apiDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()).build().apiInfo(getApiInfo());
    }

    // Documentacion General de la API
    private ApiInfo getApiInfo(){
        return new ApiInfo("GeekShirt Order Service API",
                "This API provides all methods required for order managment",
                "1.0", "TERMS OF SERVICE URL",
                new Contact("Centripio", "https://www.centripio.io", "centripio@gmail.com"),
                "LICENSE",
                "LICENSE URL",
                Collections.emptyList());
    }
}
