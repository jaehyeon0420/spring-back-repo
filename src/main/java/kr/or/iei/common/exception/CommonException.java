package kr.or.iei.common.exception;

import org.springframework.http.HttpStatus;

/*
사용자 정의 예외 : 컴파일 및 실행(런타임) 중, 예외가 발생하지 않지만 논리적인(비즈니스적인) 오류가 발생했을 때 강제로 예외를 발생시키기 위한 용도로 작성

 - UncheckedException에 대한 사용자 정의 예외 정의 시, RuntimeException을 상속받아야 함.
 
 * */
public class CommonException extends RuntimeException{
	/*
	 * CommonException에 노란줄 == 최상위 클래스 Throwable은 Serializeable을 implements 하고 있음(직렬화 가능한 클래스로 지정)
	 * 해당 클래스가 버전을 관리하기 위한 고유ID가 선언되지 않아서 경고! (없어도 JVM이 자동으로 생성해주긴 함)
	 */
	private static final long serialVersionUID = 1L;
	
	private HttpStatus errorCode;	//HTTP 응답 코드
	private String userMsg;			//사용자 출력 메시지
	
	public CommonException() {
		super();
	}
	
	public CommonException(String msg) { //콘솔 출력 메시지
		super(msg);
	}

	public HttpStatus getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(HttpStatus errorCode) {
		this.errorCode = errorCode;
	}

	public String getUserMsg() {
		return userMsg;
	}

	public void setSystemMsg(String userMsg) {
		this.userMsg = userMsg;
	}
	
	
	
}
