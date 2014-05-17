package quicklic.quicklic.quicklic;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Axis;
import quicklic.quicklic.test.TestingFunction;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class QuicklicActivity extends Activity {

	private final static int LIMTED_ITEM_COUNT = 10;
	private final static int DEFALT_POSITION = 270;

	private Context context;
	private GestureDetector gestureDetector;
	private WindowManager windowManager;

	private FrameLayout quicklicFrameLayout;

	private FrameLayout.LayoutParams fLayoutParams;
	private Button addButton;
	private ImageView quicklicImageView;
	private TestingFunction testingFunction;

	private int deviceWidth;
	private int deviceHeight;

	private int viewCount;
	private int pageIndex;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_quicklic);

		displayMetrics();
		initialize();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	/**
	 * @함수명 : onBackPressed
	 * @매개변수 :
	 * @기능(역할) : 뒤로가기 버튼을 눌렀을 때, quicklic 뷰가 다시 생성되도록 함
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 9.
	 */
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		TestingFunction.getFloatingServices().setVisibility(true);
	};

	/**
	 * @함수명 : onStop
	 * @매개변수 :
	 * @기능(역할) : 홈버튼이 눌렸을 때, quicklic 뷰가 다시 생성되도록 함
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 9.
	 */
	@Override
	protected void onStop()
	{
		TestingFunction.getFloatingServices().setVisibility(true);
		super.onStop();
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
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 5.
	 */
	private void initialize()
	{
		context = this;

		viewCount = 0;
		pageIndex = 0;

		gestureDetector = new GestureDetector(this, gestureListener);

		testingFunction = (TestingFunction) getIntent().getSerializableExtra("push");

		fLayoutParams = new FrameLayout.LayoutParams((int) (deviceWidth * 0.7), (int) (deviceWidth * 0.7));

		quicklicFrameLayout = (FrameLayout) findViewById(R.id.quicklic_main_FrameLayout);

		quicklicImageView = (ImageView) findViewById(R.id.quicklic_main_ImageView);
		quicklicImageView.setLayoutParams(fLayoutParams);
		quicklicImageView.setOnTouchListener(touchListener);

		//		addButton = (Button) findViewById(R.id.quicklic_add_Button);
		//		addButton.setOnClickListener(clickListener);

		addViewsForBalance(3);
	}

	/**
	 * @함수명 : displayMetrics
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 사용자의 Display 사이즈 정보 가져오기
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 5.
	 */
	private void displayMetrics()
	{
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		Display windowDisplay = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		windowDisplay.getMetrics(displayMetrics);

		deviceWidth = displayMetrics.widthPixels;
		deviceHeight = displayMetrics.heightPixels;
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

	private OnClickListener clickListener = new OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			if ( pageIndex == 0 )
			{
				if ( v.getId() == 0 )
				{
					quicklicFrameLayout.removeViews(1, viewCount);
					viewCount = 0;
					pageIndex = 1;
					addViewsForBalance(5);
					System.out.println("Hi0 : " + v.getId());
				}
				else if ( v.getId() == 1 )
				{
					quicklicFrameLayout.removeViews(1, viewCount);
					viewCount = 0;
					pageIndex = 2;
					addViewsForBalance(6);
					System.out.println("Hi1 : " + v.getId());
				}
				else if ( v.getId() == 2 )
				{
					quicklicFrameLayout.removeViews(1, viewCount);
					viewCount = 0;
					pageIndex = 3;
					addViewsForBalance(7);
					System.out.println("Hi2 : " + v.getId());
				}
			}
		}
	};

	/**
	 * @함수명 : addViewsForBalance
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) :
	 * @작성자 : 13 JHPark
	 * @작성일 : 2014. 5. 9.
	 */
	private void addViewsForBalance( int item_count )
	{
		final int ANGLE = 360 / item_count; // 360 / Item 개수

		Axis axis = new Axis();

		float itemSize = (deviceWidth * 0.15f); // 등록되어질 아이템의 크기
		float frameWidth = (deviceWidth * 0.7f);
		float frameHeight = (deviceWidth * 0.7f);

		// 반지름 길이 구하기
		int radius = (int) (frameHeight - itemSize) / 2 - 10;

		// 중심 좌표 구하기
		float origin_x = (frameWidth - itemSize) / 2;
		float origin_y = (frameHeight - itemSize) / 2;

		int angle_sum = 0; // 각도 누적
		for ( int i = 0; i < item_count; i++ )
		{
			// 기준 좌표와 각도를 넣어주고, 각도 만큼 떨어져 있는 좌표를 가져옴
			axis = getAxis(origin_x, origin_y, radius, angle_sum += ANGLE);

			// 레이아웃 설정 : 기본적인 크기는 정해져 있으며, 좌표 값만 설정
			FrameLayout.LayoutParams fLayoutParams = new FrameLayout.LayoutParams((int) itemSize, (int) itemSize, Gravity.TOP | Gravity.LEFT);
			fLayoutParams.leftMargin = axis.getAxis_x();
			fLayoutParams.topMargin = axis.getAxis_y();

			// TODO 다양한 정보를 갖고 있는 이미지뷰 클래스 정의
			ImageView image = new ImageView(context);
			image.setBackgroundResource(R.drawable.rendering_item);
			image.setLayoutParams(fLayoutParams);

			// TODO 추가한 아이템을 구별하기 위한 식별자와 클릭 리스너
			image.setId(i);
			viewCount++;
			image.setOnClickListener(clickListener);

			quicklicFrameLayout.addView(image);

			// TODO 새로 생성된 이미지뷰에 대한 정보를 ArrayList로 보관
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
		axis.setAxis_x((int) (origin_x + radius * Math.cos(Math.PI / 180 * (DEFALT_POSITION + angle))));
		axis.setAxis_y((int) (origin_y + radius * Math.sin(Math.PI / 180 * (DEFALT_POSITION + angle))));
		return axis;
	}

	private OnGestureListener gestureListener = new OnGestureListener()
	{

		@Override
		public boolean onSingleTapUp( MotionEvent e )
		{
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
}
