package kr.or.iei.admin.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.iei.admin.model.dao.AdminDao;
import kr.or.iei.board.model.dao.BoardDao;
import kr.or.iei.board.model.dto.Board;
import kr.or.iei.common.model.dto.PageInfo;
import kr.or.iei.common.util.PageUtil;
import kr.or.iei.member.model.dao.MemberDao;
import kr.or.iei.member.model.dto.Member;

@Service
public class AdminService {
	
	@Autowired
	private AdminDao dao;
	
	@Autowired
	private PageUtil pageUtil;
	
	public HashMap<String, Object> selectBoardList(int reqPage) {
		
		//게시글 목록과 다르게 10개씩
		int viewCnt = 10;								//한 페이지당 게시물 수
		int pageNaviSize = 5;							//페이지 네비게이션 길이
		int totalCount = dao.selectBoardCount();		//전체 게시글 수
		
		//페이징 정보
		PageInfo pageInfo = pageUtil.getPageInfo(reqPage, viewCnt, pageNaviSize, totalCount);
		
		//게시글 목록
		ArrayList<Board> boardList = dao.selectBoardList(pageInfo);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("boardList", boardList);
		resultMap.put("pageInfo", pageInfo);
		
		return resultMap;
	}

	@Transactional
	public int updateBoardStatus(Board board) {
		return dao.updateBoardStatus(board);
	}

	public HashMap<String, Object> selectMemberList(int reqPage) {
		
		int viewCnt = 10;								//한 페이지당 회원 수
		int pageNaviSize = 5;							//페이지 네비게이션 길이
		int totalCount = dao.selectMemberCount();		//전체 회원 수
		
		//페이징 정보
		PageInfo pageInfo = pageUtil.getPageInfo(reqPage, viewCnt, pageNaviSize, totalCount);
		
		//회원 목록
		ArrayList<Member> memberList = dao.selectMemberList(pageInfo);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("memberList", memberList);
		resultMap.put("pageInfo", pageInfo);
		
		return resultMap;
	}

	@Transactional
	public int updateMemberLevel(Member member) {
		return dao.updateMemberLevel(member);
	}
	
	
}
