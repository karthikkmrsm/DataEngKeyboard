package com.dataeng.keyboard;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.inputmethodservice.InputMethodService;
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
import android.widget.ScrollView;
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
    static final String M_ABC      = "abc";
    static final String M_SQL      = "sql";
    static final String M_SPARK    = "spark";
    static final String M_DB       = "db";
    static final String M_SYM      = "sym";
    static final String M_TEMPLATE = "tmpl";
    static final String M_SETTINGS = "settings";

    private String mode;
    private int    subcat  = 0;
    private boolean shifted = false;
    private boolean capsLock = false;
    private StringBuilder buffer = new StringBuilder();

    // Theme / size (loaded fresh each time keyboard opens)
    private int theme;
    private int size;

    // Root view
    private View root;

    // Panels
    private LinearLayout alphaKb, symKb, settingsPanel;
    private RecyclerView kwGrid;
    private LinearLayout templateList;
    private ScrollView templateScroll;

    // Rows
    private LinearLayout modeTabs, acStrip, scBar;
    private View scScroll, acScrollView;

    // Alpha rows
    private LinearLayout numRow, alphaRow1, alphaRow2, alphaRow3, alphaBottom;
    // Sym rows
    private LinearLayout symRow1, symRow2, symRow3, symRow4, symBottom;

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public View onCreateInputView() {
        root = LayoutInflater.from(this).inflate(R.layout.keyboard_view, null);
        bindViews();
        buildAlpha();
        buildSym();
        buildSettings();
        return root;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        theme = ThemeManager.loadTheme(this);
        size  = ThemeManager.loadSize(this);
        mode  = ThemeManager.loadDefaultMode(this);
        buffer.setLength(0);
        applyThemeToRoot();
        switchMode(mode);
    }

    // ── Bind views ────────────────────────────────────────────────────────────
    private void bindViews() {
        modeTabs      = root.findViewById(R.id.modeTabs);
        acStrip       = root.findViewById(R.id.acStrip);
        acScrollView  = root.findViewById(R.id.acScrollView);
        scBar         = root.findViewById(R.id.scBar);
        scScroll      = root.findViewById(R.id.scScroll);
        kwGrid        = root.findViewById(R.id.kwGrid);
        alphaKb       = root.findViewById(R.id.alphaKb);
        symKb         = root.findViewById(R.id.symKb);
        settingsPanel = root.findViewById(R.id.settingsPanel);
        templateScroll= root.findViewById(R.id.templateScroll);
        templateList  = root.findViewById(R.id.templateList);

        numRow    = root.findViewById(R.id.numRow);
        alphaRow1 = root.findViewById(R.id.alphaRow1);
        alphaRow2 = root.findViewById(R.id.alphaRow2);
        alphaRow3 = root.findViewById(R.id.alphaRow3);
        alphaBottom = root.findViewById(R.id.alphaBottomRow);

        symRow1   = root.findViewById(R.id.symRow1);
        symRow2   = root.findViewById(R.id.symRow2);
        symRow3   = root.findViewById(R.id.symRow3);
        symRow4   = root.findViewById(R.id.symRow4);
        symBottom = root.findViewById(R.id.symBottomRow);
    }

    // ── Apply theme to entire keyboard background ─────────────────────────────
    private void applyThemeToRoot() {
        root.setBackgroundColor(ThemeManager.surface1(theme));
        int bg = ThemeManager.bg(theme);
        if (acScrollView != null) acScrollView.setBackgroundColor(ThemeManager.surface2(theme));
    }

    // ── Mode switching ────────────────────────────────────────────────────────
    private void switchMode(String m) {
        mode = m;
        buffer.setLength(0);

        // Hide all panels
        hide(kwGrid); hide(scScroll); hide(alphaKb);
        hide(symKb);  hide(settingsPanel); hide(templateScroll);

        buildModeTabs();
        updateAC();

        switch (m) {
            case M_SQL: case M_SPARK: case M_DB:
                show(scScroll); show(kwGrid);
                buildScBar(); buildKwGrid();
                break;
            case M_ABC:
                show(alphaKb);
                refreshAlphaTheme();
                break;
            case M_SYM:
                show(symKb);
                refreshSymTheme();
                break;
            case M_TEMPLATE:
                show(templateScroll);
                buildTemplates();
                break;
            case M_SETTINGS:
                show(settingsPanel);
                buildSettingsPanel();
                break;
        }
    }

    // ── Mode tabs ─────────────────────────────────────────────────────────────
    private void buildModeTabs() {
        modeTabs.removeAllViews();
        String[][] tabs = {
            {M_ABC,"ABC"}, {M_SQL,"SQL"}, {M_SPARK,"SPARK"},
            {M_DB,"DATA\nBRICKS"}, {M_SYM,"#$@"},
            {M_TEMPLATE,"TMPL"}, {M_SETTINGS,"⚙"}
        };
        for (String[] t : tabs) {
            String id = t[0]; String label = t[1];
            TextView tv = new TextView(this);
            tv.setText(label);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            tv.setPadding(dp(9), dp(3), dp(9), dp(3));
            tv.setGravity(Gravity.CENTER);

            int accentColor = modeColor(id);
            boolean active = id.equals(mode);
            if (active) {
                tv.setTextColor(ThemeManager.bg(theme));
                tv.setBackground(ThemeManager.roundRect(accentColor, 20, this));
            } else {
                tv.setTextColor(accentColor);
                tv.setBackground(ThemeManager.roundRectStroke(
                    ThemeManager.tint(accentColor, 0.12f), accentColor, 20, 1, this));
                tv.setAlpha(0.7f);
            }
            tv.setOnClickListener(v -> switchMode(id));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(4));
            tv.setLayoutParams(lp);
            modeTabs.addView(tv);
        }
    }

    private int modeColor(String m) {
        switch (m) {
            case M_SQL:      return ThemeManager.sql(theme);
            case M_SPARK:    return ThemeManager.spark(theme);
            case M_DB:       return ThemeManager.db(theme);
            case M_TEMPLATE: return ThemeManager.accent(theme);
            case M_SETTINGS: return ThemeManager.textSec(theme);
            default:         return ThemeManager.textSec(theme);
        }
    }

    // ── Autocomplete ──────────────────────────────────────────────────────────
    private void updateAC() {
        acStrip.removeAllViews();
        String buf = buffer.toString().trim();

        // Always show shortcut keys in the AC bar
        String[] shortcuts = {"( )", "[ ]", "{ }", ".", ",", ";", ":", "=>", "!=", ">=", "<="};
        for (String s : shortcuts) {
            acStrip.addView(makeAcShortcut(s));
        }

        if (buf.length() >= 1) {
            String poolMode = (M_SQL.equals(mode)||M_SPARK.equals(mode)||M_DB.equals(mode))
                ? mode : null;
            List<String> matches = KeywordData.search(buf, poolMode);
            for (String kw : matches) {
                acStrip.addView(makeAcChip(kw));
            }
        }
    }

    private TextView makeAcShortcut(String s) {
        TextView tv = new TextView(this);
        tv.setText(s);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv.setPadding(dp(10), dp(2), dp(10), dp(2));
        tv.setTextColor(ThemeManager.accent(theme));
        tv.setBackground(ThemeManager.roundRectStroke(
            ThemeManager.tint(ThemeManager.accent(theme), 0.12f),
            ThemeManager.accent(theme), 20, 1, this));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMarginEnd(dp(4));
        lp.topMargin = dp(3); lp.bottomMargin = dp(3);
        tv.setLayoutParams(lp);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(v -> {
            if (s.equals("( )")) { commitText("()"); moveCursorLeft(); }
            else if (s.equals("[ ]")) { commitText("[]"); moveCursorLeft(); }
            else if (s.equals("{ }")) { commitText("{}"); moveCursorLeft(); }
            else commitText(s.replace(" ",""));
        });
        return tv;
    }

    private TextView makeAcChip(String kw) {
        String type = KeywordData.getType(kw);
        int color;
        switch (type) {
            case KeywordData.TYPE_SPARK: color = ThemeManager.spark(theme); break;
            case KeywordData.TYPE_DB:    color = ThemeManager.db(theme);    break;
            default:                     color = ThemeManager.sql(theme);
        }
        TextView tv = new TextView(this);
        tv.setText(kw);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tv.setTypeface(Typeface.MONOSPACE);
        tv.setPadding(dp(10), dp(2), dp(10), dp(2));
        tv.setTextColor(color);
        tv.setBackground(ThemeManager.roundRectStroke(
            ThemeManager.tint(color, 0.12f), color, 20, 1, this));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMarginEnd(dp(4));
        lp.topMargin = dp(3); lp.bottomMargin = dp(3);
        tv.setLayoutParams(lp);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(v -> insertKeyword(kw));
        return tv;
    }

    // ── Sub-category bar ──────────────────────────────────────────────────────
    private void buildScBar() {
        scBar.removeAllViews();
        Map<String, String[]> pool = getPool();
        int idx = 0;
        for (String cat : pool.keySet()) {
            final int i = idx++;
            TextView tv = new TextView(this);
            tv.setText(cat.toUpperCase());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setPadding(dp(9), dp(2), dp(9), dp(2));
            tv.setLetterSpacing(0.07f);
            if (i == subcat) {
                tv.setTextColor(ThemeManager.bg(theme));
                tv.setBackground(ThemeManager.roundRect(modeColor(mode), 20, this));
            } else {
                tv.setTextColor(ThemeManager.textSec(theme));
                tv.setBackground(ThemeManager.roundRect(
                    ThemeManager.tint(ThemeManager.textSec(theme), 0.15f), 20, this));
            }
            tv.setOnClickListener(v -> { subcat = i; buildScBar(); buildKwGrid(); });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(5));
            tv.setLayoutParams(lp);
            scBar.addView(tv);
        }
    }

    // ── Keyword grid ──────────────────────────────────────────────────────────
    private void buildKwGrid() {
        Map<String, String[]> pool = getPool();
        String[] cats = pool.keySet().toArray(new String[0]);
        if (subcat >= cats.length) subcat = 0;
        String[] kws = pool.get(cats[subcat]);
        List<String> list = kws == null ? new ArrayList<>() : Arrays.asList(kws);

        // Set height based on size
        ViewGroup.LayoutParams lp = kwGrid.getLayoutParams();
        lp.height = dp(ThemeManager.kwHeightDp(size) * 5 + 10);
        kwGrid.setLayoutParams(lp);
        kwGrid.setBackgroundColor(ThemeManager.surface1(theme));
        kwGrid.setLayoutManager(new GridLayoutManager(this, 4));
        kwGrid.setAdapter(new KwAdapter(list, mode, theme, size, this::insertKeyword));
    }

    // ── ALPHA keyboard ────────────────────────────────────────────────────────
    private void buildAlpha() {
        // Number row
        numRow.removeAllViews();
        for (char c : "1234567890".toCharArray())
            numRow.addView(makeKey(String.valueOf(c), 1f, false, false));

        buildQwertyRows();
        buildAlphaBottomRow();
    }

    private void buildQwertyRows() {
        alphaRow1.removeAllViews();
        for (String k : new String[]{"Q","W","E","R","T","Y","U","I","O","P"})
            alphaRow1.addView(makeLetterKey(k));

        alphaRow2.removeAllViews();
        for (String k : new String[]{"A","S","D","F","G","H","J","K","L"})
            alphaRow2.addView(makeLetterKey(k));

        alphaRow3.removeAllViews();
        // Shift
        TextView shift = makeKey(capsLock ? "⇪" : shifted ? "⇧↑" : "⇧", 1.4f, true, false);
        shift.setId(R.id.shift_key_id);
        shift.setTextColor(shifted || capsLock ? ThemeManager.accent(theme) : ThemeManager.textSec(theme));
        shift.setOnClickListener(v -> {
            if (capsLock) { capsLock = false; shifted = false; }
            else if (shifted) { capsLock = true; }
            else { shifted = true; }
            refreshShiftState();
        });
        alphaRow3.addView(shift);
        for (String k : new String[]{"Z","X","C","V","B","N","M"})
            alphaRow3.addView(makeLetterKey(k));
        // Backspace
        TextView del = makeKey("⌫", 1.4f, true, false);
        del.setTextColor(0xFFF87171);
        del.setOnClickListener(v -> deleteChar());
        del.setOnLongClickListener(v -> { clearWord(); return true; });
        alphaRow3.addView(del);
    }

    private void buildAlphaBottomRow() {
        alphaBottom.removeAllViews();
        // #$@ sym
        TextView sym = makeKey("#$@", 1.3f, true, false);
        sym.setTextColor(ThemeManager.accent(theme));
        sym.setOnClickListener(v -> switchMode(M_SYM));
        alphaBottom.addView(sym);
        // KW shortcut
        TextView kw = makeKey("KW", 1.3f, true, false);
        kw.setTextColor(ThemeManager.sql(theme));
        kw.setOnClickListener(v -> switchMode(M_SQL));
        alphaBottom.addView(kw);
        // . shortcut
        TextView dot = makeKey(".", 0.8f, true, false);
        dot.setTextColor(ThemeManager.textSec(theme));
        dot.setOnClickListener(v -> commitText("."));
        alphaBottom.addView(dot);
        // Space
        TextView space = makeKey("SPACE", 3.5f, true, false);
        space.setTextColor(ThemeManager.textSec(theme));
        space.setOnClickListener(v -> { commitText(" "); buffer.setLength(0); updateAC(); });
        alphaBottom.addView(space);
        // ( ) shortcut
        TextView paren = makeKey("()", 0.9f, true, false);
        paren.setTextColor(ThemeManager.accent(theme));
        paren.setOnClickListener(v -> { commitText("()"); moveCursorLeft(); });
        alphaBottom.addView(paren);
        // Enter
        TextView enter = makeKey("↵", 1.3f, true, false);
        enter.setTextColor(0xFF4ADE80);
        enter.setOnClickListener(v -> commitText("\n"));
        alphaBottom.addView(enter);
    }

    private TextView makeLetterKey(String letter) {
        TextView tv = makeKey(letter, 1f, false, false);
        tv.setTag(letter);
        tv.setOnClickListener(v -> {
            String ch = (shifted || capsLock) ? letter.toUpperCase() : letter.toLowerCase();
            commitText(ch);
            if (shifted && !capsLock) { shifted = false; refreshShiftState(); }
        });
        return tv;
    }

    private void refreshShiftState() {
        View sk = root.findViewById(R.id.shift_key_id);
        if (sk instanceof TextView) {
            TextView t = (TextView) sk;
            t.setText(capsLock ? "⇪" : shifted ? "⇧" : "⇧");
            t.setTextColor(shifted || capsLock ? ThemeManager.accent(theme) : ThemeManager.textSec(theme));
        }
        // Update letters
        refreshAlphaLetters(alphaRow1);
        refreshAlphaLetters(alphaRow2);
        refreshAlphaLetters(alphaRow3);
    }

    private void refreshAlphaLetters(LinearLayout row) {
        for (int i = 0; i < row.getChildCount(); i++) {
            View v = row.getChildAt(i);
            if (v instanceof TextView && v.getTag() instanceof String) {
                String letter = (String) v.getTag();
                ((TextView) v).setText((shifted || capsLock) ?
                    letter.toUpperCase() : letter.toLowerCase());
            }
        }
    }

    private void refreshAlphaTheme() {
        applyThemeToRow(numRow);
        applyThemeToRow(alphaRow1);
        applyThemeToRow(alphaRow2);
        applyThemeToRow(alphaRow3);
        applyThemeToRow(alphaBottom);
        alphaKb.setBackgroundColor(ThemeManager.surface1(theme));
    }

    private void applyThemeToRow(LinearLayout row) {
        if (row == null) return;
        for (int i = 0; i < row.getChildCount(); i++) {
            View v = row.getChildAt(i);
            if (v instanceof TextView) {
                GradientDrawable bg = ThemeManager.roundRect(ThemeManager.keyBg(theme), 7, this);
                v.setBackground(bg);
            }
        }
    }

    // ── SYM keyboard ──────────────────────────────────────────────────────────
    private void buildSym() {
        String[][] rows = {
            {"(",")","{","}","[","]","<",">"},
            {"\"","'","`","\\","/","|","~","^"},
            {"!","@","#","$","%","&","*","-"},
            {"+","=","_",":",";",","  ,".","?"},
        };
        LinearLayout[] rv = {symRow1, symRow2, symRow3, symRow4};
        for (int r = 0; r < rows.length; r++) {
            rv[r].removeAllViews();
            for (String s : rows[r]) rv[r].addView(makeSymKey(s));
        }
        buildSymBottom();
    }

    private void buildSymBottom() {
        symBottom.removeAllViews();
        TextView abc = makeKey("← ABC", 1.5f, true, false);
        abc.setTextColor(ThemeManager.accent(theme));
        abc.setOnClickListener(v -> switchMode(M_ABC));
        symBottom.addView(abc);
        TextView kw = makeKey("KW", 1.2f, true, false);
        kw.setTextColor(ThemeManager.sql(theme));
        kw.setOnClickListener(v -> switchMode(M_SQL));
        symBottom.addView(kw);
        // () shortcut
        TextView paren = makeKey("()", 1f, true, false);
        paren.setTextColor(ThemeManager.accent(theme));
        paren.setOnClickListener(v -> { commitText("()"); moveCursorLeft(); });
        symBottom.addView(paren);
        // . shortcut
        TextView dot = makeKey(".", 0.8f, true, false);
        dot.setTextColor(ThemeManager.textSec(theme));
        dot.setOnClickListener(v -> commitText("."));
        symBottom.addView(dot);
        TextView sp = makeKey("SPACE", 2f, true, false);
        sp.setTextColor(ThemeManager.textSec(theme));
        sp.setOnClickListener(v -> commitText(" "));
        symBottom.addView(sp);
        TextView del = makeKey("⌫", 1.2f, true, false);
        del.setTextColor(0xFFF87171);
        del.setOnClickListener(v -> deleteChar());
        del.setOnLongClickListener(v -> { clearWord(); return true; });
        symBottom.addView(del);
    }

    private TextView makeSymKey(String s) {
        TextView tv = makeKey(s, 1f, false, false);
        tv.setOnClickListener(v -> commitText(s));
        return tv;
    }

    private void refreshSymTheme() {
        applyThemeToRow(symRow1); applyThemeToRow(symRow2);
        applyThemeToRow(symRow3); applyThemeToRow(symRow4);
        applyThemeToRow(symBottom);
        symKb.setBackgroundColor(ThemeManager.surface1(theme));
    }

    // ── TEMPLATE panel ────────────────────────────────────────────────────────
    private void buildTemplates() {
        templateList.removeAllViews();
        templateScroll.setBackgroundColor(ThemeManager.surface1(theme));
        templateList.setBackgroundColor(ThemeManager.surface1(theme));

        for (String name : TemplateData.getNames()) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(dp(8), dp(5), dp(8), dp(5));
            row.setBackground(ThemeManager.roundRectStroke(
                ThemeManager.keyBg(theme), ThemeManager.border(theme), 8, 1, this));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = dp(5);
            row.setLayoutParams(lp);

            TextView title = new TextView(this);
            title.setText(name);
            title.setTextColor(ThemeManager.accent(theme));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setPadding(0, 0, 0, dp(3));
            row.addView(title);

            String preview = TemplateData.getTemplate(name);
            String shortPrev = preview.length() > 60 ? preview.substring(0, 60) + "…" : preview;
            TextView prev = new TextView(this);
            prev.setText(shortPrev);
            prev.setTextColor(ThemeManager.textSec(theme));
            prev.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
            prev.setTypeface(Typeface.MONOSPACE);
            row.addView(prev);

            row.setOnClickListener(v -> insertTemplate(TemplateData.getTemplate(name)));
            templateList.addView(row);
        }

        // Back button
        TextView back = makeKey("← Back", 1f, true, false);
        back.setTextColor(ThemeManager.accent(theme));
        back.setOnClickListener(v -> switchMode(ThemeManager.loadDefaultMode(this)));
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(40));
        blp.topMargin = dp(6);
        back.setLayoutParams(blp);
        templateList.addView(back);
    }

    // ── SETTINGS panel ────────────────────────────────────────────────────────
    private void buildSettings() {}

    private void buildSettingsPanel() {
        settingsPanel.removeAllViews();
        settingsPanel.setBackgroundColor(ThemeManager.surface1(theme));
        settingsPanel.setOrientation(LinearLayout.VERTICAL);
        settingsPanel.setPadding(dp(10), dp(6), dp(10), dp(6));

        // ── Theme section
        addSettingHeader("🎨  Keyboard Theme");
        LinearLayout themeRow = makeHScroll();
        for (int i = 0; i < ThemeManager.THEME_NAMES.length - 1; i++) {
            final int idx = i;
            TextView tv = new TextView(this);
            tv.setText(ThemeManager.THEME_NAMES[i]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setPadding(dp(10), dp(6), dp(10), dp(6));
            boolean selected = (theme == i);
            if (selected) {
                tv.setTextColor(ThemeManager.bg(i));
                tv.setBackground(ThemeManager.roundRect(ThemeManager.accent(i), 20, this));
            } else {
                tv.setTextColor(ThemeManager.accent(i));
                tv.setBackground(ThemeManager.roundRectStroke(
                    ThemeManager.tint(ThemeManager.accent(i), 0.12f),
                    ThemeManager.accent(i), 20, 1, this));
            }
            tv.setOnClickListener(v -> {
                ThemeManager.saveTheme(this, idx);
                theme = idx;
                applyThemeToRoot();
                switchMode(mode);
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(6));
            tv.setLayoutParams(lp);
            themeRow.addView(tv);
        }
        settingsPanel.addView(wrapHScroll(themeRow));

        // ── Size section
        addSettingHeader("📐  Key Size");
        LinearLayout sizeRow = makeHScroll();
        String[] sizeLabels = {"Small 🔡", "Normal ⌨️", "Large 🔠"};
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            TextView tv = new TextView(this);
            tv.setText(sizeLabels[i]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setPadding(dp(12), dp(6), dp(12), dp(6));
            boolean selected = (size == i);
            if (selected) {
                tv.setTextColor(ThemeManager.bg(theme));
                tv.setBackground(ThemeManager.roundRect(ThemeManager.accent(theme), 20, this));
            } else {
                tv.setTextColor(ThemeManager.textSec(theme));
                tv.setBackground(ThemeManager.roundRectStroke(
                    ThemeManager.tint(ThemeManager.textSec(theme), 0.12f),
                    ThemeManager.textSec(theme), 20, 1, this));
            }
            tv.setOnClickListener(v -> {
                ThemeManager.saveSize(this, idx);
                size = idx;
                buildAlpha(); buildSym();
                switchMode(mode);
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(8));
            tv.setLayoutParams(lp);
            sizeRow.addView(tv);
        }
        settingsPanel.addView(wrapHScroll(sizeRow));

        // ── Default mode
        addSettingHeader("🏠  Default Landing Keyboard");
        LinearLayout modeRow = makeHScroll();
        String[][] modes = {{M_ABC,"ABC ⌨️"},{M_SQL,"SQL"},{M_SPARK,"PySpark"},{M_DB,"Databricks"}};
        String curDef = ThemeManager.loadDefaultMode(this);
        for (String[] md : modes) {
            final String mId = md[0];
            TextView tv = new TextView(this);
            tv.setText(md[1]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setPadding(dp(12), dp(6), dp(12), dp(6));
            boolean selected = mId.equals(curDef);
            int mColor = modeColor(mId);
            if (selected) {
                tv.setTextColor(ThemeManager.bg(theme));
                tv.setBackground(ThemeManager.roundRect(mColor, 20, this));
            } else {
                tv.setTextColor(mColor);
                tv.setBackground(ThemeManager.roundRectStroke(
                    ThemeManager.tint(mColor, 0.12f), mColor, 20, 1, this));
            }
            tv.setOnClickListener(v -> {
                ThemeManager.saveDefaultMode(this, mId);
                buildSettingsPanel(); // refresh
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(8));
            tv.setLayoutParams(lp);
            modeRow.addView(tv);
        }
        settingsPanel.addView(wrapHScroll(modeRow));

        // ── Back button
        TextView back = new TextView(this);
        back.setText("← Done");
        back.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        back.setTypeface(Typeface.DEFAULT_BOLD);
        back.setTextColor(ThemeManager.bg(theme));
        back.setGravity(Gravity.CENTER);
        back.setBackground(ThemeManager.roundRect(ThemeManager.accent(theme), 20, this));
        back.setPadding(dp(20), dp(8), dp(20), dp(8));
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(40));
        blp.topMargin = dp(10);
        back.setLayoutParams(blp);
        back.setOnClickListener(v -> switchMode(ThemeManager.loadDefaultMode(this)));
        settingsPanel.addView(back);
    }

    private void addSettingHeader(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(ThemeManager.textSec(theme));
        tv.setPadding(0, dp(8), 0, dp(4));
        settingsPanel.addView(tv);
    }

    private LinearLayout makeHScroll() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(3), 0, dp(3));
        return row;
    }

    private HorizontalScrollView wrapHScroll(LinearLayout inner) {
        HorizontalScrollView hsv = new HorizontalScrollView(this);
        hsv.setHorizontalScrollBarEnabled(false);
        hsv.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        hsv.addView(inner);
        return hsv;
    }

    // ── Key factory ───────────────────────────────────────────────────────────
    private TextView makeKey(String label, float weight, boolean isFn, boolean isNum) {
        TextView tv = new TextView(this);
        tv.setText(label);
        int sp = isNum ? ThemeManager.keyTextSp(size) - 1
                       : isFn ? ThemeManager.keyTextSp(size) - 2
                               : ThemeManager.keyTextSp(size);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        tv.setTypeface(isNum ? Typeface.MONOSPACE : isFn ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT_BOLD);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(isFn ? ThemeManager.textSec(theme) : ThemeManager.textPrimary(theme));
        tv.setBackground(ThemeManager.roundRect(
            isNum ? ThemeManager.surface2(theme) : ThemeManager.keyBg(theme), 7, this));

        int h = isNum ? ThemeManager.numHeightDp(size) : ThemeManager.keyHeightDp(size);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(h), weight);
        lp.setMargins(dp(1), dp(2), dp(1), dp(2));
        tv.setLayoutParams(lp);
        tv.setMinimumWidth(dp(28));
        return tv;
    }

    // ── Text helpers ──────────────────────────────────────────────────────────
    private void insertKeyword(String kw) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        CharSequence before = ic.getTextBeforeCursor(1, 0);
        String prefix = (before != null && before.length() > 0
            && !Character.isWhitespace(before.charAt(0))) ? " " : "";
        ic.commitText(prefix + kw + " ", 1);
        buffer.setLength(0);
        updateAC();
    }

    private void insertTemplate(String tmpl) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.commitText(tmpl, 1);
        switchMode(M_ABC);
    }

    private void commitText(String text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.commitText(text, 1);
        if (text.equals(" ") || text.equals("\n")) buffer.setLength(0);
        else buffer.append(text);
        updateAC();
    }

    private void moveCursorLeft() {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.commitText("", 1);
        // Send left arrow key event
        long now = android.os.SystemClock.uptimeMillis();
        ic.sendKeyEvent(new android.view.KeyEvent(now, now,
            android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_DPAD_LEFT, 0));
        ic.sendKeyEvent(new android.view.KeyEvent(now, now,
            android.view.KeyEvent.ACTION_UP, android.view.KeyEvent.KEYCODE_DPAD_LEFT, 0));
    }

    private void deleteChar() {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.deleteSurroundingText(1, 0);
        if (buffer.length() > 0) buffer.deleteCharAt(buffer.length() - 1);
        updateAC();
    }

    private void clearWord() {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        CharSequence before = ic.getTextBeforeCursor(50, 0);
        if (before == null) return;
        int len = before.length();
        while (len > 0 && !Character.isWhitespace(before.charAt(len - 1))) len--;
        int del = before.length() - len;
        if (del > 0) ic.deleteSurroundingText(del, 0);
        buffer.setLength(0);
        updateAC();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Map<String, String[]> getPool() {
        switch (mode) {
            case M_SPARK: return KeywordData.SPARK;
            case M_DB:    return KeywordData.DB;
            default:      return KeywordData.SQL;
        }
    }

    private void show(View v) { if (v != null) v.setVisibility(View.VISIBLE); }
    private void hide(View v) { if (v != null) v.setVisibility(View.GONE); }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

}
