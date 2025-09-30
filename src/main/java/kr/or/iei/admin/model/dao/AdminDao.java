package kr.or.iei.admin.model.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import kr.or.iei.board.model.dto.Board;
import kr.or.iei.common.model.dto.PageInfo;
import kr.or.iei.member.model.dto.Member;

@Mapper
public interface AdminDao {

	int selectBoardCount();

	ArrayList<Board> selectBoardList(PageInfo pageInfo);

	int updateBoardStatus(Board board);

	int selectMemberCount();

	ArrayList<Member> selectMemberList(PageInfo pageInfo);

	int updateMemberLevel(Member member);

}
