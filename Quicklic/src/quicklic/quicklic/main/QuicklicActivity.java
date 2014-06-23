package quicklic.quicklic.main;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Axis;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.test.TestingFunction;
import quicklic.quicklic.util.DeviceMetricActivity;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

public class QuicklicActivity extends DeviceMetricActivity {

	private final static int LIMTED_ITEM_COUNT = 10;
	private final static int DEFALT_POSITION = 270;
	private final static int IMG_PADDING = 12;
	private final static int MAIN_PADDING = 20;
	private final static float SIZE_QUICKLIC_RATE = 0.7f;
	private final static float SIZE_ITEM_RATE = 0.125f;

	private Context context;
	private GestureDetector gestureDetector;

	private FrameLayout quicklicFrameLayout;
	private FrameLayout.LayoutParams mainLayoutParams;
	private FrameLayout.LayoutParams centerLayoutParams;

	private ImageView quicklicImageView;
	private ImageView centerView;

	private int viewCount;
	private int sizeOfQuicklicMain;
	private int deviceWidth;

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
		return quicklicFrameLayout;
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
		return LIMTED_ITEM_COUNT < item_count;
	}

	/**
	 * @param imageList
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
		viewCount = item_count;

		Axis axis = new Axis(); // 아이템이 놓일 좌표를 저장하는 자료구조 (float x, float y)
		final float itemSize = deviceWidth * SIZE_ITEM_RATE; // 등록되어질 아이템의 크기
		final float frameWidth = sizeOfQuicklicMain;
		final float frameHeight = sizeOfQuicklicMain;

		// 반지름 길이 구하기 : 아이템이 놓일 위치에서 20만큼의 여유공간 확보
		int radius = (int) (frameHeight - itemSize) / 2 - MAIN_PADDING;

		// 중심 좌표 구하기
		float origin_x = (frameWidth - itemSize) / 2;
		float origin_y = (frameHeight - itemSize) / 2;

		if ( centerView != null )
		{
			centerLayoutParams = new FrameLayout.LayoutParams((int) itemSize, (int) itemSize, Gravity.TOP | Gravity.LEFT);
			centerLayoutParams.leftMargin = (int) origin_x;
			centerLayoutParams.topMargin = (int) origin_y;

			centerView.setScaleType(ScaleType.CENTER_INSIDE);
			centerView.setLayoutParams(centerLayoutParams);
			quicklicFrameLayout.addView(centerView);
			viewCount++;
		}

		if ( item_count == 0 )
			return;

		// item 개수에 따른 각도 구하기
		final int ANGLE = 360 / item_count; // 360 / (Item 개수)

		int angle_sum = 0; // 각도 누적

		for ( int i = 0; i < item_count; i++ )
		{
			// TODO 다양한 정보를 갖고 있는 이미지뷰 클래스 정의
			// 레이아웃 설정 : 기본적인 크기는 정해져 있으며, 좌표 값만 설정
			ImageView itemImageView = new ImageView(context);
			itemImageView.setLayoutParams(new LayoutParams((int) itemSize, (int) itemSize));
			itemImageView.setScaleType(ScaleType.CENTER_INSIDE);
			itemImageView.setPadding(IMG_PADDING, IMG_PADDING, IMG_PADDING, IMG_PADDING);

			// image 그림 추가
			if ( imageArrayList != null && i < imageArrayList.size() )
			{
				if ( imageArrayList.get(i).getIconDrawable() != null )
					itemImageView.setImageDrawable(imageArrayList.get(i).getIconDrawable());
				else
					itemImageView.setImageResource(imageArrayList.get(i).getDrawResId());
			}

			// TODO 추가한 아이템을 구별하기 위한 Id와 Listener
			itemImageView.setId(imageArrayList.get(i).getViewId());
			itemImageView.setOnClickListener(clickListener);

			// 기준 좌표와 각도를 넣어주고, 각도 만큼 떨어져 있는 좌표를 가져옴
			axis = getAxis(origin_x, origin_y, radius, angle_sum += ANGLE);

			LinearLayout itemBackLinearLayout = new LinearLayout(this);
			FrameLayout.LayoutParams itemBackLayoutParams = new FrameLayout.LayoutParams((int) itemSize, (int) itemSize);
			itemBackLayoutParams.leftMargin = axis.getAxis_x();
			itemBackLayoutParams.topMargin = axis.getAxis_y();

			itemBackLinearLayout.setGravity(Gravity.CENTER);
			itemBackLinearLayout.setOrientation(LinearLayout.VERTICAL);
			itemBackLinearLayout.setLayoutParams(itemBackLayoutParams);
			itemBackLinearLayout.setBackgroundResource(R.drawable.rendering_item);

			itemBackLinearLayout.addView(itemImageView);
			quicklicFrameLayout.addView(itemBackLinearLayout);
		}
	}

	/**************************************
	 * Developer Section
	 **************************************/

	/**
	 * @함수명 : setContentView
	 * @매개변수 : int layoutResID
	 * @기능(역할) : 초기화 세팅
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 21.
	 */
	@Override
	public void setContentView( int layoutResID )
	{
		super.setContentView(layoutResID);
		initialize();
	}

	/**
	 * @함수명 : onTouchEvent
	 * @매개변수 :
	 * @기능(역할) : 영역 밖이 눌렸을 경우 창이 닫히도록 한다.
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 13.
	 */
	@Override
	public boolean onTouchEvent( MotionEvent event )
	{
		finish();
		return super.onTouchEvent(event);
	}

	/**
	 * @함수명 : initialize
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 초기화
	 * @작성자 : THYang 9 * @작성일 : 2014. 5. 5.
	 */
	private void initialize()
	{
		context = this;
		viewCount = 0;
		deviceWidth = getDeviceWidth();
		sizeOfQuicklicMain = (int) (deviceWidth * SIZE_QUICKLIC_RATE);

		gestureDetector = new GestureDetector(this, onGestureListener);

		mainLayoutParams = new FrameLayout.LayoutParams(sizeOfQuicklicMain, sizeOfQuicklicMain);
		quicklicFrameLayout = (FrameLayout) findViewById(R.id.quicklic_main_FrameLayout);

		quicklicImageView = (ImageView) findViewById(R.id.quicklic_main_ImageView);
		quicklicImageView.setLayoutParams(mainLayoutParams);
		quicklicImageView.setOnTouchListener(touchListener);
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

	private OnTouchListener touchListener = new OnTouchListener()
	{
		/**
		 * @함수명 : onTouch
		 * @매개변수 : View v, MotionEvent event
		 * @기능(역할) : 감지된 Touch Event를 Gesture Detector에게 넘겨줌
		 * @작성자 : THYang
		 * @작성일 : 2014. 5. 5.
		 */
		@Override
		public boolean onTouch( View v, MotionEvent event )
		{
			gestureDetector.onTouchEvent(event);
			return true;
		}
	};

	private OnGestureListener onGestureListener = new OnGestureListener()
	{

		@Override
		public boolean onSingleTapUp( MotionEvent e )
		{
			Log.e("TAG", "single");
			return false;
		}

		@Override
		public void onShowPress( MotionEvent e )
		{
		}

		@Override
		public boolean onScroll( MotionEvent e1, MotionEvent e2, float distanceX, float distanceY )
		{
			return false;
		}

		@Override
		public void onLongPress( MotionEvent e )
		{
			Log.e("TAG", "long");
		}

		@Override
		public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY )
		{
			/* MotionEvent의 시작과 끝 지점의 X,Y 좌표값을 절대 값으로  가져온 다음, 같은 축 끼리 감산 계산을 한다.
			 * MotionEvent는 어디서 부터 시작했느냐에 따라서 음수 값이 발생하기 때문에 절대 값 처리가 필요하다.
			 * 
			 * 움직임이 더 큰 쪽의 방향을 우선시 하기 위해 가로와 세로의 크기를 비교 한다.
			 * 가로의 움직임이 큰 경우, 왼쪽과 오른쪽 / 세로의 움직임이 큰 경우, 위쪽과 아래쪽
			 * 어느 방향으로 움직였느냐에 따라서 음수 값이 나오기 때문에, 음수 양수를 구분으로 방향을 정한다. 
			 */
			float xLorR = Math.abs(e1.getX()) - Math.abs(e2.getX());
			float yUorD = Math.abs(e1.getY()) - Math.abs(e2.getY());

			if ( Math.abs(xLorR) > Math.abs(yUorD) )
			{
				if ( xLorR < 0 )
				{
					Toast.makeText(context, "to Right", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(context, "to Left", Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				if ( yUorD < 0 )
				{
					Toast.makeText(context, "to Down", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(context, "to Up", Toast.LENGTH_SHORT).show();
				}
			}
			return false;
		}

		@Override
		public boolean onDown( MotionEvent e )
		{
			return false;
		}
	};

	public void homeKeyPressed()
	{
		TestingFunction.getFloatingService().getQuicklic().setVisibility(View.GONE);
		Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_LONG).show();

		TimerTask checkTask;
		checkTask = new TimerTask()
		{
			@Override
			public void run()
			{
				runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						TestingFunction.getFloatingService().getQuicklic().setVisibility(View.VISIBLE);

						finish();
					}
				});
			}
		};

		Timer mTimer = new Timer();
		mTimer.schedule(checkTask, 5000);
	}
}
