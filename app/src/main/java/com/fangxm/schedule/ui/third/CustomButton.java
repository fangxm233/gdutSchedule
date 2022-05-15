package com.fangxm.schedule.ui.third;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fangxm.schedule.R;

/**
 * Created by liuqiang on 2017/11/3.
 * 自定义带波纹点击效果的Button，支持圆角矩形，圆形按钮等样式，可通过配置文件改变按下后的样式
 */
public class CustomButton extends LinearLayout {
    private static String TAG = "CustomButton";
    private Context mContext;
    /**
     * 按钮的背景色
     */
    private int bgColor = 0;
    /**
     * 按钮被按下时的背景色
     */
    private int bgColorPress = 0;
    /**
     * 按钮不可用的背景色
     */
    private int bgColorDisable = 0;

    /**
     * 按钮正常时文字的颜色
     */
    private int textColor;
    /**
     * 按钮被按下时文字的颜色
     */
    private int textColorPress;
    /**
     * 按钮不可点击时文字的颜色
     */
    private int textColorDisable;
    /**
     * 按钮的形状
     */
    private int shapeType;
    /**
     * 矩形时有效，4个角的radius
     */
    private float cornersRadius;
    /**
     * 边框线宽度
     */
    private int strokeWidth = 0;
    /**
     * 边框线颜色
     */
    private int strokeColor;

    private ColorStateList rippleColor;


    //shape的样式
    public static final int RECTANGLE = 0;
    public static final int OVAL = 1;
    public static final int LINE = 2;
    public static final int RING = 3;



    private GradientDrawable gradientDrawable = null;



    public CustomButton(Context context) {
        this(context,null);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getAttr(attrs);
        init();
    }

    private void getAttr(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CustomButton);
        if (a != null) {
            bgColor = a.getColor(R.styleable.CustomButton_bgColor, 0);
            bgColorPress = a.getColor(R.styleable.CustomButton_bgColorPress, 0);
            bgColorDisable = a.getColor(R.styleable.CustomButton_bgColorDisable, 0);

            textColor = a.getColor(R.styleable.CustomButton_textColor, 0);
            textColorPress = a.getColor(R.styleable.CustomButton_textColorPress, 0);
            textColorDisable = a.getColor(R.styleable.CustomButton_textColorDisable, 0);

            shapeType = a.getInt(R.styleable.CustomButton_shape, GradientDrawable.RECTANGLE);
            cornersRadius = a.getFloat(R.styleable.CustomButton_cornersRadius, 0);

            strokeWidth = a.getInteger(R.styleable.CustomButton_strokeWidth1,0);
            strokeColor = a.getColor(R.styleable.CustomButton_strokeColor,0);

            rippleColor = a.getColorStateList(R.styleable.CustomButton_rippleColor);
            if (rippleColor == null || rippleColor.getDefaultColor() == 0) {
                rippleColor = createRippleColorStateList(Color.GRAY);
            }
        }
    }
    private void init() {
        setClickable(true);
//        setBackground(getDrawable(android.R.attr.state_enabled));
        setBackground(getSelector());
//        setTextColor(createColorStateList());
    }

    /**
     * 设置GradientDrawable
     *
     * @param state 按钮状态
     * @return gradientDrawable
     */
    public GradientDrawable getDrawable(int state) {
        gradientDrawable = new GradientDrawable();
        setShape();
        setBorder();
        setRadius();
        setSelectorColor(state);
        return gradientDrawable;
    }

    /**
     * 设置shape类型
     */
    private void setShape() {

        switch (shapeType) {
            case RECTANGLE:
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                break;
            case OVAL:
                gradientDrawable.setShape(GradientDrawable.OVAL);
                break;
            case LINE:
                gradientDrawable.setShape(GradientDrawable.LINE);
                break;
            case RING:
                gradientDrawable.setShape(GradientDrawable.RING);
                break;
        }
    }
    /**
     * 设置边框  宽度  颜色  虚线  间隙
     */
    private void setBorder() {
        gradientDrawable.setStroke(strokeWidth, strokeColor, 0, 0);
    }

    /**
     * 只有类型是矩形的时候设置圆角半径才有效
     */
    private void setRadius() {
        if (shapeType == GradientDrawable.RECTANGLE) {
            if (cornersRadius != 0) {
                gradientDrawable.setCornerRadius(cornersRadius);//设置圆角的半径
            }
        }
    }

    /**
     * 设置Selector的不同状态的颜色
     *
     * @param state 按钮状态
     */
    private void setSelectorColor(int state) {
        switch (state) {
            case android.R.attr.state_pressed:
                gradientDrawable.setColor(bgColorPress);
                break;
            case -android.R.attr.state_enabled:
                gradientDrawable.setColor(bgColorDisable);
                break;
            case android.R.attr.state_enabled:
                gradientDrawable.setColor(bgColor);
                break;
        }
    }

    /**
     * 设置按钮的Selector
     *
     * @return stateListDrawable
     */
    public Drawable getSelector() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        //注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
        //所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, getDrawable(android.R.attr.state_pressed));
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, getDrawable(-android.R.attr.state_enabled));
        stateListDrawable.addState(new int[]{}, getDrawable(android.R.attr.state_enabled));

        RippleDrawable rippleDrawable = new RippleDrawable(rippleColor,stateListDrawable,null);
        return rippleDrawable;
    }
    /** 设置不同状态时其文字颜色 */
    private ColorStateList createColorStateList() {
        int[] colors = new int[] { textColorPress, textColorDisable, textColor};
        int[][] states = new int[3][];
        states[0] = new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled };
        states[1] = new int[] { -android.R.attr.state_enabled};
        states[2] = new int[] { android.R.attr.state_enabled };
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    /** 设置默认ripple颜色 */
    private ColorStateList createRippleColorStateList(int color) {
        int[] colors = new int[] {color};
        int[][] states = new int[1][];
        states[0] = new int[] { };
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    /////////////////对外暴露的方法//////////////

    /**
     * 设置Shape类型
     *
     * @param type 类型
     * @return 对象
     */
    public CustomButton setShapeType(int type) {
        this.shapeType = type;
        return this;
    }


    /**
     * 设置按下的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    public CustomButton setBgPressedColor(int color) {
        this.bgColorPress = color;
//        this.bgColorPress = getResources().getColor(color);
        return this;
    }

    /**
     * 设置正常的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    public CustomButton setBgNormalColor(int color) {
        this.bgColor = color;
//        this.bgColor = getResources().getColor(color);
        return this;
    }

    /**
     * 设置不可点击的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    public CustomButton setBgDisableColor(int color) {
        this.bgColorDisable = getResources().getColor(color);
        return this;
    }

    /**
     * 设置按下的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    public CustomButton setTextPressedColor(int color) {
        this.textColorPress = getResources().getColor(color);
        return this;
    }

    /**
     * 设置正常的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    public CustomButton setTextNormalColor(int color) {
        this.textColor = getResources().getColor(color);
        return this;
    }

    /**
     * 设置不可点击的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    public CustomButton setTextDisableColor(int color) {
        this.textColorDisable = getResources().getColor(color);
        return this;
    }

    /**
     * 设置边框宽度
     *
     * @param strokeWidth 边框宽度值
     * @return 对象
     */
    public CustomButton setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    /**
     * 设置边框颜色
     *
     * @param strokeColor 边框颜色
     * @return 对象
     */
    public CustomButton setStrokeColor(int strokeColor) {
        this.strokeColor = getResources().getColor(strokeColor);
        return this;
    }


    /**
     * 设置圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    public CustomButton setCornersRadius(float radius) {
        this.cornersRadius = radius;
        return this;
    }

    public CustomButton setRippleColor(int color){
        this.rippleColor = createRippleColorStateList(getResources().getColor(color));
        return this;
    }

    /**
     * 使用shape
     * 所有与shape相关的属性设置之后调用此方法才生效
     */
    public void use() {
        init();
    }


    /**
     * 单位转换工具类
     *
     * @param context  上下文对象
     * @param dipValue 值
     * @return 返回值
     */
    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}