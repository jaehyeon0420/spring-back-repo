package kr.or.iei.member.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "로그인 회원 객체")
public class LoginMember {
	@Schema(description = "access 토큰", type = "string")
	private String accessToken;
	@Schema(description = "refresh 토큰", type = "string")
	private String refreshToken;
	@Schema(description = "회원 객체", type = "member")
	private Member member;/*
							 * private String memberId; private int memberLevel;
							 */
}
