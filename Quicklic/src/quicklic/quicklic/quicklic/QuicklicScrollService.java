package quicklic.quicklic.quicklic;

import quicklic.floating.api.R;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class QuicklicScrollService extends Service {

	private final float DEVICE_RATE = 0.16f;
	private final float VIEW_ALPHA = 0.8f;

	private WindowManager windowManager;
	private WindowManager.LayoutParams layoutParams;

	private LinearLayout scrollLinearLayout;

	private int keyboardHeight;
	private int deviceWidth;

	private Button upButton;
	private Button downButton;
	private Button leftButton;
	private Button rightButton;
	private Button moveButton;
	private Button exitButton;

	private Intent intent;

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
		Log.d("TAG", "onDestroy");
		super.onDestroy();

		if ( scrollLinearLayout != null )
			windowManager.removeView(scrollLinearLayout);
	}

	private void initialize( Intent intent )
	{
		this.intent = intent;
		deviceWidth = intent.getIntExtra("deviceWidth", 0);
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
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.RGBA_8888); // PixelFormat.RGBA_8888 : TRANSLUCENT 보다 추천한다고 함.

		layoutParams.windowAnimations = android.R.style.Animation_Dialog;
		layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		layoutParams.x = 0;
		layoutParams.y = 0;

		layoutParams.width = deviceWidth >> 1;
		layoutParams.height = keyboardHeight << 1;

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

	private OnClickListener clickListener = new OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			if ( v == upButton )
			{
				System.out.println("Up");
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
			}
			else if ( v == exitButton )
			{
				System.out.println("Exit");
				stopService(intent);
			}
		}
	};
}
