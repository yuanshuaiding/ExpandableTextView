package com.eric.expandabletextview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.eric.android.view.ExpandableTextView4List;

import java.util.ArrayList;
import java.util.List;

public class RecycleActivity extends AppCompatActivity {

    String txt = "从明天起做个幸福的人 喂马劈柴周游世界  从明天起关心粮食和蔬菜 我有一所房子  面朝大海春暖花开 从明天起和每一个亲人通信 告诉他们我的幸福 那幸福的闪电告诉我的 我将告诉每一个人 给每一条河每一座山取个温暖的名字 陌生人我也为你祝福 愿你有一个灿烂前程 愿你有情人终成眷属 愿你在尘世获得幸福 我只愿面朝大海春暖花开[[[[[[[[[[[[从明天起做个幸福的人 喂马劈柴周游世界  从明天起关心粮食和蔬菜 我有一所房子  面朝大海春暖花开 从明天起和每一个亲人通信 告诉他们我的幸福 那幸福的闪电告诉我的 我将告诉每一个人 给每一条河每一座山取个温暖的名字 陌生人我也为你祝福 愿你有一个灿烂前程 愿你有情人终成眷属 愿你在尘世获得幸福 我只愿面朝大海春暖花开";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);
        final RecyclerView recycle_view = findViewById(R.id.recycle_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycle_view.setLayoutManager(layoutManager);
        final MyAdapter adapter = new MyAdapter(initData());
        recycle_view.setAdapter(adapter);

    }

    private List<String> initData() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add(i + " : " + txt);
        }

        return datas;
    }

    private class MyAdapter extends RecyclerView.Adapter<VH> {

        private final List<String> datas;

        private MyAdapter(List<String> data) {
            this.datas = data;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(getLayoutInflater().inflate(R.layout.item_recycle, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.tv.setText(datas.get(position), position%10==0);
        }

        @Override
        public int getItemCount() {
            return datas != null ? datas.size() : 0;
        }

    }

    private static class VH extends RecyclerView.ViewHolder {
        ExpandableTextView4List tv;

        public VH(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
        }
    }
}
