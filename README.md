**YugiOh Battle Duel** 🃏⚔️

Un simulador de duelos inspirado en el universo de Yu-Gi-Oh!, desarrollado en Java con soporte para interfaz gráfica y lógica de batalla entre cartas.

🚀 Características:

-Sistema de cartas basado en un modelo de objetos (Card.java).

-Módulo de duelo que gestiona reglas, turnos y enfrentamientos (Duel.java).

-Interfaz gráfica en Swing (MainFrame.java) para interactuar con el tablero y las cartas.

-Conexión con APIs externas a través de un cliente HTTP (YgoApiClient.java).

-Arquitectura modular organizada en paquetes: duel, model, net, ui.

🛠️ Tecnologías utilizadas:

-Lenguaje: Java 17+

-Gestor de dependencias: Maven

-Interfaz gráfica: Java Swing

-Arquitectura: MVC simplificado

📂 Estructura del proyecto:

YugiOhBattleDuel/
 ├── pom.xml                # Configuración Maven
 └── src/main/java/com/example/ygo/
     ├── App.java           # Punto de entrada
     ├── duel/              # Lógica de batallas y listeners
     ├── model/             # Representación de cartas
     ├── net/               # Cliente API
     └── ui/                # Interfaz gráfica (Swing)

⚙️ Instalación y ejecución:

-Clonar el repositorio:

git clone https://github.com/tu_usuario/YugiOhBattleDuel.git
cd YugiOhBattleDuel/YugiOhBattle


-Compilar el proyecto con Maven:

mvn clean install


-Ejecutar la aplicación:

mvn exec:java -Dexec.mainClass="com.example.ygo.App"

🎮 Cómo jugar:

-Inicia la aplicación desde la interfaz gráfica.

-Selecciona tus cartas y comienza un duelo.

-El motor de batalla aplicará reglas básicas (ataque, defensa, resolución de turnos).

-Observa los resultados en el tablero gráfico.

Desarrolladores: 

Erika Ávila Garzón Cod: 2266259
Juan Sebastian Rojas Robles: 2266258
