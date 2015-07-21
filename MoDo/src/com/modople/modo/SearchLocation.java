package com.modople.modo;

import java.util.HashMap;
import java.util.List;

import net.daum.Searcher.Item;
import net.daum.Searcher.OnFinishSearchListener;
import net.daum.Searcher.Searcher;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPoint.GeoCoordinate;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


/*
	장소검색 클래스
 */
public class SearchLocation extends Activity {
	MapView mapView;			// 다음 지도
	ViewGroup mapViewContainer;
	
	ListView listView;			// 장소 리스트뷰
	
	EditText edit_search;		// 장소검색 editText
	Button btn_search;			// 장소검색 button
	
	// 장소검색 결과? 데이터
	private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();
	
	// 다음 지도 API 키
	String apikey = "8b39cd12448290de1439ed29b51b165d";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_location);

        // MapView 생성
        mapView = new MapView(this);

        // API Key	
        mapView.setDaumMapApiKey(apikey);

        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        
        edit_search = (EditText) findViewById(R.id.edit_search);	// 검색창
        btn_search = (Button) findViewById(R.id.btn_search);		// 검색버튼
        listView = (ListView) findViewById(R.id.list_result);		// 검색 결과 리스트뷰
        
        btn_search.setOnClickListener(new OnClickListener() {		// 장소검색 이벤트
			
			@Override
			public void onClick(View v) {
				// 검색 데이터
				String query = edit_search.getText().toString();
				if (query == null || query.length() == 0) {
					Toast.makeText(getApplicationContext(), "검색어를 입력하세요.", 1000).show();
					return;
				}
				hideSoftKeyboard();
				
				GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
				double latitude = geoCoordinate.latitude; 	// 위도
				double longitude = geoCoordinate.longitude;	// 경도
				int radius = 10000;							// 중심 좌표로부터의 반경거리. 특정 지역을 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
			
				int page = 1;
				
				//	키워드 검색 매소드 호출
				Searcher searcher = new Searcher();
				searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {

					@Override
					public void onSuccess(List<Item> itemList) { 		// 검색 성공 
						mapView.removeAllPOIItems(); // 기존 검색 결과 삭제
						showResult(itemList); // 검색 결과 보여줌 
					}

					@Override
					public void onFail() {								// 검색 실패
						Toast.makeText(getApplicationContext(), "API KEY의 제한 트래픽이 초과되었습니다.", 1000).show();
					}
					
				});
			}
		});   
    }
    
    // 키보드 숨기는 메소
    private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
    }

    // 장소 검색 결과를 보여준다.
    private void showResult(List<Item> itemList) {	
    	// 카메라를 움직이기 위한 객체
		MapPointBounds mapPointBounds = new MapPointBounds(); 	
		
		for (int i = 0; i < itemList.size(); i++) {
			Item item = itemList.get(i);

			MapPOIItem poiItem = new MapPOIItem();
			poiItem.setItemName(item.title);
			poiItem.setTag(i);
			MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
			poiItem.setMapPoint(mapPoint);
			mapPointBounds.add(mapPoint);
			poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
			poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);				// 장소위치에 깃발 그리기
			poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
			poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);		// 선택되면 빨간색으로 표시
			poiItem.setCustomImageAutoscale(false);
			poiItem.setCustomImageAnchor(0.5f, 1.0f);
			
			mapView.addPOIItem(poiItem);
			mTagItemMap.put(poiItem.getTag(), item);
		}
		
		// 검색 후 맨 첫번째로 검색된 장소로 카메라 이동
		mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
		
		MapPOIItem[] poiItems = mapView.getPOIItems();
		if (poiItems.length > 0) {
			mapView.selectPOIItem(poiItems[0], false);
		}
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
