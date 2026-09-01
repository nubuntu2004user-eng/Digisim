package com.example.digisim.Persistance

import com.example.digisim.ParsingLogic.Component
import com.example.digisim.ParsingLogic.Wire
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
    internal fun saveAs(file: File, components: MutableList<ComponentDto>, wires : MutableList<Wire>) {
        file.parentFile?.mkdirs()
        val circuitData = CircuitData(components, wires)
        val content = json.encodeToString(  circuitData)
        file.writeText(content)

    }
    internal fun load(file: File): CircuitData {
        if (!file.exists()) return CircuitData(mutableListOf(), mutableListOf())
        return json.decodeFromString(file.readText())
    }

}

@Serializable
internal data class CircuitData(
    val components: MutableList<ComponentDto>,
    val wires: MutableList<Wire>
)

