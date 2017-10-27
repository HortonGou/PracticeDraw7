package com.hencoder.hencoderpracticedraw7.practice.practice02;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.hencoder.hencoderpracticedraw7.R;

public class Practice02HsvEvaluatorLayout extends RelativeLayout {
    Practice02HsvEvaluatorView view;
    Button animateBt;

    public Practice02HsvEvaluatorLayout(Context context) {
        super(context);
    }

    public Practice02HsvEvaluatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice02HsvEvaluatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        view = (Practice02HsvEvaluatorView) findViewById(R.id.objectAnimatorView);
        animateBt = (Button) findViewById(R.id.animateBt);

        animateBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofInt(view, "color", 0xffff0000, 0xff00ff00);
                animator.setEvaluator(new HsvEvaluator()); // 使用自定义的 HsvEvaluator
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(2000);
                animator.start();
            }
        });
    }

    private class HsvEvaluator implements TypeEvaluator<Integer> {
        float[] startHsv = new float[3];
        float[] endHsv = new float[3];
        float[] curHsv = new float[3];

        // 重写 evaluate() 方法，让颜色按照 HSV 来变化
        @Override
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            Color.colorToHSV(startValue, startHsv);
            Color.colorToHSV(endValue, endHsv);

            /* 前面的那段代码的操作 是确定顺序 你看了那个象限的图 你知道品红 是300° 红 是 0° 现在起始是红 到品红
            如果不做这段操作
            顺序就是 红 -》 黄 》绿》品红
            而不是你想要的结果 红 - 》 品红，即0 到 -60
            反过来 起始是品红（300） 减弱到红 （0）
            不做这段操作就是
            品红 》 绿 》黄》红
            因为我们想要的结果是：
            300 - 》360*/
            if (endHsv[0] - startHsv[0] > 180) {
                endHsv[0] -= 360;
            } else if (endHsv[0] - startHsv[0] < -180) {
                endHsv[0] += 360;
            }


            curHsv[0] = (endHsv[0] - startHsv[0]) * fraction + startHsv[0];
            curHsv[1] = (endHsv[1] - startHsv[1]) * fraction + startHsv[1];
            curHsv[2] = (endHsv[2] - startHsv[2]) * fraction + startHsv[2];

            if (curHsv[0] > 360) {
                curHsv[0] -= 360;
            } else if (curHsv[0] < 0) {
                curHsv[0] += 360;
            }

            // alpha需要额外设置 ！！
            int alpha = startValue >> 24 + (int) ((endValue >> 24 - startValue >> 24) * fraction);

            return Color.HSVToColor(alpha, curHsv);
        }
    }
}
