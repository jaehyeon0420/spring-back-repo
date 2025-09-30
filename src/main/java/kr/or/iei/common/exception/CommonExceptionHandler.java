package kr.or.iei.common.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.or.iei.common.model.dto.ResponseDTO;
 

//RestController 작성된 컨트롤러에서 예외 발생 시 처리할 핸들러 클래스
@RestControllerAdvice
public class CommonExceptionHandler {
	/* 컨트롤러 각 메소드에서 try ~ catch로 발생할 예외에 대한 처리를 하고 있으므로,
	 * 아래 예외 처리 핸들러 메소드는 호출 되지 않음!
	 * 만약, AOP 이외에서도 발생한 예외에 대한 처리를 아래 핸들러에서 처리하고 싶다면
	 * 컨트롤러 메소드 catch 내부에서, CommonException 생성 후, throw 
	 * */

	//패키지 경로 : org.apache.logging.log4j
	private static final Logger logger = LogManager.getLogger(CommonExceptionHandler.class);
	
	@ExceptionHandler(CommonException.class) //이 메소드는 어떠한 예외 발생 시, 처리할 것인가 
	public ResponseEntity<ResponseDTO> commonExceptionHandle(CommonException ex, 			//AOP에서 던진 예외 객체
			                                                 HttpServletRequest request, 	
			                                                 HttpServletResponse response) {
		/* React <> Spring은 모두 비동기 통신이지만,
		 * JSP <> Spring은 비동기 통신과 동기 통신 둘 다 존재하므로, ajax 요청인지 아닌지에 따라 다른 처리 필요. (레거시 프로젝트에 코드 있음)
		 */
		
		ex.printStackTrace(); //개발자가 오류 내용을 파악할 수 있도록 콘솔에 출력
		//logger.error("Request URL : " + request.getRequestURL());				//요청 URL
		//logger.error("Exception HttpStatus : " + ex.getErrorCode());			//CommonException 생성 시, 작성한 HttpStatus 코드
		//logger.error("Exception System Message : " + ex.getMessage());		//CommonException 생성 시, 작성한 개발자 확인용 시스템 메시지
		
		ResponseDTO res = new ResponseDTO(ex.getErrorCode(), ex.getMessage(), null, "error"); 
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	//선택
	@ExceptionHandler(Exception.class) //CommonException
	public String exception(Exception ex) {
		ex.printStackTrace();
		/*
		 CommonException은 RuntimeException을 상속하고, RuntimeException은 Exception을 상속.
		 그럼 CommonException이 발생했을 때, 2개의 메소드 모두 실행되나? -> No
		 예외가 발생하면 더 구체적인 Handler를 찾음! 즉, 위 CommonException에 대한 핸들러만 실행됨. 
		 * */
		System.out.println("Exception Handler");
		return null;
	}
}
