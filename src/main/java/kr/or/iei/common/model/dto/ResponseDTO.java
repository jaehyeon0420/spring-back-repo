package kr.or.iei.common.model.dto;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "응답 데이터 형식")
public class ResponseDTO {
									
	@Schema(description = "응답 상태", type="HttpStatus") //HTTP 통신 응답 코드(요청 처리 결과에 따라, 다른 응답 코드를 할당)
	private HttpStatus httpStatus;						
	@Schema(description = "응답 메시지", type="String") //클라이언트 응답 메시지
	private String clientMsg;
	@Schema(description = "응답 데이터(등록,수정,삭제 시 boolean. 조회 시 객체)", type="Object")
	private Object resData;
	@Schema(description = "응답 아이콘", type="String")
	private String alertIcon;						//통신 자체는(httpStatus) 정상이지만, 논리적 오류 발생 시 sweetalert 아이콘은 warning 또는 error 처리를 위함 
}

