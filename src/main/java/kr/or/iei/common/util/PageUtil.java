package kr.or.iei.common.util;

import org.springframework.stereotype.Component;

import kr.or.iei.common.model.dto.PageInfo;

@Component
public class PageUtil {
	public PageInfo getPageInfo(int reqPage, int viewCnt, int pageNaviSize, int totalCount) {
		int end = reqPage * viewCnt;
		int start = end - viewCnt + 1;
		
		//전체 페이지 수. 소수점이 존재하면 올림 처리하여 나머지용 페이지 1개 추가
		int totalPage = (int) Math.ceil(totalCount/(double) viewCnt);
		
		//시작 페이지 번호
		int pageNo = ((reqPage-1) / pageNaviSize) * pageNaviSize + 1;
		
		return new PageInfo(start, end, pageNo, pageNaviSize, totalPage);
	}
}
