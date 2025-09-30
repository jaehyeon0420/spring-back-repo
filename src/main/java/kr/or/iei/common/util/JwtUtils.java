package kr.or.iei.common.util;

import java.util.Calendar;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kr.or.iei.member.model.dto.LoginMember;
import kr.or.iei.member.model.dto.Member;

@Component
public class JwtUtils{
	
	//application.properties에 선언된 값.
	@Value("${jwt.secret-key}")
	private String jwtSecretKey;
	@Value("${jwt.expire-minute}")
	private int jwtExpireMinute;
	@Value("${jwt.expire-hour-refresh}")
	private int jwtExpireHourRefresh;
	
	//1시간 유효 토큰 생성
	public String createAccessToken(String memberId, int memberLevel) {
		//1. 내부에서 사용할 방식으로, 정의한 key 변환
		SecretKey key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
		
		//2. 토큰 생성시간 및 만료 시간 설정
			//유효 시작시간
			Calendar calendar = Calendar.getInstance(); //현재시간
			Date startTime = calendar.getTime();		//현재시간 == 유효 시작시간
			//유효 만료시간
			calendar.add(Calendar.MINUTE, jwtExpireMinute); //현재시간 기준으로, 10분 더하기 
			Date expireTime = calendar.getTime();
		
		String accessToken = Jwts.builder()							//builder를 이용해 토큰 생성
							  .issuedAt(startTime)					//시작시간
							  .expiration(expireTime)				//만료시간
							  .signWith(key)						//암호화 서명
							  .claim("memberId", memberId)			//토큰에 포함할 회원정보(key = value 형태)
							  .claim("memberLevel", memberLevel)	//토큰에 포함할 회원정보(key = value 형태)
							  .compact();							//생성
		return accessToken;
	}
	
	//1년 유효 토큰 생성(코드는 동일. 유효시간만 변경)
	public String createRefreshToken(String memberId, int memberLevel) {
		//1. 내부에서 사용할 방식으로, 정의한 key 변환
		SecretKey key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
		
		//2. 토큰 생성시간 및 만료 시간 설정
			//유효 시작시간
			Calendar calendar = Calendar.getInstance(); //현재시간
			Date startTime = calendar.getTime();		//현재시간 == 유효 시작시간
			//유효 만료시간
			calendar.add(Calendar.HOUR, jwtExpireHourRefresh); //현재시간 기준으로, 2주 더하기 
			Date expireTime = calendar.getTime();
		
		String refreshToken = Jwts.builder()							//builder를 이용해 토큰 생성
							  .issuedAt(startTime)					//시작시간
							  .expiration(expireTime)				//만료시간
							  .signWith(key)						//암호화 서명
							  .claim("memberId", memberId)			//토큰에 포함할 회원정보(key = value 형태)
							  .claim("memberLevel", memberLevel)	//토큰에 포함할 회원정보(key = value 형태)
							  .compact();							//생성
		return refreshToken;
	}
	
	//토큰 검증(액세스 토큰 or 리프레시 토큰)
	public Object validateToken(String token) {
		System.out.println("token : " + token);
		Member m = new Member();
		
		try {
			
			//1. 토큰 해석을 위한 암호화 키 세팅
			SecretKey key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
			Claims claims = (Claims) Jwts.parser()			//토큰 해석 시작
					.verifyWith(key)	//해석에 필요한 key
					.build()
					.parse(token)	//해석 대상 토큰
					.getPayload();
			
			//토큰에서 데이터 추출
			String memberId = (String) claims.get("memberId");
			int memberLevel = (int) claims.get("memberLevel");
			
			m.setMemberId(memberId);
			m.setMemberLevel(memberLevel);
			
			//System.out.println(memberId);
			//System.out.println(memberLevel);
		}catch (SignatureException e) {
			//발급 토큰과 요청 토큰 불일치
			return HttpStatus.UNAUTHORIZED;
		}catch (Exception e) {
			//토큰 시간 만료
			return HttpStatus.FORBIDDEN;
		}
		//원래는 위 2가지 경우 모두 401 응답. 클라이언트에서는 응답 메시지를 통해 재발급 요청 또는 비인가접근 처리해야 함.
		
		return m;
	}
}
