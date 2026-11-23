# Digital Logic Simulator

World first digital logic simulator with embedded mcp protocol aiming to accelerate development of circuits with usage of large language models inspired by [Logisim](https://github.com/logisim-evolution/logisim-evolution) and [Digital](https://github.com/hneemann/Digital).



## Features

- Mcp support with OpenAI/OpenAI compatible endpoints

- All basic logic gates and clocks

- Asynchronous clock support

- Customizable wire colors

- Pannable canvas

- Adjustable input count (up to 64)

- Pause / resume simulation

- 74 series chips

- Persistance and ability to add custom libraries

- Avalible in 3 languages

- Colorable wires

- Undo button (Ctrl +z)

- Undo multiple button ( under “Edit” menu)

- And much more
## Getting Started

1. Go to the [Releases](file:///home/nubuntu/releases) tab

2. Download the latest `.jar` file

3. Run it with Java 21+

### Requirements

| Use case | Minimum version |
| - | - |
| Run the `.jar` | JRE 21 |
| Build with Gradle | JDK 21 |


## For Developers

This is a Kotlin Multiplatform project targeting Desktop (JVM).

### Project Structure

- `/shared` — code shared across Compose Multiplatform targets

  - `commonMain` — code common to all targets

  - other folders (e.g. `jvmMain`, `iosMain`) contain platform-specific code

### Running the App

Use your IDE's run configurations, or the command line:

```
\# Desktop app (hot reload)  
./gradlew :desktopApp:hotRun --auto  
  
\# Desktop app (standard)  
./gradlew :desktopApp:run
```

### Running Tests

```
./gradlew :shared:jvmTest
```

### Learn More

[Kotlin Multiplatform documentation →](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)

