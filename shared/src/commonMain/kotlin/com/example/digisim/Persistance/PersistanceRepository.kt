package com.example.digisim.Persistance

import com.example.digisim.ParsingLogic.Wire
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.parent
import io.github.vinceglb.filekit.readString
import io.github.vinceglb.filekit.writeString
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

internal object persistanceRepository {
    private val json = Json{
        prettyPrint = true
        ignoreUnknownKeys = true

    }

    internal fun defaultDataDir(): File {
        val userHome = System.getProperty("user.home")
        val os = System.getProperty("os.name").lowercase()
        val base = if(os.contains("win")){
                System.getenv("APPDATA") ?: "$userHome\\AppData\\Roaming"
        } else {
            "$userHome/.local/share"
        }
        return File(base , "DigiSim").apply { mkdirs() }
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

