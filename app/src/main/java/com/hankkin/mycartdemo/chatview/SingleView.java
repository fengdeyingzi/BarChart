package com.hankkin.mycartdemo.chatview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.hankkin.mycartdemo.R;
import java.util.ArrayList;
import java.util.List;
import com.hankkin.mycartdemo.DisplayUtil;

/**
 * Created by Hankkin on 16/12/11.
 * 注释:参考MyChartView原理相
 * 柱状图表
 */

public class SingleView extends View {

	//文字颜色 柱形颜色
    private Paint mTextPaint, mChartPaint;
    //控件的宽高
    private int  mHeight, mWidth;
	//文字颜色 柱形起始渐变色 末尾渐变色 选择颜色
    private int lineColor, leftColor, lefrColorBottom,selectLeftColor;
    //保存柱形数据
	private List<Float> list = new ArrayList<>();
	//点击监听
    private getNumberListener listener;
    //private int number = 1000;
	//当前选择的柱形序号
    private int selectIndex = -1;
	//未知
    private List<Integer> selectIndexRoles = new ArrayList<>();
	//柱状图单个柱子的宽度
	private int rectWidth;
	//横向间距 纵向间距
	private int horizontalSpacing, verticalSpacing;
	//柱形表最大值和最小值
	private float maxSize,minSize;

	/*
	设置柱形图数据
	*/
    public void setList(List<Float> list) {
        this.list = list;
		if(list.size()>1){
			setMinimumWidth(list.size()*horizontalSpacing);
		}
		for(Float f : list){
			if(f > maxSize){
				maxSize= f;
			}
			if(f<minSize){
				minSize = f;
			}
		}
        
        invalidate();
    }

    public SingleView(Context context) {
        this(context, null);
    }

    public SingleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
		
		rectWidth = DisplayUtil.dip2px(context,20);
		verticalSpacing = DisplayUtil.dip2px(context,25);
		horizontalSpacing = DisplayUtil.dip2px(context,25);
		mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);

        mChartPaint = new Paint();
        mChartPaint.setAntiAlias(true);
		int textSize = DisplayUtil.dip2px(context,12);
		mTextPaint.setTextSize(textSize);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		setMinimumWidth(DisplayUtil.dip2px(context,320));
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyChartView, defStyleAttr, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.MyChartView_xyColor:
                    lineColor = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.MyChartView_leftColor:
                    // 默认颜色设置为黑色
                    leftColor = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.MyChartView_leftColorBottom:
                    lefrColorBottom = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.MyChartView_selectLeftColor:
                    // 默认颜色设置为黑色
                    selectLeftColor = array.getColor(attr, Color.BLACK);
                    break;
                default:
                    bringToFront();
            }
        }
        array.recycle();
        init();
    }
/*
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = widthSize * 1 / 2;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = heightSize * 1 / 2;
        }

        setMeasuredDimension(width, height);
    }
	*/

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        
    }

    private void init() {
        
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {

        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
           // mSize = getWidth() / 25;
            
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTextPaint.setColor(lineColor);
		int rectx=getPaddingLeft();
        for (int i = 0; i < list.size(); i++) {

            //画横向文字
            
            
			Rect mBound=new Rect();
            mTextPaint.getTextBounds(String.valueOf(i + 1) + "", 0, String.valueOf(i).length(), mBound);
            canvas.drawText(String.valueOf(i + 1) + "月", 
			rectx + i*horizontalSpacing+rectWidth/2, mHeight-getPaddingBottom()/2 - mBound.height() * 1 / 10, 
			mTextPaint);
            
        }

        for (int i = 0; i < list.size(); i++) {
           //计算纵向显示比例
			float size = (mHeight-mTextPaint.getTextSize()-getPaddingBottom()-getPaddingTop()) / maxSize;
            mChartPaint.setStyle(Paint.Style.FILL);
            if (list.size() > 0) {
                if (selectIndexRoles.contains(i)){
                    mChartPaint.setShader(null);
                    mChartPaint.setColor(selectLeftColor);
                }
                else {
                    LinearGradient lg = new LinearGradient(horizontalSpacing, rectWidth + horizontalSpacing, mHeight - 100,
                        (float) (mHeight - 100 - list.get(i) * size), lefrColorBottom, leftColor, Shader.TileMode.MIRROR);
                    mChartPaint.setShader(lg);
                }
                //画柱状图
                RectF rectF = new RectF();
                rectF.left = rectx+i*horizontalSpacing;
                rectF.right = rectx+ i*horizontalSpacing+rectWidth;
                rectF.bottom = mHeight - mTextPaint.getTextSize()-getPaddingBottom();
                rectF.top = mHeight - mTextPaint.getTextSize() - getPaddingBottom() - list.get(i) * size;
                canvas.drawRoundRect(rectF, 20, 20, mChartPaint);
                //mChartWidth += rectWidth;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int x = (int) ev.getX();
        int y = (int) ev.getY();
        int left = getPaddingLeft();
        int top = 0;
        int right = horizontalSpacing;
        int bottom = mHeight - 100;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < list.size(); i++) {
                     left = i*horizontalSpacing+getPaddingLeft();
                    right += i*horizontalSpacing+horizontalSpacing;
					Rect rect = new Rect(left, top, right, bottom);
					
                    if (rect.contains(x, y)) {
                        if (listener != null){
                            listener.getNumber(i, getPaddingLeft()+ i*horizontalSpacing+rectWidth/2, y);
                            
                            selectIndex = i;
                            selectIndexRoles.clear();
                            selectIndexRoles.add(selectIndex);
                            invalidate();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }
	
	//设置文字大小 sp
	public void setTextSize(int size){
		mTextPaint.setTextSize(DisplayUtil.sp2px(getContext(),size));
		invalidate();
	}

    public void setListener(getNumberListener listener) {
        this.listener = listener;
    }

    public interface getNumberListener {
        void getNumber(int number, int x, int y);
    }
}
