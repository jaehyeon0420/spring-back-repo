package kr.or.iei.board.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.or.iei.board.model.dto.Board;
import kr.or.iei.board.model.dto.BoardFile;
import kr.or.iei.board.model.service.BoardService;
import kr.or.iei.common.model.dto.ResponseDTO;
import kr.or.iei.common.annotation.NoTokenCheck;
import kr.or.iei.common.util.FileUtils;

@RestController
@CrossOrigin("*")
@RequestMapping("/boards")
@Tag(name="BOARD", description = "게시판 API")
public class BoardController {
	
	@Autowired
	private BoardService service;
	
	@Autowired
	private FileUtils fileUtil;
	
	@Value("${file.uploadPath}")
	private String uploadPath;
	
	//@RequestParam : 쿼리스트링 형식 데이터 추출
	@GetMapping
	@NoTokenCheck //게시글 목록 조회는 로그인 없이도 가능하게 처리.
	@Operation(summary = "전체 게시글 조회", description = "요청 페이지에 따른 게시글 목록 조회")
	public ResponseEntity<ResponseDTO> selectBoardList(@RequestParam int reqPage) {
		/* 서버 -> 클라이언트로 응답할 데이터
		 * 1) 게시글 목록
		   2) 페이지 네비게이션에 필요한 데이터 (서버에서 제작하지 않음. 서버는 필요한 데이터 응답만)
		 */
		
		//실패 시, 응답 데이터 초기값 세팅 후 성공 시 변경 처리 예정
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 조회 중, 오류가 발생하였습니다.", null, "error");
		
		try {
			HashMap<String, Object> boardMap = service.selectBoardList(reqPage);
			res = new ResponseDTO(HttpStatus.OK, "", boardMap, "");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
		
	}
	
	/* 파일 업로드 경로 설정 (application.properties)
	 * 
	 * 미리 폴더 생성
	 * - C:/Temp/react/board/thumb
	 * - C:/Temp/react/editor
	 * 
	 * @ModelAttribute : multipart/form-data 형식일 때, 자바 객체로 바인딩
	 * */
	@PostMapping("/editorImage")
	@Operation(summary = "Editor 이미지 업로드", description = "Toast 에디터로 업로드한 파일 서버에 업로드")
	public ResponseEntity<ResponseDTO> uploadEditorImage(@ModelAttribute MultipartFile image){
		String filePath = "";
		
		//실패 시, 응답 데이터 초기값 세팅 후 성공 시 변경 처리 예정
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "에디터 이미지 업로드 중, 오류가 발생하였습니다.", null, "error"); //INTERNAL_SERVER_ERROR == 500
		
		try {
			filePath = fileUtil.uploadFile(image, "/editor/");
			
			//성공 시, 응답 데이터 객체 변경
			res = new ResponseDTO(HttpStatus.OK, "", "/editor/"+ filePath.substring(0,8) + "/" + filePath , "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//정상 업로드
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	/* @ModelAttribute : multipart/form-data 형식일 때, 자바 객체로 바인딩
	 */
	@PostMapping
	@Operation(summary = "게시글 등록", description = "게시글 등록 요청 시, 파일 및 게시글 등록 처리")
	public ResponseEntity<ResponseDTO> insertBoard(@ModelAttribute MultipartFile [] boardFile, 
			                                   @ModelAttribute MultipartFile  boardThumb,
			                                   @ModelAttribute Board board){
		//실패 시, 응답 데이터 초기값 세팅 후 성공 시 변경 처리 예정
		int result = 0;
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 등록 중, 오류가 발생하였습니다.", false, "error"); //INTERNAL_SERVER_ERROR == 500
		
		try {
			if(boardThumb != null) {
				String filePath = fileUtil.uploadFile(boardThumb, "/board/thumb/");
				board.setBoardThumbPath(filePath);
			}
			
			if(boardFile != null) {
				ArrayList<BoardFile> fileList = new ArrayList<>();
				
				for(int i=0; i<boardFile.length; i++) {
					String filePath = fileUtil.uploadFile(boardFile[i], "/board/");
					
					BoardFile file = new BoardFile();
					file.setFileName(boardFile[i].getOriginalFilename());
					file.setFilePath(filePath);
					
					fileList.add(file);
				}
				
				board.setFileList(fileList);
			}
			
			result = service.insertBoard(board);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//result 0인 경우 기존 res에 등록된 응답 객체로 리턴
		if(result > 0) {
			res = new ResponseDTO(HttpStatus.OK, "게시글이 등록 되었습니다.", true, "success");				
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	//상세보기 or 게시글 수정 시 호출
	@GetMapping("/{boardNo}")
	@NoTokenCheck
	@Operation(summary = "1개 게시글 조회", description = "게시글 번호를 전달받아, 1개 게시글에 대한 상세 정보 조회")
	public ResponseEntity<ResponseDTO> selectOneBoard(@PathVariable int boardNo){
		//실패 시, 응답 데이터 초기값 세팅 후 성공 시 변경 처리 예정
		
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 조회 중, 오류가 발생하였습니다.", null, "error");
		
		try {
			Board board = service.selectOneBoard(boardNo);
			res = new ResponseDTO(HttpStatus.OK, "", board, "");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
		
	}
	
	
	/* 파일 다운로드 시, return Type 
	 * org.springframework.core.io.Resource (자원을 의미함)
	 * 
	 * 파일 다운로드 메소드만, 응답 객체 형식이 ResponseDTO가 아님!
	 * JSON 응답이 아니라 파일 스트림 자체를 전달해야 브라우저가 파일 다운로드 동작을 인식하기 때문에.
	 * */
	@GetMapping("/file/{filePath}")
	@Operation(summary = "파일 다운로드", description = "파일 번호를 전달받아, 사용자가 업로드한 파일 다운로드 처리")
	public ResponseEntity<Resource> fileDown(@PathVariable String filePath) throws FileNotFoundException {
		
		//BoardFile boardFile = service.selectBoardFile(boardFileNo);
		String savePath = uploadPath+"/board/";
		File file = new File(savePath + filePath.substring(0, 8) + File.separator + filePath);
		
		//파일 다운로드를 위한 리소스 생성
		Resource resource = new InputStreamResource(new FileInputStream(file));
		
		//파일 다운로드를 위한 헤더 설정
		//org.springframework.http.HttpHeaders
		HttpHeaders headers = new HttpHeaders();
		
		/*
		 * HttpHeaders.CONTENT_DISPOSITION attachment : 브라우저가 파일을 다운로드하도록 유도
		 * HttpHeaders.CONTENT_TYPE : 응답 데이터가 바이너리 데이터임을 명시.
		 * 			org.springframework.http.MediaType;
		 * */
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;"); //파일명은 프론트에서 지정할거임.
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
		
		/* 파일을 대용량으로 다루는 SNS 사이트 같은 경우, 
		 * 파일 다운로드와 같이 변경되지 않는 정적 데이터를 서버에서 가져올 때,
		 * 프록시(중계) 서버에서 파일 데이터를 캐시함. (이후 요청 빠르게 처리).
		 *  최신 파일을 항상 다운로드하게 하려는 목적으로 해당 설정 제거.
		 * */
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		
		
		return ResponseEntity.status(HttpStatus.OK)
				             .headers(headers)
				             .contentLength(file.length())
				             .body(resource);
	}
	
	@DeleteMapping("/{boardNo}")
	@Operation(summary = "게시글 삭제", description = "게시글 번호를 전달받아, 게시글 및 파일 정보 삭제 처리")
	public ResponseEntity<ResponseDTO> deleteBoard(@PathVariable int boardNo){
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 삭제 중, 오류가 발생하였습니다.", false, "error");
		
		try {
			Board delBoard = service.deleteBoard(boardNo);
			
			if(delBoard != null) {
				File file = null;
				String savePath = "";
				
				//썸네일 제거
				if(delBoard.getBoardThumbPath() != null) { //썸네일 업로드 안했을수도 있으니 null 체크
					savePath = uploadPath + "/board/thumb/" + delBoard.getBoardThumbPath().substring(0,8) + File.separator + delBoard.getBoardThumbPath();
					file = new File(savePath);
					if(file.exists()) {
						file.delete();
					}					
				}
				
				//첨부파일 제거
				List<BoardFile> fileList = delBoard.getFileList();
				if(fileList != null) {
					savePath = uploadPath + "/board/";
					for(BoardFile delFile : fileList) {
						file = new File(savePath + delFile.getFilePath().substring(0,8) + File.separator + delFile.getFilePath());
						
						if(file.exists()) {
							file.delete();
						}
					}
				}
				
				//에디터 이미지 제거
			    // <img> 태그에서 src 속성을 추출하는 정규식 패턴
		        String regex = "<img[^>]*src=[\"']([^\"']+)[\"'][^>]*>";
		        Pattern pattern = Pattern.compile(regex); //java.util.regex.Pattern
		        Matcher matcher = pattern.matcher(delBoard.getBoardContent()); //java.util.regex.Matcher
	
		        // src URL 추출
		        while (matcher.find()) {
		            String imageUrl = matcher.group(1);  // 첫 번째 그룹에 이미지 URL이 있음
		            //System.out.println("이미지 URL: " + imageUrl); //http://localhost:9999/editor/20250521/20250521165146227_05755.jpg
		            
		            String filePath = imageUrl.substring(imageUrl.lastIndexOf("/") + 1); //마지막 파일명만 가져오기 => 20250521165146227_05755.jpg
		            savePath = uploadPath + "/editor/" + filePath.substring(0,8) + File.separator;
		            
		            file = new File(savePath + filePath);
		            if(file.exists()) {
		            	file.delete();
		            }
		        }
		        
		        //게시글 수정에서도, 게시글에 대한 기존 이미지들 삭제하기 위해서는 위 작업해야 함.
		        
		        
		        res = new ResponseDTO(HttpStatus.OK, "게시글이 삭제되었습니다.", true, "success");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
	
	
	@PatchMapping
	@Operation(summary = "게시글 수정", description = "게시글 및 파일 정보 전달받아 게시글 정보 수정 처리")
	public ResponseEntity<ResponseDTO> updateBoard (@ModelAttribute MultipartFile [] boardFile, 
			                                    @ModelAttribute MultipartFile boardThumb,
			                                    @ModelAttribute Board board,
			                                    String prevThumbPath){
		
		ResponseDTO res = new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 수정 중, 오류가 발생하였습니다.", false, "error");
		
		try {
			//새로운 썸네일 등록 시
			if(boardThumb != null) { 
				String filePath = fileUtil.uploadFile(boardThumb, "/board/thumb/");
				board.setBoardThumbPath(filePath);
				
				System.out.println("prevThumbPath : " +  prevThumbPath);
				//기존 썸네일 서버에서 삭제
				if(prevThumbPath != null) {					
					String savePath = uploadPath + "/board/thumb/";
					File file = new File(savePath + prevThumbPath.substring(0,8) + File.separator + prevThumbPath);
					
					if(file.exists()) {
						file.delete();
					}
				}
			} 
			
			//추가 첨부파일 리스트
			if(boardFile != null) {
				ArrayList<BoardFile> fileList = new ArrayList<>();
				
				for(int i=0; i<boardFile.length; i++) {
					String filePath = fileUtil.uploadFile(boardFile[i], "/board/");
					
					BoardFile file = new BoardFile();
					file.setFileName(boardFile[i].getOriginalFilename());
					file.setFilePath(filePath);
					//수정을 위한 게시글 번호
					file.setBoardNo(board.getBoardNo());
					
					fileList.add(file);
				}
				
				board.setFileList(fileList);
			}
			
			//서버에서 삭제하기 위해 필요한 정보는 filePath
			ArrayList<BoardFile> delFileList = service.updateBoard(board);
			
			//조회해온 삭제 파일 서버에서 삭제 처리
			if(delFileList != null) {
				String savePath = uploadPath + "/board/";
				for(BoardFile delFile : delFileList) {
					File file = new File(savePath + delFile.getFilePath().substring(0,8) + File.separator + delFile.getFilePath());
					
					if(file.exists()) {
						file.delete();
					}
				}
			}
			
			res = new ResponseDTO(HttpStatus.OK, "게시글이 정상적으로 수정되었습니다.", true, "success");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ResponseEntity<ResponseDTO>(res, res.getHttpStatus());
	}
}