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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "user_email";

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userEmail = "";
    private String uid = null;

    // Vistas
    LinearLayout layoutDisponibles, layoutActivas, layoutSpells;
    TextView txtResultado, txtTipoDado;
    ImageView imgAvatar;

    // Vistas del personaje
    TextView txtNombrePersonaje, txtRazaPersonaje, txtClasePersonaje, txtAlineacionPersonaje,
            txtPersonajePlaceholder;
    TextView txtCA, txtPG, txtIniciativa;
    TextView txtFUE, txtDES, txtCON, txtINT, txtSAB, txtCAR;
    TextView txtFUEmod, txtDESmod, txtCONmod, txtINTmod, txtSABmod, txtCARmod;
    LinearLayout layoutAvatarPlaceholder, layoutXpBar;
    TextView txtNivelXp, txtXpActual;
    View barraXpFill;
    TextView btnCambiarPersonaje, txtSinMisionesActivas;

    // Launchers
    ActivityResultLauncher<String> pickImageLauncher;
    ActivityResultLauncher<Intent> crearPersonajeLauncher;

    int contador = 0;
    Random random = new Random();
    ArrayList<String> historial = new ArrayList<>();

    // Estado de personajes/misiones
    private final List<PersonajeModel> misPersonajes = new ArrayList<>();
    private PersonajeModel personajeActivo = null; // null = sin personaje creado
    private final Map<String, LinearLayout> vistasMisionesActivas = new HashMap<>();
    private final Map<String, LinearLayout> vistasMisionesDisponibles = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userEmail = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        if (userEmail == null) userEmail = "";

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null) uid = mAuth.getCurrentUser().getUid();

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imgAvatar.setImageURI(uri);
                        imgAvatar.setVisibility(View.VISIBLE);
                        layoutAvatarPlaceholder.setVisibility(View.GONE);
                    }
                }
        );

        crearPersonajeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Recargar lista de personajes desde Firestore (incluye el nuevo)
                        cargarPersonajesDesdeFirestore();
                    }
                }
        );

        setContentView(R.layout.activity_main);

        layoutDisponibles = findViewById(R.id.layoutDisponibles);
        layoutActivas     = findViewById(R.id.layoutActivas);
        layoutSpells      = findViewById(R.id.layoutSpells);
        txtResultado      = findViewById(R.id.txtResultado);
        txtTipoDado       = findViewById(R.id.txtTipoDado);
        imgAvatar         = findViewById(R.id.imgAvatar);
        txtSinMisionesActivas = findViewById(R.id.txtSinMisionesActivas);

        txtNombrePersonaje     = findViewById(R.id.txtNombrePersonaje);
        txtRazaPersonaje       = findViewById(R.id.txtRazaPersonaje);
        txtClasePersonaje      = findViewById(R.id.txtClasePersonaje);
        txtAlineacionPersonaje = findViewById(R.id.txtAlineacionPersonaje);
        txtPersonajePlaceholder= findViewById(R.id.txtPersonajePlaceholder);
        txtCA          = findViewById(R.id.txtCA);
        txtPG          = findViewById(R.id.txtPG);
        txtIniciativa  = findViewById(R.id.txtIniciativa);
        txtFUE = findViewById(R.id.txtFUE); txtFUEmod = findViewById(R.id.txtFUEmod);
        txtDES = findViewById(R.id.txtDES); txtDESmod = findViewById(R.id.txtDESmod);
        txtCON = findViewById(R.id.txtCON); txtCONmod = findViewById(R.id.txtCONmod);
        txtINT = findViewById(R.id.txtINT); txtINTmod = findViewById(R.id.txtINTmod);
        txtSAB = findViewById(R.id.txtSAB); txtSABmod = findViewById(R.id.txtSABmod);
        txtCAR = findViewById(R.id.txtCAR); txtCARmod = findViewById(R.id.txtCARmod);
        layoutAvatarPlaceholder = findViewById(R.id.layoutAvatarPlaceholder);
        layoutXpBar    = findViewById(R.id.layoutXpBar);
        txtNivelXp     = findViewById(R.id.txtNivelXp);
        txtXpActual    = findViewById(R.id.txtXpActual);
        barraXpFill    = findViewById(R.id.barraXpFill);
        btnCambiarPersonaje = findViewById(R.id.btnCambiarPersonaje);

        // Avatar → galería
        imgAvatar.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            pickImageLauncher.launch("image/*");
        });
        layoutAvatarPlaceholder.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Header "TU PERSONAJE" → editar personaje activo o crear uno
        findViewById(R.id.btnHeaderPersonaje).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(this::abrirCrearPersonaje, 150);
        });

        // "✦ NUEVO" → siempre crea uno nuevo
        findViewById(R.id.btnNuevoPersonaje).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(this::abrirCrearPersonaje, 150);
        });

        //"⇄ CAMBIAR" → selector de personajes
        btnCambiarPersonaje.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            mostrarSelectorPersonajes();
        });

        // Tomo Arcano → SpellListActivity
        findViewById(R.id.btnTomoArcano).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(this::abrirGrimorio, 150);
        });

        // Cargar datos persistentes
        cargarPersonajesDesdeFirestore();
        cargarMisionesDesdeFirestore();

        //  Hechizos fijos (con imagen personalizada de Supabase)
        for (SpellModel spell : SpellModel.getHechizosFijos()) {
            agregarSpell(spell);
        }

        // Dados
        configurarBtnDado(R.id.btnD4,  4);
        configurarBtnDado(R.id.btnD6,  6);
        configurarBtnDado(R.id.btnD8,  8);
        configurarBtnDado(R.id.btnD10, 10);
        configurarBtnDado(R.id.btnD12, 12);
        configurarBtnDado(R.id.btnD20, 20);

        findViewById(R.id.btnAleatorio).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            int[] dados = {4, 6, 8, 10, 12, 20};
            tirarConAnimacion(dados[random.nextInt(dados.length)]);
        });

        findViewById(R.id.btnHistorial).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(this::mostrarHistorial, 150);
        });

        findViewById(R.id.btnAgregar).setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            contador++;
            crearMisionEnFirestore("Nueva misión " + contador,
                    "Una misión misteriosa te aguarda.", "50 XP");
        });

        configurarMenu();

        if (!userEmail.isEmpty()) guardarSesionEnFirestore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refrescar por si se editaron datos en otra pantalla
        if (uid != null && personajeActivo != null) {
            // Solo refresca visualmente, no vuelve a pegarle a la red
        }
    }

    private void abrirCrearPersonaje() {
        Intent i = new Intent(this, CrearPersonajeActivity.class);
        i.putExtra(EXTRA_USER_EMAIL, userEmail);
        crearPersonajeLauncher.launch(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void abrirGrimorio() {
        Intent i = new Intent(this, SpellListActivity.class);
        i.putExtra(EXTRA_USER_EMAIL, userEmail);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // PERSONAJES — Firestore: usuarios/{uid}/personajes/{id}

    private void cargarPersonajesDesdeFirestore() {
        if (uid == null) {
            // Sin sesión: no hay persistencia, queda en placeholder
            return;
        }
        db.collection("usuarios").document(uid).collection("personajes")
                .get()
                .addOnSuccessListener(query -> {
                    misPersonajes.clear();
                    PersonajeModel activoEncontrado = null;

                    for (QueryDocumentSnapshot doc : query) {
                        PersonajeModel p = docToPersonaje(doc);
                        misPersonajes.add(p);
                        if (p.activo) activoEncontrado = p;
                    }

                    // Si no hay ninguno marcado como activo, usar el primero
                    if (activoEncontrado == null && !misPersonajes.isEmpty()) {
                        activoEncontrado = misPersonajes.get(0);
                        marcarComoActivo(activoEncontrado);
                    }

                    personajeActivo = activoEncontrado;
                    aplicarPersonajeActivoEnUI();

                    // Mostrar botón "CAMBIAR" solo si hay 2+ personajes
                    btnCambiarPersonaje.setVisibility(
                            misPersonajes.size() > 1 ? View.VISIBLE : View.GONE);
                });
    }

    private PersonajeModel docToPersonaje(DocumentSnapshot doc) {
        PersonajeModel p = new PersonajeModel();
        p.id = doc.getId();
        p.nombre     = doc.getString("nombre");
        p.raza       = doc.getString("raza");
        p.clase      = doc.getString("clase");
        p.alineacion = doc.getString("alineacion");
        Long nivelL  = doc.getLong("nivel");
        p.nivel      = nivelL != null ? nivelL.intValue() : 1;
        Long xpL     = doc.getLong("xp");
        p.xp         = xpL != null ? xpL : 0L;
        p.fue   = safeInt(doc.getLong("fue"), 10);
        p.des   = safeInt(doc.getLong("des"), 10);
        p.con   = safeInt(doc.getLong("con"), 10);
        p.intel = safeInt(doc.getLong("int_"), 10);
        p.sab   = safeInt(doc.getLong("sab"), 10);
        p.car   = safeInt(doc.getLong("car"), 10);
        Boolean activoB = doc.getBoolean("activo");
        p.activo = activoB != null && activoB;
        return p;
    }

    private int safeInt(Long val, int def) {
        return val != null ? val.intValue() : def;
    }

    /** Marca un personaje como activo en Firestore (desmarca los demás). */
    private void marcarComoActivo(PersonajeModel nuevoActivo) {
        if (uid == null) return;
        for (PersonajeModel p : misPersonajes) {
            boolean esEste = p.id.equals(nuevoActivo.id);
            p.activo = esEste;
            db.collection("usuarios").document(uid)
                    .collection("personajes").document(p.id)
                    .update("activo", esEste);
        }
    }

    private void aplicarPersonajeActivoEnUI() {
        if (personajeActivo == null) {
            // Estado placeholder
            txtNombrePersonaje.setText("Nombre");
            txtRazaPersonaje.setText("Especie");
            txtClasePersonaje.setText("Clase");
            txtAlineacionPersonaje.setText("Alineamiento");
            txtNombrePersonaje.setTextColor(color(R.color.texto_secundario));
            txtCA.setText("-"); txtPG.setText("-"); txtIniciativa.setText("-");
            txtCA.setTextColor(color(R.color.texto_secundario));
            txtPG.setTextColor(color(R.color.texto_secundario));
            txtIniciativa.setTextColor(color(R.color.texto_secundario));
            limpiarAtributo(txtFUE, txtFUEmod);
            limpiarAtributo(txtDES, txtDESmod);
            limpiarAtributo(txtCON, txtCONmod);
            limpiarAtributo(txtINT, txtINTmod);
            limpiarAtributo(txtSAB, txtSABmod);
            limpiarAtributo(txtCAR, txtCARmod);
            txtPersonajePlaceholder.setVisibility(View.VISIBLE);
            layoutXpBar.setVisibility(View.GONE);
            return;
        }

        PersonajeModel p = personajeActivo;
        txtNombrePersonaje.setText(p.nombre);
        txtNombrePersonaje.setTextColor(color(R.color.texto));
        txtRazaPersonaje.setText(p.raza);
        txtRazaPersonaje.setTextColor(color(R.color.texto_secundario));
        txtClasePersonaje.setText(p.clase + " Nivel " + p.nivel);
        txtClasePersonaje.setTextColor(color(R.color.texto_secundario));
        txtAlineacionPersonaje.setText(p.alineacion);
        txtAlineacionPersonaje.setTextColor(color(R.color.texto_secundario));

        txtCA.setText(String.valueOf(p.getCaEstimada()));
        txtCA.setTextColor(color(R.color.texto));
        txtPG.setText(String.valueOf(p.getPgEstimados()));
        txtPG.setTextColor(color(R.color.texto));

        // Iniciativa = modificador de Destreza (D&D 5e)
        int iniciativa = p.getIniciativa();
        txtIniciativa.setText(PersonajeModel.formatMod(iniciativa));
        txtIniciativa.setTextColor(color(R.color.texto));

        setAtributo(txtFUE, txtFUEmod, p.fue);
        setAtributo(txtDES, txtDESmod, p.des);
        setAtributo(txtCON, txtCONmod, p.con);
        setAtributo(txtINT, txtINTmod, p.intel);
        setAtributo(txtSAB, txtSABmod, p.sab);
        setAtributo(txtCAR, txtCARmod, p.car);

        txtPersonajePlaceholder.setVisibility(View.GONE);

        // Barra de XP
        actualizarBarraXp(p);
    }

    private void actualizarBarraXp(PersonajeModel p) {
        if (p.nivel >= 20) {
            layoutXpBar.setVisibility(View.VISIBLE);
            txtNivelXp.setText("Nivel 20 (máximo)");
            txtXpActual.setText(p.xp + " XP");
            ((LinearLayout.LayoutParams) barraXpFill.getLayoutParams()).weight = 1f;
            barraXpFill.requestLayout();
            return;
        }
        layoutXpBar.setVisibility(View.VISIBLE);
        long siguienteUmbral = PersonajeModel.xpParaSiguienteNivel(p.nivel);
        txtNivelXp.setText("Nivel " + p.nivel);
        txtXpActual.setText(p.xp + " / " + siguienteUmbral + " XP");

        float progreso = PersonajeModel.progresoNivelActual(p.xp, p.nivel);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) barraXpFill.getLayoutParams();
        lp.weight = Math.max(0.02f, progreso); // mínimo visible
        barraXpFill.setLayoutParams(lp);
    }

    private void limpiarAtributo(TextView txtVal, TextView txtMod) {
        txtVal.setText("-");
        txtVal.setTextColor(color(R.color.texto_secundario));
        txtMod.setText("");
    }

    private void setAtributo(TextView txtVal, TextView txtMod, int valor) {
        txtVal.setText(String.valueOf(valor));
        txtVal.setTextColor(color(R.color.texto));
        int mod = PersonajeModel.modificador(valor);
        txtMod.setText(PersonajeModel.formatMod(mod));
        txtMod.setTextColor(color(R.color.texto_secundario));
    }

    /** Diálogo selector cuando hay múltiples personajes. */
    private void mostrarSelectorPersonajes() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundResource(R.drawable.seccion_bg);
        root.setPadding(dp(16), dp(20), dp(16), dp(16));

        TextView t = txt("✦  TUS PERSONAJES  ✦", 14, R.color.dorado, true);
        t.setTypeface(Typeface.create("serif", Typeface.BOLD));
        t.setGravity(Gravity.CENTER);
        root.addView(t);
        root.addView(separadorDorado());

        ScrollView scroll = new ScrollView(this);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(280)));
        LinearLayout lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        lista.setPadding(0, dp(8), 0, dp(8));

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.DialogoOscuro)
                .setView(root).create();

        for (PersonajeModel p : misPersonajes) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(dp(10), dp(10), dp(10), dp(10));
            item.setBackgroundResource(R.drawable.stat_bg);
            LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            itemLp.setMargins(0, 0, 0, dp(6));
            item.setLayoutParams(itemLp);
            item.setClickable(true);
            item.setFocusable(true);

            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            TextView nom = txt(p.nombre, 14, R.color.texto, true);
            TextView sub = txt(p.raza + " · " + p.clase + " Nv." + p.nivel,
                    11, R.color.texto_secundario, false);
            info.addView(nom);
            info.addView(sub);
            item.addView(info);

            if (p.activo) {
                TextView badge = txt("ACTIVO", 9, R.color.dorado, true);
                badge.setBackgroundColor(color(R.color.boton_bg));
                badge.setPadding(dp(8), dp(4), dp(8), dp(4));
                item.addView(badge);
            }

            item.setOnClickListener(v -> {
                if (!p.activo) {
                    marcarComoActivo(p);
                    personajeActivo = p;
                    aplicarPersonajeActivoEnUI();
                }
                dialog.dismiss();
            });
            lista.addView(item);
        }
        scroll.addView(lista);
        root.addView(scroll);
        root.addView(separadorDorado());

        Button btnNuevo = new Button(this);
        btnNuevo.setText("✦  CREAR NUEVO PERSONAJE");
        btnNuevo.setTextColor(color(R.color.dorado));
        btnNuevo.setTextSize(12);
        btnNuevo.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color(R.color.boton_bg)));
        LinearLayout.LayoutParams bnLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(44));
        bnLp.setMargins(0, dp(12), 0, dp(6));
        btnNuevo.setLayoutParams(bnLp);
        btnNuevo.setOnClickListener(v -> {
            dialog.dismiss();
            abrirCrearPersonaje();
        });
        root.addView(btnNuevo);

        Button btnCerrar = new Button(this);
        btnCerrar.setText("CERRAR");
        btnCerrar.setTextColor(color(R.color.texto_secundario));
        btnCerrar.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color(R.color.boton_bg)));
        btnCerrar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(40)));
        btnCerrar.setOnClickListener(v -> dialog.dismiss());
        root.addView(btnCerrar);

        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    /** Suma XP al personaje activo y aplica level-up automático si corresponde. */
    private void otorgarXpAPersonajeActivo(long xpGanada) {
        if (personajeActivo == null || uid == null) return;

        long xpNueva = personajeActivo.xp + xpGanada;
        int nivelAnterior = personajeActivo.nivel;
        int nivelNuevo = PersonajeModel.calcularNivelPorXp(xpNueva);

        personajeActivo.xp = xpNueva;
        personajeActivo.nivel = nivelNuevo;

        Map<String, Object> updates = new HashMap<>();
        updates.put("xp", xpNueva);
        updates.put("nivel", nivelNuevo);

        db.collection("usuarios").document(uid)
                .collection("personajes").document(personajeActivo.id)
                .update(updates);

        aplicarPersonajeActivoEnUI();

        if (nivelNuevo > nivelAnterior) {
            mostrarDialogoLevelUp(nivelAnterior, nivelNuevo);
        } else {
            mostrarToastXp(xpGanada);
        }
    }

    private void mostrarToastXp(long xpGanada) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundResource(R.drawable.seccion_bg);
        root.setPadding(dp(20), dp(16), dp(20), dp(16));
        TextView t = txt("✦  +" + xpGanada + " XP  ✦", 16, R.color.dorado, true);
        t.setGravity(Gravity.CENTER);
        root.addView(t);
        AlertDialog d = new AlertDialog.Builder(this, R.style.DialogoOscuro).setView(root).create();
        if (d.getWindow() != null) d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.show();
        new Handler(Looper.getMainLooper()).postDelayed(d::dismiss, 1200);
    }

    private void mostrarDialogoLevelUp(int nivelAnterior, int nivelNuevo) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundResource(R.drawable.seccion_bg);
        root.setPadding(dp(20), dp(24), dp(20), dp(20));

        TextView icon = txt("⬆️", 40, R.color.dorado, false);
        icon.setGravity(Gravity.CENTER);
        icon.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(icon);

        TextView t = txt("¡SUBISTE DE NIVEL!", 16, R.color.dorado, true);
        t.setTypeface(Typeface.create("serif", Typeface.BOLD));
        t.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tLp.setMargins(0, dp(8), 0, dp(4));
        t.setLayoutParams(tLp);
        root.addView(t);

        TextView sub = txt("Nivel " + nivelAnterior + "  →  Nivel " + nivelNuevo,
                14, R.color.texto, false);
        sub.setGravity(Gravity.CENTER);
        root.addView(sub);

        root.addView(separadorDorado());

        Button btn = new Button(this);
        btn.setText("¡EXCELENTE!");
        btn.setTextColor(color(R.color.dorado));
        btn.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color(R.color.boton_bg)));
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(44));
        bLp.setMargins(0, dp(12), 0, 0);
        btn.setLayoutParams(bLp);
        root.addView(btn);

        AlertDialog d = new AlertDialog.Builder(this, R.style.DialogoOscuro).setView(root).create();
        if (d.getWindow() != null) d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        btn.setOnClickListener(v -> d.dismiss());
        d.show();
    }

    // MISIONES — Firestore: usuarios/{uid}/misiones/{id}
    // Estados: disponible | activa | completada | cancelada

    private void cargarMisionesDesdeFirestore() {
        if (uid == null) {
            cargarMisionesDefaultLocal();
            return;
        }
        db.collection("usuarios").document(uid).collection("misiones")
                .get()
                .addOnSuccessListener(query -> {
                    if (query.isEmpty()) {
                        crearMisionesDefault();
                        return;
                    }
                    layoutDisponibles.removeAllViews();
                    layoutActivas.removeAllViews();
                    vistasMisionesActivas.clear();
                    vistasMisionesDisponibles.clear();

                    for (QueryDocumentSnapshot doc : query) {
                        MisionModel m = docToMision(doc);
                        if ("disponible".equals(m.estado)) {
                            renderMisionDisponible(m);
                        } else if ("activa".equals(m.estado)) {
                            renderMisionActiva(m);
                        }
                        // completada/cancelada no se muestran en Inicio
                    }
                    actualizarVisibilidadSinMisiones();
                });
    }

    private MisionModel docToMision(DocumentSnapshot doc) {
        MisionModel m = new MisionModel();
        m.id = doc.getId();
        m.nombre = doc.getString("nombre");
        m.descripcion = doc.getString("descripcion");
        m.recompensa = doc.getString("recompensa");
        Long xpL = doc.getLong("xpRecompensa");
        m.xpRecompensa = xpL != null ? xpL : MisionModel.extraerXp(m.recompensa);
        m.estado = doc.getString("estado");
        if (m.estado == null) m.estado = "disponible";
        return m;
    }

    private void crearMisionesDefault() {
        crearMisionEnFirestore("Entrega especial",
                "Entrega un paquete sellado al Gremio de Comerciantes.", "100 XP");
        crearMisionEnFirestore("Caza de bandidos",
                "Elimina a los bandidos del camino del norte.", "200 XP");
    }

    private void cargarMisionesDefaultLocal() {
        // Sin sesión: solo visual, no persiste
        MisionModel m1 = new MisionModel("Entrega especial",
                "Entrega un paquete sellado al Gremio de Comerciantes.", "100 XP", 100, "disponible");
        m1.id = "local_1";
        MisionModel m2 = new MisionModel("Caza de bandidos",
                "Elimina a los bandidos del camino del norte.", "200 XP", 200, "disponible");
        m2.id = "local_2";
        renderMisionDisponible(m1);
        renderMisionDisponible(m2);
        actualizarVisibilidadSinMisiones();
    }

    private void crearMisionEnFirestore(String nombre, String descripcion, String recompensa) {
        long xp = MisionModel.extraerXp(recompensa);
        if (uid == null) {
            MisionModel m = new MisionModel(nombre, descripcion, recompensa, xp, "disponible");
            m.id = "local_" + System.currentTimeMillis();
            renderMisionDisponible(m);
            actualizarVisibilidadSinMisiones();
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", nombre);
        data.put("descripcion", descripcion);
        data.put("recompensa", recompensa);
        data.put("xpRecompensa", xp);
        data.put("estado", "disponible");
        data.put("creadaEn", com.google.firebase.Timestamp.now());

        db.collection("usuarios").document(uid).collection("misiones")
                .add(data)
                .addOnSuccessListener(ref -> {
                    MisionModel m = new MisionModel(nombre, descripcion, recompensa, xp, "disponible");
                    m.id = ref.getId();
                    renderMisionDisponible(m);
                    actualizarVisibilidadSinMisiones();
                });
    }

    /** Cambia el estado de una misión en Firestore. */
    private void actualizarEstadoMision(String misionId, String nuevoEstado) {
        if (uid == null || misionId.startsWith("local_")) return; // sin persistencia
        db.collection("usuarios").document(uid).collection("misiones")
                .document(misionId)
                .update("estado", nuevoEstado);
    }

    // Render: Misión disponible (con botón ACEPTAR)
    private void renderMisionDisponible(MisionModel m) {
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

        TextView icono = new TextView(this);
        icono.setText("📜"); icono.setTextSize(22);
        icono.setPadding(0, 0, dp(10), 0);

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        info.addView(txt(m.nombre, 13, R.color.texto, true));
        info.addView(txt(m.descripcion, 11, R.color.texto_secundario, false));
        info.addView(txt("Recompensa:  " + m.recompensa, 11, R.color.dorado_claro, false));

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
                    () -> aceptarMision(m, wrapper), 120);
        });

        item.addView(icono); item.addView(info); item.addView(btn);
        wrapper.addView(item);
        layoutDisponibles.addView(wrapper);
        vistasMisionesDisponibles.put(m.id, wrapper);
    }

    private void aceptarMision(MisionModel m, LinearLayout wrapper) {
        if (wrapper.getParent() == null) return;
        if (Boolean.TRUE.equals(wrapper.getTag())) return;
        wrapper.setTag(true);

        actualizarEstadoMision(m.id, "activa");

        wrapper.animate()
                .alpha(0f).translationX(200f).setDuration(250)
                .withEndAction(() -> {
                    wrapper.clearAnimation();
                    wrapper.post(() -> {
                        if (wrapper.getParent() != null) layoutDisponibles.removeView(wrapper);
                        vistasMisionesDisponibles.remove(m.id);
                        if (layoutDisponibles.getChildCount() > 0) {
                            View primer = layoutDisponibles.getChildAt(0);
                            if (primer instanceof LinearLayout) {
                                LinearLayout pw = (LinearLayout) primer;
                                if (pw.getChildCount() > 0 && "sep".equals(pw.getChildAt(0).getTag())) {
                                    pw.removeViewAt(0);
                                }
                            }
                        }
                        m.estado = "activa";
                        renderMisionActivaAnimada(m);
                        actualizarVisibilidadSinMisiones();
                    });
                }).start();
    }

    // Render: Misión activa (con botones COMPLETAR / CANCELAR)
    private void renderMisionActivaAnimada(MisionModel m) {
        if (layoutActivas.getChildCount() > 0) agregarSeparador(layoutActivas);
        LinearLayout item = buildItemMisionActiva(m);
        layoutActivas.addView(item);
        vistasMisionesActivas.put(m.id, item);
        item.setAlpha(0f);
        item.setTranslationX(-200f);
        item.animate().alpha(1f).translationX(0f).setDuration(250).start();
    }

    private void renderMisionActiva(MisionModel m) {
        if (layoutActivas.getChildCount() > 0) agregarSeparador(layoutActivas);
        LinearLayout item = buildItemMisionActiva(m);
        layoutActivas.addView(item);
        vistasMisionesActivas.put(m.id, item);
    }

    private LinearLayout buildItemMisionActiva(MisionModel m) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(0, dp(10), 0, dp(10));

        LinearLayout filaInfo = new LinearLayout(this);
        filaInfo.setOrientation(LinearLayout.HORIZONTAL);
        filaInfo.setGravity(Gravity.CENTER_VERTICAL);

        TextView icono = new TextView(this);
        icono.setText("⚔️"); icono.setTextSize(22);
        icono.setPadding(0, 0, dp(10), 0);
        icono.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        info.addView(txt(m.nombre, 13, R.color.texto, true));
        info.addView(txt("Recompensa:  " + m.recompensa, 11, R.color.dorado_claro, false));

        TextView badge = new TextView(this);
        badge.setText("EN CURSO");
        badge.setTextColor(color(R.color.dorado));
        badge.setTextSize(9);
        badge.setPadding(dp(8), dp(4), dp(8), dp(4));
        badge.setBackgroundColor(color(R.color.boton_bg));

        filaInfo.addView(icono); filaInfo.addView(info); filaInfo.addView(badge);
        item.addView(filaInfo);

        // Fila de botones: COMPLETAR / CANCELAR
        LinearLayout filaBtns = new LinearLayout(this);
        filaBtns.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams filaBtnsLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        filaBtnsLp.setMargins(0, dp(8), 0, 0);
        filaBtns.setLayoutParams(filaBtnsLp);

        Button btnCompletar = new Button(this);
        btnCompletar.setText("✓  COMPLETAR");
        btnCompletar.setTextSize(10);
        btnCompletar.setTextColor(color(R.color.dorado));
        btnCompletar.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color(R.color.boton_bg)));
        LinearLayout.LayoutParams compLp = new LinearLayout.LayoutParams(
                0, dp(34), 1);
        compLp.setMargins(0, 0, dp(4), 0);
        btnCompletar.setLayoutParams(compLp);

        Button btnCancelar = new Button(this);
        btnCancelar.setText("✕  CANCELAR");
        btnCancelar.setTextSize(10);
        btnCancelar.setTextColor(0xFFC05050);
        btnCancelar.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color(R.color.boton_bg)));
        LinearLayout.LayoutParams cancLp = new LinearLayout.LayoutParams(
                0, dp(34), 1);
        cancLp.setMargins(dp(4), 0, 0, 0);
        btnCancelar.setLayoutParams(cancLp);

        btnCompletar.setOnClickListener(v -> {
            btnCompletar.setEnabled(false);
            btnCancelar.setEnabled(false);
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(
                    () -> completarMision(m, item), 120);
        });

        btnCancelar.setOnClickListener(v -> {
            btnCompletar.setEnabled(false);
            btnCancelar.setEnabled(false);
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(
                    () -> cancelarMision(m, item), 120);
        });

        filaBtns.addView(btnCompletar);
        filaBtns.addView(btnCancelar);
        item.addView(filaBtns);

        return item;
    }

    private void completarMision(MisionModel m, LinearLayout item) {
        actualizarEstadoMision(m.id, "completada");
        removerMisionActivaConAnimacion(item, () -> {
            // Otorgar XP al personaje activo
            if (personajeActivo != null) {
                otorgarXpAPersonajeActivo(m.xpRecompensa);
            }
        });
    }

    private void cancelarMision(MisionModel m, LinearLayout item) {
        actualizarEstadoMision(m.id, "cancelada");
        removerMisionActivaConAnimacion(item, null);
    }

    private void removerMisionActivaConAnimacion(LinearLayout item, Runnable alFinalizar) {
        item.animate()
                .alpha(0f).translationX(200f).setDuration(250)
                .withEndAction(() -> {
                    item.clearAnimation();
                    item.post(() -> {
                        if (item.getParent() != null) layoutActivas.removeView(item);
                        if (layoutActivas.getChildCount() > 0) {
                            View primer = layoutActivas.getChildAt(0);
                            // Si el primer hijo es un separador suelto, quitarlo
                        }
                        actualizarVisibilidadSinMisiones();
                        if (alFinalizar != null) alFinalizar.run();
                    });
                }).start();
    }

    private void actualizarVisibilidadSinMisiones() {
        txtSinMisionesActivas.setVisibility(
                layoutActivas.getChildCount() == 0 ? View.VISIBLE : View.GONE);
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

    // HECHIZOS
    private void agregarSpell(SpellModel spell) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, dp(6), 0, dp(6));
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setClickable(true);
        item.setFocusable(true);
        item.setBackground(getDrawable(android.R.drawable.list_selector_background));

        int[] badgeColors = getBadgeColors(spell.levelInt);
        TextView badgeView = new TextView(this);
        badgeView.setText(spell.getBadgeLabel());
        badgeView.setTextColor(badgeColors[1]);
        badgeView.setTextSize(9);
        badgeView.setPadding(dp(8), dp(4), dp(8), dp(4));
        badgeView.setBackgroundColor(badgeColors[0]);
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bLp.setMargins(0, 0, dp(10), 0);
        badgeView.setLayoutParams(bLp);

        TextView txtNombreSpell = txt(spell.name, 13, R.color.texto, false);
        txtNombreSpell.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        TextView arrow = txt("›", 16, R.color.dorado_borde, false);

        item.addView(badgeView);
        item.addView(txtNombreSpell);
        item.addView(arrow);

        item.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press));
            new Handler(Looper.getMainLooper()).postDelayed(
                    () -> abrirDetalleHechizo(spell), 150);
        });

        layoutSpells.addView(item);
    }

    private void abrirDetalleHechizo(SpellModel spell) {
        Intent intent = new Intent(this, DetalleHechizo.class);
        intent.putExtra(DetalleHechizo.EXTRA_SLUG,         spell.slug);
        intent.putExtra(DetalleHechizo.EXTRA_NAME,         spell.name);
        intent.putExtra(DetalleHechizo.EXTRA_DESC,         spell.desc);
        intent.putExtra(DetalleHechizo.EXTRA_HIGHER_LEVEL, spell.higherLevel);
        intent.putExtra(DetalleHechizo.EXTRA_RANGE,        spell.range);
        intent.putExtra(DetalleHechizo.EXTRA_COMPONENTS,   spell.components);
        intent.putExtra(DetalleHechizo.EXTRA_MATERIAL,     spell.material);
        intent.putExtra(DetalleHechizo.EXTRA_CASTING_TIME, spell.castingTime);
        intent.putExtra(DetalleHechizo.EXTRA_LEVEL,        spell.level);
        intent.putExtra(DetalleHechizo.EXTRA_LEVEL_INT,    spell.levelInt);
        intent.putExtra(DetalleHechizo.EXTRA_SCHOOL,       spell.school);
        intent.putExtra(DetalleHechizo.EXTRA_DURATION,     spell.duration);
        intent.putExtra(DetalleHechizo.EXTRA_CONCENTRATION,spell.requiresConcentration);
        intent.putExtra(DetalleHechizo.EXTRA_RITUAL,       spell.canBeCastAsRitual);
        intent.putExtra(DetalleHechizo.EXTRA_DND_CLASS,    spell.dndClass);
        intent.putExtra(DetalleHechizo.EXTRA_IMAGEN_URL,   spell.imagenUrl);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private int[] getBadgeColors(int levelInt) {
        switch (levelInt) {
            case 0:  return new int[]{color(R.color.badge_truco),  color(R.color.badge_truco_texto)};
            case 1:  return new int[]{color(R.color.badge_nivel1), color(R.color.badge_nivel1_texto)};
            case 2:  return new int[]{color(R.color.badge_nivel2), color(R.color.badge_nivel2_texto)};
            default: return new int[]{color(R.color.badge_nivel3), color(R.color.badge_nivel3_texto)};
        }
    }


    // FIREBASE: sesión

    private void guardarSesionEnFirestore() {
        if (uid == null) return;
        Map<String, Object> datos = new HashMap<>();
        datos.put("email", userEmail);
        datos.put("ultimaConexion", com.google.firebase.Timestamp.now());
        db.collection("usuarios").document(uid).set(datos,
                com.google.firebase.firestore.SetOptions.merge());
    }


    // MENÚ
    private void configurarMenu() {
        findViewById(R.id.menuInicio).setOnClickListener(v ->
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_press)));

        findViewById(R.id.menuInventario).setOnClickListener(v ->
                pulsarYEjecutar(v, () -> {
                    Intent i = new Intent(this, InventarioActivity.class);
                    i.putExtra(EXTRA_USER_EMAIL, userEmail);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }));

        findViewById(R.id.menuNotas).setOnClickListener(v ->
                pulsarYEjecutar(v, () -> {
                    Intent i = new Intent(this, NotasActivity.class);
                    i.putExtra(EXTRA_USER_EMAIL, userEmail);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }));

        findViewById(R.id.menuAjustes).setOnClickListener(v ->
                pulsarYEjecutar(v, this::mostrarDialogoAjustes));
    }

    private void mostrarDialogoAjustes() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundResource(R.drawable.seccion_bg);
        root.setPadding(dp(20), dp(20), dp(20), dp(20));

        TextView t = txt("✦  AJUSTES  ✦", 14, R.color.dorado, true);
        t.setTypeface(Typeface.create("serif", Typeface.BOLD));
        t.setGravity(Gravity.CENTER);
        root.addView(t);
        root.addView(separadorDorado());

        if (!userEmail.isEmpty()) {
            TextView tvEmail = new TextView(this);
            tvEmail.setText("Sesión iniciada como:\n" + userEmail);
            tvEmail.setTextColor(color(R.color.texto_secundario));
            tvEmail.setTextSize(12);
            tvEmail.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, dp(12), 0, dp(4));
            tvEmail.setLayoutParams(lp);
            root.addView(tvEmail);
        }

        TextView m = txt("⚙️  Más ajustes estarán\ndisponibles en la próxima versión.",
                13, R.color.texto, false);
        m.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams mLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mLp.setMargins(0, dp(8), 0, dp(12));
        m.setLayoutParams(mLp);
        root.addView(m);
        root.addView(separadorDorado());

        Button btnCerrar = new Button(this);
        btnCerrar.setText("⚔  CERRAR SESIÓN");
        btnCerrar.setTextColor(0xFFE53935);
        btnCerrar.setTextSize(11);
        btnCerrar.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color(R.color.boton_bg)));
        LinearLayout.LayoutParams cLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(42));
        cLp.setMargins(0, dp(10), 0, dp(6));
        btnCerrar.setLayoutParams(cLp);
        root.addView(btnCerrar);

        Button btnOk = new Button(this);
        btnOk.setText("ACEPTAR");
        btnOk.setTextColor(color(R.color.dorado));
        btnOk.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color(R.color.boton_bg)));
        btnOk.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(42)));
        root.addView(btnOk);

        AlertDialog d = new AlertDialog.Builder(this, R.style.DialogoOscuro)
                .setView(root).create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        btnOk.setOnClickListener(v -> d.dismiss());
        btnCerrar.setOnClickListener(v -> {
            d.dismiss();
            mAuth.signOut();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        d.show();
    }

    // HISTORIAL DE DADOS
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
        TextView e1 = txt("✦", 14, R.color.dorado, false); e1.setPadding(0, 0, dp(8), 0);
        TextView titulo = txt("HISTORIAL DE TIRADAS", 14, R.color.dorado, true);
        titulo.setTypeface(Typeface.create("serif", Typeface.BOLD));
        TextView e2 = txt("✦", 14, R.color.dorado, false); e2.setPadding(dp(8), 0, 0, 0);
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
                spacer.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

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
        cerrar.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(color(R.color.boton_bg)));
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

    // DADOS

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
        final int DURACION = 700, INTERVALO = 80, FLASHES = DURACION / INTERVALO;
        txtResultado.setText("?");
        txtTipoDado.setText("d" + caras);

        ObjectAnimator rotAnim = ObjectAnimator.ofFloat(txtResultado, "rotation", 0f, 360f);
        rotAnim.setDuration(DURACION);
        rotAnim.start();

        Handler handler = new Handler(Looper.getMainLooper());
        for (int i = 0; i < FLASHES; i++) {
            final int idx = i;
            handler.postDelayed(() -> {
                txtResultado.setText(String.valueOf(random.nextInt(caras) + 1));
                txtResultado.setTextColor(idx % 2 == 0
                        ? color(R.color.dorado) : color(R.color.texto));
            }, idx * INTERVALO);
        }

        handler.postDelayed(() -> {
            historial.add(0, "d" + caras + "|" + resultado);
            if (historial.size() > 20) historial.remove(historial.size() - 1);

            int colorFinal;
            String textoTipo;
            if (caras == 20 && resultado == 1) {
                colorFinal = Color.parseColor("#C05050");
                textoTipo = "(d20)  💀";
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
            scaleX.setDuration(400); scaleY.setDuration(400);
            scaleX.setInterpolator(new OvershootInterpolator(2.5f));
            scaleY.setInterpolator(new OvershootInterpolator(2.5f));
            scaleX.start(); scaleY.start();

            if (caras == 20 && resultado == 20) {
                ObjectAnimator pulso = ObjectAnimator.ofFloat(txtResultado, "alpha", 1f, 0.3f, 1f);
                pulso.setDuration(300); pulso.setRepeatCount(3); pulso.setStartDelay(400);
                pulso.start();
            }
        }, DURACION + 50);
    }


    // UTILITIES

    private void pulsarYEjecutar(View v, Runnable accion) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.btn_press);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation a) {}
            @Override public void onAnimationRepeat(Animation a) {}
            @Override public void onAnimationEnd(Animation a) { accion.run(); }
        });
        v.startAnimation(anim);
    }

    private View separadorDorado() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        v.setBackgroundColor(color(R.color.dorado_borde));
        return v;
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
}