package kr.or.iei.admin.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.iei.admin.model.service.AdminService;
import kr.or.iei.board.model.dto.Board;
import kr.or.iei.common.model.dto.ResponseDTO;
import kr.or.iei.member.model.dto.Member;

@CrossOrigin("*")
@RestController
@RequestMapping("/admins")
@Tag(name = "ADMIN", description = "관리자 기능 API")
public class AdminController {
	/* 현재 컨트롤러에 작성된 모든 요청은 관리자만 요청 가능.
	 * 메소드 호출 이전에, 요청 사용자가 관리자인지 체크 => AOP에 추가 작성
	 * */
	
	@Autowired
	private AdminService adminService;
	
	
	//@RequestParam : 쿼리스트링 형식 데이터 추출
	//게시글 목록 조회
	@GetMapping("/board")
	@Operation(summary = "게시글 목록 조회", description = "페이지 번호에 따른, 게시글 목록 조회")
	public ResponseEntity<ResponseDTO> getBoardList(@RequestParam int reqPage) {
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 목록 조회 중, 오류가 발생하였습니다.", null, "error");
		
		try {
			HashMap<String, Object> boardMap = adminService.selectBoardList(reqPage);
			res = new ResponseDTO(HttpStatus.OK, "", boardMap, "");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	@PatchMapping("/board")
	@Operation(summary = "게시글 상태 변경", description = "게시글 상태(공개/비공개) 변경")
	public ResponseEntity<ResponseDTO> updateBoardStatus(@RequestBody Board board){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 상태 변경 중, 오류가 발생하였습니다.", false, "error");
		
		try {
			int result = adminService.updateBoardStatus(board);
			
			if(result > 0) {
				res = new ResponseDTO(HttpStatus.OK, "", true, "");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	@GetMapping("/member")
	@Operation(summary = "회원 목록 조회", description = "페이지 번호에 따른 회원 목록 조회")
	public ResponseEntity<ResponseDTO> getMemberList(@RequestParam int reqPage){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "조회 실패", null, "error");
		
		try {
			HashMap<String, Object> memberMap = adminService.selectMemberList(reqPage);
			res = new ResponseDTO(HttpStatus.OK, "", memberMap, "");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	@PatchMapping("/member")
	@Operation(summary = "회원 등급 변경", description = "회원 등급 변경")
	public ResponseEntity<ResponseDTO> updateMemberLevel(@RequestBody Member member){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "회원 등급 변경 중, 오류가 발생하였습니다.", false, "error");
		
		try {
			int result = adminService.updateMemberLevel(member);
			if(result > 0) {
				res = new ResponseDTO(HttpStatus.OK, "", true, "");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
}
