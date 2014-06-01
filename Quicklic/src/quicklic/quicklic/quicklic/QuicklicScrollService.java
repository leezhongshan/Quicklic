package quicklic.quicklic.quicklic;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import quicklic.floating.api.R;
import quicklic.quicklic.test.TestingFunction;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class QuicklicScrollService extends Service {
	private static final int DOUBLE_PRESS_INTERVAL = 300;
	private static final int LIMITED_MOVE_DISTANCE = 10;
	private static final float DEVICE_RATE = 0.16f;
	private static final float VIEW_ALPHA = 0.8f;
	private static final int MAX_TASK_NUM = 300;
	private static final int MAX_SERVICE_NUM = 300;

	private WindowManager windowManager;
	private WindowManager.LayoutParams layoutParams;

	private LinearLayout scrollLinearLayout;

	private int keyboardHeight;
	private int deviceWidth;
	private int deviceHeight;

	private Button upButton;
	private Button downButton;
	private Button leftButton;
	private Button rightButton;
	private Button moveButton;
	private Button exitButton;

	private Intent intent;

	private boolean moveToSide;
	private boolean isDoubleClicked = false;
	private boolean isMoved = false;
	private Timer timer;
	private long lastPressTime;

	@Override
	public IBinder onBind( Intent intent )
	{
		return null;
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		Log.d("TAG", "onStartCommand - flags :" + flags + ", id : " + startId);

		// 이미 실행중인 Service 가 있다면, 추가 수행 금지
		if ( startId == 1 || flags == 1 )
		{
			// Quicklic View 숨기기
			TestingFunction.getFloatingServices().setVisibility(false);

			initialize(intent);
			createManager();
			createScrollLayout();
			addViewInWindowManager();
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
		if ( scrollLinearLayout != null )
			windowManager.removeView(scrollLinearLayout);
		super.onDestroy();
	}

	private void initialize( Intent intent )
	{
		this.intent = intent;
		timer = new Timer();
		deviceWidth = intent.getIntExtra("deviceWidth", 0);
		deviceHeight = intent.getIntExtra("deviceHeight", 0);
		keyboardHeight = (int) (deviceWidth * DEVICE_RATE);
	}

	private void createManager()
	{
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
	}

	private void addViewInWindowManager()
	{
		/* WindowManager.LayoutParams.TYPE_PHONE : Window를 최상위로 유지
		 * WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE :  다른 영역에 TouchEvent가 발생했을 때 인지 하지 않음
		 * PixelFormat.RGBA_8888 : 투명
		 */
		layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
						WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
				PixelFormat.RGBA_8888); // PixelFormat.RGBA_8888 : TRANSLUCENT 보다 추천한다고 함.

		layoutParams.windowAnimations = android.R.style.Animation_Dialog;
		layoutParams.gravity = Gravity.TOP | Gravity.LEFT;

		layoutParams.width = deviceWidth >> 1;
		layoutParams.height = keyboardHeight << 1;

		layoutParams.x = deviceWidth - layoutParams.width;
		layoutParams.y = deviceHeight - layoutParams.height;

		// WindowManager에 layoutParams속성을 갖는 Quicklic ImageView 추가
		windowManager.addView(scrollLinearLayout, layoutParams);
	}

	private void createScrollLayout()
	{
		scrollLinearLayout = new LinearLayout(this);
		LinearLayout backSectionLinearLayout = new LinearLayout(this);
		LinearLayout firstSectionLinearLayout = new LinearLayout(this);
		LinearLayout secondSectionLinearLayout = new LinearLayout(this);

		FrameLayout.LayoutParams scrollLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams sectionLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);

		sectionLayoutParams.weight = 0.5f;
		buttonLayoutParams.weight = 1 / 3f;
		buttonLayoutParams.setMargins(2, 2, 2, 2);

		upButton = new Button(this);
		downButton = new Button(this);
		leftButton = new Button(this);
		rightButton = new Button(this);
		moveButton = new Button(this);
		exitButton = new Button(this);

		scrollLinearLayout.setOrientation(LinearLayout.VERTICAL);
		scrollLinearLayout.setBackgroundColor(Color.TRANSPARENT);
		scrollLinearLayout.setWeightSum(1);
		scrollLinearLayout.setLayoutParams(scrollLayoutParams);

		backSectionLinearLayout.setOrientation(LinearLayout.VERTICAL);
		backSectionLinearLayout.setBackgroundColor(Color.WHITE);
		backSectionLinearLayout.setAlpha(VIEW_ALPHA);
		backSectionLinearLayout.setWeightSum(1);
		backSectionLinearLayout.setPadding(2, 2, 2, 2);
		backSectionLinearLayout.setLayoutParams(sectionLayoutParams);

		firstSectionLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		firstSectionLinearLayout.setBackgroundColor(Color.TRANSPARENT);
		firstSectionLinearLayout.setAlpha(VIEW_ALPHA);
		firstSectionLinearLayout.setWeightSum(1);
		firstSectionLinearLayout.setLayoutParams(sectionLayoutParams);

		secondSectionLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		secondSectionLinearLayout.setBackgroundColor(Color.TRANSPARENT);
		secondSectionLinearLayout.setAlpha(VIEW_ALPHA);
		secondSectionLinearLayout.setWeightSum(1);
		secondSectionLinearLayout.setLayoutParams(sectionLayoutParams);

		upButton.setHeight(keyboardHeight);
		upButton.setLayoutParams(buttonLayoutParams);
		upButton.setTextAppearance(this, R.style.Scroll_Button);
		upButton.setBackgroundColor(Color.BLACK);
		upButton.setText(R.string.scroll_up);

		downButton.setHeight(keyboardHeight);
		downButton.setLayoutParams(buttonLayoutParams);
		downButton.setTextAppearance(this, R.style.Scroll_Button);
		downButton.setBackgroundColor(Color.BLACK);
		downButton.setText(R.string.scroll_down);

		leftButton.setHeight(keyboardHeight);
		leftButton.setLayoutParams(buttonLayoutParams);
		leftButton.setTextAppearance(this, R.style.Scroll_Button);
		leftButton.setBackgroundColor(Color.BLACK);
		leftButton.setText(R.string.scroll_left);

		rightButton.setHeight(keyboardHeight);
		rightButton.setLayoutParams(buttonLayoutParams);
		rightButton.setTextAppearance(this, R.style.Scroll_Button);
		rightButton.setBackgroundColor(Color.BLACK);
		rightButton.setText(R.string.scroll_right);

		moveButton.setHeight(keyboardHeight);
		moveButton.setLayoutParams(buttonLayoutParams);
		moveButton.setTextAppearance(this, R.style.Scroll_Button);
		moveButton.setBackgroundColor(Color.BLACK);
		moveButton.setText(R.string.scroll_move);

		exitButton.setHeight(keyboardHeight);
		exitButton.setLayoutParams(buttonLayoutParams);
		exitButton.setTextAppearance(this, R.style.Scroll_Button);
		exitButton.setBackgroundColor(Color.BLACK);
		exitButton.setText(R.string.scroll_exit);

		upButton.setOnClickListener(clickListener);
		downButton.setOnClickListener(clickListener);
		leftButton.setOnClickListener(clickListener);
		rightButton.setOnClickListener(clickListener);
		moveButton.setOnClickListener(clickListener);
		exitButton.setOnClickListener(clickListener);

		moveButton.setOnTouchListener(onTouchListener);

		firstSectionLinearLayout.addView(moveButton);
		firstSectionLinearLayout.addView(upButton);
		firstSectionLinearLayout.addView(exitButton);

		secondSectionLinearLayout.addView(leftButton);
		secondSectionLinearLayout.addView(downButton);
		secondSectionLinearLayout.addView(rightButton);

		backSectionLinearLayout.addView(firstSectionLinearLayout);
		backSectionLinearLayout.addView(secondSectionLinearLayout);

		scrollLinearLayout.addView(backSectionLinearLayout);
	}

	private void getRunningTaskList()
	{
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskinfo = activityManager.getRunningTasks(MAX_TASK_NUM);

		System.out.println("Task" + taskinfo.size());
		for ( int i = 0; i < taskinfo.size(); i++ )
		{
			System.out.println("Task : " + taskinfo.get(i).topActivity.getPackageName());
		}

		List<RunningServiceInfo> serviceinfo = activityManager.getRunningServices(MAX_SERVICE_NUM);
		System.out.println("Service " + serviceinfo.size());
		for ( int i = 0; i < serviceinfo.size(); i++ )
		{
			if ( serviceinfo.get(i).foreground )
				System.out.println("Service : " + serviceinfo.get(i).service.getClassName());
		}
	}

	private OnClickListener clickListener = new OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			if ( v == upButton )
			{
				System.out.println("Up");
				getRunningTaskList();
			}
			else if ( v == downButton )
			{
				System.out.println("Down");
			}
			else if ( v == leftButton )
			{
				System.out.println("Left");
			}
			else if ( v == rightButton )
			{
				System.out.println("Right");
			}
			else if ( v == moveButton )
			{
				System.out.println("Move");
				long pressTime = System.currentTimeMillis();

				/* Double Clicked
				 * 현재 누른 시간과 마지막으로 누른 시간을 감산한 결과가
				 * DOUBLE_PRESS_INTERVAL보다 작으면 Double Clicked.
				 */
				if ( (pressTime - lastPressTime) <= DOUBLE_PRESS_INTERVAL )
				{
					// TODO Double Clicked
					System.out.println("key double");
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
								// TODO Single Clicked
								System.out.println("key single");
							}
						}
					};
					// DOUBLE_PRESS_INTERVAL 시간동안 Handler 를 Delay 시킴.
					handler.sendMessageDelayed(message, DOUBLE_PRESS_INTERVAL);
				}
				// 현재 누른 시간을 마지막 누른 시간으로 저장
				lastPressTime = pressTime;
			}
			else if ( v == exitButton )
			{
				// 서비스 종료
				stopService(intent);

				// MainActivity 시작
				intent = new Intent(QuicklicScrollService.this, QuicklicMainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		}
	};

	private OnTouchListener onTouchListener = new OnTouchListener()
	{
		private int initialX;
		private int initialY;
		private float initialTouchX;
		private float initialTouchY;
		private int moveTouchX;
		private int moveTouchY;

		@Override
		public boolean onTouch( View v, MotionEvent event )
		{
			try
			{
				if ( v == moveButton )
				{
					switch ( event.getAction() )
					{
					case MotionEvent.ACTION_DOWN:
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
						windowManager.updateViewLayout(scrollLinearLayout, layoutParams);

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

						if ( moveToSide && isMoved )
						{
							//	moveToSide();
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
	};
}
