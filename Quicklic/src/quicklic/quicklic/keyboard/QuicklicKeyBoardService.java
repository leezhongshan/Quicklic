package quicklic.quicklic.keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import quicklic.floating.api.R;
import quicklic.quicklic.main.QuicklicMainActivity;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class QuicklicKeyBoardService extends Service {

	private static final int DOUBLE_PRESS_INTERVAL = 300;
	private static final int LIMITED_MOVE_DISTANCE = 10;
	private static float KEY_HEIGHT_RATE;
	private static float KEY_WIDTH_RATE;
	private static final float VIEW_ALPHA = 0.8f;
	private static final int MAX_TASK_NUM = 300;

	private WindowManager windowManager;
	private WindowManager.LayoutParams layoutParams;

	private LinearLayout keyboardLinearLayout;

	private int keyboardHeight;
	private int keyboardWidth;
	private int deviceWidth;
	private int deviceHeight;

	private Button leftButton;
	private Button rightButton;
	private Button moveButton;
	private Button exitButton;

	private Intent intent;

	private boolean isDoubleClicked = false;
	private boolean isMoved = false;
	private Timer timer;
	private long lastPressTime;

	private ActivityManager activityManager;
	private ArrayList<String> packageArrayList;
	private int packageIndex;

	@Override
	public IBinder onBind( Intent intent )
	{
		return null;
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		super.onConfigurationChanged(newConfig);
		displayMetrics();
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		// 이미 실행중인 Service 가 있다면, 추가 수행 금지
		if ( startId == 1 || flags == 1 )
		{
			// Quicklic View 숨기기
			try
			{
				initialize(intent);
				createManager();
				displayMetrics();
				createKeyBoard();
				getRunningTaskList();
				addViewInWindowManager();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			stopService(intent);
		}

		return Service.START_NOT_STICKY;
	}

	public void onDestroy()
	{
		if ( keyboardLinearLayout != null )
			windowManager.removeView(keyboardLinearLayout);
		super.onDestroy();
	}

	private void displayMetrics()
	{
		Display windowDisplay = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		windowDisplay.getMetrics(displayMetrics);

		// Device의 Display에서 width와 height 구하기
		deviceWidth = displayMetrics.widthPixels;
		deviceHeight = displayMetrics.heightPixels;

		// 화면 회전의 방향에 따른 floating resize
		if ( windowDisplay.getRotation() == Surface.ROTATION_0 )
		{
			KEY_HEIGHT_RATE = 0.125f;
			KEY_WIDTH_RATE = 0.25f;
		}
		else
		{
			KEY_HEIGHT_RATE = 0.07f;
			KEY_WIDTH_RATE = 0.14f;
		}

		keyboardHeight = (int) (deviceWidth * KEY_HEIGHT_RATE) << 1;
		keyboardWidth = (int) (deviceWidth * KEY_WIDTH_RATE);
	}

	private void initialize( Intent intent )
	{
		this.intent = intent;
		timer = new Timer();
		packageArrayList = new ArrayList<String>();
		packageIndex = 0;

		KEY_HEIGHT_RATE = 0.125f;
		KEY_WIDTH_RATE = 0.25f;
	}

	private void createManager()
	{
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	}

	private void addViewInWindowManager()
	{
		/* WindowManager.LayoutParams.TYPE_PHONE : Window를 최상위로 유지
		 * WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE :  다른 영역에 TouchEvent가 발생했을 때 인지 하지 않음
		 * PixelFormat.RGBA_8888 : 투명
		 */
		layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
				PixelFormat.RGBA_8888);

		layoutParams.windowAnimations = android.R.style.Animation_Dialog;
		layoutParams.gravity = Gravity.TOP | Gravity.LEFT;

		layoutParams.width = keyboardWidth;
		layoutParams.height = keyboardHeight;

		layoutParams.x = deviceWidth - layoutParams.width;
		layoutParams.y = deviceHeight - layoutParams.height;

		// WindowManager에 layoutParams속성을 갖는 Quicklic ImageView 추가
		windowManager.addView(keyboardLinearLayout, layoutParams);
	}

	private void createKeyBoard()
	{
		keyboardLinearLayout = new LinearLayout(this);
		LinearLayout backSectionLinearLayout = new LinearLayout(this);
		LinearLayout firstSectionLinearLayout = new LinearLayout(this);
		LinearLayout secondSectionLinearLayout = new LinearLayout(this);

		FrameLayout.LayoutParams keyboardLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams sectionLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);

		sectionLayoutParams.weight = 0.5f;
		buttonLayoutParams.weight = 0.5f;

		leftButton = new Button(this);
		rightButton = new Button(this);
		moveButton = new Button(this);
		exitButton = new Button(this);

		keyboardLinearLayout.setOrientation(LinearLayout.VERTICAL);
		keyboardLinearLayout.setBackgroundColor(Color.TRANSPARENT);
		keyboardLinearLayout.setWeightSum(1);
		keyboardLinearLayout.setLayoutParams(keyboardLayoutParams);

		backSectionLinearLayout.setOrientation(LinearLayout.VERTICAL);
		backSectionLinearLayout.setBackgroundColor(Color.WHITE);
		backSectionLinearLayout.setAlpha(VIEW_ALPHA);
		backSectionLinearLayout.setWeightSum(1);
		backSectionLinearLayout.setPadding(2, 2, 2, 2);
		backSectionLinearLayout.setLayoutParams(sectionLayoutParams);

		firstSectionLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		firstSectionLinearLayout.setBackgroundColor(Color.BLACK);
		firstSectionLinearLayout.setAlpha(VIEW_ALPHA);
		firstSectionLinearLayout.setWeightSum(1);
		firstSectionLinearLayout.setLayoutParams(sectionLayoutParams);

		secondSectionLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		secondSectionLinearLayout.setBackgroundColor(Color.BLACK);
		secondSectionLinearLayout.setAlpha(VIEW_ALPHA);
		secondSectionLinearLayout.setWeightSum(1);
		secondSectionLinearLayout.setLayoutParams(sectionLayoutParams);

		leftButton.setHeight(keyboardHeight);
		leftButton.setLayoutParams(buttonLayoutParams);
		leftButton.setTextAppearance(this, R.style.KeyBoard_Button);
		leftButton.setBackgroundResource(R.drawable.key_left);

		rightButton.setHeight(keyboardHeight);
		rightButton.setLayoutParams(buttonLayoutParams);
		rightButton.setTextAppearance(this, R.style.KeyBoard_Button);
		rightButton.setBackgroundResource(R.drawable.key_right);

		moveButton.setHeight(keyboardHeight);
		moveButton.setLayoutParams(buttonLayoutParams);
		moveButton.setTextAppearance(this, R.style.KeyBoard_Button);
		moveButton.setBackgroundResource(R.drawable.key_move);

		exitButton.setHeight(keyboardHeight);
		exitButton.setLayoutParams(buttonLayoutParams);
		exitButton.setTextAppearance(this, R.style.KeyBoard_Button);
		exitButton.setBackgroundResource(R.drawable.key_exit);

		leftButton.setOnClickListener(clickListener);
		rightButton.setOnClickListener(clickListener);
		moveButton.setOnClickListener(clickListener);
		exitButton.setOnClickListener(clickListener);

		moveButton.setOnTouchListener(onTouchListener);

		firstSectionLinearLayout.addView(moveButton);
		firstSectionLinearLayout.addView(exitButton);

		secondSectionLinearLayout.addView(leftButton);
		secondSectionLinearLayout.addView(rightButton);

		backSectionLinearLayout.addView(firstSectionLinearLayout);
		backSectionLinearLayout.addView(secondSectionLinearLayout);

		keyboardLinearLayout.addView(backSectionLinearLayout);
	}

	/**
	 * @함수명 : getRunningTaskList
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 현재 실행중인 Task의 목록을 가져오기
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 18.
	 */
	private void getRunningTaskList()
	{
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
		String launcherName = resolveInfo.activityInfo.packageName;

		List<RunningTaskInfo> taskinfo = activityManager.getRunningTasks(MAX_TASK_NUM);
		for ( int i = 0; i < taskinfo.size(); i++ )
		{
			String packageName = taskinfo.get(i).topActivity.getPackageName();

			if ( !(packageName.contains(launcherName) || packageName.contains(".phone") || packageName.contains("quicklic")
					|| packageName.contains(".contacts") || packageName.contains("skt.prod")) )
			{
				System.out.println(packageName);
				packageArrayList.add(packageName);
			}
		}
	}

	private OnClickListener clickListener = new OnClickListener()
	{
		/**
		 * @함수명 : onClick
		 * @매개변수 : View v
		 * @기능(역할) : 키보드에 등록된 버튼에 대한 어플리케이션 간 전환 및 키보드 서비스의 이동과 종료
		 * @작성자 : THYang
		 * @작성일 : 2014. 6. 18.
		 */
		@SuppressLint("HandlerLeak")
		@Override
		public void onClick( View v )
		{
			PackageManager packageManager = getPackageManager();
			if ( v == leftButton )
			{
				if ( packageArrayList.size() == 0 )
				{
					Toast.makeText(getApplicationContext(), R.string.keyboard_move_no, Toast.LENGTH_SHORT).show();
					return;
				}

				if ( packageIndex > 0 )
				{
					--packageIndex;

					try
					{
						Intent intent = packageManager.getLaunchIntentForPackage(packageArrayList.get(packageIndex));
						startActivity(intent);
					}
					catch (Exception e)
					{
						Toast.makeText(getApplicationContext(), R.string.keyboard_run_no, Toast.LENGTH_SHORT).show();
						packageArrayList.remove(packageIndex);
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), R.string.keyboard_move_first, Toast.LENGTH_SHORT).show();
				}

			}
			else if ( v == rightButton )
			{
				if ( packageArrayList.size() == 0 )
				{
					Toast.makeText(getApplicationContext(), R.string.keyboard_move_no, Toast.LENGTH_SHORT).show();
					return;
				}
				if ( packageIndex != packageArrayList.size() - 1 )
				{
					++packageIndex;

					try
					{
						Intent intent = packageManager.getLaunchIntentForPackage(packageArrayList.get(packageIndex));
						startActivity(intent);
					}
					catch (Exception e)
					{
						Toast.makeText(getApplicationContext(), R.string.keyboard_run_no, Toast.LENGTH_SHORT).show();
						packageArrayList.remove(packageIndex);
						packageIndex--;
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), R.string.keyboard_move_finish, Toast.LENGTH_SHORT).show();
				}

			}
			else if ( v == moveButton )
			{
				long pressTime = System.currentTimeMillis();

				/* Double Clicked
				 * 현재 누른 시간과 마지막으로 누른 시간을 감산한 결과가
				 * DOUBLE_PRESS_INTERVAL보다 작으면 Double Clicked.
				 */
				if ( (pressTime - lastPressTime) <= DOUBLE_PRESS_INTERVAL )
				{
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
				intent = new Intent(QuicklicKeyBoardService.this, QuicklicMainActivity.class);
				startService(intent);
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
						windowManager.updateViewLayout(keyboardLinearLayout, layoutParams);

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
