package com.example.digisim.Persistance

import com.example.digisim.ParsingLogic.Wire
import com.example.digisim.UiUtils.AppState
import com.example.digisim.UiUtils.Language
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.parent
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readString
import io.github.vinceglb.filekit.writeString
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

internal object persistanceRepository {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    internal fun defaultDataDir(): PlatformFile {
        val userHome = System.getProperty("user.home")
        val os = System.getProperty("os.name").lowercase()
        val base = if (os.contains("win")) {
            System.getenv("APPDATA") ?: "$userHome\\AppData\\Roaming"
        } else {
            "$userHome/.local/share"
        }
        val file = File(base, "DigiSim.json")
        file.parentFile?.mkdirs()
        return PlatformFile(file.path)
    }

    internal suspend fun saveState(appState: AppState) {
        val appStateDto = AppStateToDto(appState)
        val content = json.encodeToString(appStateDto)
        defaultDataDir().writeString(content)
    }

    internal suspend fun loadState(): AppStateDto {
        val file = defaultDataDir()
        return try {
            if (file.exists()) {
                file.readString()?.let { 
                    json.decodeFromString<AppStateDto>(it) 
                } ?: AppStateDto()
            } else {
                AppStateDto()
            }
        } catch (e: Exception) {
            AppStateDto()
        }
    }

    internal suspend fun saveAs(file: PlatformFile, components: MutableList<ComponentDto>, wires: MutableList<Wire>) {
        val circuitData = CircuitData(components, wires)
        val content = json.encodeToString(circuitData)
        file.writeString(content)
    }

    internal suspend fun load(file: PlatformFile?): CircuitData? {
        file?.exists()?.let { if (!it) return CircuitData(mutableListOf(), mutableListOf()) }
        return file?.readString()?.let { json.decodeFromString(it) }
    }
}

@Serializable
internal data class CircuitData(
    val components: MutableList<ComponentDto>,
    val wires: MutableList<Wire>
)
