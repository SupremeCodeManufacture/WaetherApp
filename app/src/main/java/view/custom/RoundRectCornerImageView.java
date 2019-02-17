package view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundRectCornerImageView extends ImageView {

    private Path mPath;

    public RoundRectCornerImageView(Context context) {
        super(context);
        init();
    }

    public RoundRectCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundRectCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float radius = 18.0f;
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        mPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(mPath);
        super.onDraw(canvas);
    }
}