package com.miapp.dndcompanion;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
        View menuInicio = findViewById(R.id.menuInicio);
        menuInicio.setOnClickListener(v -> pulsarYEjecutar(v, () -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }));

        View menuInventario = findViewById(R.id.menuInventario);
        menuInventario.setOnClickListener(v -> pulsarYEjecutar(v, () -> {
            startActivity(new Intent(this, InventarioActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }));

        View menuNotas = findViewById(R.id.menuNotas);
        menuNotas.setOnClickListener(v ->
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press)));

        View menuAjustes = findViewById(R.id.menuAjustes);
        menuAjustes.setOnClickListener(v -> pulsarYEjecutar(v, () ->
                mostrarDialogoEstetico("Ajustes",
                        "⚙️  Los ajustes estarán\ndisponibles en la próxima versión.")
        ));
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
    private void pulsarYEjecutar(View v, Runnable accion) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.btn_press);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation a) {}
            @Override public void onAnimationRepeat(Animation a) {}
            @Override public void onAnimationEnd(Animation a) { accion.run(); }
        });
        v.startAnimation(anim);
    }
    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }

    private int color(int res) {
        return androidx.core.content.ContextCompat.getColor(this, res);
    }

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

    private View separadorDorado() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        v.setBackgroundColor(color(R.color.dorado_borde));
        return v;
    }
}