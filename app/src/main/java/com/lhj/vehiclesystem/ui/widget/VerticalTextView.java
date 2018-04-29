package com.lhj.vehiclesystem.ui.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * @discription 竖向显示文本。
 * 多行显示时宽度不能使用Wrap——content 否则不能显示多行
 * 支持gravity中的center,center_vertical和center_horizon其他暂时不支持。
 * 多行文本可设置linespace属性来设置行间距
 * @autor songzhihang
 * @time 2017/11/2  上午10:23
 **/
public class VerticalTextView extends TextView {
    private static final String TAG = "VirticalTextView";
    public final static int ORIENTATION_UP_TO_DOWN = 0;
    Rect text_bounds = new Rect();
    private final int direction = ORIENTATION_UP_TO_DOWN;
    private int lines = 0;
    private int textWidth = 0;
    private int offsetX = 0;
    private int offsetY = 0;

    public VerticalTextView(Context context) {
        super(context);
        init();
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public VerticalTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        offsetX = (int) getTextSize() / 5;
        offsetY = (int) getTextSize() / 9;
    }

    /**
     * 重写settext 防止动态设值不刷新的问题
     *
     * @param text
     */
    public void setText(String text) {
        super.setText(text);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getPaint().getTextBounds(getText().toString(), 0, getText().length(),
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
        int times = 0;
        int startX = 0;
        int startY = 0;
        int stopX = 0;
        int stopY = 0;
        int start = 0;
        int end = 0;
        ArrayList<String> texts = new ArrayList<>();
        lines = 0;
        CharSequence textStr = getText();
        Log.e(TAG, "getText().length():" + textStr.length());
        for (int i = 0; i < textStr.length(); i++) {//根据行数就行绘制
            float sumWidth = 0;
            if (times >= 20) {
                invalidate();//chongxin绘制
                return;
            }
            for (int j = start; j < textStr.length(); j++) {//测量每行能显示文本数量
                char text = textStr.toString().charAt(j);
                float width = getPaint().measureText(String.valueOf(text));
                Log.e(TAG, "width:" + width);
                sumWidth = sumWidth + width;
                Log.e(TAG, "sumWidth:" + sumWidth);
                Log.e(TAG, "getMeasuredHeight() - getPaddingBottom() - getPaddingTop():" + (getMeasuredHeight() - getPaddingBottom() - getPaddingTop()));
                if ((int) sumWidth <= (getMeasuredHeight() - getPaddingBottom() - getPaddingTop() + 15)) {
                    Log.e(TAG, "end:" + end);
                    end = textStr.length();
                    if (end == 0) {
                        times++;
                    }
                    continue;
                } else {
                    end = j;
                    j = textStr.length();//退出
                }
            }
            Log.e(TAG, "end:" + end);
            String text = "";
            text = textStr.subSequence(start, end).toString();
            texts.add(text);
            start = end;//维护计数
            i = end - 1;//取结束位置保持行数的正确性(i本身需要++)
            lines++;
            if (end == 0) {
                times++;
            }
        }
        Log.e(TAG, "getLineSpacingExtra():" + getLineSpacingExtra());
        Log.e(TAG, "getGravity():" + getGravity());
        Log.e(TAG, "getMaxLines():" + getMaxLines());
        int needLines = lines;
        if (getMaxLines() <= lines) {
            lines = getMaxLines();
        }
        for (int i = 0; i < lines; i++) {
            Path path = new Path();//通过linespace 来获取行间距,进行添加
            startX = offsetX + (text_bounds.height() + (getLineSpacingExtra() <= 0 ? 3 : (int) getLineSpacingExtra())) * i + getPaddingLeft();//每行的x坐标机型偏移保证在下一列
            startY = offsetY + getPaddingTop();
            stopY = startY + (int) getPaint().measureText(texts.get(i));
            if (getGravity() == Gravity.CENTER || (getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL) {//居中算法 默认水平方向居中
                startX = startX + (getMeasuredWidth() - offsetX - getPaddingLeft() - getPaddingRight() - (text_bounds.height()) * lines + (getLineSpacingExtra() <= 0 ? 3 : (int) getLineSpacingExtra()) * (lines - 1)) / 2;
            }
            if ((getGravity() & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL || getGravity() == Gravity.CENTER) {
//                if (i + 1 >= lines) {//最后一行需要居中
                startY = startY + (getMeasuredHeight() - getPaddingBottom() - getPaddingTop() - (int) getPaint().measureText(texts.get(i))) / 2;
                stopY = startY + (int) getPaint().measureText(texts.get(i));
//                }
            }
            stopX = startX;
            path.moveTo(startX, startY);
            path.lineTo(stopX, stopY);
            getPaint().setColor(getCurrentTextColor());
            if (i == lines - 1 && i < needLines - 1) {//此时显示不全
                canvas.drawTextOnPath(texts.get(i).substring(0, texts.get(i).length() - 1) + "...", path, 0, 0, getPaint());//绘制每行的文本
            } else {
                canvas.drawTextOnPath(texts.get(i), path, 0, 0, getPaint());//绘制每行的文本
            }
        }
        canvas.restore();
    }
}
