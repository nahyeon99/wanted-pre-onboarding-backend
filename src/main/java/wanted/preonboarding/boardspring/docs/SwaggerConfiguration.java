package wanted.preonboarding.boardspring.docs;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import wanted.preonboarding.boardspring.domain.dto.MemberDefaultResponseDto;
import wanted.preonboarding.boardspring.domain.dto.PostDefaultResponseDto;

import java.util.Arrays;
import java.util.List;

import static wanted.preonboarding.boardspring.auth.SecurityConfig.AUTHENTICATION_HEADER_NAME;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        TypeResolver typeResolver = new TypeResolver();

        return new Docket(DocumentationType.OAS_30)
                .additionalModels(
                        typeResolver.resolve(MemberDefaultResponseDto.class),
                        typeResolver.resolve(PostDefaultResponseDto.class)
                )
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo()).securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()));
    }

    private ApiKey apiKey() {

        return new ApiKey(AUTHENTICATION_HEADER_NAME, AUTHENTICATION_HEADER_NAME, "header");
    }

    private SecurityContext securityContext() {
        return springfox
                .documentation
                .spi.service
                .contexts
                .SecurityContext
                .builder()
                .securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference(AUTHENTICATION_HEADER_NAME, authorizationScopes));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("board API")
                .version("1.0.0")
                .build();
    }
}