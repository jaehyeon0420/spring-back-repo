package kr.or.iei.member.model.dao;

import org.apache.ibatis.annotations.Mapper;

import kr.or.iei.member.model.dto.Member;

@Mapper
public interface MemberDao {

	int insertMember(Member member);

	int idDuplChk(String memberId);

	Member memberLogin(String memberId);

	Member selectOneMember(String memberId);

	int updateMember(Member member);

	int updateMemberPw(Member member);

	int deleteMember(String memberId);


}
