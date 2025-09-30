package kr.or.iei.board.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "게시판 파일 객체")
public class BoardFile {
	@Schema(description = "파일 번호", type = "number")
	private int boardFileNo;
	@Schema(description = "게시글 번호", type = "number")
	private int boardNo;
	@Schema(description = "사용자 업로드 파일명", type = "string")
	private String fileName;
	@Schema(description = "서버 업로드 파일명", type = "string")
	private String filePath;
	
}
