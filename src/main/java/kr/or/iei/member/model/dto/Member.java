package kr.or.iei.member.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Member 클래스")
public class Member {
	@Schema(description = "회원아이디", type = "string")
	private String memberId;
	@Schema(description = "회원비밀번호", type = "string")
	private String memberPw;
	@Schema(description = "회원이름", type = "string")
	private String memberName;
	@Schema(description = "회원전화번호", type = "string")
	private String memberPhone;
	@Schema(description = "회원등급", type = "number")
	private int memberLevel;
}

