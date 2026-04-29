package com.miapp.dndcompanion; // ← tu package

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    LinearLayout layoutDisponibles, layoutActivas, layoutSpells;
    TextView txtResultado, txtTipoDado;
    int contador = 0;
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutDisponibles = findViewById(R.id.layoutDisponibles);
        layoutActivas     = findViewById(R.id.layoutActivas);
        layoutSpells      = findViewById(R.id.layoutSpells);
        txtResultado      = findViewById(R.id.txtResultado);
        txtTipoDado       = findViewById(R.id.txtTipoDado);

        // misiones iniciales placeholder
        agregarMisionDisponible("Entrega especial",
                "Entrega un paquete sellado al Gremio de Comerciantes.", "100 XP");
        agregarMisionDisponible("Caza de bandidos",
                "Elimina a los bandidos del camino del norte.", "200 XP");

        // Hechizos placeholder
        agregarSpell("Descarga de Escarcha", "TRUCO");
        agregarSpell("Mano Mágica",           "TRUCO");
        agregarSpell("Escudo",                "NIVEL 1");
        agregarSpell("Misiles Mágicos",       "NIVEL 1");
        agregarSpell("Armadura de Magia",     "NIVEL 2");
        agregarSpell("Bola de Fuego",         "NIVEL 3");

        // + mision
        findViewById(R.id.btnAgregar).setOnClickListener(v -> {
            contador++;
            agregarMisionDisponible("Nueva misión " + contador,
                    "Una misión misteriosa te aguarda.", "50 XP");
        });

        // Dados
        findViewById(R.id.btnD4).setOnClickListener(v  -> tirar(4));
        findViewById(R.id.btnD6).setOnClickListener(v  -> tirar(6));
        findViewById(R.id.btnD8).setOnClickListener(v  -> tirar(8));
        findViewById(R.id.btnD10).setOnClickListener(v -> tirar(10));
        findViewById(R.id.btnD20).setOnClickListener(v -> tirar(20));
        findViewById(R.id.btnAleatorio).setOnClickListener(v -> {
            int[] dados = {4, 6, 8, 10, 12, 20};
            tirar(dados[random.nextInt(dados.length)]);
        });
    }

    private void tirar(int caras) {
        int resultado = random.nextInt(caras) + 1;
        txtResultado.setText(String.valueOf(resultado));
        txtTipoDado.setText("(d" + caras + ")");
    }

    private void agregarMisionDisponible(String nombre, String descripcion, String recompensa) {
        if (layoutDisponibles.getChildCount() > 0) {
            agregarSeparador(layoutDisponibles);
        }

        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, 12, 0, 12);

        TextView icono = new TextView(this);
        icono.setText("📜");
        icono.setTextSize(26);
        icono.setPadding(0, 0, 10, 0);
        icono.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView txtNombre = new TextView(this);
        txtNombre.setText(nombre);
        txtNombre.setTextColor(getResources().getColor(R.color.texto));
        txtNombre.setTextSize(14);

        TextView txtDesc = new TextView(this);
        txtDesc.setText(descripcion);
        txtDesc.setTextColor(getResources().getColor(R.color.texto_secundario));
        txtDesc.setTextSize(12);

        TextView txtXP = new TextView(this);
        txtXP.setText("Recompensa:  " + recompensa);
        txtXP.setTextColor(getResources().getColor(R.color.dorado_claro));
        txtXP.setTextSize(12);

        info.addView(txtNombre);
        info.addView(txtDesc);
        info.addView(txtXP);

        Button btnAceptar = new Button(this);
        btnAceptar.setText("ACEPTAR");
        btnAceptar.setTextSize(11);
        btnAceptar.setTextColor(getResources().getColor(R.color.dorado));
        btnAceptar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                getResources().getColor(R.color.boton_bg)));
        btnAceptar.setOnClickListener(v -> agregarMisionActiva(nombre, recompensa, item));

        item.addView(icono);
        item.addView(info);
        item.addView(btnAceptar);
        layoutDisponibles.addView(item);
    }

    private void agregarMisionActiva(String nombre, String recompensa, LinearLayout itemAEliminar) {
        layoutDisponibles.removeView(itemAEliminar);

        if (layoutActivas.getChildCount() > 0) {
            agregarSeparador(layoutActivas);
        }

        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, 12, 0, 12);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView icono = new TextView(this);
        icono.setText("⚔️");
        icono.setTextSize(26);
        icono.setPadding(0, 0, 10, 0);
        icono.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView txtNombre = new TextView(this);
        txtNombre.setText(nombre);
        txtNombre.setTextColor(getResources().getColor(R.color.texto));
        txtNombre.setTextSize(14);

        TextView txtXP = new TextView(this);
        txtXP.setText("Recompensa:  " + recompensa);
        txtXP.setTextColor(getResources().getColor(R.color.dorado_claro));
        txtXP.setTextSize(12);

        info.addView(txtNombre);
        info.addView(txtXP);

        TextView badge = new TextView(this);
        badge.setText("EN CURSO");
        badge.setTextColor(getResources().getColor(R.color.dorado));
        badge.setTextSize(10);
        badge.setPadding(10, 5, 10, 5);
        badge.setBackgroundColor(getResources().getColor(R.color.boton_bg));

        item.addView(icono);
        item.addView(info);
        item.addView(badge);
        layoutActivas.addView(item);
    }

    private void agregarSpell(String nombre, String nivel) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, 5, 0, 5);
        item.setGravity(android.view.Gravity.CENTER_VERTICAL);
        //colores distintos x nivel
        int badgeBg, badgeTexto;
        switch (nivel) {
            case "TRUCO":
                badgeBg    = getResources().getColor(R.color.badge_truco);
                badgeTexto = getResources().getColor(R.color.badge_truco_texto);
                break;
            case "NIVEL 1":
                badgeBg    = getResources().getColor(R.color.badge_nivel1);
                badgeTexto = getResources().getColor(R.color.badge_nivel1_texto);
                break;
            case "NIVEL 2":
                badgeBg    = getResources().getColor(R.color.badge_nivel2);
                badgeTexto = getResources().getColor(R.color.badge_nivel2_texto);
                break;
            default: // NIVEL 3+
                badgeBg    = getResources().getColor(R.color.badge_nivel3);
                badgeTexto = getResources().getColor(R.color.badge_nivel3_texto);
                break;
        }

        TextView badgeView = new TextView(this);
        badgeView.setText(nivel);
        badgeView.setTextColor(badgeTexto);
        badgeView.setTextSize(9);
        badgeView.setPadding(8, 4, 8, 4);
        badgeView.setBackgroundColor(badgeBg);
        LinearLayout.LayoutParams badgeLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        badgeLp.setMargins(0, 0, 10, 0);
        badgeView.setLayoutParams(badgeLp);

        TextView txtNombre = new TextView(this);
        txtNombre.setText(nombre);
        txtNombre.setTextColor(getResources().getColor(R.color.texto));
        txtNombre.setTextSize(13);
        txtNombre.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        item.addView(badgeView);
        item.addView(txtNombre);
        layoutSpells.addView(item);
    }

    private void agregarSeparador(LinearLayout parent) {
        android.view.View sep = new android.view.View(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        lp.setMargins(0, 4, 0, 4);
        sep.setLayoutParams(lp);
        sep.setBackgroundColor(getResources().getColor(R.color.borde));
        parent.addView(sep);
    }
}