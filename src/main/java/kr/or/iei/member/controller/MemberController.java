package kr.or.iei.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.or.iei.common.annotation.NoTokenCheck;
import kr.or.iei.common.exception.CommonException;
import kr.or.iei.common.model.dto.ResponseDTO;
import kr.or.iei.common.util.JwtUtils;
import kr.or.iei.member.model.dto.LoginMember;
import kr.or.iei.member.model.dto.Member;
import kr.or.iei.member.model.service.MemberService;


@RestController
@CrossOrigin("*")
@RequestMapping("/members")
@Tag(name = "MEMBER", description = "회원관리 API")
public class MemberController {

	/* 회원관리 개발 후
	 * 회원가입 => 로그인 이후, 모든 요청에 대해 토큰 검증을 해야하므로 귀찮기도 하고, 코드가 중복됨.
	 * 컨트롤러 메소드 중, 토큰 체크 필요한 메소드가 호출되기 이전에 검증 코드를
	 * AOP 사용하여 처리하는것으로 변경
	 * 
	 * 1) AOP 생성
	 * 2) 사용자 정의 어노테이션 생성(AOP에서 메소드 구분을 위함)
	 * 3) 사용자 정의 Exception 생성
	 * 4) ExceptionHandler 생성
	 * 5) 기존 토큰 검증 로직 및 검증 결과에 따른 처리 부분 제거
	 *    단, 토큰 검증 후 리턴되는 Member 정보를 사용하는 곳은 제외.
	 *    (MemberService.selectOneMember)
	 */
	
	@Autowired
	private MemberService memberService;

	
	@PostMapping //등록 == POST
	@NoTokenCheck //로그인 체크 X
	@Operation(summary = "회원가입", description = "회원정보를 입력받아 회원 등록 처리")
	public ResponseEntity<ResponseDTO> memberJoin(@RequestBody Member member){
		//실패했을 때 응답 객체 초기 세팅
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "회원가입 중, 오류가 발생하였습니다.", false, "error");
		
		try {
			int result = memberService.insertMember(member);
			
			if(result > 0) {
				res = new ResponseDTO(HttpStatus.OK, "회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.", true, "success");
			}else {
				res = new ResponseDTO(HttpStatus.OK, "회원가입 중, 오류가 발생하였습니다.", false, "warning");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
		
	}
	
	//아이디 중복체크
	@GetMapping("/{memberId}/id-check")
	@NoTokenCheck //로그인 체크 X
	@Operation(summary = "아이디 중복 체크", description = "회원가입할 아이디를 입력받아, 중복 체크 처리")
	public ResponseEntity<ResponseDTO> idDuplChk(@PathVariable String memberId) {
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "아이디 중복 체크 중, 오류가 발생하였습니다.", false, "error");
		
		try {
			int count = memberService.idDuplChk(memberId);
			
			res = new ResponseDTO(HttpStatus.OK, "", count, "");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	/*
	 기존 SSR(Server Side Rendering)에서 로그인 정보를 세션에 등록하였음.
	 REST API는 무상태성(StateLess)의 특징으로, 서버가 클라이언트의 특정 정보를 저장하지 않도록 설계.
	 로그인 시, 서버는 유효한 Token을 발급하고 클라이언트는 이후 요청시마다 토큰을 서버로 전달하고 서버는 토큰의 유효성을 검사함.
	 이 때 사용되는 토큰은 JWT(JSON Web Token).
	 - pom.xml에 라이브러리 3개 추가. (mavenrepository에 "jjwt" 검색)
	 - application.properties 수정
	 	jwt.secret-key : 토큰 발급 및 복호화 시 사용되는 Key
	 	jwt.expire-minute=10 : 유효 시간 10분
		jwt.expire-hour-refresh=336 : 자동 로그인 기간(2주)
	 - JwtUtils.java 생성
	 
	 - accessToken : API에 접근 권한 인증 용도로 사용
	   10분마다 재발급 처리됨. 유출되어 해커가 사용하더라도 시간 제한을 10분으로 두는 것!
	 - refreshToken : accessToken 재발급 용도
	   자동 로그인 기능! 브라우저 종료 및 재접속해도 해당 기간동안 자동 로그인 처리.
	 * */
	@PostMapping("/auth/login")
	@NoTokenCheck
	@Operation(summary = "로그인", description = "아이디 및 비밀번호를 입력받아 로그인 처리")
	public ResponseEntity<ResponseDTO> memberLogin(@RequestBody Member member){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "로그인 시도 중, 오류가 발생하였습니다.", null, "error");
		
		try {
			//예외 발생 시, 초기 res 변수 세팅값으로 응답!
			LoginMember loginMember = memberService.memberLogin(member);
			
			if(loginMember != null) {
				res = new ResponseDTO(HttpStatus.OK, "", loginMember, ""); //axios 인터셉터에서 clientMsg 존재시에만, alert 띄우므로 아이콘 설정 불필요
			}else {
				res = new ResponseDTO(HttpStatus.OK, "아이디 및 비밀번호를 확인하세요.", null, "warning");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	
	/* 토큰 사용법 확인용 마이페이지 
	 @Autowired
	private JwtUtils jwtUtils;
	@GetMapping
	public ResponseEntity<Member> myPage(HttpServletRequest request) {
		//System.out.println(jwtToken); -> 로그인 시 발급받은 토큰 정보
		//토큰이 RequestHeader에 포함되었다고 해서 통과!가 아니라, 유효시간이 지났는지?
		//토큰에 사용된 Key가 application.properties에 작성한 key와 같은지? 검증 절차 필요. jwtUtils에 작성
		
		String accessToken = request.getHeader("Authorization");
		System.out.println("로그인 시 발급 accessToken : " + accessToken);
		Object validateRes = jwtUtils.validateToken(accessToken);
		
		if(validateRes instanceof HttpStatus httpStatus) {
			return ResponseEntity.status(httpStatus).build();
		}else {
			return ResponseEntity.ok((Member) validateRes);
		}
		
	}*/
	
	
	//AOP 적용 이전
	/* accessToken이 만료되어 refreshToken으로 accessToken 토큰 재발급 요청 처리.
	 * 
	 * 요청 흐름
	 * 
	 * (1) ex 사용자가 게시글 작성 요청.
	 * (2) 게시글 작성 도중 Service에서, 액세스 토큰 검증 및 액세스 토큰 만료되어 403 응답
	 * (3) axios response Interceptor의 에러 처리에서, "/refresh"로 재발급 요청하며, 리프레시 토큰 헤더에 포함.
	 * (4) 재발급 이전에 Service에서, 리프레시 토큰 검증 메소드 호출
	 * (5) axios response Interceptor의 에러 처리에서, 재발급된 액세스 토큰 스토어 및 axios 헤더에 등록 후, 기존 요청 (1) 재요청
	 */
	/*
	@PostMapping("/refresh")
	@Operation(summary = "토큰 재발급", description = "토큰 검증 및 만료 시, 전달한 아이디 및 레벨로 재발급")
	public ResponseEntity<ResponseDTO> refreshToken(HttpServletRequest request, @RequestBody Member member) {
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 재발급 실패", null);
		
		try {
			//요청 헤더에서 리프레시 토큰 추출
			String refreshToken = request.getHeader("refreshToken");
			Object validateRes = memberService.refreshToken(refreshToken, member);
			
			if(validateRes instanceof HttpStatus httpStatus) {
				//refreshToken에도 문제가 있는 경우
				res = new ResponseDTO(httpStatus, "토큰 재발급 실패", null);
			}else {
				//정상적으로 accessToken이 발급된 경우
				res = new ResponseDTO(HttpStatus.OK, "토큰 재발급 성공", validateRes);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	*/
	
	//AOP 적용 이후
	/* accessToken이 만료되어 refreshToken으로 accessToken 토큰 재발급 요청 처리.
	 * 
	 * 요청 흐름
	 * 
	 * (1) ex 사용자가 게시글 작성 요청.
	 * (2) AOP에서 액세스 토큰 검증 후, 만료되어 403 응답
	 * (3) axios response Interceptor의 에러 처리에서, "/refresh"로 재발급 요청
	 * (4) AOP에서 리프레시 토큰 검증 후, 액세스 토큰 재발급 및 응답
	 * (5) axios response Interceptor의 에러 처리에서, 재발급된 토큰 스토어 및 axios 헤더에 등록 후, 기존 요청 (1) 재요청
	 * 
	 */
	
	@PostMapping("/refresh")
	@Operation(summary = "토큰 재발급", description = "토큰 검증 및 만료 시, 전달한 아이디 및 레벨로 재발급")
	public ResponseEntity<ResponseDTO> refreshToken(@RequestBody Member member) {
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "", null, "error");
		
		try {
			//정상적으로 accessToken이 발급된 경우
			String reAccessToken = memberService.refreshToken(member);
			res = new ResponseDTO(HttpStatus.OK, "", reAccessToken, "");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	
	//마이페이지 - AOP 적용 이전
	/*
	@GetMapping("/{memberId}")
	@Operation(summary = "회원 1명 정보 조회", description = "토큰 전달받아 회원 1명 정보 조회")
	public ResponseEntity<ResponseDTO> selectOneMember(@RequestHeader("Authorization") String accessToken, @PathVariable String memberId){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "정보 조회 중, 오류가 발생하였습니다.", null);
		
		try {
			Object member = memberService.selectOneMember(accessToken, memberId);
			
			if(member instanceof HttpStatus httpStatus) {
				//token에 문제가 있는 경우 
				res = new ResponseDTO(httpStatus, "조회 실패", null);
			}else {
				//정상적으로 조회된 경우
				res = new ResponseDTO(HttpStatus.OK, "", member);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}*/
	
	//마이페이지 - AOP 적용 이후
	@GetMapping("/{memberId}")
	@Operation(summary = "회원 1명 정보 조회", description = "회원 아이디 회원 1명 정보 조회")
	public ResponseEntity<ResponseDTO> selectOneMember(@PathVariable String memberId){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "정보 조회 중, 오류가 발생하였습니다.", null, "error");
		
		try {
			Member member = memberService.selectOneMember(memberId);
			
			if(member != null) {
				res = new ResponseDTO(HttpStatus.OK, "", member, "");				
			}else {
				res = new ResponseDTO(HttpStatus.OK, "회원 정보가 존재하지 않습니다.", null, "warning");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	//회원 정보 수정 - AOP 적용 이전
	/*
	@PatchMapping
	@Operation(summary = "회원 정보 수정", description = "수정할 회원 정보 입력받아 수정 처리")
	public ResponseEntity<ResponseDTO> updateMember(@RequestHeader("Authorization") String accessToken, @RequestBody Member member){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "수정 중, 오류가 발생하였습니다.", false);
		
		try {
			Object result = memberService.updateMember(accessToken, member);
			
			if(result instanceof HttpStatus httpStatus) {
				//token에 문제가 있는 경우 
				res = new ResponseDTO(httpStatus, "수정 중, 오류가 발생하였습니다.", false);
			}else {
				//정상적으로 수정된 경우
				if((Integer) result > 0){
					res = new ResponseDTO(HttpStatus.OK, "회원 정보 수정이 완료되었습니다.", true);
				}else{
					res = new ResponseDTO(HttpStatus.OK, "회원 정보 수정 중, 오류가 발생하였습니다.", false);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	} */
	
	//회원 정보 수정 - AOP 적용 이후
	@PatchMapping
	@Operation(summary = "회원 정보 수정", description = "수정할 회원 정보 입력받아 수정 처리")
	public ResponseEntity<ResponseDTO> updateMember(@RequestBody Member member){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "수정 중, 오류가 발생하였습니다.", false, "error");
		
		try {
			int result = memberService.updateMember(member);
			
			if(result > 0) {
				res = new ResponseDTO(HttpStatus.OK, "회원 정보 수정이 완료되었습니다.", true, "success");				
			}else {
				res = new ResponseDTO(HttpStatus.OK, "수정된 회원이 존재하지 않습니다.", false, "warning");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	//비밀번호 변경 이전, 기존 비밀번호 체크 - AOP 적용 이전
	/*
	@PostMapping("/auth/password-check")
	@Operation(summary = "기존 비밀번호 체크", description = "비밀번호 변경 시, 기존 비밀번호 입력 받아 일치성 체크")
	public ResponseEntity<ResponseDTO> chkMemberPw(@RequestHeader("Authorization") String accessToken, @RequestBody Member member){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "체크 실패", false);
		
		try {
			
			Object result = memberService.chkMemberPw(accessToken, member);
			
			if(result instanceof HttpStatus httpStatus) {
				//token에 문제가 있는 경우 
				res = new ResponseDTO(httpStatus, "체크 실패", false);
			}else {
				//비밀번호 일치 결과 리턴
				res = new ResponseDTO(HttpStatus.OK, "체크 성공", result);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}*/
	
	//비밀번호 변경 이전, 기존 비밀번호 체크 - AOP 적용 이후
	@PostMapping("/auth/password-check")
	@Operation(summary = "기존 비밀번호 체크", description = "비밀번호 변경 시, 기존 비밀번호 입력 받아 일치성 체크")
	public ResponseEntity<ResponseDTO> chkMemberPw(@RequestBody Member member){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "기존 비밀번호 체크 중, 오류가 발생하였습니다.", false, "error");
		
		try {
			
			boolean result = memberService.chkMemberPw(member);
			
			//비밀번호 일치 결과 리턴
			if(result) {
				res = new ResponseDTO(HttpStatus.OK, "기존 비밀번호가 일치합니다. 변경할 비밀번호를 입력하세요.", result, "success");				
			}else {
				res = new ResponseDTO(HttpStatus.OK, "기존 비밀번호가 일치하지 않습니다. ", result, "warning");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	//비밀번호 변경 - AOP 적용 이전
	/*
	@PatchMapping("/password")
	@Operation(summary = "비밀번호 변경", description = "새 비밀번호 입력 받아 비밀번호 변경 처리")
	public ResponseEntity<ResponseDTO> updateMemberPw(@RequestHeader("Authorization") String accessToken, @RequestBody Member member){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "비밀번호 변경 실패", false);
		
		try {
			
			Object result = memberService.updateMemberPw(accessToken, member);
			
			if(result instanceof HttpStatus httpStatus) {
				//token에 문제가 있는 경우 
				res = new ResponseDTO(httpStatus, "비밀번호 변경 실패", false);
			}else {
				//비밀번호 변경 결과 리턴
				if((Integer) result > 0) {
					res = new ResponseDTO(HttpStatus.OK, "비밀번호 변경 성공", true);					
				}else {
					res = new ResponseDTO(HttpStatus.OK, "비밀번호 변경 실패", false);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	*/
	
	//비밀번호 변경 - AOP 적용 이후
	@PatchMapping("/password")
	@Operation(summary = "비밀번호 변경", description = "새 비밀번호 입력 받아 비밀번호 변경 처리")
	public ResponseEntity<ResponseDTO> updateMemberPw(@RequestBody Member member){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "비밀번호 변경 중, 오류가 발생하였습니다.", false, "error");
		
		try {
			
			int result = memberService.updateMemberPw(member);
			
			if((Integer) result > 0) {
				res = new ResponseDTO(HttpStatus.OK, "비밀번호가 정상적으로 변경 되었습니다. 변경된 비밀번호로 다시 로그인 하시길 바랍니다.", true, "success");					
			}else {
				res = new ResponseDTO(HttpStatus.OK, "비밀번호 중, 오류가 발생하였습니다.", false, "warning");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	//회원 탈퇴 - AOP 적용 이전
	/*
	@DeleteMapping("/{memberId}")
	@Operation(summary = "회원 탈퇴", description = "토큰 전달받아, 회원 정보 삭제 처리")
	public ResponseEntity<ResponseDTO> deleteMember(@RequestHeader("Authorization") String accessToken, @PathVariable String memberId){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "회원탈퇴 실패", false);
		
		try {
			
			Object result = memberService.deleteMember(accessToken, memberId);
			
			if(result instanceof HttpStatus httpStatus) {
				//token에 문제가 있는 경우 
				res = new ResponseDTO(httpStatus, "회원탈퇴 실패", false);
			}else {
				//비밀번호 변경 결과 리턴
				if((Integer) result > 0) {
					res = new ResponseDTO(HttpStatus.OK, "회원탈퇴 성공", true);					
				}else {
					res = new ResponseDTO(HttpStatus.OK, "회원탈퇴 실패", false);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	*/
	
	//회원 탈퇴 - AOP 적용 이후
	@DeleteMapping("/{memberId}")
	@Operation(summary = "회원 탈퇴", description = "토큰 전달받아, 회원 정보 삭제 처리")
	public ResponseEntity<ResponseDTO> deleteMember(@PathVariable String memberId){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "회원 탈퇴 중, 오류가 발생하였습니다.", false, "error");
		
		try {
			
			int result = memberService.deleteMember(memberId);
			
			if(result > 0) {
				res = new ResponseDTO(HttpStatus.OK, "회원 탈퇴가 완료되었습니다.", true, "success");
			}else {
				res = new ResponseDTO(HttpStatus.OK, "회원 탈퇴 중, 오류가 발생하였습니다.", false, "warning");
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
}
