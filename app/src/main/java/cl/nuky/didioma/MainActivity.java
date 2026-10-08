package cl.nuky.didioma;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(24), dp(48), dp(24), dp(24));
        root.setBackgroundColor(Color.rgb(17,19,21));

        TextView title = new TextView(this);
        title.setText("DIDIOMA");
        title.setTextColor(Color.WHITE);
        title.setTextSize(34);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        root.addView(title, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView info = new TextView(this);
        info.setText("Teclado oficial del idioma.\n\n1. Activa DIDIOMA.\n2. Selecciónalo como teclado.\n3. Escribe normalmente.\n\nNo hay mayúsculas. La segunda capa contiene los signos gramaticales.");
        info.setTextColor(Color.rgb(220,225,230));
        info.setTextSize(17);
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, dp(24), 0, dp(24));
        root.addView(info, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button enable = button("1 · Activar teclado");
        enable.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        root.addView(enable, lp());

        Button choose = button("2 · Elegir DIDIOMA");
        choose.setOnClickListener(v -> {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.showInputMethodPicker();
        });
        root.addView(choose, lp());

        TextView note = new TextView(this);
        note.setText("El teclado muestra los glifos con DIDIOMA.ttf. Fuera del teclado, cada app decide cómo renderizar los caracteres.");
        note.setTextColor(Color.rgb(160,170,180));
        note.setTextSize(13);
        note.setGravity(Gravity.CENTER);
        note.setPadding(0, dp(28), 0, 0);
        root.addView(note, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(root);
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(17);
        b.setAllCaps(false);
        return b;
    }

    private LinearLayout.LayoutParams lp() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(58));
        p.setMargins(0, dp(10), 0, 0);
        return p;
    }
}
