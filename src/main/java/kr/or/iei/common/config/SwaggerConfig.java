package kr.or.iei.common.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
	
	private ApiInfo swaggerInfo() {
		/*
		 title() 		: API 문서의 제목
		 description() 	: API 문서 설명  
		 * */
		return new ApiInfoBuilder().title("REACT_WEB API").build();
	}
	
	//테스트 시, 요청 형식 지정
	private Set<String> getConsumeContentType(){
		Set<String> consumes = new HashSet<String>();
		
		//테스트 요청 시, JSON 타입의 데이터 또는 Form 태그 형식의 요청 허용
		consumes.add("application/json;charset=UTF-8");
		consumes.add("application/x-www-form-urlencoded");
		return consumes;
	}
	
	//테스트 시, 응답 형식 지정
	private Set<String> getProduceContentType(){
		Set<String> produces = new HashSet<String>();
		
		//테스트 결과 응답 시, JSON 타입 또는 일반 문자열로 응답될 수 있음.
		produces.add("application/json;charset=UTF-8");
		produces.add("plain/text;charset=UTF-8");
		return produces;
	}
	
	@Bean
	public Docket swaggerApi() {
		//API 문서를 자동으로 만들어주는 객체 생성하며 위에서 만든 메소드 바인딩
		return new Docket(DocumentationType.SWAGGER_2)
				   .consumes(getConsumeContentType())						//요청 형식
				   .produces(getProduceContentType())						//응답 형식
				   .apiInfo(swaggerInfo()).select()							//API 정보
				   .apis(RequestHandlerSelectors.basePackage("kr.or.iei"))	//문서로 만들 API들이 존재하는 베이스 패키지
				   .paths(PathSelectors.any())
				   .build()
				   .useDefaultResponseMessages(false);
	}
	
	@Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Spring Boot API 문서")
                .version("v1")
                .description("스웨거 테스트"))
            .components(new Components()
                .addSecuritySchemes("accessToken",  //이름 중요
                    new SecurityScheme()
                        .type(Type.APIKEY)          
                        .in(In.HEADER)              
                        .name("Authorization")      //헤더 설정 이름
                )
            )
            .addSecurityItem(new SecurityRequirement().addList("accessToken")); //위 설정한 이름과 동일하게
    }
}
