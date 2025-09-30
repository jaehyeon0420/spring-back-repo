package kr.or.iei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) //스프링 시큐리티 적용하여, 첫 화면이 로그인 화면으로 보이는 설정 해제하기
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
