package com.disu.urlkeeper.customization;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 07/08/2022 | 10119239 | DEA INESIA SRI UTAMI | IF6
 */

public class CustomLinearLayoutManager extends androidx.recyclerview.widget.LinearLayoutManager {

    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CustomLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
