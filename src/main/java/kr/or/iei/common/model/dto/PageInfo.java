package kr.or.iei.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "페이징 정보 객체")
public class PageInfo {
	@Schema(description = "시작 번호", type = "number")
	private int start;
	@Schema(description = "끝 번호", type = "number")
	private int end;
	@Schema(description = "페이지 시작 번호", type = "number")
	private int pageNo;
	@Schema(description = "페이지 네비게이션 사이즈", type = "number")
	private int pageNaviSize;
	@Schema(description = "전체 페이지 갯수", type = "number")
	private int totalPage;
}
