package quicklic.floating.api;

import android.view.View;

public interface FloatingInterface
{
	public void setContext( FloatingService floatingServices );

	/**
	 * @함수명 : setDrawableQuiclic
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : Quicklic 이미지 설정
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 25.
	 */
	public int setDrawableQuicklic();

	/**
	 * @함수명 : setSize
	 * @매개변수 :
	 * @반환 : float
	 * @기능(역할) : Floating 이미지 크기 지정
	 * @작성자 : JHPark
	 * @작성일 : 2014. 6. 25.
	 */
	public float setSize();

	/**
	 * @함수명 : setAnimation
	 * @매개변수 :
	 * @반환 : boolean
	 * @기능(역할) : 처음 Animation 사용 설정
	 * @작성자 : JHPark
	 * @작성일 : 2014. 6. 25.
	 */
	public boolean setAnimation();

	/**
	 * @함수명 : touched
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 싱글 터치시 호출되는 함수
	 * @작성자 : 13 JHPark
	 * @작성일 : 2014. 6. 26.
	 */
	public void touched( View v );

	/**
	 * @함수명 : doubleTouched
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 더블 터치시 호출되는 함수
	 * @작성자 : 13 JHPark
	 * @작성일 : 2014. 6. 26.
	 */
	public void doubleTouched( View v );

	/**
	 * @함수명 : longTouched
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 긴 터치 시 호출되는 함수
	 * @작성자 : 13 JHPark
	 * @작성일 : 2014. 6. 26.
	 */
	public void longTouched( View v );

	/**
	 * @함수명 : setQuicklicVisibility
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : Floating 이미지 Visibility 변경
	 * @작성자 : 13 JHPark
	 * @작성일 : 2014. 6. 26.
	 */
	public void setQuicklicVisibility( boolean bool );
}
