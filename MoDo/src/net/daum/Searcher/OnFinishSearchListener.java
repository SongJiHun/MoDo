package net.daum.Searcher;

import java.util.List;

/*
	장소검색 이벤트 리스너
	검색 성공, 검색 실패에 대한 처리
 */

public interface OnFinishSearchListener {
	public void onSuccess(List<Item> itemList);
	public void onFail();
}
