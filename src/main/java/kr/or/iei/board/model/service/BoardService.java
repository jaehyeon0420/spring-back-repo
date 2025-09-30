package kr.or.iei.board.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.iei.board.model.dao.BoardDao;
import kr.or.iei.board.model.dto.Board;
import kr.or.iei.board.model.dto.BoardFile;
import kr.or.iei.common.model.dto.PageInfo;
import kr.or.iei.common.util.PageUtil;

@Service
public class BoardService {
	
	@Autowired
	private BoardDao dao;
	
	@Autowired
	private PageUtil pageUtil;

	public HashMap<String, Object> selectBoardList(int reqPage) {
		
		int viewCnt = 12;							//한 페이지당 게시물 수
		int pageNaviSize = 5;						//페이지 네비게이션 길이
		int totalCount = dao.selectBoardCount();	//전체 게시글 수
		
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
	public int insertBoard(Board board) {
		int boardNo = dao.selectBoardNo();
		
		board.setBoardNo(boardNo);
		
		int result = dao.insertBoard(board);
		
		if(result > 0) {
			ArrayList<BoardFile> fileList = (ArrayList<BoardFile>) board.getFileList();
			
			if(fileList != null) {//등록한 파일 없는 경우, Controller에서 ArrayList 생성하지 않음.
				
				for(int i=0; i<fileList.size(); i++) {
					BoardFile file = fileList.get(i);
					file.setBoardNo(boardNo);
					
					dao.insertBoardFile(file);
				}
			}
		}
		
		return result;
	}

	public Board selectOneBoard(int boardNo) {
		Board board = dao.selectOneBoard(boardNo);
		//게시글에 대한 파일은, Mybatis에서 제공하는 resultMap 사용.
		//board.setFileList(dao.selectBoardFileList(boardNo));
		return board;
	}

	public BoardFile selectBoardFile(int boardFileNo) {
		return dao.selectBoardFile(boardFileNo);
	}

	@Transactional
	public Board deleteBoard(int boardNo) {
		Board board = dao.selectOneBoard(boardNo);
		
		if(board != null) {
			//파일은 Cascade에 의해 자동 삭제
			int result = dao.deleteBoard(boardNo);
			
			if(result > 0) {
				return board;
			}else {
				return null;
			}
		}
		
		return board;
	}

	@Transactional
	public ArrayList<BoardFile> updateBoard(Board board) {
		//게시글 수정
		int result = dao.updateBoard(board);
		
		if(result > 0) {
			//서버에서 파일 삭제를 위해, 삭제 대상 파일 리스트 조회 후 삭제
			ArrayList<BoardFile> delFileList = new ArrayList<>();
			if(board.getDelBoardFileNo() != null) {
				//조회
				delFileList = dao.selectDelBoardFile(board.getDelBoardFileNo());
				
				//삭제
				result += dao.deleteBoardFile(board.getDelBoardFileNo());
				
			}
			
			//새 첨부파일 등록
			if(board.getFileList() != null) {
				for(BoardFile newFile : board.getFileList()) {
					result += dao.insertBoardFile(newFile);
				}
			}
			
			return delFileList;
		}
		
		return null;
	}
}
