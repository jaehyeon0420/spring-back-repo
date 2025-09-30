package kr.or.iei.board.model.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import kr.or.iei.board.model.dto.Board;
import kr.or.iei.board.model.dto.BoardFile;
import kr.or.iei.common.model.dto.PageInfo;

@Mapper
public interface BoardDao {

	int selectBoardCount();

	ArrayList<Board> selectBoardList(PageInfo pageInfo);

	int selectBoardNo();

	int insertBoard(Board board);

	int insertBoardFile(BoardFile file);

	Board selectOneBoard(int boardNo);

	BoardFile selectBoardFile(int boardFileNo);

	int deleteBoard(int boardNo);

	int updateBoard(Board board);

	ArrayList<BoardFile> selectDelBoardFile(int[] delBoardFileNo);

	int deleteBoardFile(int[] delBoardFileNo);

}
