package com.eric.expandabletextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.eric.android.view.ExpandableTextView;

public class MainActivity extends AppCompatActivity {

    String txt = "从明天起做个幸福的人 喂马劈柴周游世界  从明天起关心粮食和蔬菜 我有一所房子  面朝大海春暖花开 从明天起和每一个亲人通信 告诉他们我的幸福 那幸福的闪电告诉我的 我将告诉每一个人 给每一条河每一座山取个温暖的名字 陌生人我也为你祝福 愿你有一个灿烂前程 愿你有情人终成眷属 愿你在尘世获得幸福 我只愿面朝大海春暖花开";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExpandableTextView mExpandableTextView1 = findViewById(R.id.expand_tv1);
        mExpandableTextView1.setText(txt);
        ExpandableTextView mExpandableTextView2 = findViewById(R.id.expand_tv2);
        mExpandableTextView2.setText(txt);
        ExpandableTextView mExpandableTextView3 = findViewById(R.id.expand_tv3);
        mExpandableTextView3.setText(txt);
        ExpandableTextView mExpandableTextView4 = findViewById(R.id.expand_tv4);
        mExpandableTextView4.setText(txt);
    }
}
