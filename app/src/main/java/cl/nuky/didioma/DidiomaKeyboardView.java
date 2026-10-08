package cl.nuky.didioma;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import java.util.*;

public class DidiomaKeyboardView extends View {
    public interface Listener {
        void onCommit(String value);
        void onBackspace();
        void onEnter();
        void onNextKeyboard();
    }

    private Listener listener;
    private Typeface didioma;
    private final Paint keyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint utilityPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pronunciationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Key> keys = new ArrayList<>();

    private boolean symbols = false;
    private boolean numbers = false;
    private String pressedValue = null;

    private static final String[][] LETTERS = {
            {"A","B","D","E","F","G","H"},
            {"I","J","K","L","M","N","Ñ"},
            {"O","P","R","S","T","U","W"},
            {"X","Y","Z","É","Ú","Ü","Ş"}
    };

    private static final String[][] SYMBOLS = {
            {"~","`","^","*","±","°","|"},
            {"¡","¥","||"," "," "," "," "}
    };

    private static final String[][] NUMBERS = {
            {"1","2","3","4","5","6","7","8","9","0"},
            {".",",","-","/",":",";","(",")","@","#"}
    };

    private static final Map<String,String> PRONUNCIATION = new HashMap<>();
    static {
        PRONUNCIATION.put("A","a");
        PRONUNCIATION.put("B","b");
        PRONUNCIATION.put("D","d");
        PRONUNCIATION.put("E","e");
        PRONUNCIATION.put("F","f");
        PRONUNCIATION.put("G","g");
        PRONUNCIATION.put("H","h");
        PRONUNCIATION.put("I","i");
        PRONUNCIATION.put("J","j");
        PRONUNCIATION.put("K","k");
        PRONUNCIATION.put("L","l");
        PRONUNCIATION.put("M","m");
        PRONUNCIATION.put("N","n");
        PRONUNCIATION.put("Ñ","ñ");
        PRONUNCIATION.put("O","o");
        PRONUNCIATION.put("P","p");
        PRONUNCIATION.put("R","r");
        PRONUNCIATION.put("S","s");
        PRONUNCIATION.put("T","t");
        PRONUNCIATION.put("U","u");
        PRONUNCIATION.put("W","w");
        PRONUNCIATION.put("X","x");
        PRONUNCIATION.put("Y","y");
        PRONUNCIATION.put("Z","z");
        PRONUNCIATION.put("É","th");
        PRONUNCIATION.put("Ú","rr");
        PRONUNCIATION.put("Ü","ll");
        PRONUNCIATION.put("Ş","sh");
    }

    public DidiomaKeyboardView(Context c) {
        super(c);
        setBackground(new ColorDrawable(Color.rgb(17,19,21)));

        didioma = Typeface.createFromAsset(c.getAssets(), "DIDIOMA.ttf");

        keyPaint.setColor(Color.rgb(42,47,53));

        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(didioma);
        textPaint.setTextAlign(Paint.Align.CENTER);

        utilityPaint.setColor(Color.WHITE);
        utilityPaint.setTypeface(Typeface.DEFAULT_BOLD);
        utilityPaint.setTextAlign(Paint.Align.CENTER);

        pronunciationPaint.setColor(Color.rgb(170,180,190));
        pronunciationPaint.setTypeface(Typeface.DEFAULT);
        pronunciationPaint.setTextAlign(Paint.Align.RIGHT);
    }

    public void setListener(Listener l) {
        listener = l;
    }

    @Override
    protected void onMeasure(int ws, int hs) {
        int w = MeasureSpec.getSize(ws);
        int desired = (int)(getResources().getDisplayMetrics().density * 330);
        setMeasuredDimension(w, resolveSize(desired, hs));
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        keys.clear();

        if (numbers) drawGrid(c, NUMBERS, false, false);
        else if (symbols) drawGrid(c, SYMBOLS, true, false);
        else drawGrid(c, LETTERS, true, true);

        drawBottom(c);
    }

    private void drawGrid(Canvas c, String[][] rows, boolean nativeFont, boolean showPronunciation) {
        float gap = dp(5);
        float top = dp(8);
        float bottomBar = dp(58);
        float usableH = getHeight() - bottomBar - top - dp(4);
        float rowH = usableH / rows.length;

        for (int r = 0; r < rows.length; r++) {
            int cols = rows[r].length;
            float keyW = (getWidth() - gap * (cols + 1)) / cols;

            for (int col = 0; col < cols; col++) {
                String val = rows[r][col];
                if (val.equals(" ") && symbols) continue;

                float l = gap + col * (keyW + gap);
                float t = top + r * rowH + gap / 2;
                RectF rect = new RectF(l, t, l + keyW, t + rowH - gap);

                drawKey(c, rect, val, val, nativeFont, false, showPronunciation);
            }
        }
    }

    private void drawBottom(Canvas c) {
        float gap = dp(5);
        float y = getHeight() - dp(54);
        float h = dp(48);

        float[] weights = {1.0f, 1.0f, 3.3f, 1.0f, 1.0f};
        String[] labels = {numbers ? "ABC" : "123", symbols ? "ABC" : "◇", "espacio", "⌫", "↵"};
        String[] actions = {"#NUM", "#SYM", " ", "#BS", "#ENTER"};

        float total = 0;
        for (float f : weights) total += f;

        float unit = (getWidth() - gap * (weights.length + 1)) / total;
        float x = gap;

        for (int i = 0; i < weights.length; i++) {
            float w = unit * weights[i];
            RectF rect = new RectF(x, y, x + w, y + h);
            drawKey(c, rect, labels[i], actions[i], false, true, false);
            x += w + gap;
        }
    }

    private void drawKey(Canvas c, RectF r, String label, String value,
                         boolean nativeFont, boolean utility, boolean showPronunciation) {

        boolean isPressed = pressedValue != null && pressedValue.equals(value);
        keyPaint.setColor(isPressed ? Color.rgb(59,66,74) : Color.rgb(42,47,53));
        c.drawRoundRect(r, dp(8), dp(8), keyPaint);

        Paint p = (nativeFont && !utility) ? textPaint : utilityPaint;
        p.setTextSize(utility ? dp(15) : dp(27));

        Paint.FontMetrics fm = p.getFontMetrics();
        float cy = r.centerY() - (fm.ascent + fm.descent) / 2;

        if (showPronunciation) cy -= dp(2);

        c.drawText(label, r.centerX(), cy, p);

        if (showPronunciation) {
            String pronunciation = PRONUNCIATION.get(value);
            if (pronunciation != null) {
                pronunciationPaint.setTextSize(dp(9));
                c.drawText(
                        pronunciation,
                        r.right - dp(6),
                        r.bottom - dp(5),
                        pronunciationPaint
                );
            }
        }

        keys.add(new Key(r, value));
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            Key k = find(e.getX(), e.getY());
            pressedValue = k == null ? null : k.value;
            invalidate();
            return true;
        }

        if (e.getAction() == MotionEvent.ACTION_UP) {
            Key k = find(e.getX(), e.getY());

            // Antes comparábamos objetos Key. Al redibujar, Android crea una lista nueva
            // y por eso ninguna tecla llegaba a activarse. Ahora comparamos su valor.
            if (k != null && pressedValue != null && pressedValue.equals(k.value)) {
                activate(k.value);
            }

            pressedValue = null;
            invalidate();
            return true;
        }

        if (e.getAction() == MotionEvent.ACTION_CANCEL) {
            pressedValue = null;
            invalidate();
            return true;
        }

        return true;
    }

    private Key find(float x, float y) {
        for (Key k : keys) {
            if (k.rect.contains(x, y)) return k;
        }
        return null;
    }

    private void activate(String v) {
        if (listener == null) return;

        switch (v) {
            case "#NUM":
                numbers = !numbers;
                symbols = false;
                invalidate();
                break;
            case "#SYM":
                symbols = !symbols;
                numbers = false;
                invalidate();
                break;
            case "#BS":
                listener.onBackspace();
                break;
            case "#ENTER":
                listener.onEnter();
                break;
            case "||":
                listener.onCommit("||");
                break;
            default:
                listener.onCommit(v);
                break;
        }
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }

    private static class Key {
        RectF rect;
        String value;

        Key(RectF r, String v) {
            rect = r;
            value = v;
        }
    }
}
