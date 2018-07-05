package quicklic.quicklic.util;

import java.util.ArrayList;

import quicklic.floating.api.FloatingService;
import quicklic.floating.api.FloatingService.RemoteBinder;
import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Axis;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.favorite.QuicklicFavoriteService;
import quicklic.quicklic.hardware.QuicklicHardwareService;
import quicklic.quicklic.keyboard.QuicklicKeyBoardService;
import quicklic.quicklic.main.QuicklicMainService;
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

public class BaseQuicklic extends DeviceMetricQuicklic {

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

	private boolean isMain;

	private LinearLayout detectLayout;

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

	protected FrameLayout getQuicklicFrameLayout()
	{
		return quicklicLayout;
	}

	protected boolean isItemFull( int item_count )
	{
		if ( item_count != 0 )
			return (item_count % LIMTED_ITEM_COUNT) == 0;
		else
			return false;
	}

	protected void addViewsForBalance( int item_count, ArrayList<Item> imageArrayList, OnClickListener clickListener )
	{
		FrameLayout pagerFrameLayout = new FrameLayout(this);
		quickPagerArrayList.clear();

		viewPager = new ViewPager(this);
		viewCount = item_count;

		itemSize = deviceWidth * SIZE_ITEM_RATE;
		final float frameWidth = sizeOfQuicklicMain;
		final float frameHeight = sizeOfQuicklicMain;

		int radius = (int) (frameHeight - itemSize) / 2 - MAIN_PADDING;

		origin_x = (frameWidth - itemSize) / 2;
		origin_y = (frameHeight - itemSize) / 2;

		int pagerCount = 1;

		if ( item_count != 0 )
		{
			pagerCount = (item_count / LIMTED_ITEM_COUNT);
			if ( item_count % LIMTED_ITEM_COUNT != 0 )
				pagerCount += 1;

			final int ANGLE = 360 / ((item_count > LIMTED_ITEM_COUNT) ? LIMTED_ITEM_COUNT : item_count); // 360 / (Item 개수)

			for ( int pageNum = 0; pageNum < pagerCount; pageNum++ )
			{
				pagerFrameLayout = new FrameLayout(this);

				int angle_sum = 0;
				int pagerItemCount = item_count / (LIMTED_ITEM_COUNT * (pageNum + 1)) > 0 ? LIMTED_ITEM_COUNT : item_count % LIMTED_ITEM_COUNT; // 각 Page 당 Item 개수 계산
				int begin = pageNum * LIMTED_ITEM_COUNT;
				int finish = begin + pagerItemCount;

				for ( int itemNum = begin; itemNum < finish; itemNum++ )
				{
					Axis axis = new Axis();

					ImageView itemImageView = new ImageView(context);
					itemImageView.setLayoutParams(new LayoutParams((int) itemSize, (int) itemSize));
					itemImageView.setScaleType(ScaleType.CENTER_INSIDE);
					itemImageView.setPadding(IMG_PADDING, IMG_PADDING, IMG_PADDING, IMG_PADDING);


					if ( imageArrayList != null && itemNum < imageArrayList.size() )
					{
						if ( imageArrayList.get(itemNum).getIconDrawable() != null )
							itemImageView.setImageDrawable(imageArrayList.get(itemNum).getIconDrawable());
						else
							itemImageView.setImageResource(imageArrayList.get(itemNum).getDrawResId());
					}


					itemImageView.setId(imageArrayList.get(itemNum).getViewId());
					itemImageView.setOnClickListener(clickListener);


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

	protected ViewPager getViewPager()
	{
		return viewPager;
	}

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

	protected LinearLayout getDetectLayout()
	{
		return detectLayout;
	}

	protected void setIsMain( boolean enable )
	{
		isMain = enable;
	}

	/**************************************
	 * Developer Section
	 **************************************/

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


	protected void initializeQuicklic()
	{
		context = this;
		isMain = true;


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

		quicklicLayout = new FrameLayout(this);
		quicklicLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

		detectLayout = new LinearLayout(this);
		detectLayout.setGravity(Gravity.CENTER);
		detectLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		detectLayout.setOnTouchListener(detectListener);
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
			stopServices();

			if ( !isMain )
			{
				setFloatingVisibility(false);
				Intent intent = new Intent(getApplicationContext(), QuicklicMainService.class);
				startService(intent);
			}
			else
			{
				setFloatingVisibility(true);
			}
			return false;
		}

		/**
		 * @함수명 : stopServices
		 * @매개변수 :
		 * @반환 : void
		 * @기능(역할) : 실행 되는 모든 Service를 종료
		 * @작성자 : 13 JHPark
		 * @작성일 : 2014. 8. 21.
		 */
		private void stopServices()
		{
			Intent intent;

			intent = new Intent(getApplicationContext(), QuicklicMainService.class);
			stopService(intent);

			intent = new Intent(getApplicationContext(), QuicklicFavoriteService.class);
			stopService(intent);

			intent = new Intent(getApplicationContext(), QuicklicHardwareService.class);
			stopService(intent);

			intent = new Intent(getApplicationContext(), QuicklicKeyBoardService.class);
			stopService(intent);
		}
	};

}
