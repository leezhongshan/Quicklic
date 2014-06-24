package quicklic.floating.api;

import java.util.Timer;
import java.util.TimerTask;

import quicklic.quicklic.test.FinishService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class FloatingService extends Service
{
	private static final String TAG = "Quicklic";
	private static final int NOTIFICATION_ID = 1;
	private static final int DOUBLE_PRESS_INTERVAL = 300;
	private static final int LIMITED_MOVE_DISTANCE = 10;

	private WindowManager windowManager;
	private WindowManager.LayoutParams layoutParams;

	private ImageView quicklic;

	private Context context;
	private int deviceWidth;
	private int deviceHeight;
	private int deviceHorizontalCenter;
	private int deviceVerticalCenter;

	private boolean moveToSide;
	private boolean isDoubleClicked = false;
	private boolean isMoved = false;

	private FloatingInterface floatingInterface;
	private NotificationManager notificationManager;

	private Timer timer;
	private long lastPressTime;

	/*****************************************************************************/
	/** API Section **/
	/*****************************************************************************/

	/**
	 * @함수명 : changeMoveToSide
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 애니메이션 기능 On/Off
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 5.
	 */
	public void changeMoveToSide()
	{
		if ( moveToSide )
		{
			moveToSide = false;
		}
		else
		{
			moveToSide = true;
		}
	}

	public void setVisibility( boolean bool )
	{
		if ( bool )
		{
			getQuicklic().setVisibility(View.VISIBLE);
		}
		else
		{
			getQuicklic().setVisibility(View.GONE);
		}
	}

	/**
	 * @함수명 : getQuicklic
	 * @매개변수 :
	 * @반환 : ImageView
	 * @기능(역할) : Quicklic 이미지 객체 가져오기
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 30.
	 */
	public ImageView getQuicklic()
	{
		return quicklic;
	}

	/**
	 * @함수명 : stopQuicklicService
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : Quicklic Floating 서비스 종료
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 30.
	 */
	public void stopQuicklicService()
	{

	}

	/*****************************************************************************/
	/** Developer Section **/
	/*****************************************************************************/
	@Override
	public IBinder onBind( Intent arg0 )
	{
		Log.d(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate()
	{
		Log.d(TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		Log.d(TAG, "onStartCommand - flags :" + flags + ", id : " + startId);

		// TODO 이미 실행중인 Service 가 있다면, 추가 수행 금지
		if ( startId == 1 || flags == 1 )
		{
			initialize(intent);
			managerCreate();
			displayMetrics();
			quicklicCreate();
			quicklicSetting();
			quicklicNotification();
		}
		else
		{
			stopService(intent);
		}

		// START_REDELIVER_INTENT : START_STICKY와 마찬가지로 Service 종료 시,
		// 시스템이 다시 재시작 시켜주지만 intent 값은 그대로 유지시켜준다.
		return Service.START_REDELIVER_INTENT;
	}

	public void onDestroy()
	{
		Log.d(TAG, "onDestroy");
		super.onDestroy();

		if ( getQuicklic() != null )
			windowManagerRemoveView(getQuicklic());

		notificationManager.cancel(NOTIFICATION_ID);
	}

	private void initialize( Intent intent )
	{
		context = this;
		timer = new Timer();

		floatingInterface = (FloatingInterface) intent.getSerializableExtra("push");
		floatingInterface.setContext(this);
		moveToSide = floatingInterface.setAnimation();
	}

	private void managerCreate()
	{
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
	}

	private void displayMetrics()
	{
		Display windowDisplay = windowManager.getDefaultDisplay();

		DisplayMetrics displayMetrics = new DisplayMetrics();
		windowDisplay.getMetrics(displayMetrics);

		deviceWidth = displayMetrics.widthPixels;
		deviceHeight = displayMetrics.heightPixels;
	}

	private void quicklicCreate()
	{
		int imageWidth = (int) (deviceWidth * floatingInterface.setSize());
		int imageHeight = (int) (deviceWidth * floatingInterface.setSize());

		quicklic = new ImageView(this);

		//TODO 이미지 변경 가능 하도록
		getQuicklic().setImageResource(R.drawable.floating);

		// Device의 Display에서 가운데 위치 구하기
		deviceHorizontalCenter = (deviceWidth - imageWidth) / 2;
		deviceVerticalCenter = (deviceHeight - imageHeight) / 2;

		/* WindowManager.LayoutParams.TYPE_PHONE : Window를 최상위로 유지
		 * WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE :  다른 영역에 TouchEvent가 발생했을 때 인지 하지 않음
		 * PixelFormat.TRANSLUCENT : 투명
		 */
		layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888); // PixelFormat.RGBA_8888 : TRANSLUCENT 보다 추천한다고 함.

		layoutParams.windowAnimations = android.R.style.Animation_Dialog;

		layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		layoutParams.x = deviceHorizontalCenter;
		layoutParams.y = deviceVerticalCenter;

		//TODO 사용자가 크기 설정 가능하도록
		layoutParams.width = imageWidth;
		layoutParams.height = imageHeight;

		// WindowManager에 layoutParams속성을 갖는 Quicklic ImageView 추가
		windowManagerAddView(getQuicklic(), layoutParams);

	}

	/**
	 * @함수명 : quicklicSetting
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : quicklic에 click,longClick,touch Listener 를 설정
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 5.
	 */
	private void quicklicSetting()
	{
		getQuicklic().setOnTouchListener(touchListener);
		getQuicklic().setOnClickListener(clickListener);
		getQuicklic().setOnLongClickListener(longClickListener);
	}

	/**
	 * @함수명 : quicklicNotification
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : Quicklic 서비스를 수행 시, Notification 창에 알림 설정
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 5.
	 */
	private void quicklicNotification()
	{
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent intent = PendingIntent.getActivity(context, 0, new Intent(context, FinishService.class), 0);

		Notification notification = new NotificationCompat.Builder(getApplicationContext()).setContentTitle("Quicklic").setContentText("Quicklic이 실행중입니다! 누르시면 종료됩니다. ")
				.setSmallIcon(R.drawable.ic_launcher)
				.setTicker("Quicklic을 시작합니다.").setOngoing(true).setContentIntent(intent).build();

		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	/**
	 * @함수명 : windowManagerAddView
	 * @매개변수 : View view, LayoutParams layoutParams
	 * @반환 : void
	 * @기능(역할) : windowManager에 view 를 추가
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 5.
	 */
	private void windowManagerAddView( View view, LayoutParams layoutParams )
	{
		windowManager.addView(view, layoutParams);
	}

	/**
	 * @함수명 : windowManagerUpdateViewLayout
	 * @매개변수 : View view, LayoutParams layoutParams
	 * @반환 : void
	 * @기능(역할) : windowManager에 view 를 갱신
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 5.
	 */
	private void windowManagerUpdateViewLayout( View view, LayoutParams layoutParams )
	{
		windowManager.updateViewLayout(view, layoutParams);
	}

	//a
	/**
	 * @함수명 : windowManagerRemoveView
	 * @매개변수 : View view
	 * @반환 : void
	 * @기능(역할) : windowManager에서 view 를 제거
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 7.
	 */
	private void windowManagerRemoveView( View view )
	{
		windowManager.removeView(view);
	}

	private OnClickListener clickListener = new OnClickListener()
	{
		private View view;

		@Override
		public void onClick( View v )
		{
			this.view = v;
			try
			{
				if ( v == getQuicklic() )
				{
					long pressTime = System.currentTimeMillis();

					/* Double Clicked
					 * 현재 누른 시간과 마지막으로 누른 시간을 감산한 결과가
					 * DOUBLE_PRESS_INTERVAL보다 작으면 Double Clicked.
					 */
					if ( (pressTime - lastPressTime) <= DOUBLE_PRESS_INTERVAL )
					{
						floatingInterface.doubleTouched(view);

						// Double Clicked 인 경우, 핸들러가 실행되도 Single Click 작업 하지 않음.
						isDoubleClicked = true;
					}
					// Single Clicked
					else
					{
						isDoubleClicked = false;

						// Handler 에 Single Click 시 수행할 작업을 등록
						Message message = new Message();
						Handler handler = new Handler()
						{
							public void handleMessage( Message message )
							{
								// Double Clicked 가 아니고 객체의 이동 수행을 하지 않았다면 실행.
								if ( !isDoubleClicked && isMoved == false )
								{
									floatingInterface.touched(view);
								}
							}
						};
						// DOUBLE_PRESS_INTERVAL 시간동안 Handler 를 Delay 시킴.
						handler.sendMessageDelayed(message, DOUBLE_PRESS_INTERVAL);
					}
					// 현재 누른 시간을 마지막 누른 시간으로 저장
					lastPressTime = pressTime;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	};

	private OnLongClickListener longClickListener = new OnLongClickListener()
	{
		@Override
		public boolean onLongClick( View v )
		{
			if ( !isMoved )
			{
				floatingInterface.longTouched(v);
			}
			return true;
		}
	};

	private OnTouchListener touchListener = new OnTouchListener()
	{
		private int initialX;
		private int initialY;
		private float initialTouchX;
		private float initialTouchY;
		private int moveTouchX;
		private int moveTouchY;
		ImageView imageView;

		@Override
		public boolean onTouch( View v, MotionEvent event )
		{
			imageView = getQuicklic();
			try
			{
				if ( v == imageView )
				{
					switch ( event.getAction() )
					{
					case MotionEvent.ACTION_DOWN:
						isMoved = false;

						initialX = layoutParams.x;
						initialY = layoutParams.y;
						initialTouchX = event.getRawX();
						initialTouchY = event.getRawY();
						break;

					case MotionEvent.ACTION_MOVE:
						moveTouchX = (int) (event.getRawX() - initialTouchX);
						moveTouchY = (int) (event.getRawY() - initialTouchY);

						layoutParams.x = initialX + moveTouchX;
						layoutParams.y = initialY + moveTouchY;
						windowManagerUpdateViewLayout(getQuicklic(), layoutParams);

						// 터치 감지 : X와 Y좌표가 10이하인 경우에는 움직임이 없다고 판단하고 single touch 이벤트 발생.
						isMoved = true;
						if ( Math.abs(moveTouchX) < LIMITED_MOVE_DISTANCE && Math.abs(moveTouchY) < LIMITED_MOVE_DISTANCE )
							isMoved = false;

						break;

					case MotionEvent.ACTION_UP:
						if ( isMoved )
						{
							timer.schedule(new TimerTask()
							{
								@Override
								public void run()
								{
									// DOUBLE_PRESS_INTERVAL+100 milliseconds 가 지나가면, 다시 클릭 가능해짐. 
									isMoved = false;
								}
							}, DOUBLE_PRESS_INTERVAL + 100);
						}

						//TODO UP되어 있을 때, 이동하는 모습 Animation으로 추후 적용 할 부분
						if ( moveToSide && isMoved )
						{
							moveToSide();
						}
						break;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return false;
		}

		private void moveToSide()
		{

			initialX = layoutParams.x;
			initialY = layoutParams.y;

			int toX = layoutParams.x;
			int toY = layoutParams.y;

			System.out.println(getQuicklic().getHeight() + " " + getQuicklic().getWidth());
			if ( layoutParams.x > deviceHorizontalCenter )
			{
				if ( layoutParams.y > deviceVerticalCenter )
				{
					if ( deviceWidth - (layoutParams.x + imageView.getWidth()) > deviceHeight - (layoutParams.y + imageView.getHeight()) )
					{
						toY = deviceHeight - imageView.getHeight();
					}
					else
					{
						toX = deviceWidth - imageView.getWidth();
					}
				}
				else
				{
					if ( deviceWidth - (layoutParams.x + imageView.getWidth()) > layoutParams.y )
					{
						toY = 0;
					}
					else
					{
						toX = deviceWidth - imageView.getWidth();
					}
				}
			}
			else
			{
				if ( layoutParams.y > deviceVerticalCenter )
				{
					if ( layoutParams.x > deviceHeight - (layoutParams.y + imageView.getHeight()) )
					{
						toY = deviceHeight - imageView.getHeight();
					}
					else
					{
						toX = 0;
					}
				}
				else
				{
					if ( layoutParams.x > layoutParams.y )
					{
						toY = 0;
					}
					else
					{
						toX = 0;
					}
				}
			}

			layoutParams.x = toX;
			layoutParams.y = toY;

			windowManagerUpdateViewLayout(imageView, layoutParams);
		}
	};
}