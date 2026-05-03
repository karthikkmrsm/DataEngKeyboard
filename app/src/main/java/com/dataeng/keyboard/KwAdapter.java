package com.dataeng.keyboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class KwAdapter extends RecyclerView.Adapter<KwAdapter.VH> {

    public interface OnKwClick { void onClick(String kw); }

    private final List<String> items;
    private final String mode;
    private final OnKwClick listener;

    public KwAdapter(List<String> items, String mode, OnKwClick listener) {
        this.items    = items;
        this.mode     = mode;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kw_chip, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        String kw  = items.get(pos);
        String type = KeywordData.getType(kw);
        Context ctx = h.tv.getContext();

        // Display text (truncate if long)
        String display = kw.length() > 17 ? kw.substring(0, 15) + "…" : kw;
        h.tv.setText(display);
        h.tv.setContentDescription(kw);

        // Background & text colour by type
        int bgRes, colorRes;
        switch (type) {
            case KeywordData.TYPE_SPARK:
                bgRes    = R.drawable.key_bg_spark;
                colorRes = R.color.spark_color;
                break;
            case KeywordData.TYPE_DB:
                bgRes    = R.drawable.key_bg_db;
                colorRes = R.color.db_color;
                break;
            default:
                bgRes    = R.drawable.key_bg_sql;
                colorRes = R.color.sql_color;
        }
        h.tv.setBackground(ctx.getDrawable(bgRes));
        h.tv.setTextColor(ctx.getColor(colorRes));

        h.tv.setOnClickListener(v -> listener.onClick(kw));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv;
        VH(View v) { super(v); tv = v.findViewById(R.id.kwText); }
    }
}
