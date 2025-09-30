package kr.or.iei.common.aop;

import java.util.Locale;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.or.iei.common.exception.CommonException;
import kr.or.iei.common.util.JwtUtils;
import kr.or.iei.member.model.dto.Member;

/* AOP : 공통 기능을 핵심 비즈니스 로직과 분리해서, 
 *       재사용성과 유지보수성을 높이기 위한 관점 지향 프로그래밍.
 * 
 * 
 * */
@Component	
@Aspect 	
public class ValidateAOP {

	/* - 아래 로그인 체크 AOP 작성 시, 추가할 것.
	 * 
	 * - WebConfig에서 사전에 해당 Bean이 등록되어 있어야 함.
	 * - message 프로퍼티 파일 명칭 및 생성 경로 준수해야함. 또한 우클릭 - properties에서 인코딩 설정 변경할 것
	 * 
	 */
	@Autowired
	private MessageSource message;
		
		
	/*
	 AOP 관련 용어
	 
	 - Advice 정의 관련
	 1) Pointcut  : 공통 기능을 적용시킬 메소드를 정의하는 표현식
	 2) JoinPoint : 공통 기능이 실행될 수 있는 지점(메소드)을 의미.
	 
	 - Advice 동작 시점 관련
	 1) Before : Pointcut으로 지정한 메소드 실행 이전에 동작하는 Advice
	 2) After  : Pointcut으로 지정한 메소드 실행 이후에 동작하는 Advice
	 3) Around : Pointcut으로 지정한 메소드 실행 이전 및 이후에 동작하는 Advice를 작성. (메소드 호출 자체를 가로채어 처리함)
	 4) After Returning : Pointcut으로 지정한 메소드가 정상 리턴하면 동작
	 5) After Throwing  : Pointcut으로 지정한 메소드 실행 중, 예외가 발생하면 동작
	  
	 * */
	/*
	@Pointcut("execution(* kr.or.iei.member.controller.MemberController.*(..))")
	public void pointCut1() {}
	
	@Before("pointCut1()")
	public void test1(JoinPoint jp) {
		//System.out.println("firstPointcut 동작!!");
		
		//메소드 정보 획득 (aspect 패키지 하위 클래스 import 할 것)
		MethodSignature signature =  (MethodSignature) jp.getSignature();
		
		//1) 실행 메소드 명칭
		System.out.println(jp.getSignature().getName());
		
		System.out.println("=====================================");
		
		//2) 실행 메소드의 매개변수 타입 리스트
		Class<?>[] paramTypes =  signature.getParameterTypes();
		for(int i=0; i<paramTypes.length; i++) {
			System.out.println(paramTypes[i].toString() + " : " + paramTypes[i].equals(HttpSession.class));
			System.out.println(paramTypes[i].toString() + " : " + paramTypes[i].equals(Member.class));
		}
		
		
		System.out.println("=====================================");
		
		//3) 실행 메소드의 매개변수 이름
		String [] paramNames =  signature.getParameterNames();
		for(int i=0; i<paramNames.length; i++) {
			System.out.println(paramNames[i]);
		}
		
		
		
	}
	*/
	
	/* 아래 로그인 체크를 위해 주석 처리 
	
	[Pointcut 표현식]
	
	execution(반환자료형 실행메소드경로(매개변수))
	
	//kr.or.iei.member.model.service.MemberService의 모든 메소드.
	@Pointcut("execution(* kr.or.iei.member.model.service.MemberService.*(..))")
	public void pointCut1() {}
	
	//kr.or.iei.member.model.service.MemberService의 모든 메소드 중, 반환형이 Member인 메소드
	@Pointcut("execution(kr.or.iei.member.model.dto.Member kr.or.iei.member.model.service.MemberService.*(..))")
	public void pointCut2() {}
	
	//kr.or.iei.member.model.service.MemberService의 모든 메소드 중, 반환형이 Member이고, 메소드명이 Member로 끝나는 메소드
	@Pointcut("execution(kr.or.iei.member.model.dto.Member kr.or.iei.member.model.service.MemberService.*Member(..))")
	public void pointCut3() {}
	
	//kr.or.iei.member.model.service.MemberService의 모든 메소드 중, 반환형이 Member이고, 메소드명이 Member로 끝나는 메소드 && 매개변수 타입이 Member인 메소드
	@Pointcut("execution(kr.or.iei.member.model.dto.Member kr.or.iei.member.model.service.MemberService.*(kr.or.iei.member.model.dto.Member))")
	public void pointCut4() {}
	
	
	//@Before : pointCut1()으로 지정한 메소드가 실행되기 이전에 실행
	@Before("pointCut1()")
	public void test1(JoinPoint jp) {
		//System.out.println("firstPointcut 동작!!");
		System.out.println("Before 실행 메소드 명칭 : " + jp.getSignature().getName());
		
		//매개변수 리스트
		Object args [] = jp.getArgs();
		for(int i=0; i<args.length; i++) {
		}
		
	}
	
	//동일한 포인트컷 및 동일한 Advice 여러개 작성 가능. 작성한 순서대로 실행된다.
	@Before("pointCut1()")
	public void test11(JoinPoint jp) {
		//System.out.println("firstPointcut 동작!!");
		System.out.println("Before2 실행 메소드 명칭 : " + jp.getSignature().getName());
		
		//매개변수 리스트
		Object args [] = jp.getArgs();
		for(int i=0; i<args.length; i++) {
		}
		
	}
	
	//@After : pointCut1()으로 지정한 메소드 실행 이후에 실행
	@After("pointCut1()")
	public void test2(JoinPoint jp) {
		//Before보다 나중에 콘솔에 출력.
		System.out.println("After 실행 메소드 명칭 : " + jp.getSignature().getName());
	}
	
	//@Around : 포인트컷으로 지정한 메소드가 실행되기 이전과 이후에 부가 기능 코드를 작성할 때 사용. 즉, 현재 메소드에서 지정한 메소드를 실행시켜주어야 함.
	@Around("pointCut1()")
	public void test3(ProceedingJoinPoint pjp) throws Throwable {

		String methodName = pjp.getSignature().getName();
		
		StopWatch stopWatch = new StopWatch();
		
		stopWatch.start();
		System.out.println("stopWatch Start !");
		
		pjp.proceed(); //포인트컷으로 지정한 메소드 실행(해당 메소드에 출력문구 하나 작성)
		
		stopWatch.stop();
		System.out.println("stopWatch Stop !");
		
		System.out.println("메소드 수행 시간 : " + stopWatch.getTotalTimeMillis() + "초(ms)");
		
	}
	
	*/
	
	/* Advice를 적용할 메소드를 Pointcut으로 지정할 때, 위와 같이 패턴을 적용할수도 있고, 어노테이션 유뮤로 지정할 수 있음.
	 * 
	 * - 사용자 정의 어노테이션을 생성하고, 해당 어노테이션을 이용하고 대상 메소드(포인트컷)를 지정할것임. (NoLoginChk.java 생성 및 로그인 필요없는 컨트롤러 메소드에 어노테이션 추가)
	 * - 우리가 편집 할 수 있는 파일중에, 클라이언트 요청을 가장 먼저 받는 Controller의 모든 메소드에서, 로그인 유무를 체크할것인데
	 *   메소드들중, 로그인 체크를 해야하는 메소드와 하지 않아야하는 메소드를 구분하여 포인트컷으로 잡기에는 귀찮고, 코드 또한 늘어남.
	 * - 방금 생성한 사용자 어노테이션(NoLoginChk)가 정의된 메소드는 로그인 유무 체크를 하지 않고, 그 외 모든 메소드들에 대해서 로그인 여부 검사  
	 *   
	 */
	
	@Autowired
	private JwtUtils jwtUtils;
	
	//controller 패키지 내부 파일들에 작성된 모든 메소드
	@Pointcut("execution(* kr.or.iei.*.controller.*.*(..))")
	public void allControllerPointCut() {}
		
	//NoLoginChk 어노테이션
	@Pointcut("@annotation(kr.or.iei.common.annotation.NoTokenCheck)")
	public void noTokenCheckAnnotation() {}
	
	//모든 Controller 모든 메소드 중, NoTokenCheck 어노테이션이 작성되지 않은 메소드 
	@Before("allControllerPointCut() && !noTokenCheckAnnotation()")
	public void validateCheck() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
		
		//요청 헤더에 포함된 토큰 추출(url에 따라 리프레시 토큰 또는 액세스 토큰 추출)
		//URI ex : /admin/member
		//URL ex : http://localhost:9999/admin/member
		String uri = request.getRequestURI();
		
		String token = uri.endsWith("refresh") 
				       ? request.getHeader("refreshToken") 
				       : request.getHeader("Authorization");
		
		
		
		//토큰 유효성 검증 실패 시, 예외를 발생시킴 => Before는 컨트롤러 이전에 실행되므로 예외가 컨트롤러로 전달되지 않음. => 예외처리기 작성
		//헤더에 토큰이 포함되지 않아 null이여도 validate에서 Exception 발생.
		//이제 모든 컨트롤러, 서비스 메소드에서 토큰 검증 및 검증 결과에 따른 응답 처리 부분 제거
		Object resObj = jwtUtils.validateToken(token);
		
		if(resObj instanceof HttpStatus httpStatus) {
			CommonException ex = new CommonException("invalid jwtToken in request Header");
			ex.setErrorCode(httpStatus);
			throw ex;
			
		}
		
		if(resObj instanceof Member member){//토큰 검증 결과 Object가 Member일 때
			
			//관리자 기능 메소드 요청인데, 일반 회원인 경우
			if(uri.startsWith("/admin") && member.getMemberLevel() != 1) {
				CommonException ex = new CommonException("invalid jwtToken in request Header");
				//ex.setErrorCode(401);
				throw ex;
			}
		}
		
	}
	
}
