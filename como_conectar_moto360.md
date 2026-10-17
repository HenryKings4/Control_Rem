# Guía de Conexión: Moto 360 Gen 1 (Depuración por Bluetooth)

El Moto 360 de 1.ª generación no cuenta con Wi-Fi, por lo que la única forma de probar aplicaciones desde Android Studio es creando un puente (túnel) de depuración a través de la conexión Bluetooth de tu celular.

Esta conexión es famosa por ser inestable. Si "Host" aparece desconectado o el comando `adb connect` falla, sigue estrictamente estos pasos para solucionarlo.

---

## Requisitos Previos

1. **Celular conectado por USB** a la computadora.
2. **Depuración USB activada** en el celular.
3. **App Wear OS** instalada en el celular.

---

## Paso 1: Configurar el Reloj (Moto 360)

1. Toca la pantalla para encender el reloj.
2. Desliza y entra a **Configuración** (Ajustes).
3. Ve a **Opciones de desarrollador** (Si no las tienes, ve a *Acerca de* y toca el *Número de compilación* 7 veces).
4. Asegúrate de tener activas estas dos opciones:
   - **Depuración ADB** (ON)
   - **Depuración por Bluetooth** (ON)

---

## Paso 2: Configurar el Celular

1. Abre la app **Wear OS**.
2. Toca el ícono del **Engranaje** (Configuración) en la parte superior derecha.
3. Desliza hacia abajo y busca **Depuración por Bluetooth**.
4. Debes ver algo similar a esto:
   - `Host: Desconectado` (Cambiará a conectado al hacer el puente).
   - `Target: Conectado` (Si dice desconectado, toca la pantalla del reloj para despertarlo).

---

## Paso 3: Crear el Puente ADB desde la Computadora

Abre la terminal (PowerShell o CMD) en Android Studio y ejecuta estos comandos. *Asegúrate de que la pantalla del Moto 360 esté encendida.*

1. **Reiniciar el servidor ADB** (Limpia conexiones fantasma):
   ```powershell
   adb kill-server
   ```
   *(Nota: Si "adb" no está en tu PATH, usa la ruta completa: `& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" kill-server`)*

2. **Crear el puente de red hacia el Bluetooth:**
   ```powershell
   adb forward tcp:4444 tcp:4444
   ```

3. **Conectar la PC al reloj:**
   ```powershell
   adb connect 127.0.0.1:4444
   ```

4. **¡IMPORTANTE!** Inmediatamente después de lanzar el comando anterior, mira tu Moto 360. Si aparece una ventana que dice **"¿Permitir depuración ADB?"** (`Allow ADB debugging?`), acepta y selecciona "Permitir siempre desde esta computadora".

5. **Verificar la conexión:**
   ```powershell
   adb devices
   ```
   Deberías ver:
   ```
   List of devices attached
   [ID_DEL_CELULAR]    device
   127.0.0.1:4444      device
   ```
   *(Si el puerto `4444` dice `offline`, significa que el reloj se apagó antes de aceptar el permiso o el túnel falló).*

---

## Solución Definitiva si Falla ("Host: Desconectado")

Si haces los pasos anteriores y te arroja `failed to connect to 127.0.0.1:4444`, y en la app Wear OS sigue diciendo "Host: Desconectado", **el túnel interno del celular se quedó atascado**. 

Para destrabarlo, haz esta limpieza de permisos (fue lo que funcionó):

1. **En el Moto 360:**
   - Ve a Opciones de desarrollador > **Revocar autorizaciones de depuración**.
   - Apaga y vuelve a encender "Depuración ADB".

2. **En el Celular:**
   - Ve a Opciones de desarrollador > **Revocar autorizaciones de depuración USB**.
   - Desconecta el cable USB de la PC y vuélvelo a conectar.
   - Cuando en el celular salte la alerta "¿Permitir depuración USB?", marca la casilla **"Permitir siempre..."** y acepta.

3. **En la app Wear OS (Celular):**
   - Apaga "Depuración por Bluetooth" y enciéndela de nuevo.
   - Verifica que diga `Target: Conectado`.

4. Vuelve al **Paso 3** y lanza los comandos en la computadora. El puente se reconstruirá y la conexión será exitosa.

---

## Método Alternativo Comprobado (Uso de localabstract)

Si el comando de crear el puente falla o el dispositivo se mantiene en `offline`, el problema suele resolverse apuntando específicamente al ID del teléfono y usando el socket `localabstract`. Este es el método exacto utilizado por el asistente para conectarse e instalar la app exitosamente:

1. **Limpiar conexiones y puertos previos:**
   ```powershell
   adb disconnect 127.0.0.1:4444
   adb disconnect localhost:4444
   adb forward --remove-all
   ```

2. **Crear el túnel usando el socket localabstract del celular:**
   Primero, averigua el ID de tu celular conectado con `adb devices` (ej. `a9555b2e`). Luego crea el puente asegurándote de usar ese dispositivo específicamente con la bandera `-s`:
   ```powershell
   adb -s <ID_DEL_CELULAR> forward tcp:4444 localabstract:/adb-hub
   ```

3. **Conectar a localhost (en lugar de 127.0.0.1):**
   ```powershell
   adb connect localhost:4444
   ```
   *(Si el primer intento da error, vuelve a ejecutar `adb disconnect localhost:4444` seguido de `adb connect localhost:4444`).*

4. **Verificar el estado "device":**
   ```powershell
   adb devices
   ```
   El reloj debería aparecer como `localhost:4444  device`.

### Comandos de Compilación e Instalación Automática (Vía Terminal)

Una vez lograda la conexión a `localhost:4444`, puedes usar estos comandos para compilar la app y empujarla al reloj sin usar la interfaz gráfica:

1. **Compilar el APK del módulo Wear:**
   ```powershell
   ./gradlew :wear:assembleDebug
   ```

2. **Instalar la aplicación en el smartwatch:**
   ```powershell
   adb -s localhost:4444 install -r "wear\build\outputs\apk\debug\wear-debug.apk"
   ```

3. **Ejecutar la app de forma remota:**
   ```powershell
   adb -s localhost:4444 shell am start -n com.example.control_rem.wear/.WearMainActivity
   ```