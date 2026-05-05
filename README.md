# D&D Companion

> Aplicación móvil Android para Dungeons & Dragons 5ª Edición  
> Parcial 1 · Desarrollo de Aplicaciones Móviles · ACN4A

---

## 📋 Informe de Pantallas

El informe describe las 3 pantallas de la aplicación, sus funcionalidades esperadas y el flujo de uso de cada una.

**[→ Ver Informe Interactivo](https://rawcdn.githack.com/Krayxzlim/parcial-1-am-acn4a-aufieri_luciano/f221c9c7c4f72d5f843da67e2c0e0f9414639e0e/informe_dnd_companion.html)**

> Si el link no abre, descargá el archivo [`informe_dnd_companion.html`](./informe_dnd_companion.html) haciendo clic derecho → *Guardar como* y abrilo en tu navegador.

---

## 📱 Pantallas

| # | Pantalla | Activity | Descripción |
|---|----------|----------|-------------|
| 1 | **Inicio** | `MainActivity` | Personaje, misiones, hechizos y tirada de dados |
| 2 | **Inventario** | `InventarioActivity` | Carga, monedas y objetos del personaje |
| 3 | **Notas** | `NotasActivity` | Diario de campaña, NPCs y nota rápida |

---

## ✦ Funcionalidades implementadas

### Pantalla 1 — Inicio
- Ficha del personaje con avatar seleccionable desde galería
- Misiones disponibles con botón ACEPTAR — animación de salida + entrada con `ViewPropertyAnimator`
- Misiones activas generadas dinámicamente por código Java
- Hechizos conocidos con spellcard en `AlertDialog` personalizado
- Tirador de dados (d4, d6, d8, d10, d12, d20) con animación de ruleta (`ObjectAnimator` rotación 360° + flashes)
- Efectos especiales para 20 natural (pulso dorado) y pifia (rojo 💀)
- Historial de las últimas 15 tiradas en diálogo

### Pantalla 2 — Inventario
- Barra de carga proporcional (peso actual / máximo)
- Monedas D&D 5e: PP, PO, PE, PC
- Lista de objetos con tipo, peso y propiedades
- Badge de equipado (`Eq.`) y cantidad para consumibles (`×N`)
- Botón ＋ para agregar objetos dinámicamente

### Pantalla 3 — Notas
- Diario de aventuras con entradas por sesión
- Registro de NPCs con indicador de relación por color (aliado / antagonista / neutral)
- Nota rápida con `EditText` multilinea y confirmación via `Toast`

---

## 🗂 Estructura del proyecto

```
app/src/main/
├── java/com/miapp/dndcompanion/
│   ├── MainActivity.java
│   ├── InventarioActivity.java
│   └── NotasActivity.java
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
```

---

## 🛠 Tecnologías

| Elemento | Tecnología |
|----------|-----------|
| Lenguaje | Java |
| Layout raíz | `ConstraintLayout` |
| Listas dinámicas | `LinearLayout` + `addView()` |
| Animaciones misiones | `ViewPropertyAnimator` |
| Animaciones dados | `ObjectAnimator`, `OvershootInterpolator` |
| Animaciones botones | `Animation` XML (`btn_press.xml`) |
| Diálogos | `AlertDialog` con vista personalizada |
| Galería | `ActivityResultLauncher` + `GetContent` |
| Notificaciones | `Toast` |
| Navegación | `startActivity` + `overridePendingTransition` |

---

## 👤 Integrante

| Apellido | Usuario GitHub |
|----------|----------------|
| Aufieri Luciano | [@Krayxzlim](https://github.com/Krayxzlim) |

---

<p align="center">
  <sub>✦ &nbsp; D&D Companion · ACN4A · 2025 &nbsp; ✦</sub>
</p>
