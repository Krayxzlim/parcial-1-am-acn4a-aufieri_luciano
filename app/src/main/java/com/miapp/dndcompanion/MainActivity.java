package com.miapp.dndcompanion;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Random;
import android.net.Uri;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class MainActivity extends AppCompatActivity {

    LinearLayout layoutDisponibles, layoutActivas, layoutSpells;
    TextView txtResultado, txtTipoDado;
    ImageView imgAvatar;
    ActivityResultLauncher<String> pickImageLauncher;
    int contador = 0;
    Random random = new Random();
    ArrayList<String> historial = new ArrayList<>();

    //Animaciones
    Animation animBtn, animDado, animMisionSale, animMisionEntra;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imgAvatar.setImageURI(uri);
                    }
                }
        );
        setContentView(R.layout.activity_main);

        // Precargar
        animBtn        = AnimationUtils.loadAnimation(this, R.anim.btn_press);
        animDado       = AnimationUtils.loadAnimation(this, R.anim.dado_roll);
        animMisionSale = AnimationUtils.loadAnimation(this, R.anim.mision_aceptar);
        animMisionEntra= AnimationUtils.loadAnimation(this, R.anim.mision_entrar);

        layoutDisponibles = findViewById(R.id.layoutDisponibles);
        layoutActivas     = findViewById(R.id.layoutActivas);
        layoutSpells      = findViewById(R.id.layoutSpells);
        txtResultado      = findViewById(R.id.txtResultado);
        txtTipoDado       = findViewById(R.id.txtTipoDado);

        imgAvatar = findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            pickImageLauncher.launch("image/*");
        });

        // Misiones
        agregarMisionDisponible("Entrega especial",
                "Entrega un paquete sellado al Gremio de Comerciantes.", "100 XP");
        agregarMisionDisponible("Caza de bandidos",
                "Elimina a los bandidos del camino del norte.", "200 XP");

        // Hechizos
        agregarSpell("Descarga de Escarcha", "TRUCO",
                "Conjuración", "Acción", "60 pies", "1 criatura", "Instantáneo", false, false,
                "Lanzas un rayo de frío contra una criatura. Realiza un ataque de hechizo a distancia. Si impacta, inflige 1d8 de daño de frío y la velocidad del objetivo se reduce en 10 pies hasta el inicio de tu siguiente turno.\n\nEl daño aumenta en 1d8 cuando alcanzas nivel 5 (2d8), nivel 11 (3d8) y nivel 17 (4d8).");
        agregarSpell("Mano Mágica", "TRUCO",
                "Conjuración", "Acción", "30 pies", "Un objeto", "1 minuto", false, false,
                "Una mano espectral flotante aparece en un punto elegido. Puede recoger o manipular objetos, abrir puertas, depositar objetos y usar herramientas sencillas.\n\nNo puede atacar, activar objetos mágicos ni cargar más de 10 libras.");
        agregarSpell("Escudo", "NIVEL 1",
                "Abjuración", "Reacción", "Personal", "Tú mismo", "1 ronda", false, false,
                "Una barrera invisible de fuerza mágica aparece para protegerte. Se activa cuando eres atacado o cuando una criatura te lanza Proyectil Mágico.\n\n+5 a la CA hasta el inicio de tu siguiente turno. Eres inmune a Proyectil Mágico este turno.");
        agregarSpell("Misiles Mágicos", "NIVEL 1",
                "Evocación", "Acción", "120 pies", "Una o más criaturas", "Instantáneo", false, false,
                "Creas tres dardos brillantes de fuerza mágica que impactan automáticamente. Cada dardo inflige 1d4+1 de daño de fuerza.\n\n*En niveles superiores:* el hechizo crea un dardo adicional por cada nivel por encima del 1.");
        agregarSpell("Armadura de Magia", "NIVEL 2",
                "Abjuración", "Acción", "Toque", "Una criatura dispuesta", "8 horas", false, false,
                "Tocas a una criatura dispuesta y la envuelves en protección mágica. Su CA se convierte en 13 + modificador de Destreza.\n\nEl hechizo finaliza si el objetivo equipa armadura.");
        agregarSpell("Bola de Fuego", "NIVEL 3",
                "Evocación", "Acción", "150 pies", "Esfera 20 pies", "Instantáneo", true, false,
                "Un destello brillante explota en un punto elegido. Cada criatura en el área realiza salvación de Destreza CD 14.\n\nFalla: 8d6 daño de fuego.\nÉxito: mitad del daño.\n\n*En niveles superiores:* +1d6 por cada nivel por encima del 3.");

        configurarBtnDado(R.id.btnD4,  4);
        configurarBtnDado(R.id.btnD6,  6);
        configurarBtnDado(R.id.btnD8,  8);
        configurarBtnDado(R.id.btnD10, 10);
        configurarBtnDado(R.id.btnD12, 12);
        configurarBtnDado(R.id.btnD20, 20);

        View btnAleatorio = findViewById(R.id.btnAleatorio);
        btnAleatorio.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            int[] dados = {4, 6, 8, 10, 12, 20};
            tirarConAnimacion(dados[random.nextInt(dados.length)]);
        });

        View btnHistorial = findViewById(R.id.btnHistorial);
        btnHistorial.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(this::mostrarHistorial, 150);
        });

        View btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            contador++;
            agregarMisionDisponible("Nueva misión " + contador, "Una misión misteriosa te aguarda.", "50 XP");
        });

        configurarMenu();
    }


    // pulsar boton
    private void pulsarYEjecutar(View v, Runnable accion) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.btn_press);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation a) {}
            @Override public void onAnimationRepeat(Animation a) {}
            @Override public void onAnimationEnd(Animation a) { accion.run(); }
        });
        v.startAnimation(anim);
    }


    //anim tirada dado
    private void configurarBtnDado(int btnId, int caras) {
        View btn = findViewById(btnId);
        btn.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(
                    () -> tirarConAnimacion(caras), 100);
        });
    }

    private void tirarConAnimacion(int caras) {
        int resultado = random.nextInt(caras) + 1;

        final int DURACION_RULETA = 700;
        final int INTERVALO = 80;
        final int FLASHES = DURACION_RULETA / INTERVALO;
        txtResultado.setText("?");
        txtTipoDado.setText("d" + caras);

        ObjectAnimator rotAnim = ObjectAnimator.ofFloat(txtResultado, "rotation", 0f, 360f);
        rotAnim.setDuration(DURACION_RULETA);
        rotAnim.setRepeatCount(0);
        rotAnim.start();

        final Handler handler = new Handler(Looper.getMainLooper());
        for (int i = 0; i < FLASHES; i++) {
            final int idx = i;
            handler.postDelayed(() -> {
                int numRandom = random.nextInt(caras) + 1;
                txtResultado.setText(String.valueOf(numRandom));
                // Color oscila entre dorado y blanco
                txtResultado.setTextColor(idx % 2 == 0
                        ? color(R.color.dorado)
                        : color(R.color.texto));
            }, idx * INTERVALO);
        }

        handler.postDelayed(() -> {
            historial.add(0, "d" + caras + "|" + resultado);
            if (historial.size() > 20) historial.remove(historial.size() - 1);

            int colorFinal;
            String textoTipo;
            if (caras == 20 && resultado == 1) {
                colorFinal = Color.parseColor("#C05050");
                textoTipo = "(d" + caras + ")  💀";
            } else if (caras == 20 && resultado == 20) {
                colorFinal = color(R.color.dorado);
                textoTipo = "(d20)  ✦";
            } else {
                colorFinal = color(R.color.dorado);
                textoTipo = "(d" + caras + ")";
            }

            txtResultado.setText(String.valueOf(resultado));
            txtResultado.setTextColor(colorFinal);
            txtTipoDado.setText(textoTipo);

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(txtResultado, "scaleX", 0.3f, 1.15f, 1.0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(txtResultado, "scaleY", 0.3f, 1.15f, 1.0f);
            scaleX.setDuration(400);
            scaleY.setDuration(400);
            scaleX.setInterpolator(new OvershootInterpolator(2.5f));
            scaleY.setInterpolator(new OvershootInterpolator(2.5f));
            scaleX.start();
            scaleY.start();

            //20 natural: color pulsante
            if (caras == 20 && resultado == 20) {
                ObjectAnimator pulso = ObjectAnimator.ofFloat(txtResultado, "alpha", 1f, 0.3f, 1f);
                pulso.setDuration(300);
                pulso.setRepeatCount(3);
                pulso.setStartDelay(400);
                pulso.start();
            }

        }, DURACION_RULETA + 50);
    }

    // ANIMACIÓN MISIÓN
    private void aceptarMisionConAnimacion(String nombre, String recompensa, LinearLayout wrapper) {
        if (wrapper.getParent() == null) return;
        if (Boolean.TRUE.equals(wrapper.getTag())) return;
        wrapper.setTag(true);

        wrapper.animate()
                .alpha(0f)
                .translationX(200f)
                .setDuration(250)
                .withEndAction(() -> {
                    wrapper.clearAnimation();
                    wrapper.post(() -> {
                        if (wrapper.getParent() != null) {
                            layoutDisponibles.removeView(wrapper);
                        }
                        if (layoutDisponibles.getChildCount() > 0) {
                            View primerChild = layoutDisponibles.getChildAt(0);
                            if (primerChild instanceof LinearLayout) {
                                LinearLayout primerWrapper = (LinearLayout) primerChild;
                                if (primerWrapper.getChildCount() > 0
                                        && "sep".equals(primerWrapper.getChildAt(0).getTag())) {
                                    primerWrapper.removeViewAt(0);
                                }
                            }
                        }
                        agregarMisionActivaAnimada(nombre, recompensa);
                    });
                })
                .start();
    }

    private void agregarMisionActivaAnimada(String nombre, String recompensa) {
        if (layoutActivas.getChildCount() > 0) agregarSeparador(layoutActivas);
        LinearLayout item = buildItemMisionActiva(nombre, recompensa);
        layoutActivas.addView(item);

        item.setAlpha(0f);
        item.setTranslationX(-200f);
        item.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(250)
                .start();
    }
    private LinearLayout buildItemMisionActiva(String nombre, String recompensa) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, dp(10), 0, dp(10));
        item.setGravity(Gravity.CENTER_VERTICAL);

        TextView icono = new TextView(this);
        icono.setText("⚔️");
        icono.setTextSize(22);
        icono.setPadding(0, 0, dp(10), 0);
        icono.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        info.addView(txt(nombre, 13, R.color.texto, true));
        info.addView(txt("Recompensa:  " + recompensa, 11, R.color.dorado_claro, false));

        TextView badge = new TextView(this);
        badge.setText("EN CURSO");
        badge.setTextColor(color(R.color.dorado));
        badge.setTextSize(9);
        badge.setPadding(dp(8), dp(4), dp(8), dp(4));
        badge.setBackgroundColor(color(R.color.boton_bg));

        item.addView(icono);
        item.addView(info);
        item.addView(badge);
        return item;
    }

    // MENÚ INFERIOR
    private void configurarMenu() {
        View menuInicio = findViewById(R.id.menuInicio);
        menuInicio.setOnClickListener(v ->
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press))
        );

        View menuInventario = findViewById(R.id.menuInventario);
        menuInventario.setOnClickListener(v -> pulsarYEjecutar(v, () -> {
            startActivity(new Intent(this, InventarioActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }));

        View menuNotas = findViewById(R.id.menuNotas);
        menuNotas.setOnClickListener(v -> pulsarYEjecutar(v, () -> {
            startActivity(new Intent(this, NotasActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }));

        View menuAjustes = findViewById(R.id.menuAjustes);
        menuAjustes.setOnClickListener(v -> pulsarYEjecutar(v, () ->
                mostrarDialogoEstetico("Ajustes",
                        "⚙️  Los ajustes estarán\ndisponibles en la próxima versión.")
        ));
    }

    // HISTORIAL
    private void mostrarHistorial() {
        LinearLayout raiz = new LinearLayout(this);
        raiz.setOrientation(LinearLayout.VERTICAL);
        raiz.setBackgroundResource(R.drawable.seccion_bg);

        LinearLayout encabezado = new LinearLayout(this);
        encabezado.setOrientation(LinearLayout.VERTICAL);
        encabezado.setGravity(Gravity.CENTER);
        encabezado.setPadding(dp(16), dp(16), dp(16), dp(12));
        encabezado.setBackgroundColor(color(R.color.seccion_fondo));

        LinearLayout filaTitulo = new LinearLayout(this);
        filaTitulo.setOrientation(LinearLayout.HORIZONTAL);
        filaTitulo.setGravity(Gravity.CENTER_VERTICAL);

        TextView e1 = txt("✦", 14, R.color.dorado, false);
        e1.setPadding(0, 0, dp(8), 0);
        TextView titulo = txt("HISTORIAL DE TIRADAS", 14, R.color.dorado, true);
        titulo.setTypeface(Typeface.create("serif", Typeface.BOLD));
        TextView e2 = txt("✦", 14, R.color.dorado, false);
        e2.setPadding(dp(8), 0, 0, 0);

        filaTitulo.addView(e1); filaTitulo.addView(titulo); filaTitulo.addView(e2);
        encabezado.addView(filaTitulo);

        TextView subtitulo = txt(historial.isEmpty() ? "Sin tiradas registradas" :
                        "Últimas " + Math.min(historial.size(), 15) + " tiradas",
                10, R.color.texto_secundario, false);
        subtitulo.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subLp.setMargins(0, dp(4), 0, 0);
        subtitulo.setLayoutParams(subLp);
        encabezado.addView(subtitulo);
        raiz.addView(encabezado);
        raiz.addView(separadorDorado());

        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(300)));
        scroll.setBackgroundColor(color(R.color.fondo));

        LinearLayout lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        lista.setPadding(0, dp(4), 0, dp(4));

        if (historial.isEmpty()) {
            LinearLayout vacio = new LinearLayout(this);
            vacio.setOrientation(LinearLayout.VERTICAL);
            vacio.setGravity(Gravity.CENTER);
            vacio.setPadding(0, dp(40), 0, dp(40));
            TextView ico = txt("🎲", 36, R.color.borde, false);
            ico.setGravity(Gravity.CENTER);
            ico.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView msg = txt("Aún no tiraste ningún dado", 13, R.color.texto_secundario, false);
            msg.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams msgLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            msgLp.setMargins(0, dp(8), 0, 0);
            msg.setLayoutParams(msgLp);
            vacio.addView(ico); vacio.addView(msg);
            lista.addView(vacio);
        } else {
            int mostrar = Math.min(historial.size(), 15);
            for (int i = 0; i < mostrar; i++) {
                String[] p = historial.get(i).split("\\|");
                String tipoDado = p.length > 0 ? p[0] : "?";
                String valStr   = p.length > 1 ? p[1] : "?";
                int val = 0;
                try { val = Integer.parseInt(valStr); } catch (Exception ignored) {}

                LinearLayout fila = new LinearLayout(this);
                fila.setOrientation(LinearLayout.HORIZONTAL);
                fila.setGravity(Gravity.CENTER_VERTICAL);
                fila.setPadding(dp(16), dp(10), dp(16), dp(10));
                fila.setBackgroundColor(i % 2 == 0 ? color(R.color.seccion_fondo) : color(R.color.fondo));

                TextView numView = txt("#" + (i + 1), 10, R.color.borde, false);
                numView.setGravity(Gravity.CENTER);
                numView.setTypeface(Typeface.MONOSPACE);
                LinearLayout.LayoutParams numLp = new LinearLayout.LayoutParams(dp(28),
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                numLp.setMargins(0, 0, dp(10), 0);
                numView.setLayoutParams(numLp);

                TextView badgeDado = new TextView(this);
                badgeDado.setText(tipoDado);
                badgeDado.setTextColor(color(R.color.dorado));
                badgeDado.setTextSize(11);
                badgeDado.setTypeface(Typeface.create("serif", Typeface.BOLD));
                badgeDado.setBackgroundColor(color(R.color.boton_bg));
                badgeDado.setPadding(dp(8), dp(3), dp(8), dp(3));
                LinearLayout.LayoutParams badgeLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                badgeLp.setMargins(0, 0, dp(12), 0);
                badgeDado.setLayoutParams(badgeLp);

                View spacer = new View(this);
                spacer.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                int colorRes;
                String sufijo = "";
                if (val == 1) { colorRes = 0; sufijo = "  💀"; }
                else if (tipoDado.equals("d20") && val == 20) { colorRes = R.color.dorado; sufijo = "  ✦"; }
                else { colorRes = R.color.texto; }

                TextView resView = new TextView(this);
                resView.setText(valStr + sufijo);
                resView.setTextSize(20);
                resView.setTypeface(Typeface.DEFAULT_BOLD);
                resView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                if (colorRes == 0) resView.setTextColor(Color.parseColor("#C05050"));
                else resView.setTextColor(color(colorRes));

                fila.addView(numView); fila.addView(badgeDado);
                fila.addView(spacer); fila.addView(resView);
                lista.addView(fila);
            }
        }
        scroll.addView(lista);
        raiz.addView(scroll);
        raiz.addView(separadorDorado());

        Button cerrar = new Button(this);
        cerrar.setText("✦  CERRAR  ✦");
        cerrar.setTextColor(color(R.color.dorado));
        cerrar.setTextSize(12);
        cerrar.setTypeface(Typeface.create("serif", Typeface.BOLD));
        cerrar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color(R.color.boton_bg)));
        LinearLayout.LayoutParams cerrarLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(44));
        cerrarLp.setMargins(dp(16), dp(10), dp(16), dp(16));
        cerrar.setLayoutParams(cerrarLp);
        raiz.addView(cerrar);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.DialogoOscuro)
                .setView(raiz).create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        cerrar.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, 150);
        });
        dialog.show();
    }

    // SPELLCARDS
    private void mostrarTarjetaHechizo(String nombre, String nivel, String escuela,
                                       String tiempo, String alcance, String objetivo,
                                       String duracion, boolean concentracion, boolean ritual,
                                       String descripcion) {
        int badgeBg, badgeTexto;
        switch (nivel) {
            case "TRUCO":  badgeBg = R.color.badge_truco;  badgeTexto = R.color.badge_truco_texto;  break;
            case "NIVEL 1":badgeBg = R.color.badge_nivel1; badgeTexto = R.color.badge_nivel1_texto; break;
            case "NIVEL 2":badgeBg = R.color.badge_nivel2; badgeTexto = R.color.badge_nivel2_texto; break;
            default:       badgeBg = R.color.badge_nivel3; badgeTexto = R.color.badge_nivel3_texto; break;
        }

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.seccion_bg);

        LinearLayout cab = new LinearLayout(this);
        cab.setOrientation(LinearLayout.VERTICAL);
        cab.setPadding(dp(16), dp(16), dp(16), dp(12));
        cab.setBackgroundColor(color(R.color.seccion_fondo));

        LinearLayout filaBadge = new LinearLayout(this);
        filaBadge.setOrientation(LinearLayout.HORIZONTAL);
        filaBadge.setGravity(Gravity.CENTER_VERTICAL);

        TextView badgeView = new TextView(this);
        badgeView.setText(nivel);
        badgeView.setTextColor(color(badgeTexto));
        badgeView.setBackgroundColor(color(badgeBg));
        badgeView.setTextSize(9);
        badgeView.setPadding(dp(10), dp(4), dp(10), dp(4));
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bLp.setMargins(0, 0, dp(8), 0);
        badgeView.setLayoutParams(bLp);

        TextView escuelaView = txt(escuela.toUpperCase(), 10, R.color.texto_secundario, false);
        escuelaView.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        filaBadge.addView(badgeView);
        filaBadge.addView(escuelaView);

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
            r.setTextSize(8);
            filaBadge.addView(r);
        }
        cab.addView(filaBadge);

        TextView nomView = txt(nombre, 20, R.color.dorado, true);
        nomView.setTypeface(Typeface.create("serif", Typeface.BOLD));
        LinearLayout.LayoutParams nomLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        nomLp.setMargins(0, dp(6), 0, 0);
        nomView.setLayoutParams(nomLp);
        cab.addView(nomView);
        card.addView(cab);
        card.addView(separadorDorado());

        LinearLayout props = new LinearLayout(this);
        props.setOrientation(LinearLayout.HORIZONTAL);
        props.setBackgroundColor(color(R.color.stat_fondo));
        props.setPadding(dp(12), dp(10), dp(12), dp(10));
        props.addView(propCol("TIEMPO",   tiempo));
        props.addView(propSep());
        props.addView(propCol("ALCANCE",  alcance));
        props.addView(propSep());
        props.addView(propCol("OBJETIVO", objetivo));
        props.addView(propSep());
        props.addView(propCol("DURACIÓN", duracion));
        card.addView(props);
        card.addView(separadorBorde());

        ScrollView descScroll = new ScrollView(this);
        descScroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(220)));
        TextView descView = new TextView(this);
        descView.setText(descripcion);
        descView.setTextColor(color(R.color.texto));
        descView.setTextSize(13);
        descView.setPadding(dp(16), dp(14), dp(16), dp(14));
        descView.setLineSpacing(dp(3), 1.0f);
        descScroll.addView(descView);
        card.addView(descScroll);
        card.addView(separadorDorado());

        Button cerrar = new Button(this);
        cerrar.setText("✦  CERRAR TARJETA  ✦");
        cerrar.setTextColor(color(R.color.dorado));
        cerrar.setTextSize(12);
        cerrar.setTypeface(Typeface.create("serif", Typeface.BOLD));
        cerrar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color(R.color.boton_bg)));
        LinearLayout.LayoutParams cLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(44));
        cLp.setMargins(dp(16), dp(10), dp(16), dp(16));
        cerrar.setLayoutParams(cLp);
        card.addView(cerrar);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.DialogoOscuro)
                .setView(card).create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        cerrar.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, 150);
        });
        dialog.show();
    }

    // HECHIZOS
    private void agregarSpell(String nombre, String nivel, String escuela,
                              String tiempo, String alcance, String objetivo,
                              String duracion, boolean concentracion, boolean ritual,
                              String descripcion) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, dp(6), 0, dp(6));
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setClickable(true);
        item.setFocusable(true);
        item.setBackground(getDrawable(android.R.drawable.list_selector_background));

        int badgeBg, badgeTexto;
        switch (nivel) {
            case "TRUCO":  badgeBg = R.color.badge_truco;  badgeTexto = R.color.badge_truco_texto;  break;
            case "NIVEL 1":badgeBg = R.color.badge_nivel1; badgeTexto = R.color.badge_nivel1_texto; break;
            case "NIVEL 2":badgeBg = R.color.badge_nivel2; badgeTexto = R.color.badge_nivel2_texto; break;
            default:       badgeBg = R.color.badge_nivel3; badgeTexto = R.color.badge_nivel3_texto; break;
        }

        TextView badgeView = new TextView(this);
        badgeView.setText(nivel);
        badgeView.setTextColor(color(badgeTexto));
        badgeView.setTextSize(9);
        badgeView.setPadding(dp(8), dp(4), dp(8), dp(4));
        badgeView.setBackgroundColor(color(badgeBg));
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bLp.setMargins(0, 0, dp(10), 0);
        badgeView.setLayoutParams(bLp);

        TextView txtNombre = txt(nombre, 13, R.color.texto, false);
        txtNombre.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        TextView arrow = txt("›", 16, R.color.dorado_borde, false);

        item.addView(badgeView); item.addView(txtNombre); item.addView(arrow);

        item.setOnClickListener(v -> {
            // Animación de pulsación en la fila
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    mostrarTarjetaHechizo(nombre, nivel, escuela, tiempo, alcance,
                            objetivo, duracion, concentracion, ritual, descripcion), 150);
        });

        layoutSpells.addView(item);
    }

    // MISIONES
    private void agregarMisionDisponible(String nombre, String descripcion, String recompensa) {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);

        if (layoutDisponibles.getChildCount() > 0) {
            View sep = new View(this);
            sep.setTag("sep");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1);
            lp.setMargins(0, dp(3), 0, dp(3));
            sep.setLayoutParams(lp);
            sep.setBackgroundColor(color(R.color.borde));
            wrapper.addView(sep);
        }

        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, dp(10), 0, dp(10));
        item.setGravity(Gravity.CENTER_VERTICAL);

        item.setTag(wrapper);

        TextView icono = new TextView(this);
        icono.setText("📜");
        icono.setTextSize(22);
        icono.setPadding(0, 0, dp(10), 0);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        info.addView(txt(nombre, 13, R.color.texto, true));
        info.addView(txt(descripcion, 11, R.color.texto_secundario, false));
        info.addView(txt("Recompensa:  " + recompensa, 11, R.color.dorado_claro, false));

        androidx.appcompat.widget.AppCompatButton btn =
                new androidx.appcompat.widget.AppCompatButton(this);
        btn.setText("ACEPTAR");
        btn.setTextSize(10);
        btn.setTextColor(color(R.color.dorado));
        btn.setBackgroundColor(color(R.color.boton_bg));

        btn.setOnClickListener(v -> {
            btn.setEnabled(false);
            btn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(
                    () -> aceptarMisionConAnimacion(nombre, recompensa, wrapper), 120);
        });

        item.addView(icono);
        item.addView(info);
        item.addView(btn);

        wrapper.addView(item);
        layoutDisponibles.addView(wrapper);
    }

    // formato alerts
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
        b.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(d::dismiss, 150);
        });
        d.show();
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

    private View separadorBorde() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        v.setBackgroundColor(color(R.color.borde));
        return v;
    }

    private void agregarSeparador(LinearLayout parent) {
        View sep = new View(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        lp.setMargins(0, dp(3), 0, dp(3));
        sep.setLayoutParams(lp);
        sep.setBackgroundColor(color(R.color.borde));
        parent.addView(sep);
    }
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

        col.addView(et); col.addView(val);
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
}