# D&D Companion

> Aplicación móvil Android para Dungeons & Dragons 5ª Edición
> Parcial 2 · Desarrollo de Aplicaciones Móviles · ACN4A

---

## 📋 Informe de Pantallas

El informe describe las pantallas de la aplicación, sus funcionalidades esperadas y el flujo de uso de cada una.

**[→ Ver Informe Interactivo](https://rawcdn.githack.com/Krayxzlim/parcial-1-am-acn4a-aufieri_luciano/main/informe_dnd_companion.html)**

> Si el link no abre, descargá el archivo [`informe_dnd_companion.html`](./informe_dnd_companion.html) haciendo clic derecho → *Guardar como* y abrilo en tu navegador.

---

## 📱 Pantallas

| # | Pantalla | Activity | Descripción |
|---|----------|----------|-------------|
| 0 | **Login** | `LoginActivity` | Autenticación con Firebase Auth (email/contraseña) |
| 1 | **Inicio** | `MainActivity` | Personaje activo, misiones, hechizos y tirada de dados |
| 2 | **Crear Personaje** | `CrearPersonajeActivity` | Creación de personaje con razas desde la Open5e API |
| 3 | **Grimorio** | `SpellListActivity` | Listado completo de hechizos desde la Open5e API con filtros |
| 4 | **Detalle de Hechizo** | `DetalleHechizo` | Ficha completa de un hechizo con imagen descargada por URL |
| 5 | **Inventario** | `InventarioActivity` | Carga, monedas y objetos del personaje |
| 6 | **Notas** | `NotasActivity` | Diario de campaña, NPCs y nota rápida |

---

## ✦ Funcionalidades implementadas

### Pantalla 0 — Login
- Autenticación con **Firebase Authentication** (registro e inicio de sesión por email/contraseña)
- Redirección automática a `MainActivity` si ya existe una sesión activa
- Pasaje del email del usuario a `MainActivity` vía **Intent extras**

### Pantalla 1 — Inicio
- Ficha del **personaje activo** con avatar seleccionable desde galería
- Placeholder visual ("Nombre", "Especie", "Clase", "Alineamiento") cuando no hay personaje creado
- Selector **"⇄ CAMBIAR"** para alternar entre múltiples personajes guardados
- Botón **"✦ NUEVO"** para crear un personaje adicional en cualquier momento
- Barra de progreso de XP con nivel actual y XP necesaria para el siguiente nivel
- Atributos (FUE, DES, CON, INT, SAB, CAR) con modificador calculado según reglas de D&D 5e
- **Iniciativa** calculada como el modificador de Destreza, según D&D 5e
- CA y PG estimados a partir de Destreza/Constitución y nivel
- Misiones disponibles con botón ACEPTAR — animación de salida + entrada con `ViewPropertyAnimator`
- Misiones activas con botones **COMPLETAR** y **CANCELAR**
- Al completar una misión, la recompensa en XP se suma automáticamente al personaje activo, con **subida de nivel automática** según la tabla oficial de XP de D&D 5e
- Diálogo de "¡Subiste de nivel!" cuando se cruza un umbral de experiencia
- Seis hechizos fijos con imagen personalizada alojada en Supabase
- **Tomo Arcano** — acceso directo al grimorio completo (`SpellListActivity`)
- Tirador de dados (d4, d6, d8, d10, d12, d20) con animación de ruleta (`ObjectAnimator` rotación 360° + flashes)
- Efectos especiales para 20 natural (pulso dorado) y pifia (rojo 💀)
- Historial de las últimas 15 tiradas en diálogo

### Pantalla 2 — Crear Personaje
- Selector de **raza** poblado dinámicamente desde la **Open5e API** (`/v1/races/`)
- Información de raza (bonificadores de atributo, tamaño, velocidad, idiomas) mostrada al elegir
- Selector de clase (12 clases del SRD) y alineación (9 alineamientos)
- Selector de nivel inicial (1–20) con XP inicial calculada automáticamente
- Editor de atributos base con botones −/+ (rango 3–20) y modificador en vivo
- Persistencia completa en **Firestore**: `usuarios/{uid}/personajes/{id}`
- El primer personaje creado por un usuario se marca automáticamente como **activo**

### Pantalla 3 — Grimorio (lista de hechizos)
- Carga de hasta 300 hechizos desde la **Open5e API** (`/v1/spells/`), paginando automáticamente
- Filtro por **nombre** (búsqueda en tiempo real)
- Filtro por **nivel** (Truco a Nivel 9)
- Filtro por **escuela de magia** (Abjuración, Evocación, Necromancia, etc.)
- Filtro por **clase** (bardo, clérigo, druida, mago, etc.)
- Los cuatro filtros se combinan simultáneamente
- Tap en un hechizo → `DetalleHechizo` con todos los datos vía Intent extras

### Pantalla 4 — Detalle de Hechizo
- Recibe 16 campos vía **Intent extras**, alineados con los nombres de la Open5e API
- Descarga de imagen del hechizo desde URL con **Glide** (placeholder + crossfade)
- Los 6 hechizos fijos muestran su imagen personalizada de Supabase; el resto de la API muestra placeholder
- Tabla de propiedades: tiempo de lanzamiento, alcance, duración, componentes
- Sección de materiales (si el hechizo los requiere)
- Sección "En niveles superiores" cuando aplica

### Pantalla 5 — Inventario
- Barra de carga proporcional (peso actual / máximo)
- Monedas D&D 5e: PP, PO, PE, PC
- Lista de objetos con tipo, peso y propiedades
- Badge de equipado (`Eq.`) y cantidad para consumibles (`×N`)
- Botón ＋ para agregar objetos dinámicamente

### Pantalla 6 — Notas
- Diario de aventuras con entradas por sesión
- Registro de NPCs con indicador de relación por color (aliado / antagonista / neutral)
- Nota rápida con `EditText` multilinea y confirmación vía `Toast`

---

## 🗂 Estructura del proyecto

```
app/src/main/
├── java/com/miapp/dndcompanion/
│   ├── LoginActivity.java
│   ├── MainActivity.java
│   ├── CrearPersonajeActivity.java
│   ├── SpellListActivity.java
│   ├── DetalleHechizo.java
│   ├── InventarioActivity.java
│   ├── NotasActivity.java
│   ├── SpellModel.java
│   ├── PersonajeModel.java
│   └── MisionModel.java
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   ├── activity_inventario.xml
│   │   └── activity_notas.xml
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   └── dimens.xml
│   ├── anim/
│   │   ├── btn_press.xml
│   │   ├── dado_roll.xml
│   │   ├── mision_aceptar.xml
│   │   └── mision_entrar.xml
│   └── drawable/
│       ├── seccion_bg.xml
│       ├── stat_bg.xml
│       ├── esquina.xml
│       ├── shield_bg.xml
│       ├── heart_bg.xml
│       ├── light_bg.xml
│       ├── avatar_bg.xml
│       └── tomo_bg.xml
└── google-services.json
```

---

## 🔥 Modelo de datos en Firestore

```
usuarios/{uid}
│  email
│  ultimaConexion
│
├── personajes/{id}
│     nombre, raza, clase, alineacion
│     nivel, xp
│     fue, des, con, int_, sab, car
│     activo (boolean)
│     creadoEn
│
└── misiones/{id}
      nombre, descripcion, recompensa
      xpRecompensa
      estado: "disponible" | "activa" | "completada" | "cancelada"
      creadaEn
```

---

## 🌐 Integración con Open5e API

| Recurso | Endpoint | Uso |
|---------|----------|-----|
| Hechizos | `https://api.open5e.com/v1/spells/` | Grimorio completo, con filtros por nivel/escuela/clase |
| Razas | `https://api.open5e.com/v1/races/` | Selector de raza en Crear Personaje |

Las llamadas se realizan en un `ExecutorService` (hilo de fondo) y los resultados se aplican en el hilo principal vía `Handler(Looper.getMainLooper())`. Las respuestas se parsean con `org.json` (sin librerías externas de networking).

---

## 🛠 Tecnologías

| Elemento | Tecnología |
|----------|-----------|
| Lenguaje | Java |
| Layout raíz | `ConstraintLayout` |
| Listas dinámicas | `LinearLayout` + `addView()` |
| Autenticación | `Firebase Authentication` (email/contraseña) |
| Base de datos | `Firebase Firestore` (personajes y misiones persistentes) |
| Descarga de imágenes | `Glide 4.16` |
| Consumo de API REST | `HttpURLConnection` + `org.json`, en `ExecutorService` |
| Pasaje de datos | `Intent` extras entre todas las Activities |
| Animaciones misiones | `ViewPropertyAnimator` |
| Animaciones dados | `ObjectAnimator`, `OvershootInterpolator` |
| Animaciones botones | `Animation` XML (`btn_press.xml`) |
| Diálogos | `AlertDialog` con vista personalizada |
| Galería | `ActivityResultLauncher` + `GetContent` |
| Notificaciones | `Toast` |
| Navegación | `startActivity` + `overridePendingTransition` |

---

## 📊 Sistema de experiencia y nivel (D&D 5e)

La subida de nivel utiliza la tabla oficial del *Player's Handbook*:

| Nivel | XP requerida | Nivel | XP requerida |
|-------|-------------|-------|---------------|
| 1 | 0 | 11 | 85.000 |
| 2 | 300 | 12 | 100.000 |
| 3 | 900 | 13 | 120.000 |
| 4 | 2.700 | 14 | 140.000 |
| 5 | 6.500 | 15 | 165.000 |
| 6 | 14.000 | 16 | 195.000 |
| 7 | 23.000 | 17 | 225.000 |
| 8 | 34.000 | 18 | 265.000 |
| 9 | 48.000 | 19 | 305.000 |
| 10 | 64.000 | 20 | 355.000 |

Al completar una misión, el XP de la recompensa se suma al personaje activo (`PersonajeModel.calcularNivelPorXp()`), y si se cruza un umbral, el nivel se actualiza automáticamente en Firestore junto con un diálogo de celebración en la app.

---

## 👤 Integrante

| Apellido | Usuario GitHub |
|----------|----------------|
| Aufieri Luciano | [@Krayxzlim](https://github.com/Krayxzlim) |

---

<p align="center">
  <sub>✦ &nbsp; D&D Companion · ACN4A · 2025 &nbsp; ✦</sub>
</p>
