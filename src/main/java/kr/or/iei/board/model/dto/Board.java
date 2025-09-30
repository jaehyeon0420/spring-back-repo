package kr.or.iei.board.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "게시판 객체")
public class Board {
	@Schema(description = "게시글 번호", type = "number")
	private int boardNo;
	@Schema(description = "게시글 제목", type = "string")
	private String boardTitle;
	 //게시글 작성 시, 동일한 이름의 파일 객체를 이 변수에 넣으려고 해서, 오류 발생함.
	@Schema(description = "게시글 썸네일 이미지 경로", type = "string")
	private String boardThumbPath;
	@Schema(description = "게시글 내용", type = "string")
	private String boardContent;
	@Schema(description = "게시글 작성자", type = "string")
	private String boardWriter;
	@Schema(description = "게시글 상태(공개/비공개)", type = "string")
	private int boardStatus;
	@Schema(description = "게시글 작성일", type = "string")
	private String boardDate;
	
	//게시글 수정 시, 삭제 파일 번호 리스트 추가
	@Schema(description = "삭제 파일번호 배열", type = "number Arr")
	private int [] delBoardFileNo;
	
	@Schema(description = "게시글 파일 리스트", type = "ArrayList")
	private List<BoardFile> fileList;
}
