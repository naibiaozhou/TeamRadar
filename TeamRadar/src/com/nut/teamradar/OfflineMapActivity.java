package com.nut.teamradar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapManager.OfflineMapDownloadListener;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.nut.teamradar.util.AMapUtil;
import com.nut.teamradar.util.OffLineMapUtils;

public class OfflineMapActivity extends Activity implements
	OfflineMapDownloadListener{
	private OfflineMapManager amapManager = null;// ���ߵ�ͼ���ؿ�����
	private List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();// ����һ��Ŀ¼��ʡֱϽ��
	private HashMap<Object, List<OfflineMapCity>> cityMap = new HashMap<Object, List<OfflineMapCity>>();// �������Ŀ¼����
	private int groupPosition;// ��¼һ��Ŀ¼��position
	private int childPosition;// ��¼����Ŀ¼��position
	private int completeCode;// ��¼���ر���
	private boolean isStart = false;// �ж��Ƿ�ʼ����,true��ʾ��ʼ���أ�false��ʾ����ʧ��
	private boolean[] isOpen;// ��¼һ��Ŀ¼�Ƿ��

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		  /*
         * �������ߵ�ͼ�洢Ŀ¼�����������ߵ�ͼ���ʼ����ͼ����;
         * ʹ�ù����п���������, ���������������ߵ�ͼ�洢��·����
         * ����Ҫ�����ߵ�ͼ���غ�ʹ�õ�ͼҳ�涼����·������
         * */
	    //Demo��Ϊ�������������ʹ�����ص����ߵ�ͼ��ʹ��Ĭ��λ�ô洢���������Զ�������
        //MapsInitialihenger.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		MapsInitializer.sdcardDir = OffLineMapUtils.getSdCacheDir(this.getApplicationContext());
		setContentView(R.layout.offlinemap_activity);
		init();
	}

	private MapView mapView;
	/**
	 * ��ʼ��UI�����ļ�
	 */
	private void init() {
		//�˰汾���ƣ�ʹ�����ߵ�ͼ�����ʼ��һ��MapView 
		mapView=new MapView(this);
		amapManager = new OfflineMapManager(this, this);
		ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.list);
		expandableListView.setGroupIndicator(null);
		provinceList = amapManager.getOfflineMapProvinceList();
		List<OfflineMapProvince> bigCityList = new ArrayList<OfflineMapProvince>();// ��ʡ��ʽ����ֱϽ�С��۰ġ�ȫ����Ҫͼ
		List<OfflineMapCity> cityList = new ArrayList<OfflineMapCity>();// ���и�ʽ����ֱϽ�С��۰ġ�ȫ����Ҫͼ
		List<OfflineMapCity> gangaoList = new ArrayList<OfflineMapCity>();// ����۰ĳ���
		List<OfflineMapCity> gaiyaotuList = new ArrayList<OfflineMapCity>();// �����Ҫͼ
		for (int i = 0; i < provinceList.size(); i++) {
			OfflineMapProvince offlineMapProvince = provinceList.get(i);
			List<OfflineMapCity> city = new ArrayList<OfflineMapCity>();
			OfflineMapCity aMapCity = getCicy(offlineMapProvince);
			if (offlineMapProvince.getCityList().size() != 1) {
				city.add(aMapCity);
				city.addAll(offlineMapProvince.getCityList());
			} else {
				cityList.add(aMapCity);
				bigCityList.add(offlineMapProvince);
			}
			cityMap.put(i + 3, city);
		}
		OfflineMapProvince title = new OfflineMapProvince();

		title.setProvinceName("��Ҫͼ");
		provinceList.add(0, title);
		title = new OfflineMapProvince();
		title.setProvinceName("ֱϽ��");
		provinceList.add(1, title);
		title = new OfflineMapProvince();
		title.setProvinceName("�۰�");
		provinceList.add(2, title);
		provinceList.removeAll(bigCityList);

		for (OfflineMapProvince aMapProvince : bigCityList) {
			if (aMapProvince.getProvinceName().contains("���")
					|| aMapProvince.getProvinceName().contains("����")) {
				gangaoList.add(getCicy(aMapProvince));
			} else if (aMapProvince.getProvinceName().contains("ȫ����Ҫͼ")) {
				gaiyaotuList.add(getCicy(aMapProvince));
			}
		}
		try{
		cityList.remove(4);// ��List��������ɾ�����
		cityList.remove(4);// ��List��������ɾ������
		cityList.remove(4);// ��List��������ɾ��ȫ����Ҫͼ
		}
		catch(Throwable e){
			e.printStackTrace();
		}
		cityMap.put(0, gaiyaotuList);// ��HashMap�е�0λ�����ȫ����Ҫͼ
		cityMap.put(1, cityList);// ��HashMap�е�1λ�����ֱϽ��
		cityMap.put(2, gangaoList);// ��HashMap�е�2λ����Ӹ۰�
		isOpen = new boolean[provinceList.size()];
		// Ϊ�б������Դ
		expandableListView.setAdapter(adapter);
		expandableListView
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {

					@Override
					public void onGroupCollapse(int groupPosition) {;
						isOpen[groupPosition] = false;
					}
				});

		expandableListView
				.setOnGroupExpandListener(new OnGroupExpandListener() {

					@Override
					public void onGroupExpand(int groupPosition) {
						isOpen[groupPosition] = true;
					}
				});
		// ���ö���item����ļ�����
		expandableListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				try {
					// ����ȫ����Ҫͼ��ֱϽ�С��۰����ߵ�ͼ����
					if (groupPosition == 0 || groupPosition == 1
							|| groupPosition == 2) {
						isStart = amapManager.downloadByProvinceName(cityMap
								.get(groupPosition).get(childPosition)
								.getCity());
					}
					// ���ظ�ʡ�����ߵ�ͼ����
					else {
						// ���ظ�ʡ�б��е�ʡ�����ߵ�ͼ����
						if (childPosition == 0) {
							isStart = amapManager
									.downloadByProvinceName(provinceList.get(
											groupPosition).getProvinceName());
						}
						// ���ظ�ʡ�б��еĳ������ߵ�ͼ����
						else if (childPosition > 0) {
							isStart = amapManager.downloadByCityName(cityMap
									.get(groupPosition).get(childPosition)
									.getCity());
						}
					}
				} catch (AMapException e) {
					e.printStackTrace();
					Log.e("���ߵ�ͼ����", "���ߵ�ͼ�����׳��쳣" + e.getErrorMessage());
				}
				// ���浱ǰ������������ʡ�ݻ��߳��е�positionλ��
				if (isStart) {
					OfflineMapActivity.this.groupPosition = groupPosition;
					OfflineMapActivity.this.childPosition = childPosition;
				}
				return false;
			}
		});
	}

	/**
	 * ��һ��ʡ�Ķ���ת��Ϊһ���еĶ���
	 */
	public OfflineMapCity getCicy(OfflineMapProvince aMapProvince) {
		OfflineMapCity aMapCity = new OfflineMapCity();
		aMapCity.setCity(aMapProvince.getProvinceName());
		aMapCity.setSize(aMapProvince.getSize());
		aMapCity.setCompleteCode(aMapProvince.getcompleteCode());
		aMapCity.setState(aMapProvince.getState());
		aMapCity.setUrl(aMapProvince.getUrl());
		return aMapCity;
	}

	final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {

		@Override
		public int getGroupCount() {
			return provinceList.size();
		}

		/**
		 * ��ȡһ����ǩ����
		 */
		@Override
		public Object getGroup(int groupPosition) {
			return provinceList.get(groupPosition).getProvinceName();
		}

		/**
		 * ��ȡһ����ǩ��ID
		 */
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/**
		 * ��ȡһ����ǩ�¶�����ǩ������
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			return cityMap.get(groupPosition).size();
		}

		/**
		 * ��ȡһ����ǩ�¶�����ǩ������
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return cityMap.get(groupPosition).get(childPosition).getCity();
		}

		/**
		 * ��ȡ������ǩ��ID
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		/**
		 * ָ��λ����Ӧ������ͼ
		 */
		@Override
		public boolean hasStableIds() {
			return true;
		}

		/**
		 * ��һ����ǩ��������
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView group_text;
			ImageView group_image;
			if (convertView == null) {
				convertView = (RelativeLayout) RelativeLayout.inflate(
						getBaseContext(), R.layout.offlinemap_group, null);
			}
			group_text = (TextView) convertView.findViewById(R.id.group_text);
			group_image = (ImageView) convertView
					.findViewById(R.id.group_image);
			group_text.setText(provinceList.get(groupPosition)
					.getProvinceName());
			if (isOpen[groupPosition]) {
				group_image.setImageDrawable(getResources().getDrawable(
						R.drawable.downarrow));
			} else {
				group_image.setImageDrawable(getResources().getDrawable(
						R.drawable.rightarrow));
			}
			return convertView;
		}

		/**
		 * ��һ����ǩ�µĶ�����ǩ��������
		 */
		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = (RelativeLayout) RelativeLayout.inflate(
						getBaseContext(), R.layout.offlinemap_child, null);
			}
			ViewHolder holder = new ViewHolder(convertView);
			holder.cityName.setText(cityMap.get(groupPosition)
					.get(childPosition).getCity());
			holder.citySize.setText((cityMap.get(groupPosition).get(
					childPosition).getSize())
					/ (1024 * 1024f) + "MB");
			if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.SUCCESS) {
				holder.cityDown.setText("��װ���");
			} else if (cityMap.get(groupPosition).get(childPosition).getState() == -2) {
				holder.cityDown.setText("��������" + completeCode + "%");
			} else if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.UNZIP) {
				holder.cityDown.setText("���ڽ�ѹ"+ completeCode + "%");
			} else if (cityMap.get(groupPosition).get(childPosition).getState() == OfflineMapStatus.LOADING) {
				holder.cityDown.setText("����");
			}

			return convertView;
		}

		class ViewHolder {
			TextView cityName;
			TextView citySize;
			TextView cityDown;

			public ViewHolder(View view) {
				cityName = (TextView) view.findViewById(R.id.city_name);
				citySize = (TextView) view.findViewById(R.id.city_size);
				cityDown = (TextView) view.findViewById(R.id.city_down);
			}
		}

		/**
		 * ��ѡ���ӽڵ��ʱ�򣬵��ø÷���
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	};

	/**
	 * ���ߵ�ͼ���ػص�����
	 */
	@Override
	public void onDownload(int status, int completeCode, String downName) {
		switch (status) {
		case OfflineMapStatus.SUCCESS:
			changeOfflineMapTitle(OfflineMapStatus.SUCCESS);
			break;
		case OfflineMapStatus.LOADING:
			OfflineMapActivity.this.completeCode = completeCode;
			changeOfflineMapTitle(-2);// -2��ʾ�����������ߵ�ͼ����
			break;
		case OfflineMapStatus.UNZIP:
			OfflineMapActivity.this.completeCode = completeCode;
			changeOfflineMapTitle(OfflineMapStatus.UNZIP);
			break;
		case OfflineMapStatus.WAITING:
			break;
		case OfflineMapStatus.PAUSE:
			break;
		case OfflineMapStatus.STOP:
			break;
		case OfflineMapStatus.ERROR:
			break;
		default:
			break;
		}
		((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
	}

	/**
	 * �������ߵ�ͼ����״̬����
	 */
	private void changeOfflineMapTitle(int status) {
		if (groupPosition == 0 || groupPosition == 1 || groupPosition == 2) {
			cityMap.get(groupPosition).get(childPosition).setState(status);// -2��ʾ�����������ߵ�ͼ����
		} else {
			if (childPosition == 0) {
				for (int i = 0; i < cityMap.get(groupPosition).size(); i++) {
					cityMap.get(groupPosition).get(i).setState(status);// -2��ʾ�����������ߵ�ͼ����
				}
			} else {
				cityMap.get(groupPosition).get(childPosition).setState(status);// -2��ʾ�����������ߵ�ͼ����
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mapView!=null){
		mapView.onDestroy();
		}
		}
}
