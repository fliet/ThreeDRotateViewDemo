package com.example.threedrotateview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

/**
 * 更好的思路
 *
 * @author wp
 * @date 2018/8/6.
 */

public class ThreeDRotateView extends View {
    private Paint paint = new Paint();
    private Camera camera = new Camera();
    private Bitmap bitmap;

    private float degreeCameraYR;
    private float degreeRotate;
    private float degreeCameraYB;

    private AnimatorSet animatorSet;


    public ThreeDRotateView(Context context) {
        super(context);
    }

    public ThreeDRotateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreeDRotateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.maps);

        ObjectAnimator cameraRightAnimator = ObjectAnimator.ofFloat(this, "degreeCameraYR", 0, 45);
        cameraRightAnimator.setDuration(1000);

        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(this, "degreeRotate", 0, 270);
        rotateAnimator.setDuration(1500);
        rotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator cameraBottomAnimator = ObjectAnimator.ofFloat(this, "degreeCameraYB", 0, 45);
        cameraBottomAnimator.setDuration(1000);

        animatorSet = new AnimatorSet();
        animatorSet.playSequentially(cameraRightAnimator, rotateAnimator, cameraBottomAnimator);
    }

    /*
        三维坐标系
            | z
            |
            |____________ y
           /
          /
         /x


         该效果分为两步实现
             步一
                 将一半的图片实现三维旋转的效果；裁剪掉另一半不需要三维旋转的图片

             步二
                 对图片进行旋转裁剪，裁剪掉需要三维旋转的那一半图片
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int startX = centerX - bitmapWidth / 2;
        int startY = centerY - bitmapHeight / 2;


        /*
            分步一
                三维旋转 + 裁剪掉不需要旋转的图片
                    旋转图片 + 三维旋转 + 恢复旋转，实现了360度的三维旋转效果
                    如果单单是旋转图片，那么效果只会是图片被旋转了
                    如果单单是三维旋转，那么效果只会是一个方向想被三维旋转了

                 旋转起到两个作用：
                     裁剪只需要裁剪固定位置的图片即可，因为旋转交由旋转去工作了
                     实现360度三维旋转
          */
        canvas.save();

        canvas.translate(centerX, centerY);    // f. 移回原来的位置
        canvas.rotate(-degreeRotate);
        canvas.clipRect(0, -centerY, centerX, centerY);   // e. 将旋转后的图片进行裁剪。由于图片是旋转的，所以只针对固定区域的位置裁剪即可实现旋转裁剪的效果

        camera.save();
        camera.rotateY(-degreeCameraYR);                 // d. 使用camera，将图片延Y轴方向旋转，degreeCameraYR动效
        camera.setLocation(0, 0, -12);
        camera.applyToCanvas(canvas);
        camera.restore();

        canvas.rotate(degreeRotate);             // c. 对图片进行旋转
        canvas.translate(-centerX, -centerY);   // b. 将图片中心移动至原点，也就是将图片移动至camera的正下方，防止出现图片畸形的效果

        canvas.drawBitmap(bitmap, startX, startY, paint);   // a. 绘制bitmap

        canvas.restore();

        /*-----------------------------------------------------------------------------------------------------------*/

        /*
            分步二

         */
        canvas.save();

        canvas.translate(centerX, centerY);

        canvas.rotate(-degreeRotate);

        canvas.clipRect(-centerX, -centerY, 0, centerY);

        camera.save();
        camera.rotateY(degreeCameraYB);
        camera.setLocation(0, 0, -12);
        camera.applyToCanvas(canvas);
        camera.restore();

        canvas.rotate(degreeRotate);
        canvas.translate(-centerX, -centerY);

        canvas.drawBitmap(bitmap, startX, startY, paint);

        canvas.restore();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        animatorSet.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        animatorSet.end();
    }

    public float getDegreeCameraYR() {
        return degreeCameraYR;
    }

    public void setDegreeCameraYR(float degreeCameraYR) {
        this.degreeCameraYR = degreeCameraYR;

        invalidate();
    }

    public float getDegreeRotate() {
        return degreeRotate;
    }

    public void setDegreeRotate(float degreeRotate) {
        this.degreeRotate = degreeRotate;

        invalidate();
    }

    public float getDegreeCameraYB() {
        return degreeCameraYB;
    }

    public void setDegreeCameraYB(float degreeCameraYB) {
        this.degreeCameraYB = degreeCameraYB;

        invalidate();
    }
}
