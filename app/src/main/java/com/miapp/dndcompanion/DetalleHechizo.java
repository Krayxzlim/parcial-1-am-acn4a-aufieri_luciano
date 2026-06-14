package com.miapp.dndcompanion;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

/**
 * Detalle de hechizo.
 * Datos desde Intent extras desde MainActivity.
 * Descarga la imagen del hechizo desde URL usando Glide.
 */
public class DetalleHechizo extends AppCompatActivity {

    //Claves para los extras
    public static final String EXTRA_NOMBRE        = "hechizo_nombre";
    public static final String EXTRA_NIVEL         = "hechizo_nivel";
    public static final String EXTRA_ESCUELA       = "hechizo_escuela";
    public static final String EXTRA_TIEMPO        = "hechizo_tiempo";
    public static final String EXTRA_ALCANCE       = "hechizo_alcance";
    public static final String EXTRA_OBJETIVO      = "hechizo_objetivo";
    public static final String EXTRA_DURACION      = "hechizo_duracion";
    public static final String EXTRA_CONCENTRACION = "hechizo_concentracion";
    public static final String EXTRA_RITUAL        = "hechizo_ritual";
    public static final String EXTRA_DESCRIPCION   = "hechizo_descripcion";
    public static final String EXTRA_IMAGEN_URL    = "hechizo_imagen_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Leer extras de Intent
        Intent intent = getIntent();
        String nombre        = intent.getStringExtra(EXTRA_NOMBRE);
        String nivel         = intent.getStringExtra(EXTRA_NIVEL);
        String escuela       = intent.getStringExtra(EXTRA_ESCUELA);
        String tiempo        = intent.getStringExtra(EXTRA_TIEMPO);
        String alcance       = intent.getStringExtra(EXTRA_ALCANCE);
        String objetivo      = intent.getStringExtra(EXTRA_OBJETIVO);
        String duracion      = intent.getStringExtra(EXTRA_DURACION);
        boolean concentracion= intent.getBooleanExtra(EXTRA_CONCENTRACION, false);
        boolean ritual       = intent.getBooleanExtra(EXTRA_RITUAL, false);
        String descripcion   = intent.getStringExtra(EXTRA_DESCRIPCION);
        String imagenUrl     = intent.getStringExtra(EXTRA_IMAGEN_URL);

        //Layout raíz
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(color(R.color.fondo));
        scrollView.setScrollbars(View.SCROLLBARS_INSIDE_OVERLAY);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        scrollView.addView(root);

        //Header dorado
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER);
        header.setBackgroundColor(color(R.color.seccion_fondo));
        header.setPadding(dp(16), dp(36), dp(16), dp(12));

        // Botón volver
        TextView btnVolver = new TextView(this);
        btnVolver.setText("← VOLVER");
        btnVolver.setTextColor(color(R.color.dorado_claro));
        btnVolver.setTextSize(11);
        btnVolver.setTypeface(Typeface.MONOSPACE);
        btnVolver.setClickable(true);
        btnVolver.setFocusable(true);
        LinearLayout.LayoutParams volverLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        volverLp.setMargins(0, 0, 0, dp(8));
        btnVolver.setLayoutParams(volverLp);
        btnVolver.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        header.addView(btnVolver);

        // Badge nivel + escuela
        LinearLayout filaBadge = new LinearLayout(this);
        filaBadge.setOrientation(LinearLayout.HORIZONTAL);
        filaBadge.setGravity(Gravity.CENTER_VERTICAL);

        int[] badgeColors = getBadgeColors(nivel);
        TextView badgeView = new TextView(this);
        badgeView.setText(nivel != null ? nivel : "");
        badgeView.setTextColor(badgeColors[1]);
        badgeView.setBackgroundColor(badgeColors[0]);
        badgeView.setTextSize(9);
        badgeView.setPadding(dp(10), dp(4), dp(10), dp(4));
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        bLp.setMargins(0, 0, dp(8), 0);
        badgeView.setLayoutParams(bLp);

        TextView escuelaView = new TextView(this);
        escuelaView.setText(escuela != null ? escuela.toUpperCase() : "");
        escuelaView.setTextColor(color(R.color.texto_secundario));
        escuelaView.setTextSize(10);
        escuelaView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        if (concentracion) {
            TextView c = new TextView(this);
            c.setText("◎ CONC.");
            c.setTextColor(color(R.color.dorado_claro));
            c.setTextSize(8);
            c.setBackgroundColor(color(R.color.boton_bg));
            c.setPadding(dp(6), dp(3), dp(6), dp(3));
            filaBadge.addView(c);
        }
        if (ritual) {
            TextView r = new TextView(this);
            r.setText(" ® ");
            r.setTextColor(color(R.color.dorado));
            r.setTextSize(9);
            filaBadge.addView(r);
        }

        filaBadge.addView(badgeView);
        filaBadge.addView(escuelaView);
        header.addView(filaBadge);

        // Nombre del hechizo
        TextView nomView = new TextView(this);
        nomView.setText(nombre != null ? nombre : "");
        nomView.setTextSize(26);
        nomView.setTextColor(color(R.color.dorado));
        nomView.setTypeface(Typeface.create("serif", Typeface.BOLD));
        nomView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nomLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        nomLp.setMargins(0, dp(8), 0, 0);
        nomView.setLayoutParams(nomLp);
        header.addView(nomView);

        root.addView(header);
        root.addView(separadorDorado());

        //Imagen del hechizo desde URL (Glide)
        ImageView imgHechizo = new ImageView(this);
        imgHechizo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgHechizo.setBackgroundColor(color(R.color.stat_fondo));
        LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(200));
        imgHechizo.setLayoutParams(imgLp);

        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Glide.with(this)
                    .load(imagenUrl)
                    .transition(DrawableTransitionOptions.withCrossFade(400))
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imgHechizo);
        } else {
            // Imagen de placeholder si no hay URL
            imgHechizo.setImageResource(R.drawable.ic_launcher_foreground);
        }
        root.addView(imgHechizo);
        root.addView(separadorDorado());

        //Props de hechizo
        LinearLayout props = new LinearLayout(this);
        props.setOrientation(LinearLayout.HORIZONTAL);
        props.setBackgroundColor(color(R.color.stat_fondo));
        props.setPadding(dp(12), dp(14), dp(12), dp(14));

        props.addView(propCol("TIEMPO DE LANZAMIENTO", tiempo != null ? tiempo : "-"));
        props.addView(propSep());
        props.addView(propCol("ALCANCE", alcance != null ? alcance : "-"));
        props.addView(propSep());
        props.addView(propCol("OBJETIVO", objetivo != null ? objetivo : "-"));
        props.addView(propSep());
        props.addView(propCol("DURACIÓN", duracion != null ? duracion : "-"));
        root.addView(props);
        root.addView(separadorBorde());

        //descripción
        LinearLayout secDesc = new LinearLayout(this);
        secDesc.setOrientation(LinearLayout.VERTICAL);
        secDesc.setPadding(dp(20), dp(20), dp(20), dp(20));
        secDesc.setBackgroundColor(color(R.color.fondo));

        TextView lblDesc = new TextView(this);
        lblDesc.setText("✦  DESCRIPCIÓN");
        lblDesc.setTextColor(color(R.color.dorado));
        lblDesc.setTextSize(11);
        lblDesc.setTypeface(Typeface.create("serif", Typeface.BOLD));
        lblDesc.setLetterSpacing(0.08f);
        LinearLayout.LayoutParams lblLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lblLp.setMargins(0, 0, 0, dp(10));
        lblDesc.setLayoutParams(lblLp);
        secDesc.addView(lblDesc);

        View sepDesc = new View(this);
        sepDesc.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        sepDesc.setBackgroundColor(color(R.color.dorado_borde));
        LinearLayout.LayoutParams sepLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        sepLp.setMargins(0, 0, 0, dp(14));
        sepDesc.setLayoutParams(sepLp);
        secDesc.addView(sepDesc);

        TextView descView = new TextView(this);
        descView.setText(descripcion != null ? descripcion : "");
        descView.setTextColor(color(R.color.texto));
        descView.setTextSize(15);
        descView.setLineSpacing(dp(4), 1.0f);
        descView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        secDesc.addView(descView);
        root.addView(secDesc);

        setContentView(scrollView);
    }

    private int[] getBadgeColors(String nivel) {
        if (nivel == null) return new int[]{color(R.color.badge_truco), color(R.color.badge_truco_texto)};
        switch (nivel) {
            case "TRUCO":   return new int[]{color(R.color.badge_truco),  color(R.color.badge_truco_texto)};
            case "NIVEL 1": return new int[]{color(R.color.badge_nivel1), color(R.color.badge_nivel1_texto)};
            case "NIVEL 2": return new int[]{color(R.color.badge_nivel2), color(R.color.badge_nivel2_texto)};
            default:        return new int[]{color(R.color.badge_nivel3), color(R.color.badge_nivel3_texto)};
        }
    }

    private LinearLayout propCol(String etiqueta, String valor) {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER);
        col.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView et = new TextView(this);
        et.setText(etiqueta);
        et.setTextColor(color(R.color.dorado_claro));
        et.setTextSize(7);
        et.setGravity(Gravity.CENTER);
        et.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView val = new TextView(this);
        val.setText(valor);
        val.setTextColor(color(R.color.texto));
        val.setTextSize(10);
        val.setGravity(Gravity.CENTER);
        val.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        col.addView(et);
        col.addView(val);
        return col;
    }

    private View propSep() {
        View v = new View(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(1),
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, dp(4), 0, dp(4));
        v.setLayoutParams(lp);
        v.setBackgroundColor(color(R.color.borde));
        return v;
    }

    private View separadorDorado() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        v.setBackgroundColor(color(R.color.dorado_borde));
        return v;
    }

    private View separadorBorde() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        v.setBackgroundColor(color(R.color.borde));
        return v;
    }

    private int dp(int val) {
        return Math.round(val * getResources().getDisplayMetrics().density);
    }

    private int color(int res) {
        return androidx.core.content.ContextCompat.getColor(this, res);
    }
}