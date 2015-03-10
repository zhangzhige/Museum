package com.waltz3d.museum;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;

public class LocationFragment extends BaseFragment implements BaiduMap.OnMapClickListener,OnGetRoutePlanResultListener{

	
	private static final String LTAG = LocationFragment.class.getSimpleName();
	
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Log.d(LTAG, "action: " + s);
		}
	}
	
	private boolean useDefaultIcon = false;

    //地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    //如果不处理touch事件，则无需继承，直接使用MapView即可
    private MapView mMapView = null;    // 地图View
    private BaiduMap mBaidumap = null;
    //搜索相关
    private RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    
    private SDKReceiver mReceiver;
    
    private View mRootView;
    
    private ImageView imageView_mylocation;
    
    private ImageView imageView_resume_location;
    
    private LocationClient mLocClient;
	
    private MyLocationListenner myListener = new MyLocationListenner();
	
    private LocationMode mCurrentMode = LocationMode.NORMAL;
	
    private BitmapDescriptor mCurrentMarker;
    
    private GeoCoder mGeoCoder = null;

    private Marker mMarkerA;
    
    private LatLng mUserLocation;
    
    private LatLng ptCenter = new LatLng(26.150757f,119.164128f);
    
    private ProgressDialog mProgressDialog;
    
    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mRootView = inflater.inflate(R.layout.activity_routeplan, container, false);
        Log.d(LTAG, "onCreateView");
    	//初始化地图
        mProgressDialog = new ProgressDialog(getActivity());
		mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
					Toast.makeText(getActivity(), "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
					return;
				}
				mBaidumap.clear();
				MarkerOptions mMarkerOptions = new MarkerOptions().position(result.getLocation());
				mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka));
				mMarkerOptions.title(result.getAddress());
				mMarkerA = (Marker) mBaidumap.addOverlay(mMarkerOptions);
				mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
			}
			
			@Override
			public void onGetGeoCodeResult(GeoCodeResult arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		getActivity().registerReceiver(mReceiver, iFilter);
		
        mMapView = (MapView) mRootView.findViewById(R.id.map);
        int count = mMapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ImageView) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        
        mBaidumap = mMapView.getMap();
        //地图点击事件处理
        mBaidumap.setOnMapClickListener(this);
        mBaidumap.setMyLocationEnabled(true);
        mBaidumap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
        
        mBaidumap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				if (marker == mMarkerA ) {
					marker.getTitle();
					Util.showToast(getActivity(), "闽侯县闽江北岸昙石村甘蔗街道昙石村330号昙石山遗址博物馆", Toast.LENGTH_LONG);
				}
				return false;
			}
		});
        
        mLocClient = new LocationClient(getActivity());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
		
        imageView_mylocation = (ImageView) mRootView.findViewById(R.id.imageView_mylocation);
        imageView_mylocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mUserLocation == null){
					Util.showToast(getActivity(), "抱歉，暂未获取到您的位置，无法导航，请稍后再试", Toast.LENGTH_LONG);
					return;
				}
				double distance = DistanceUtil.getDistance(mUserLocation, ptCenter);
				PlanNode stNode = PlanNode.withLocation(mUserLocation);
		        PlanNode enNode = PlanNode.withLocation(ptCenter);
				if(distance >= 2000){//大于2k取驾车路线
					mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
				}else{
					 mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));
				}
			}
		});
        
        imageView_resume_location = (ImageView) mRootView.findViewById(R.id.imageView_resume_location);
        imageView_resume_location.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Util.showDialog(mProgressDialog, "正在导航...");
				mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
			}
		});
        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
    	return mRootView;
    }
    

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaidumap.setMyLocationData(locData);
			mUserLocation = new LatLng(location.getLatitude(),location.getLongitude());
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	



    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
    	Util.dismissDialog(mProgressDialog);
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
    	Util.dismissDialog(mProgressDialog);
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            result.getRouteLines().get(0);
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
    	Util.dismissDialog(mProgressDialog);
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    //定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
    	return false;
    }

    @Override
	public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
	public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
	public void onDestroy() {
    	if(mSearch!=null){
    		mSearch.destroy();
    	}
        if(mMapView!=null){
        	mMapView.onDestroy();
        }
        super.onDestroy();
    }


}