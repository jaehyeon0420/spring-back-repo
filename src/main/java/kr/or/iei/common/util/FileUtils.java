package kr.or.iei.common.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtils {
	
	@Value("${file.uploadPath}")
	private String uploadPath;
	
	public String uploadFile(MultipartFile file, String savePath) throws IOException{
		//서버에 저장되는 파일명 중복 방지를 위한 작업.
		//서버 저장 파일명 예시 : 20250509121620323_01549.jpg (오늘날짜 밀리세컨까지 + _랜덤숫자 5자리 + 확장자)
		
		int ranNum = new Random().nextInt(10000)+1; // 1~10000까지 중, 랜덤 숫자
		
		String str = "_" + String.format("%05d", ranNum); // "_랜덤숫자5자리"
		
		String name = file.getOriginalFilename(); //사용자가 업로드 한 파일 명칭 => test.jpg
		String ext = null; //확장자를 저장할 변수
		
		int dot = name.lastIndexOf(".");  //파일명 뒤에서부터 마침표(.)의 위치. 없으면 -1을 리턴
		
		if(dot != -1) { //파일명에 마침표가 있을 때
			ext = name.substring(dot);  // .jpg
		}else {
			ext = "";
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String serverFileName = sdf.format(new Date(System.currentTimeMillis())) + str + ext;
													//"test.jpg" => "20250509121620323_01549.jpg"
		
		String serverDirectory = serverFileName.substring(0, 8); //"20250509121620323_01549.jpg" => "20250509"
		
		savePath = uploadPath + savePath + serverDirectory + File.separator;
		//"C:/Temp/react" +  "/board/thumb/" + "20250509" + "/""
			
		//업로드 처리
		BufferedOutputStream bos = null;
		
		try {
			//오늘 날짜 디렉토리 생성
			File directory = new File(savePath);
			if(!directory.exists()) {
				directory.mkdirs();
			}
			
			byte[] bytes = file.getBytes();
			bos = new BufferedOutputStream(new FileOutputStream(new File(savePath + serverFileName)));
			bos.write(bytes);
		}finally {
			bos.close();			
			
		}
		
		
		return serverFileName;
	}
}
