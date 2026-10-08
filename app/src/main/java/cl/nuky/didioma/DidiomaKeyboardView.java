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
    private final List<Key> keys = new ArrayList<>();
    private boolean symbols = false;
    private boolean numbers = false;
    private Key pressed;

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
    }

    public void setListener(Listener l) { listener = l; }

    @Override protected void onMeasure(int ws, int hs) {
        int w = MeasureSpec.getSize(ws);
        int desired = (int)(getResources().getDisplayMetrics().density * 330);
        setMeasuredDimension(w, resolveSize(desired, hs));
    }

    @Override protected void onDraw(Canvas c) {
        super.onDraw(c);
        keys.clear();
        if (numbers) drawGrid(c, NUMBERS, false);
        else if (symbols) drawGrid(c, SYMBOLS, true);
        else drawGrid(c, LETTERS, true);
        drawBottom(c);
    }

    private void drawGrid(Canvas c, String[][] rows, boolean nativeFont) {
        float gap = dp(5);
        float top = dp(8);
        float bottomBar = dp(58);
        float usableH = getHeight() - bottomBar - top - dp(4);
        float rowH = usableH / rows.length;
        for (int r=0; r<rows.length; r++) {
            int cols = rows[r].length;
            float keyW = (getWidth() - gap*(cols+1)) / cols;
            for (int col=0; col<cols; col++) {
                String val = rows[r][col];
                if (val.equals(" ") && symbols) continue;
                float l = gap + col*(keyW+gap);
                float t = top + r*rowH + gap/2;
                RectF rect = new RectF(l,t,l+keyW,t+rowH-gap);
                drawKey(c, rect, val, val, nativeFont, false);
            }
        }
    }

    private void drawBottom(Canvas c) {
        float gap = dp(5);
        float y = getHeight()-dp(54);
        float h = dp(48);
        float[] weights = {1.0f,1.0f,3.3f,1.0f,1.0f};
        String[] labels = {numbers?"ABC":"123", symbols?"ABC":"◇", "espacio", "⌫", "↵"};
        String[] actions = {"#NUM","#SYM"," ","#BS","#ENTER"};
        float total = 0; for(float f:weights) total+=f;
        float unit = (getWidth()-gap*(weights.length+1))/total;
        float x=gap;
        for(int i=0;i<weights.length;i++){
            float w=unit*weights[i];
            RectF rect=new RectF(x,y,x+w,y+h);
            drawKey(c,rect,labels[i],actions[i],false,true);
            x+=w+gap;
        }
    }

    private void drawKey(Canvas c, RectF r, String label, String value, boolean nativeFont, boolean utility) {
        keyPaint.setColor(pressed != null && pressed.rect.equals(r) ? Color.rgb(59,66,74) : Color.rgb(42,47,53));
        c.drawRoundRect(r, dp(8), dp(8), keyPaint);
        Paint p = (nativeFont && !utility) ? textPaint : utilityPaint;
        p.setTextSize(utility ? dp(15) : dp(27));
        Paint.FontMetrics fm=p.getFontMetrics();
        float cy=r.centerY()-(fm.ascent+fm.descent)/2;
        c.drawText(label,r.centerX(),cy,p);
        keys.add(new Key(r,value));
    }

    @Override public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction()==MotionEvent.ACTION_DOWN){
            pressed=find(e.getX(),e.getY()); invalidate(); return true;
        }
        if(e.getAction()==MotionEvent.ACTION_UP){
            Key k=find(e.getX(),e.getY());
            if(k!=null && pressed==k) activate(k.value);
            pressed=null; invalidate(); return true;
        }
        if(e.getAction()==MotionEvent.ACTION_CANCEL){ pressed=null; invalidate(); return true; }
        return true;
    }

    private Key find(float x,float y){ for(Key k:keys) if(k.rect.contains(x,y)) return k; return null; }

    private void activate(String v){
        if(listener==null)return;
        switch(v){
            case "#NUM": numbers=!numbers; symbols=false; invalidate(); break;
            case "#SYM": symbols=!symbols; numbers=false; invalidate(); break;
            case "#BS": listener.onBackspace(); break;
            case "#ENTER": listener.onEnter(); break;
            case "||": listener.onCommit("||"); break;
            default: listener.onCommit(v); break;
        }
    }

    private float dp(float v){ return v*getResources().getDisplayMetrics().density; }
    private static class Key { RectF rect; String value; Key(RectF r,String v){rect=r;value=v;} }
}
