package com.dq.stripe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import kotlin.math.abs
import kotlin.math.tan

class QStripeView : View {

    private var mPaint :Paint
    private var mPath = Path()

    private var startColor = 0
    private var endColor = 0

    // 倾斜角度， 3/4 or 315/360 = 45 度。 Rotation of the shapes， 3/4 = 45 degree
    private var rotation: Double = 0.0

    // 如果不倾斜，那么gapWidth 和 barWidth相同比较好看，如果倾斜，建议gapWidth是barWidth的两倍左右
    private var barWidth = 50f

    // 间距宽度
    private var gapWidth = 50f

    // 移动速度，默认400
    private var moveDuration: Long = 400

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("ResourceAsColor")
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        val attributes =
            context?.obtainStyledAttributes(attrs, R.styleable.QStripeView)
        attributes?.run {
            //上方color，下方color
            startColor = getColor(R.styleable.QStripeView_gradientStartColor, android.R.color.darker_gray)
            endColor = getColor(R.styleable.QStripeView_gradientEndColor, android.R.color.transparent)

            //条纹宽度、间距
            barWidth = getDimensionPixelSize(R.styleable.QStripeView_barWidth, 50).toFloat()
            gapWidth = getDimensionPixelSize(R.styleable.QStripeView_gapWidth, 50).toFloat()

            // 移动速度，默认400
            moveDuration = getInteger(R.styleable.QStripeView_moveDuration, 400).toLong()

            //角度
            rotation = Math.PI * getInteger(R.styleable.QStripeView_degree, 315) / 360

            attributes.recycle()
        }

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)//抗锯齿标志
        mPaint.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //  在最理想化的 在 LinearLayout + 本控件xml写死高度情况下：
        //  super.onMeasure + setMeasuredDimension 都写 = 调用两次onMeasure
        //  只写super.onMeasure = 调用两次onMeasure
        //  只写setMeasuredDimension = 调用两次onMeasure + measuredHeight为0
        //  在RelativeLayout下，onMeasure会回调4次
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //onDraw方法，超出measuredWidth的部分直接他就不画了，所以我让他的measuredWidth超出屏幕宽度。这样带来的问题是 动画只能向左走
        setMeasuredDimension(measuredWidth + (gapWidth + barWidth).toInt(), measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //总高度 * tan(角度) = x偏差值
        val endStartDiffX = measuredHeight * abs(tan(rotation)).toFloat()

        //每个小梯形的左上角的X值
        var startX = 0 - endStartDiffX // - (gapWidth + barWidth)

        //一共需要多少根小梯形，第一个+1是为了取整，第二个+1是为了动画
        val needRectCount = ((measuredWidth + endStartDiffX) / (gapWidth + barWidth)).toInt() + 1 + 1

        for (i in 0..needRectCount) {
            mPath.moveTo(startX,0f)

            //总高度 * tan(角度) = x值
            val endX = endStartDiffX + startX
            //画线到梯形的左下角坐标
            mPath.lineTo(endX, measuredHeight.toFloat())
            //画线到梯形的右下角坐标
            mPath.lineTo(endX + barWidth, measuredHeight.toFloat())
            //画线到梯形的右上角坐标
            mPath.lineTo(barWidth + startX,0f)
            //画线到梯形的左上角坐标
            mPath.lineTo(startX,0f)

            mPath.close()

            startX += (gapWidth + barWidth)
        }

        //第2步：把刚才框好的小梯形们涂上渐变颜色

        //这个LinearGradient只能每次都new（但是很危险，因为一时半会无法释放）。如果用一个全局变量的话，会不起作用。
        val linearGradientShader: Shader = LinearGradient(
            0f, 0f, 0f, measuredHeight.toFloat(), intArrayOf(startColor, endColor), floatArrayOf(0.0f, 1.0f), Shader.TileMode.CLAMP
        )
        //paint设置为渐变着色器
        mPaint.shader = linearGradientShader
        //涂上渐变颜色
        canvas.drawPath(mPath, mPaint)

        //如果你这样频繁重绘，会导致很卡（即便你没有在onDraw里new对象）。问题是系统的ProgressBar也是频繁调用了postInvalidateOnAnimation但是他不卡，推测可能是他onDraw里没绘制东西
//        postInvalidateOnAnimation()
    }

    //即便调用多次也无所谓的，系统底层会处理只保留最后一个动画
    private fun startTranslateAnimation() {
        // TranslateAnimation(10,20,10,20)：即为以起始点(当前x+10,当前y+10)，移动到终点(当前x+20,当前y+20)。
        // 效果为当前View跳跃到相对于当前位置的(10,10)点，移动到了相对于当前位置的(20,20)点

        // 当前View从自己的位置移动到了自己位置右方的(10,10)点
        val translateAnimation = TranslateAnimation(0f, - (gapWidth + barWidth), 0f, 0f)

        //渐变动画
        translateAnimation.duration = moveDuration
        translateAnimation.repeatMode = Animation.RESTART
        translateAnimation.repeatCount = Animation.INFINITE

        //移除动画的加减速效果
        translateAnimation.interpolator = LinearInterpolator()

        startAnimation(translateAnimation)
    }

    //-> Ac.onCreate.结束 -> 本类onAttachedToWindow()
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //开启平移动画，动画不会频繁触发onDraw
        startTranslateAnimation()
    }
}
