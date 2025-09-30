package kr.or.iei.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.Filter;
import kr.or.iei.common.filter.EncodingFilter;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	@Value("${file.uploadPath}")
	private String uploadPath;
	
	@Bean //객체 만들어서, 컨테이너에 수동으로 등록하기
	public BCryptPasswordEncoder bCrypt() {
		return new BCryptPasswordEncoder();
	}
	
	//자원에 대한 접근 권한 부여
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/editor/**") //요청 URL
		        .addResourceLocations("file:///" + uploadPath + "/editor/"); //연결할 경로
		
		registry.addResourceHandler("/board/**") //요청 URL
        .addResourceLocations("file:///" + uploadPath + "/board/"); //연결할 경로
	}
	
	
	
	/*
	//클라이언트 요청 파라미터 중, 특수문자들을 쿼리 스트링으로 전달할 수 있도록 허용
	@Override
	public void customize(TomcatServletWebServerFactory factory) {
		factory.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "<>[\\]^`{|}"));
	}
	*/
	
	@Bean //Ioc 컨테이너에 Bean으로 등록
	public FilterRegistrationBean<Filter> EncodingFilter() {
        // SpringBoot 에서는 FilterRegistrationBean을 이용해서 필터 설정(was 올릴 때 서블릿 컨테이너 올릴 때 알아서 등록을 해준다.)
		
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new EncodingFilter()); 	// 등록할 필터 클래스
        filterRegistrationBean.setOrder(1); 						// 필터 순서 (낮을수록 우선순위 높음)
        filterRegistrationBean.addUrlPatterns("/*"); 				// 필터를 적용할 url 패턴(모든 요청에 대해 필터 동작)

        return filterRegistrationBean;
    }
}
