package com.example.administrator.studyview.CTickView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.administrator.studyview.R;
import com.example.lib_view.tick.CTickView;

/**
 * @author chenjiawang
 * @package com.example.administrator.studyview.CTickView
 * @fileName CTickViewTest
 * @date on 2018/6/14
 * @describe TODO
 * @email chen-jw@crystal-optech.com
 */

public class CTickViewTest extends LinearLayout implements View.OnClickListener{
    private CTickView McTickView;
    public CTickViewTest(Context context) {
        super(context);
        init(context);
    }

    public CTickViewTest(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CTickViewTest(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        View view =  LayoutInflater.from(context).inflate(R.layout.layout_ctick,this,true);
        McTickView =(CTickView)view.findViewById(R.id.ctv_tick);
        view.findViewById(R.id.btn_ctv_start).setOnClickListener(this);
        view.findViewById(R.id.btn_ctv_stop).setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ctv_start:
                McTickView.start();
                break;
            case R.id.btn_ctv_stop:
                McTickView.stop();
                break;
        }
    }
}
