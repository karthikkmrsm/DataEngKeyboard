package com.dataeng.keyboard;

import android.content.Context;
import android.util.TypedValue;
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
    private final int theme;
    private final int size;
    private final OnKwClick listener;

    public KwAdapter(List<String> items, String mode, int theme, int size, OnKwClick listener) {
        this.items    = items;
        this.mode     = mode;
        this.theme    = theme;
        this.size     = size;
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
        String kw   = items.get(pos);
        String type = KeywordData.getType(kw);
        Context ctx = h.tv.getContext();

        String display = kw.length() > 17 ? kw.substring(0, 15) + "…" : kw;
        h.tv.setText(display);
        h.tv.setContentDescription(kw);

        // Text size from theme size setting
        h.tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, ThemeManager.keyTextSp(size) - 3);

        // Height
        ViewGroup.LayoutParams lp = h.tv.getLayoutParams();
        lp.height = Math.round(ThemeManager.kwHeightDp(size)
                    * ctx.getResources().getDisplayMetrics().density);
        h.tv.setLayoutParams(lp);

        int color, fillColor;
        switch (type) {
            case KeywordData.TYPE_SPARK:
                color     = ThemeManager.spark(theme);
                fillColor = ThemeManager.tint(color, 0.13f);
                break;
            case KeywordData.TYPE_DB:
                color     = ThemeManager.db(theme);
                fillColor = ThemeManager.tint(color, 0.13f);
                break;
            default:
                color     = ThemeManager.sql(theme);
                fillColor = ThemeManager.tint(color, 0.13f);
        }
        h.tv.setTextColor(color);
        h.tv.setBackground(ThemeManager.roundRectStroke(fillColor, color, 7, 1, ctx));
        h.tv.setOnClickListener(v -> listener.onClick(kw));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv;
        VH(View v) { super(v); tv = v.findViewById(R.id.kwText); }
    }
}
