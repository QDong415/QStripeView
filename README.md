## QStripeView 简介

Loading Animation Stripe View, enable to custom speed,direction,gapWidth. Base on onDraw and animation ,very lightful

仿`快手直播`界面加载中，`滚动条纹Loading`，适用于 视频加载动画，直播界面加载动画。基于onDraw和animation实现，占用内存极小，非常轻量级

## IOS版本
[ios版本链接](https://github.com/QDong415/StripeAnimationLayer)

## Screenshot 预览图

![](https://upload-images.jianshu.io/upload_images/26002059-b17dc9ac39fc2617.gif?imageMogr2/auto-orient/strip|imageView2/2/w/270/format/webp)

## 导入
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.QDong415:QStripeView:v1.0'
	}
```

## 使用
```xml
 <com.dq.stripe.QStripeView
        android:id="@+id/stripe_view"
        app:gradientStartColor="@android:color/darker_gray"
        app:gradientEndColor="@android:color/transparent"
        app:gapWidth="12dp"
        app:barWidth="10dp"
        app:moveDuration="500"
        android:layout_width="match_parent"
        android:layout_height="300dp"/>
```

```xml
   <declare-styleable name="QStripeView">
        <!-- 上方color -->
        <attr name="gradientStartColor" format="color"/>
        <!-- 下方color -->
        <attr name="gradientEndColor" format="color"/>
        <!-- 每个小条间距，dp，默认8 -->
        <attr name="gapWidth" format="dimension"/>
        <!-- 每个小条宽度，dp，默认10 -->
        <attr name="barWidth" format="dimension"/>
        <!-- 移动速度，默认400 -->
        <attr name="moveDuration" format="integer" />
        <!-- 角度，默认315，315是向右偏移45度 -->
        <attr name="degree" format="integer" />
    </declare-styleable>
```

## Author：DQ

有问题联系QQ：285275534, 285275534@qq.com