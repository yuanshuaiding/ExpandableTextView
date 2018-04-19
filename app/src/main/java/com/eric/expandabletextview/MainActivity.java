package com.eric.expandabletextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import com.eric.android.view.ExpandableTextView;

public class MainActivity extends AppCompatActivity {

    String txt = "<p>接下来要创建一个登录界面，新建 LoginActivity（在 Android Studio 中，建议创建空的活动），编辑 activity_login.xml：</p>\n" +
            "<pre><code>&lt;LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
            "    android:layout_width=\"match_parent\"\n" +
            "    android:layout_height=\"match_parent\"\n" +
            "    android:gravity=\"center_horizontal\"\n" +
            "    android:orientation=\"vertical\"&gt;\n" +
            "\n" +
            "    &lt;LinearLayout\n" +
            "        android:layout_width=\"match_parent\"\n" +
            "        android:layout_height=\"60dp\"\n" +
            "        android:orientation=\"horizontal\"&gt;\n" +
            "\n" +
            "        &lt;TextView\n" +
            "            android:layout_width=\"90dp\"\n" +
            "            android:layout_height=\"wrap_content\"\n" +
            "            android:layout_gravity=\"center_vertical\"\n" +
            "            android:text=\"账号：\"\n" +
            "            android:textSize=\"18sp\" /&gt;\n" +
            "\n" +
            "        &lt;EditText\n" +
            "            android:id=\"@+id/account\"\n" +
            "            android:layout_width=\"0dp\"\n" +
            "            android:layout_height=\"wrap_content\"\n" +
            "            android:layout_gravity=\"center_vertical\"\n" +
            "            android:layout_weight=\"1\" /&gt;\n" +
            "    &lt;/LinearLayout&gt;\n" +
            "\n" +
            "    &lt;LinearLayout\n" +
            "        android:layout_width=\"match_parent\"\n" +
            "        android:layout_height=\"60dp\"\n" +
            "        android:orientation=\"horizontal\"&gt;\n" +
            "\n" +
            "        &lt;TextView\n" +
            "            android:layout_width=\"90dp\"\n" +
            "            android:layout_height=\"wrap_content\"\n" +
            "            android:layout_gravity=\"center_vertical\"\n" +
            "            android:text=\"密码：\"\n" +
            "            android:textSize=\"18sp\" /&gt;\n" +
            "\n" +
            "        &lt;EditText\n" +
            "            android:id=\"@+id/password\"\n" +
            "            android:layout_width=\"0dp\"\n" +
            "            android:layout_height=\"wrap_content\"\n" +
            "            android:layout_gravity=\"center_vertical\"\n" +
            "            android:layout_weight=\"1\"\n" +
            "            android:inputType=\"textPassword\" /&gt;\n" +
            "    &lt;/LinearLayout&gt;\n" +
            "\n" +
            "    &lt;Button\n" +
            "        android:id=\"@+id/login\"\n" +
            "        android:layout_width=\"match_parent\"\n" +
            "        android:layout_height=\"60dp\"\n" +
            "        android:text=\"登录\" /&gt;\n" +
            "&lt;/LinearLayout&gt;\n" +
            "\n" +
            "</code></pre>\n" +
            "<p>我们使用 LinearLayout 编写了一个登陆布局，使用纵向排列，从上到下分别是账号、密码和登陆按钮。</p>\n" +
            "<p>接着修改 LoginActivity 中的代码：</p>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv_normal = findViewById(R.id.tv_normal);
        tv_normal.setText(Html.fromHtml(txt));
        ExpandableTextView mExpandableTextView1 = findViewById(R.id.expand_tv1);
        mExpandableTextView1.setText(Html.fromHtml(txt));
        ExpandableTextView mExpandableTextView2 = findViewById(R.id.expand_tv2);
        mExpandableTextView2.setText(Html.fromHtml(txt));
        ExpandableTextView mExpandableTextView3 = findViewById(R.id.expand_tv3);
        mExpandableTextView3.setText(Html.fromHtml(txt));
        ExpandableTextView mExpandableTextView4 = findViewById(R.id.expand_tv4);
        mExpandableTextView4.setText(Html.fromHtml(txt));
    }
}
