package com.miapp.dndcompanion;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class NotasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);

        configurarMenu();

        // Botón guardar nota
        findViewById(R.id.btnGuardarNota).setOnClickListener(v ->
                Toast.makeText(this, "✦ Nota guardada", Toast.LENGTH_SHORT).show()
        );
    }

    private void configurarMenu() {
        findViewById(R.id.menuInicio).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        findViewById(R.id.menuInventario).setOnClickListener(v -> {
            startActivity(new Intent(this, InventarioActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        findViewById(R.id.menuNotas).setOnClickListener(v -> { /* ya estamos aquí */ });

        findViewById(R.id.menuAjustes).setOnClickListener(v ->
                mostrarDialogoEstetico("Ajustes",
                        "⚙️  Los ajustes estarán\ndisponibles en la próxima versión.")
        );
    }
    private void mostrarDialogoEstetico(String titulo, String mensaje) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundResource(R.drawable.seccion_bg);
        root.setPadding(dp(20), dp(20), dp(20), dp(20));

        TextView t = txt("✦  " + titulo.toUpperCase() + "  ✦", 14, R.color.dorado, true);
        t.setTypeface(Typeface.create("serif", Typeface.BOLD));
        t.setGravity(Gravity.CENTER);
        root.addView(t);

        root.addView(separadorDorado());

        TextView m = txt(mensaje, 13, R.color.texto, false);
        m.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams mLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mLp.setMargins(0, dp(12), 0, dp(12));
        m.setLayoutParams(mLp);
        root.addView(m);

        root.addView(separadorDorado());

        Button b = new Button(this);
        b.setText("ACEPTAR");
        b.setTextColor(color(R.color.dorado));
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color(R.color.boton_bg)));
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(42));
        bLp.setMargins(0, dp(12), 0, 0);
        b.setLayoutParams(bLp);
        root.addView(b);

        AlertDialog d = new AlertDialog.Builder(this, R.style.DialogoOscuro)
                .setView(root).create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        b.setOnClickListener(v -> d.dismiss());
        d.show();
    }
    /** dp a px */
    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }

    /** getColor */
    private int color(int res) {
        return getResources().getColor(res);
    }

    /** TextView con los parámetros más comunes*/
    private TextView txt(String texto, float sp, int colorRes, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextSize(sp);
        tv.setTextColor(color(colorRes));
        if (bold) tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return tv;
    }

    /**Separador línea dorada*/
    private View separadorDorado() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        v.setBackgroundColor(color(R.color.dorado_borde));
        return v;
    }

    /**Separador línea gris*/
    private View separadorBorde() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        v.setBackgroundColor(color(R.color.borde));
        return v;
    }

    /**separador (misiones, hechizos)*/
    private void agregarSeparador(LinearLayout parent) {
        View sep = new View(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        lp.setMargins(0, dp(3), 0, dp(3));
        sep.setLayoutParams(lp);
        sep.setBackgroundColor(color(R.color.borde));
        parent.addView(sep);
    }

    /**columna para tarjeta de hechizo*/
    private LinearLayout propCol(String etiqueta, String valor) {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER);
        col.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView et = txt(etiqueta, 7, R.color.dorado_claro, false);
        et.setGravity(Gravity.CENTER);
        et.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView val = txt(valor, 10, R.color.texto, false);
        val.setGravity(Gravity.CENTER);
        val.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        col.addView(et);
        col.addView(val);
        return col;
    }

    /**Separador vertical*/
    private View propSep() {
        View v = new View(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(1),
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, dp(4), 0, dp(4));
        v.setLayoutParams(lp);
        v.setBackgroundColor(color(R.color.borde));
        return v;
    }
}