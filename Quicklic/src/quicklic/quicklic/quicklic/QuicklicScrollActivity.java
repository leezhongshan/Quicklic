package quicklic.quicklic.quicklic;

import quicklic.floating.api.R;
import quicklic.quicklic.util.DeviceMetricActivity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class QuicklicScrollActivity extends DeviceMetricActivity {

	private final float DEVICE_RATE = 0.2f;

	private LinearLayout mainLinearLayout;
	private Button upButton;
	private Button downButton;
	private Button leftButton;
	private Button rightButton;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scroll_quicklic);
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
				PixelFormat.RGBA_8888); // PixelFormat.RGBA_8888 : TRANSLUCENT 보다 추천한다고 함.

		layoutParams.gravity = Gravity.BOTTOM;
		layoutParams.x = 0;
		layoutParams.y = 0;

		//TODO 사용자가 크기 설정 가능하도록
		layoutParams.width = getDeviceWidth();
		layoutParams.height = (int) (getDeviceWidth() * 0.2f);

		getWindow().setAttributes(layoutParams);

		initialize();
	}

	private void initialize()
	{
		mainLinearLayout = (LinearLayout) findViewById(R.id.scroll_main_LinearLayout);
		upButton = (Button) findViewById(R.id.scroll_up_Button);
		downButton = (Button) findViewById(R.id.scroll_down_Button);
		leftButton = (Button) findViewById(R.id.scroll_left_Button);
		rightButton = (Button) findViewById(R.id.scroll_right_Button);

		upButton.setHeight((int) (getDeviceWidth() * DEVICE_RATE));
		downButton.setHeight((int) (getDeviceWidth() * DEVICE_RATE));
		leftButton.setHeight((int) (getDeviceWidth() * DEVICE_RATE));
		rightButton.setHeight((int) (getDeviceWidth() * DEVICE_RATE));

		upButton.setOnClickListener(clickListener);
		downButton.setOnClickListener(clickListener);
		leftButton.setOnClickListener(clickListener);
		rightButton.setOnClickListener(clickListener);

		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(getDeviceWidth(), (int) (getDeviceWidth() * 0.2f));
		mainLinearLayout.setLayoutParams(layoutParams);

		//		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_PHONE,
		//				//WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
		//				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
		//				PixelFormat.RGBA_8888); // PixelFormat.RGBA_8888 : TRANSLUCENT 보다 추천한다고 함.
		//
		//		layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		//		layoutParams.x = 0;
		//		layoutParams.y = 0;
		//
		//		//TODO 사용자가 크기 설정 가능하도록
		//		layoutParams.width = getDeviceWidth();
		//		layoutParams.height = (int) (getDeviceWidth() * 0.2f);

		//				ImageView imageView = new ImageView(this);
		//				imageView.setImageResource(R.drawable.favorite);
		//				imageView.setLayoutParams(new LinearLayout.LayoutParams(30, 30));
		//		
		//				// WindowManager에 layoutParams속성을 갖는 Quicklic ImageView 추가
		//				getUWindowManager().addView(imageView, winLayoutParams);
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
			else if ( v == mainLinearLayout )
			{
				System.out.println("Layout");
			}
		}
	};
}