package kr.or.iei.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import kr.or.iei.common.util.JwtUtils;
import kr.or.iei.member.model.dao.MemberDao;
import kr.or.iei.member.model.dto.LoginMember;
import kr.or.iei.member.model.dto.Member;

@Service
public class MemberService {
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private JwtUtils jwtUtils;

	@Transactional
	public int insertMember(Member member) {
		String encodePw = encoder.encode(member.getMemberPw());
		member.setMemberPw(encodePw);
		return memberDao.insertMember(member);
	}

	public int idDuplChk(String memberId) {
		return memberDao.idDuplChk(memberId);
	}

	public LoginMember memberLogin(Member member) {
		//아이디만 전달
		Member selectMember = memberDao.memberLogin(member.getMemberId());
		
		//아이디 잘못 입력 시, 회원정보가 없으니 null이 리턴됨. 패스워드 검사 이전에 null 처리
		if(selectMember == null) {
			return null;
		}
		
		//입력 비밀번호와 DB에 저장된 암호화 비밀번호 검증
		if(encoder.matches(member.getMemberPw(), selectMember.getMemberPw())) {
			String accessToken = jwtUtils.createAccessToken(selectMember.getMemberId(), selectMember.getMemberLevel());
			String refreshToken = jwtUtils.createRefreshToken(selectMember.getMemberId(), selectMember.getMemberLevel());
			
			//비밀번호는 프론트에서 보관하지 않을 것이므로 null 처리
			selectMember.setMemberPw(null);
			LoginMember loginMember = new LoginMember(accessToken, refreshToken, selectMember);
			return loginMember;
		}else {			
			return null;
		}
	}


	//refreshToken으로 accessToken 재발급 요청 - AOP 적용 이전
	/*
	public Object refreshToken(String refreshToken, Member member) {
		//1.refreshToken도 만료되었는지 검증
		Object resObj = jwtUtils.validateToken(refreshToken);
		
		if(resObj instanceof HttpStatus httpStatus) {
			//refreshToken도 만료되었거나(403), 토큰 불일치(401) 
			return resObj;
		}else {
			//검증 리턴값이 Member면 검증 통과 == accessToken 현재 시간 기준으로 재발급!			
			String accessToken = jwtUtils.createAccessToken(member.getMemberId(), member.getMemberLevel());
			
			return accessToken;
		}
		
	}*/
	
	//refreshToken으로 accessToken 재발급 요청 - AOP 적용 이후
	
	public String refreshToken(Member member) {
		//전달받은 아이디와 레벨로 액세스 토큰 재발급	
		String reAccessToken = jwtUtils.createAccessToken(member.getMemberId(), member.getMemberLevel());
		return reAccessToken;
		
	}
	
	
	//마이페이지 - AOP 적용 이전
	/*
	public Object selectOneMember(String accessToken, String memberId) {
		Object resObj = jwtUtils.validateToken(accessToken);
		
		if(resObj instanceof HttpStatus httpStatus) {
			//accessToken 만료되었거나(403), 토큰 불일치(401) 
			return resObj;
		}else {
			//리턴 값이 Member면 accessToken 검증 통과 == DB에서 정보 조회
			
			Member member = memberDao.selectOneMember(memberId);
			member.setMemberPw(null); //화면에서 보여줄 필요 없음.
			return member;
		}
	}
	*/
	//마이페이지 - AOP 적용 이후 - 매퍼에서 조회 컬럼 중, 비밀번호 제거할 것
	public Member selectOneMember(String memberId) {
		Member member = memberDao.selectOneMember(memberId);
		return member;
	}
	
	//회원 정보 수정 - AOP 적용 이전
	/*
	@Transactional
	public Object updateMember(String accessToken, Member member) {
		Object resObj = jwtUtils.validateToken(accessToken);
		
		if(resObj instanceof HttpStatus httpStatus) {
			//accessToken 만료되었거나(403), 토큰 불일치(401) 
			return resObj;
		}else {
			//리턴 값이 Member면 accessToken 검증 통과 == DB 정보 수정
			return memberDao.updateMember(member);
		}
	}*/
	//회원 정보 수정 - AOP 적용 이후
	@Transactional
	public int updateMember(Member member) {
		return memberDao.updateMember(member);
		
	}
	
	//비밀번호 변경 전, 기존 비밀번호 체크 - AOP 적용 이전
	/*
	public Object chkMemberPw(String accessToken, Member member) {
		Object resObj = jwtUtils.validateToken(accessToken);
		
		if(resObj instanceof HttpStatus httpStatus) {
			//accessToken 만료되었거나(403), 토큰 불일치(401) 
			return resObj;
		}else {
			//리턴 값이 Member면 accessToken 검증 통과 == 기존 비밀번호 체크! 메소드 재사용
			Member m = memberDao.selectOneMember(member.getMemberId()); 
			
			//평문 비밀번호와 암호화 비밀번호 일치성 검증
			if(encoder.matches(member.getMemberPw(), m.getMemberPw())) {
				return true;
			}
			
			return false;
		}
	}*/
	
	//비밀번호 변경 전, 기존 비밀번호 체크 - AOP 적용 이후
	public boolean chkMemberPw(Member member) {
		Member m = memberDao.selectOneMember(member.getMemberId()); 
		
		//평문 비밀번호와 암호화 비밀번호 일치성 검증
		if(encoder.matches(member.getMemberPw(), m.getMemberPw())) {
			return true;
		}
		
		return false;
	}
	
	//비밀번호 변경 - AOP 적용 이전
	/*
	@Transactional
	public Object updateMemberPw(String accessToken, Member member) {
		Object resObj = jwtUtils.validateToken(accessToken);
		
		if(resObj instanceof HttpStatus httpStatus) {
			//accessToken 만료되었거나(403), 토큰 불일치(401) 
			return resObj;
		}else {
			//리턴 값이 Member면 accessToken 검증 통과 == 비밀번호 변경
			
			//입력 비밀번호 암호화 처리 후, 재할당
			String encodePw = encoder.encode(member.getMemberPw());
			member.setMemberPw(encodePw);
			
			int result = memberDao.updateMemberPw(member); 
			
			return result;
		}
	} */
	
	//비밀번호 변경 - AOP 적용 이후
	@Transactional
	public int updateMemberPw(Member member) {			
		//입력 비밀번호 암호화 처리 후, 재할당
		String encodePw = encoder.encode(member.getMemberPw());
		member.setMemberPw(encodePw);
		
		int result = memberDao.updateMemberPw(member); 
		
		return result;
		
	}
	
	//회원 탈퇴 - AOP 적용 이전
	/*
	@Transactional
	public Object deleteMember(String accessToken, String memberId) {
		Object resObj = jwtUtils.validateToken(accessToken);
		
		if(resObj instanceof HttpStatus httpStatus) {
			//accessToken 만료되었거나(403), 토큰 불일치(401) 
			return resObj;
		}else {
			//리턴 값이 Member면 accessToken 검증 통과 == 회원 탈퇴 처리
			
			int result = memberDao.deleteMember(memberId); 
			
			return result;
		}
	} */
	
	//회원 탈퇴 - AOP 적용 이후
	@Transactional
	public int deleteMember(String memberId) {
		int result = memberDao.deleteMember(memberId); 
		
		return result;
	}
}
