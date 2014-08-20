package quicklic.quicklic.util;

import java.util.ArrayList;

import quicklic.floating.api.FloatingService;
import quicklic.floating.api.FloatingService.RemoteBinder;
import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Axis;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.favorite.QuicklicFavoriteActivity;
import quicklic.quicklic.hardware.QuicklicHardwareActivity;
import quicklic.quicklic.main.QuicklicMainActivity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class QuicklicActivity extends DeviceMetricActivity {

	private final static int LIMTED_ITEM_COUNT = 10;
	private final static int DEFALT_POSITION = 270;
	private int IMG_PADDING = 12;
	private int MAIN_PADDING = 20;
	private float SIZE_QUICKLIC_RATE = 0.7f;
	private float SIZE_ITEM_RATE = 0.125f;

	private Context context;

	private FrameLayout quicklicLayout;
	private FrameLayout.LayoutParams centerLayoutParams;

	private ImageView centerView;

	private int viewCount;
	private int sizeOfQuicklicMain;
	private int deviceWidth;

	private RemoteBinder remoteBinder = new RemoteBinder();

	private ArrayList<FrameLayout> quickPagerArrayList = new ArrayList<FrameLayout>();
	private ViewPager viewPager;
	private ItemPagerAdapter itemPagerAdapter;
	private float origin_x;
	private float origin_y;
	private float itemSize;

	private LinearLayout detectLayout;

	@Override
	public void onCreate()
	{
		super.onCreate();
		initializeQuicklic();
		bindService(new Intent(this, FloatingService.class), serviceConnection, Service.BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy()
	{
		if ( serviceConnection != null )
			unbindService(serviceConnection);
		try
		{
			getWindowManager().removeView(getDetectLayout());
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		super.onConfigurationChanged(newConfig);
		getWindowManager().removeView(detectLayout);
		initializeQuicklic();
	}

	/**************************************
	 * Support Function Section
	 **************************************/

	/**
	 * @함수명 : getViewCount
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : 현재 추가된 Item의 개수를 반환
	 * @작성자 : 13 JHPark
	 * @작성일 : 2014. 5. 21.
	 */
	protected int getViewCount()
	{
		return viewCount;
	}

	protected void setCenterView( ImageView centerView )
	{
		this.centerView = centerView;
	}

	protected ImageView getCenterView()
	{
		return centerView;
	}

	/**
	 * @함수명 : getQuicklicFrameLayout
	 * @매개변수 :
	 * @반환 : FrameLayout
	 * @기능(역할) : MainActivity의 FrameLayout을 반환
	 * @작성자 : 13 JHPark
	 * @작성일 : 2014. 5. 21.
	 */
	protected FrameLayout getQuicklicFrameLayout()
	{
		return quicklicLayout;
	}

	/**
	 * @함수명 : isItemFull
	 * @매개변수 : int item_count
	 * @반환 : boolean
	 * @기능(역할) : 아이템 개수가 최대 치를 넘는지 검사
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 21.
	 */
	protected boolean isItemFull( int item_count )
	{
		if ( item_count != 0 )
			return (item_count % LIMTED_ITEM_COUNT) == 0;
		else
			return false;
	}

	/**
	 * @함수명 : addViewsForBalance
	 * @매개변수 : int item_count, ArrayList<Drawable> imageArrayList, OnClickListener clickListener
	 * @반환 : void
	 * @기능(역할) : Item의 개수에 따라서 균등하게 배치 해줌, 등록될 Item의 이미지는 ArrayList에 있음
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 9.
	 * @수정자 : JHPark, THYang
	 */
	protected void addViewsForBalance( int item_count, ArrayList<Item> imageArrayList, OnClickListener clickListener )
	{
		FrameLayout pagerFrameLayout = new FrameLayout(this);
		quickPagerArrayList.clear();

		viewPager = new ViewPager(this);
		viewCount = item_count;

		itemSize = deviceWidth * SIZE_ITEM_RATE; // 등록되어질 아이템의 크기
		final float frameWidth = sizeOfQuicklicMain;
		final float frameHeight = sizeOfQuicklicMain;

		// 반지름 길이 구하기 : 아이템이 놓일 위치에서 MAIN_PADDING만큼의 여유공간 확보
		int radius = (int) (frameHeight - itemSize) / 2 - MAIN_PADDING;

		// 중심 좌표 구하기
		origin_x = (frameWidth - itemSize) / 2;
		origin_y = (frameHeight - itemSize) / 2;

		int pagerCount = 1;

		if ( item_count != 0 )
		{
			// item 개수에 따른 page 수 구하기
			pagerCount = (item_count / LIMTED_ITEM_COUNT);
			if ( item_count % LIMTED_ITEM_COUNT != 0 )
				pagerCount += 1;

			// item 개수에 따른 각도 구하기
			final int ANGLE = 360 / ((item_count > LIMTED_ITEM_COUNT) ? LIMTED_ITEM_COUNT : item_count); // 360 / (Item 개수)

			for ( int pageNum = 0; pageNum < pagerCount; pageNum++ ) //pager count 만큼 뷰를 생성해서 돌린다.
			{
				pagerFrameLayout = new FrameLayout(this);

				int angle_sum = 0; // 각도 누적
				int pagerItemCount = item_count / (LIMTED_ITEM_COUNT * (pageNum + 1)) > 0 ? LIMTED_ITEM_COUNT : item_count % LIMTED_ITEM_COUNT; // 각 Page 당 Item 개수 계산
				int begin = pageNum * LIMTED_ITEM_COUNT;
				int finish = begin + pagerItemCount;

				for ( int itemNum = begin; itemNum < finish; itemNum++ )
				{
					Axis axis = new Axis(); // 아이템이 놓일 좌표를 저장하는 자료구조 (float x, float y)

					// 레이아웃 설정 : 기본적인 크기는 정해져 있으며, 좌표 값만 설정
					ImageView itemImageView = new ImageView(context);
					itemImageView.setLayoutParams(new LayoutParams((int) itemSize, (int) itemSize));
					itemImageView.setScaleType(ScaleType.CENTER_INSIDE);
					itemImageView.setPadding(IMG_PADDING, IMG_PADDING, IMG_PADDING, IMG_PADDING);

					// image 그림 추가
					if ( imageArrayList != null && itemNum < imageArrayList.size() )
					{
						if ( imageArrayList.get(itemNum).getIconDrawable() != null )
							itemImageView.setImageDrawable(imageArrayList.get(itemNum).getIconDrawable());
						else
							itemImageView.setImageResource(imageArrayList.get(itemNum).getDrawResId());
					}

					// 추가한 아이템을 구별하기 위한 id 와 Listener
					itemImageView.setId(imageArrayList.get(itemNum).getViewId());
					itemImageView.setOnClickListener(clickListener);

					// 기준 좌표와 각도를 넣어주고, 각도 만큼 떨어져 있는 좌표를 가져옴
					axis = getAxis(origin_x, origin_y, radius, angle_sum += ANGLE);

					FrameLayout.LayoutParams itemBackLayoutParams = new FrameLayout.LayoutParams((int) itemSize, (int) itemSize);
					itemBackLayoutParams.leftMargin = axis.getAxis_x();
					itemBackLayoutParams.topMargin = axis.getAxis_y();

					LinearLayout itemBackLinearLayout = new LinearLayout(this);
					itemBackLinearLayout.setGravity(Gravity.CENTER);
					itemBackLinearLayout.setOrientation(LinearLayout.VERTICAL);
					itemBackLinearLayout.setLayoutParams(itemBackLayoutParams);
					itemBackLinearLayout.setBackgroundResource(R.drawable.rendering_item);
					itemBackLinearLayout.addView(itemImageView);

					pagerFrameLayout.addView(itemBackLinearLayout);
				}
				pagerFrameLayout.setBackgroundResource(R.drawable.rendering_circle);
				quickPagerArrayList.add(pagerFrameLayout);
			}
		}
		else
		{
			pagerFrameLayout.setBackgroundResource(R.drawable.rendering_circle);
			quickPagerArrayList.add(pagerFrameLayout);
		}

		// ViewPager setting
		itemPagerAdapter = new ItemPagerAdapter(this, pagerCount, quickPagerArrayList);
		viewPager.setLayoutParams(new LinearLayout.LayoutParams(sizeOfQuicklicMain, sizeOfQuicklicMain));
		viewPager.setAdapter(itemPagerAdapter);
		quicklicLayout.addView(viewPager);

		setCenterView(itemSize, origin_x, origin_y);
	}

	/**
	 * @함수명 : setFloatingVisibility
	 * @매개변수 : boolean enable
	 * @반환 : void
	 * @기능(역할) : floating image 의 visibility 설정
	 * @작성자 : THYang
	 * @작성일 : 2014. 8. 10.
	 */
	protected void setFloatingVisibility( boolean enable )
	{
		try
		{
			remoteBinder.setFloatingVisibility(enable);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @함수명 : getFloatingVisibility
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : floating image 의 visibility 상태 가져오기
	 * @작성자 : THYang
	 * @작성일 : 2014. 8. 10.
	 */
	protected int getFloatingVisibility()
	{
		try
		{
			return remoteBinder.getFloatingVisibility();
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
		return View.VISIBLE;
	}

	public LinearLayout getDetectLayout()
	{
		return detectLayout;
	}

	/**************************************
	 * Developer Section
	 **************************************/

	/**
	 * Service Connection
	 */
	private ServiceConnection serviceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceDisconnected( ComponentName componentName )
		{

		}

		@Override
		public void onServiceConnected( ComponentName componentName, IBinder iBinder )
		{
			remoteBinder = (RemoteBinder) iBinder;
		}
	};

	/**
	 * @함수명 : initializeQuicklic
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 초기화
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 5.
	 */
	protected void initializeQuicklic()
	{
		context = this;

		// 화면 회전의 방향에 따른 resize 비율
		if ( getOrientation() == Surface.ROTATION_0 )
		{
			IMG_PADDING = 12;
			MAIN_PADDING = 20;
			SIZE_QUICKLIC_RATE = 0.7f;
			SIZE_ITEM_RATE = 0.12f;
		}
		else
		{
			IMG_PADDING = 12;
			MAIN_PADDING = 20;
			SIZE_QUICKLIC_RATE = 0.4f;
			SIZE_ITEM_RATE = 0.07f;
		}

		viewCount = 0;
		deviceWidth = getDeviceWidth();
		sizeOfQuicklicMain = (int) (deviceWidth * SIZE_QUICKLIC_RATE);

		detectLayout = new LinearLayout(this);
		detectLayout.setGravity(Gravity.CENTER);
		detectLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		detectLayout.setOnTouchListener(detectListener);

		quicklicLayout = new FrameLayout(this);
		quicklicLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
		detectLayout.addView(quicklicLayout);

		getWindowManager().addView(detectLayout, getLayoutParams());
	}

	private void setCenterView( float itemSize, float origin_x, float origin_y )
	{
		if ( centerView != null )
		{
			centerLayoutParams = new FrameLayout.LayoutParams((int) itemSize, (int) itemSize, Gravity.TOP | Gravity.LEFT);
			centerLayoutParams.leftMargin = (int) origin_x;
			centerLayoutParams.topMargin = (int) origin_y;

			centerView.setScaleType(ScaleType.CENTER_INSIDE);
			centerView.setLayoutParams(centerLayoutParams);
			quicklicLayout.addView(centerView);

			viewCount++;
		}
	}

	/**
	 * @함수명 : getAxis
	 * @매개변수 : float origin_x, float origin_y, float radius, double angle
	 * @반환 : Axis
	 * @기능(역할) : 기준 좌표에서 떨어져 있는 각도 만큼에 위치한 좌표를 얻어옴
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 9.
	 */
	private Axis getAxis( float origin_x, float origin_y, float radius, double angle )
	{
		Axis axis = new Axis();
		double angles = Math.PI / 180 * (DEFALT_POSITION + angle);
		axis.setAxis_x((int) (origin_x + radius * Math.cos(angles)));
		axis.setAxis_y((int) (origin_y + radius * Math.sin(angles)));
		return axis;
	}

	public float getOrigin_x()
	{
		return origin_x;
	}

	public float getOrigin_y()
	{
		return origin_y;
	}

	public float getItemSize()
	{
		return itemSize;
	}

	private OnTouchListener detectListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch( View v, MotionEvent event )
		{
			getWindowManager().removeView(detectLayout);

			FloatingService.setVisibility(true);

			//TODO HOW TO SET?

			Intent intent = new Intent(getApplicationContext(), QuicklicMainActivity.class);
			stopService(intent);

			Intent intent2 = new Intent(getApplicationContext(), QuicklicFavoriteActivity.class);
			stopService(intent2);

			Intent intent3 = new Intent(getApplicationContext(), QuicklicHardwareActivity.class);
			stopService(intent3);

			return false;
		}
	};

}
