package com.dataeng.keyboard;

import android.content.Context;
import android.graphics.Typeface;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataEngIME extends InputMethodService {

    // Modes
    private static final String M_SQL   = "sql";
    private static final String M_SPARK = "spark";
    private static final String M_DB    = "db";
    private static final String M_ABC   = "abc";
    private static final String M_SYM   = "sym";

    private String mode     = M_SQL;
    private int    subcat   = 0;
    private boolean shifted = false;

    // Buffer for autocomplete matching
    private StringBuilder typingBuffer = new StringBuilder();

    // Views
    private View      kbView;
    private LinearLayout modeTabs, acStrip, scBar;
    private View      scScroll;
    private RecyclerView kwGrid;
    private LinearLayout alphaKb, symKb;
    private LinearLayout numRow, alphaRow1, alphaRow2, alphaRow3, alphaBottomRow;
    private LinearLayout symRow1, symRow2, symRow3, symRow4, symBottomRow;

    @Override
    public View onCreateInputView() {
        kbView = LayoutInflater.from(this).inflate(R.layout.keyboard_view, null);

        modeTabs       = kbView.findViewById(R.id.modeTabs);
        acStrip        = kbView.findViewById(R.id.acStrip);
        scBar          = kbView.findViewById(R.id.scBar);
        scScroll       = kbView.findViewById(R.id.scScroll);
        kwGrid         = kbView.findViewById(R.id.kwGrid);
        alphaKb        = kbView.findViewById(R.id.alphaKb);
        symKb          = kbView.findViewById(R.id.symKb);
        numRow         = kbView.findViewById(R.id.numRow);
        alphaRow1      = kbView.findViewById(R.id.alphaRow1);
        alphaRow2      = kbView.findViewById(R.id.alphaRow2);
        alphaRow3      = kbView.findViewById(R.id.alphaRow3);
        alphaBottomRow = kbView.findViewById(R.id.alphaBottomRow);
        symRow1        = kbView.findViewById(R.id.symRow1);
        symRow2        = kbView.findViewById(R.id.symRow2);
        symRow3        = kbView.findViewById(R.id.symRow3);
        symRow4        = kbView.findViewById(R.id.symRow4);
        symBottomRow   = kbView.findViewById(R.id.symBottomRow);

        buildModeTabs();
        buildAlpha();
        buildSym();
        switchMode(M_SQL);
        return kbView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        typingBuffer.setLength(0);
        updateAC();
    }

    // ─────────────────────────────────────────────────────────
    // MODE TABS
    // ─────────────────────────────────────────────────────────
    private void buildModeTabs() {
        modeTabs.removeAllViews();
        String[][] tabs = {
            {M_SQL,   "SQL",        "#2DD4BF"},
            {M_SPARK, "PYSPARK",    "#A78BFA"},
            {M_DB,    "DATABRICKS", "#FB923C"},
            {M_ABC,   "ABC",        "#7A8499"},
            {M_SYM,   "#$@",        "#4F9CF9"},
        };
        for (String[] t : tabs) {
            TextView tv = makeTab(t[0], t[1], t[2]);
            modeTabs.addView(tv);
        }
    }

    private TextView makeTab(String modeId, String label, String hex) {
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv.setPadding(dp(10), dp(4), dp(10), dp(4));
        tv.setTextColor(parseColor(hex));
        tv.setBackground(buildRoundedBg(parseColor(hex) & 0x33FFFFFF | (parseColor(hex) & 0x00FFFFFF), 20));
        tv.setOnClickListener(v -> switchMode(modeId));
        tv.setTag(modeId);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(dp(4));
        tv.setLayoutParams(lp);
        return tv;
    }

    private void highlightTab() {
        for (int i = 0; i < modeTabs.getChildCount(); i++) {
            View child = modeTabs.getChildAt(i);
            if (child instanceof TextView) {
                String tag = (String) child.getTag();
                child.setAlpha(tag.equals(mode) ? 1f : 0.38f);
                child.setScaleX(tag.equals(mode) ? 1.05f : 1f);
                child.setScaleY(tag.equals(mode) ? 1.05f : 1f);
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // SWITCH MODE
    // ─────────────────────────────────────────────────────────
    private void switchMode(String m) {
        mode = m;
        subcat = 0;
        highlightTab();
        typingBuffer.setLength(0);
        updateAC();

        // Hide all panels
        kwGrid.setVisibility(View.GONE);
        scScroll.setVisibility(View.GONE);
        alphaKb.setVisibility(View.GONE);
        symKb.setVisibility(View.GONE);

        switch (m) {
            case M_SQL:
            case M_SPARK:
            case M_DB:
                scScroll.setVisibility(View.VISIBLE);
                kwGrid.setVisibility(View.VISIBLE);
                buildScBar();
                buildKwGrid();
                break;
            case M_ABC:
                alphaKb.setVisibility(View.VISIBLE);
                break;
            case M_SYM:
                symKb.setVisibility(View.VISIBLE);
                break;
        }
    }

    // ─────────────────────────────────────────────────────────
    // SUB-CATEGORY BAR
    // ─────────────────────────────────────────────────────────
    private void buildScBar() {
        scBar.removeAllViews();
        Map<String, String[]> pool = getPool();
        int idx = 0;
        for (String cat : pool.keySet()) {
            final int i = idx;
            TextView tv = new TextView(this);
            tv.setText(cat.toUpperCase());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setPadding(dp(9), dp(2), dp(9), dp(2));
            tv.setLetterSpacing(0.08f);
            if (i == subcat) {
                tv.setTextColor(getColor(R.color.bg));
                tv.setBackground(buildRoundedBg(modeAccentColor(), 20));
            } else {
                tv.setTextColor(getColor(R.color.text_secondary));
                tv.setBackground(buildRoundedBg(0x22FFFFFF, 20));
            }
            tv.setOnClickListener(v -> { subcat = i; buildScBar(); buildKwGrid(); });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(4));
            tv.setLayoutParams(lp);
            scBar.addView(tv);
            idx++;
        }
    }

    // ─────────────────────────────────────────────────────────
    // KEYWORD GRID
    // ─────────────────────────────────────────────────────────
    private void buildKwGrid() {
        Map<String, String[]> pool = getPool();
        String[] cats = pool.keySet().toArray(new String[0]);
        if (subcat >= cats.length) subcat = 0;
        String[] kws = pool.get(cats[subcat]);
        List<String> list = kws == null ? new ArrayList<>() : Arrays.asList(kws);

        kwGrid.setLayoutManager(new GridLayoutManager(this, 4));
        kwGrid.setAdapter(new KwAdapter(list, mode, this::insertKeyword));
    }

    // ─────────────────────────────────────────────────────────
    // AUTOCOMPLETE STRIP
    // ─────────────────────────────────────────────────────────
    private void updateAC() {
        acStrip.removeAllViews();
        String buf = typingBuffer.toString().trim();
        if (buf.isEmpty()) {
            TextView hint = new TextView(this);
            hint.setText("type to search keywords…");
            hint.setTextColor(getColor(R.color.text_hint));
            hint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            hint.setTypeface(Typeface.MONOSPACE);
            acStrip.addView(hint);
            return;
        }
        List<String> results = KeywordData.search(buf, isKwMode() ? mode : null);
        for (String kw : results) {
            acStrip.addView(makeAcChip(kw));
        }
    }

    private TextView makeAcChip(String kw) {
        TextView tv = new TextView(this);
        tv.setText(kw);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tv.setTypeface(Typeface.MONOSPACE);
        tv.setPadding(dp(10), dp(3), dp(10), dp(3));
        String type = KeywordData.getType(kw);
        int color;
        switch (type) {
            case KeywordData.TYPE_SPARK: color = getColor(R.color.spark_color); break;
            case KeywordData.TYPE_DB:    color = getColor(R.color.db_color);    break;
            default:                     color = getColor(R.color.sql_color);
        }
        tv.setTextColor(color);
        tv.setBackground(buildRoundedBg(color & 0x22FFFFFF, 20));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(dp(4));
        tv.setLayoutParams(lp);
        tv.setOnClickListener(v -> insertKeyword(kw));
        return tv;
    }

    // ─────────────────────────────────────────────────────────
    // ALPHA KEYBOARD
    // ─────────────────────────────────────────────────────────
    private void buildAlpha() {
        // Number row
        numRow.removeAllViews();
        for (char c : "1234567890".toCharArray()) {
            numRow.addView(makeAlphaKey(String.valueOf(c), 1f, false, false, true));
        }

        // QWERTY rows
        String[] r1 = {"Q","W","E","R","T","Y","U","I","O","P"};
        String[] r2 = {"A","S","D","F","G","H","J","K","L"};
        String[] r3 = {"Z","X","C","V","B","N","M"};

        buildAlphaRow(alphaRow1, r1, false);
        buildAlphaRow(alphaRow2, r2, false);

        alphaRow3.removeAllViews();
        // Shift key
        TextView shiftKey = makeAlphaKey("⇧", 1.5f, true, false, false);
        shiftKey.setId(R.id.shift_key_id);
        shiftKey.setOnClickListener(v -> toggleShift());
        alphaRow3.addView(shiftKey);
        for (String c : r3) {
            alphaRow3.addView(makeAlphaKey(c, 1f, false, false, false));
        }
        // Backspace
        TextView del = makeAlphaKey("⌫", 1.5f, true, false, false);
        del.setTextColor(getColor(R.color.red));
        del.setOnClickListener(v -> deleteChar());
        del.setOnLongClickListener(v -> { clearWord(); return true; });
        alphaRow3.addView(del);

        // Bottom row
        alphaBottomRow.removeAllViews();
        TextView symBtn = makeAlphaKey("#$@", 1.5f, true, false, false);
        symBtn.setTextColor(getColor(R.color.accent));
        symBtn.setOnClickListener(v -> switchMode(M_SYM));
        alphaBottomRow.addView(symBtn);

        TextView kwBtn = makeAlphaKey("KW", 1.5f, true, false, false);
        kwBtn.setTextColor(getColor(R.color.sql_color));
        kwBtn.setOnClickListener(v -> switchMode(M_SQL));
        alphaBottomRow.addView(kwBtn);

        TextView space = makeAlphaKey("SPACE", 4f, true, false, false);
        space.setTextColor(getColor(R.color.text_hint));
        space.setOnClickListener(v -> commitText(" "));
        alphaBottomRow.addView(space);

        TextView dot = makeAlphaKey(".", 1f, true, false, false);
        dot.setOnClickListener(v -> commitText("."));
        alphaBottomRow.addView(dot);

        TextView enter = makeAlphaKey("↵", 1.5f, true, false, false);
        enter.setTextColor(getColor(R.color.green));
        enter.setOnClickListener(v -> commitText("\n"));
        alphaBottomRow.addView(enter);
    }

    private void buildAlphaRow(LinearLayout row, String[] keys, boolean isFn) {
        row.removeAllViews();
        for (String k : keys) {
            row.addView(makeAlphaKey(k, 1f, isFn, false, false));
        }
    }

    private TextView makeAlphaKey(String label, float weight, boolean isFn, boolean isAccent, boolean isNum) {
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, isFn || isNum ? 12 : 16);
        tv.setTypeface(isFn || isNum ? Typeface.MONOSPACE : Typeface.DEFAULT_BOLD);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(getColor(isAccent ? R.color.accent : isFn ? R.color.text_secondary : R.color.text_primary));
        tv.setBackground(getDrawable(isNum ? R.drawable.key_bg_default : R.drawable.key_bg_default));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, weight);
        lp.setMargins(dp(2), dp(2), dp(2), dp(2));
        tv.setLayoutParams(lp);
        if (!label.equals("⇧") && !label.equals("⌫") && !label.equals("↵")
                && !label.equals("SPACE") && !label.equals("#$@") && !label.equals("KW") && !label.equals(".")) {
            tv.setOnClickListener(v -> {
                String ch = shifted ? label.toUpperCase() : label.toLowerCase();
                commitText(ch);
                if (shifted) { shifted = false; refreshShiftKey(); }
            });
        }
        return tv;
    }

    private void toggleShift() {
        shifted = !shifted;
        refreshShiftKey();
        // Update all letter keys in rows 1-3
        updateAlphaCase(alphaRow1);
        updateAlphaCase(alphaRow2);
        updateAlphaCase(alphaRow3);
    }

    private void refreshShiftKey() {
        View sk = kbView.findViewById(R.id.shift_key_id);
        if (sk instanceof TextView) {
            ((TextView)sk).setTextColor(getColor(shifted ? R.color.accent : R.color.text_secondary));
        }
    }

    private void updateAlphaCase(LinearLayout row) {
        for (int i = 0; i < row.getChildCount(); i++) {
            View v = row.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                String t = tv.getText().toString();
                if (t.length() == 1 && Character.isLetter(t.charAt(0))) {
                    tv.setText(shifted ? t.toUpperCase() : t.toLowerCase());
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // SYMBOL KEYBOARD
    // ─────────────────────────────────────────────────────────
    private void buildSym() {
        String[][] rows = {
            {"(", ")", "{", "}", "[", "]", "<", ">"},
            {"\"", "'", "`", "\\", "/", "|", "~", "^"},
            {"!", "@", "#", "$", "%", "&", "*", "-"},
            {"+", "=", "_", ":", ";", ",", ".", "?"},
        };
        LinearLayout[] rowViews = {symRow1, symRow2, symRow3, symRow4};
        for (int r = 0; r < rows.length; r++) {
            rowViews[r].removeAllViews();
            for (String s : rows[r]) {
                rowViews[r].addView(makeSymKey(s, s, false));
            }
        }
        // Bottom row
        symBottomRow.removeAllViews();
        TextView abcBtn = makeSymKey("← ABC", null, true);
        abcBtn.setTextColor(getColor(R.color.accent));
        abcBtn.setOnClickListener(v -> switchMode(M_ABC));
        symBottomRow.addView(abcBtn);

        TextView kwBtn = makeSymKey("SQL KW", null, true);
        kwBtn.setTextColor(getColor(R.color.sql_color));
        kwBtn.setOnClickListener(v -> switchMode(M_SQL));
        symBottomRow.addView(kwBtn);

        TextView sp = makeSymKey("SPACE", null, true);
        sp.setTextColor(getColor(R.color.text_hint));
        sp.setOnClickListener(v -> commitText(" "));
        symBottomRow.addView(sp);

        TextView del = makeSymKey("⌫", null, true);
        del.setTextColor(getColor(R.color.red));
        del.setOnClickListener(v -> deleteChar());
        del.setOnLongClickListener(v -> { clearWord(); return true; });
        symBottomRow.addView(del);
    }

    private TextView makeSymKey(String label, String charToType, boolean isFn) {
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, isFn ? 11 : 15);
        tv.setTypeface(Typeface.MONOSPACE);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(getColor(R.color.text_secondary));
        tv.setBackground(getDrawable(isFn ? R.drawable.key_bg_default : R.drawable.key_bg_default));
        float weight = isFn ? 1.5f : 1f;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, weight);
        lp.setMargins(dp(2), dp(2), dp(2), dp(2));
        tv.setLayoutParams(lp);
        if (charToType != null) {
            tv.setOnClickListener(v -> commitText(charToType));
        }
        return tv;
    }

    // ─────────────────────────────────────────────────────────
    // TEXT INPUT HELPERS
    // ─────────────────────────────────────────────────────────
    private void insertKeyword(String kw) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        // Check if we need a leading space
        CharSequence before = ic.getTextBeforeCursor(1, 0);
        if (before != null && before.length() > 0 && !Character.isWhitespace(before.charAt(0))) {
            ic.commitText(" " + kw + " ", 1);
        } else {
            ic.commitText(kw + " ", 1);
        }
        typingBuffer.setLength(0);
        updateAC();
    }

    private void commitText(String text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.commitText(text, 1);
        if (text.equals(" ") || text.equals("\n")) {
            typingBuffer.setLength(0);
        } else {
            typingBuffer.append(text);
        }
        updateAC();
    }

    private void deleteChar() {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.deleteSurroundingText(1, 0);
        if (typingBuffer.length() > 0) {
            typingBuffer.deleteCharAt(typingBuffer.length() - 1);
        }
        updateAC();
    }

    private void clearWord() {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        CharSequence sel = ic.getSelectedText(0);
        if (!TextUtils.isEmpty(sel)) { ic.commitText("", 1); return; }
        CharSequence before = ic.getTextBeforeCursor(50, 0);
        if (before == null) return;
        int len = before.length();
        while (len > 0 && !Character.isWhitespace(before.charAt(len - 1))) len--;
        int del = before.length() - len;
        if (del > 0) ic.deleteSurroundingText(del, 0);
        typingBuffer.setLength(0);
        updateAC();
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────
    private Map<String, String[]> getPool() {
        switch (mode) {
            case M_SPARK: return KeywordData.SPARK;
            case M_DB:    return KeywordData.DB;
            default:      return KeywordData.SQL;
        }
    }

    private boolean isKwMode() {
        return M_SQL.equals(mode) || M_SPARK.equals(mode) || M_DB.equals(mode);
    }

    private int modeAccentColor() {
        switch (mode) {
            case M_SPARK: return getColor(R.color.spark_color);
            case M_DB:    return getColor(R.color.db_color);
            default:      return getColor(R.color.sql_color);
        }
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private static int parseColor(String hex) {
        return android.graphics.Color.parseColor(hex);
    }

    private android.graphics.drawable.GradientDrawable buildRoundedBg(int color, int radiusDp) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(dp(radiusDp));
        return gd;
    }
}
