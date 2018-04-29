package com.lhj.vehiclesystem.ui.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * @discription 竖向显示文本。
 * 多行显示时宽度不能使用Wrap——content 否则不能显示多行
 * 支持gravity中的center,center_vertical和center_horizon其他暂时不支持。
 * 多行文本可设置linespace属性来设置行间距
 * @autor songzhihang
 * @time 2017/11/2  上午10:23
 **/
public class VerticalEditText extends EditText {
    private static final String TAG = "VerticalEditText";
    public final static int ORIENTATION_UP_TO_DOWN = 0;
    Rect text_bounds = new Rect();
    private final int direction = ORIENTATION_UP_TO_DOWN;
    private int lines = 0;
    private int textWidth = 0;
    private boolean isShowCursor = false;
    private int count = 0;
    private int num = 0;

    private List<Float[]> locationXs = new ArrayList<>();
    private List<Float[]> locationYs = new ArrayList<>();

    public VerticalEditText(Context context) {
        super(context);
        init();
    }

    public VerticalEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public VerticalEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        setLongClickable(false);//取消剪切板功能
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence contentStr = "";
        if (!hasFocus() && TextUtils.isEmpty(getText()) && !TextUtils.isEmpty(getHint())) {//没有焦点,并且内容为空时,可以显示提示语
            contentStr = getHint();
        } else {
            contentStr = getText();
        }
        getPaint().getTextBounds(contentStr.toString(), 0, contentStr.length(),
                text_bounds);
//        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        if (direction == ORIENTATION_UP_TO_DOWN) {
            setMeasuredDimension(measureWidth(widthMeasureSpec),
                    measureHeight(heightMeasureSpec));
            return;
        }
        //先测量高度。如果能完全显示则不管,否则测量宽度,如果能够换行则换行;
        int result = 0;
        int heightspecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightspecSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthspecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthspecSize = MeasureSpec.getSize(widthMeasureSpec);
        if (heightspecMode == MeasureSpec.EXACTLY) {
            result = heightspecSize;
        } else {
            result = text_bounds.width() + getPaddingLeft() + getPaddingRight();
            if (heightspecMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, heightspecSize);
            }
        }
        if (result >= text_bounds.width() + getPaddingLeft() + getPaddingRight()) {//高度够放置内容。则正常设置宽高
            setMeasuredDimension(measureWidth(widthMeasureSpec),
                    measureHeight(heightMeasureSpec));
            return;
        } else {//宽度
            lines = text_bounds.width() / result + 1;//计算文本需要的行数
            Log.e(TAG, "lines:onMeasure" + lines);
            int width = lines * text_bounds.height() + getPaddingTop() + getPaddingBottom();//计算行数需要的宽度
//            if (widthspecMode == MeasureSpec.AT_MOST) {
//                width = Math.min(width, widthspecSize);//取宽度的最小值
//            }
            if (textWidth == 0) {
                textWidth = width;
                measure(width, getHeight());
                layout(getLeft(), getTop(), getLeft() + textWidth, getBottom());//重新绘制容器
            }
            lines = 0;
            setMeasuredDimension(width,
                    measureHeight(heightMeasureSpec));
//            if (widthspecMode == MeasureSpec.EXACTLY) {//宽度写死,判断宽度
//                setMeasuredDimension(measureWidth(widthMeasureSpec),
//                        measureHeight(heightMeasureSpec));
//                return;
//            } else {
//                lines = text_bounds.width() / result + 1;//计算文本需要的行数
//                int width = lines * text_bounds.height() + getPaddingTop() + getPaddingBottom();//计算行数需要的宽度
//                if (widthspecMode == MeasureSpec.AT_MOST) {
//                    width = Math.min(width, widthspecSize);//取宽度的最小值
//                }
//                setMeasuredDimension(width,
//                        measureHeight(heightMeasureSpec));
//            }
        }
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = text_bounds.height() + getPaddingTop()
                    + getPaddingBottom() + 10;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize) + 10;
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = text_bounds.width() + getPaddingLeft() + getPaddingRight() + 15;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize) + 15;
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//		 super.onDraw(canvas);
        canvas.save();
        count++;
        if (count >= 1 && !isShowCursor) {
            isShowCursor = true;
            num = 0;
        }
        if (num >= 1 && isShowCursor) {
            count = 0;
            isShowCursor = false;
        }
        Log.e(TAG, "isShowCursor:" + count);
        Log.e(TAG, "isShowCursor:" + isShowCursor);
        Log.e(TAG, "isShowCursor:" + num);
        int startX = 0;
        int startY = 0;
        int stopX = 0;
        int stopY = 0;
        int start = 0;
        int end = 0;
        TextPaint textPaint = getPaint();
        CharSequence contentStr = "";
        if (TextUtils.isEmpty(getText()) && !TextUtils.isEmpty(getHint())) {//没有焦点,并且内容为空时,可以显示提示语
            contentStr = getHint();
            textPaint.setColor(getCurrentHintTextColor());
        } else {
            contentStr = getText();
            textPaint.setColor(getCurrentTextColor());
        }
        getPaint().getTextBounds(contentStr.toString(), 0, contentStr.length(),
                text_bounds);
        ArrayList<String> texts = new ArrayList<>();
        lines = 0;
        Log.e(TAG, "contentStr.length():" + contentStr.length());
        locationXs.clear();
        locationYs.clear();
        if (hasFocus() && isShowCursor && getSelectionStart() == 0) {//显示光标
            Log.e(TAG, "isShowCursor:" + getSelectionStart());
            Log.e(TAG, "当前位置:" + getSelectionStart());
            num++;
            int left = getPaddingLeft();
            int top = getPaddingTop() + 3;
            RectF rectF = new RectF(left, top, left + text_bounds.height(), top + 3);
            Paint paint = new Paint();
            paint.setTextSize(getTextSize());
            paint.setColor(getCurrentTextColor());
            canvas.drawRect(rectF, paint);
        }
        for (int i = 0; i < contentStr.length(); i++) {//根据行数就行绘制
            float sumWidth = 0;
            for (int j = start; j < contentStr.length(); j++) {//测量每行能显示文本数量
                if (j == getSelectionStart() - 1) {
                    if (hasFocus() && isShowCursor) {//显示光标
                        Log.e(TAG, "isShowCursor:" + getSelectionStart());
                        num++;
                        int left = (text_bounds.height() + (getLineSpacingExtra() <= 0 ? 3 : (int) getLineSpacingExtra())) * lines + getPaddingLeft();
                        CharSequence charSequence = contentStr.subSequence(start, j + 1).toString();
                        Rect char_bounds = new Rect();
                        getPaint().getTextBounds(contentStr.toString(), 0, charSequence.length(),
                                char_bounds);
                        int top = char_bounds.width() + getPaddingTop();
                        RectF rectF = new RectF(left, top, left + text_bounds.height() + 2, top + 3);
//                        Paint paint=getPaint();
//                        paint.setColor(cursor);
                        canvas.drawRect(rectF, getPaint());
                    }
                }
                char text = contentStr.toString().charAt(j);
                float width = getPaint().measureText(String.valueOf(text));
                Float[] xarrs = new Float[2];
                Float[] yarrs = new Float[2];
                float xarrsstart = (text_bounds.height() + (getLineSpacingExtra() <= 0 ? 3 : (int) getLineSpacingExtra())) * lines + getPaddingLeft();
                xarrs[0] = xarrsstart;
                xarrs[1] = xarrsstart + text_bounds.height();
                yarrs[0] = sumWidth + getPaddingTop();
                Log.e(TAG, "width:" + width);
                sumWidth = sumWidth + width;
                yarrs[1] = sumWidth + getPaddingTop();
                Log.e(TAG, "sumWidth:" + sumWidth);
                Log.e(TAG, "getMeasuredHeight() - getPaddingBottom() - getPaddingTop():" + (getMeasuredHeight() - getPaddingBottom() - getPaddingTop()));
                if ((int) sumWidth <= (getMeasuredHeight() - getPaddingBottom() - getPaddingTop() + 15)) {
                    Log.e(TAG, "end:" + end);
                    end = contentStr.length();
                    locationXs.add(xarrs);
                    locationYs.add(yarrs);
                    continue;
                } else {
                    end = j;
                    j = contentStr.length();//退出
                }
            }
            Log.e(TAG, "end:" + end);
            String text = "";
            text = contentStr.subSequence(start, end).toString();
            texts.add(text);
            start = end;//维护计数
            i = end - 1;//取结束位置保持行数的正确性
            lines++;
        }
        Log.e(TAG, "getLineSpacingExtra():" + getLineSpacingExtra());
        Log.e(TAG, "getGravity():" + getGravity());
        for (int i = 0; i < lines; i++) {
            Path path = new Path();//通过linespace 来获取行间距,进行添加
            startX = (text_bounds.height() + (getLineSpacingExtra() <= 0 ? 3 : (int) getLineSpacingExtra())) * i + getPaddingLeft();//每行的x坐标机型偏移保证在下一列
            startY = getPaddingTop();
            stopY = startY + (int) getPaint().measureText(texts.get(i));
//            if (getGravity() == Gravity.CENTER || (getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {//居中算法 默认水平方向居中
//                startX = startX + (getMeasuredWidth() - getPaddingLeft() - getPaddingRight() -
//                        (text_bounds.height()) * lines + (getLineSpacingExtra() <= 0 ? 4 : (int) getLineSpacingExtra()) * (lines - 1)) / 2;
//            }
//            if ((getGravity() & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL || getGravity() == Gravity.CENTER) {
////                if (i + 1 >= lines) {//最后一行需要居中
//                startY = startY + (getMeasuredHeight() - getPaddingBottom() - getPaddingTop() - (int) getPaint().measureText(texts.get(i))) / 2;
//                stopY = startY + (int) getPaint().measureText(texts.get(i));
////                }
//            }
            stopX = startX;
            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);
            canvas.drawTextOnPath(texts.get(i), path, 0, 0, getPaint());//绘制每行的文本
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.e(TAG, "locationXs.getX():" + event.getX());
            Log.e(TAG, "locationXs.getY():" + event.getY());
            if (!TextUtils.isEmpty(getText())) {
                for (int i = 0; i < getText().length(); i++) {
                    Log.e(TAG, "locationXs.get(i)[0]:" + locationXs.get(i)[0]);
                    Log.e(TAG, "locationXs.get(i)[1]:" + locationXs.get(i)[1]);
                    Log.e(TAG, "locationYs.get(i)[0]:" + locationYs.get(i)[0]);
                    Log.e(TAG, "locationYs.get(i)[1]:" + locationYs.get(i)[1]);
                    if (event.getX() <= locationXs.get(i)[1] && event.getX() >= locationXs.get(i)[0]) {
                        if (event.getY() <= locationYs.get(i)[1] && event.getY() >= locationYs.get(i)[0]) {
                            setSelection(i + 1);
                            return false;
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

}
