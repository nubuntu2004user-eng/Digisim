package com.example.digisim.UiUtils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TranslationViewModel : ViewModel() {
    var currentLanguage by mutableStateOf(Language.ENGLISH)

    private val translations = mapOf(
        "File" to mapOf(Language.ENGLISH to "File", Language.DEUTSCH to "Datei", Language.UKRAINIAN to "Файл"),
        "Clear" to mapOf(Language.ENGLISH to "Clear", Language.DEUTSCH to "Löschen", Language.UKRAINIAN to "Очистити"),
        "Save as" to mapOf(Language.ENGLISH to "Save as", Language.DEUTSCH to "Speichern unter", Language.UKRAINIAN to "Зберегти як"),
        "Load" to mapOf(Language.ENGLISH to "Load", Language.DEUTSCH to "Laden", Language.UKRAINIAN to "Завантажити"),
        "Edit" to mapOf(Language.ENGLISH to "Edit", Language.DEUTSCH to "Bearbeiten", Language.UKRAINIAN to "Редагувати"),
        "Undo" to mapOf(Language.ENGLISH to "Undo", Language.DEUTSCH to "Rückgängig", Language.UKRAINIAN to "Скасувати"),
        "Undo multiple" to mapOf(Language.ENGLISH to "Undo multiple", Language.DEUTSCH to "Rückgängig mehrere", Language.UKRAINIAN to "Скасувати кілька"),
        "Help" to mapOf(Language.ENGLISH to "Help", Language.DEUTSCH to "Hilfe", Language.UKRAINIAN to "Допомога"),
        "How many steps to undo" to mapOf(Language.ENGLISH to "How many steps to undo", Language.DEUTSCH to "Wie viele Schritte rückgängig machen", Language.UKRAINIAN to "Скільки кроків скасувати"),
        "Enter number here" to mapOf(Language.ENGLISH to "Enter number here", Language.DEUTSCH to "Geben Sie hier eine Zahl ein", Language.UKRAINIAN to "Введіть число тут"),
        "Properties" to mapOf(Language.ENGLISH to "Properties", Language.DEUTSCH to "Eigenschaften", Language.UKRAINIAN to "Властивості"),
        "Change input count" to mapOf(Language.ENGLISH to "Change input count", Language.DEUTSCH to "Eingabeanzahl ändern", Language.UKRAINIAN to "Змінити кількість входів"),
        "Delete" to mapOf(Language.ENGLISH to "Delete", Language.DEUTSCH to "Löschen", Language.UKRAINIAN to "Видалити"),
        "Frequency" to mapOf(Language.ENGLISH to "Frequency", Language.DEUTSCH to "Frequenz", Language.UKRAINIAN to "Частота"),
        "Change frequency" to mapOf(Language.ENGLISH to "Change frequency", Language.DEUTSCH to "Frequenz ändern", Language.UKRAINIAN to "Змінити частоту"),
        "Blue" to mapOf(Language.ENGLISH to "Blue", Language.DEUTSCH to "Blau", Language.UKRAINIAN to "Синій"),
        "Black" to mapOf(Language.ENGLISH to "Black", Language.DEUTSCH to "Schwarz", Language.UKRAINIAN to "Чорний"),
        "Red" to mapOf(Language.ENGLISH to "Red", Language.DEUTSCH to "Rot", Language.UKRAINIAN to "Червоний"),
        "Green" to mapOf(Language.ENGLISH to "Green", Language.DEUTSCH to "Grün", Language.UKRAINIAN to "Зелений"),
        "Magenta" to mapOf(Language.ENGLISH to "Magenta", Language.DEUTSCH to "Magenta", Language.UKRAINIAN to "Пурпуровий"),
        "Cyan" to mapOf(Language.ENGLISH to "Cyan", Language.DEUTSCH to "Zyan", Language.UKRAINIAN to "Блакитний"),
        "Poke" to mapOf(Language.ENGLISH to "Poke", Language.DEUTSCH to "Antippen", Language.UKRAINIAN to "Натиснути"),
        "Drag" to mapOf(Language.ENGLISH to "Drag", Language.DEUTSCH to "Ziehen", Language.UKRAINIAN to "Перетягнути"),
        "Wire" to mapOf(Language.ENGLISH to "Wire", Language.DEUTSCH to "Draht", Language.UKRAINIAN to "Дріт"),
        "Text" to mapOf(Language.ENGLISH to "Text", Language.DEUTSCH to "Text", Language.UKRAINIAN to "Текст"),
        "Select Color: " to mapOf(Language.ENGLISH to "Select Color: ", Language.DEUTSCH to "Farbe wählen: ", Language.UKRAINIAN to "Вибрати колір: "),
        "Library: " to mapOf(Language.ENGLISH to " Library: ", Language.DEUTSCH to " Bibliothek: ", Language.UKRAINIAN to " Бібліотека: "),
        "ID: " to mapOf(Language.ENGLISH to "ID: ", Language.DEUTSCH to "ID: ", Language.UKRAINIAN to "ID: "),
        "Component type: " to mapOf(Language.ENGLISH to "Component type: ", Language.DEUTSCH to "Komponententyp: ", Language.UKRAINIAN to "Тип компонента: "),
        "Input count: " to mapOf(Language.ENGLISH to "Input count: ", Language.DEUTSCH to "Eingabeanzahl: ", Language.UKRAINIAN to "Кількість входів: "),
        "Wires in: " to mapOf(Language.ENGLISH to "Wires in: ", Language.DEUTSCH to "Eingangskabel: ", Language.UKRAINIAN to "Вхідні дроти: "),
        "From component: " to mapOf(Language.ENGLISH to "From component: ", Language.DEUTSCH to "Von Komponente: ", Language.UKRAINIAN to "Від компонента: "),
        "Port: " to mapOf(Language.ENGLISH to "Port: ", Language.DEUTSCH to "Port: ", Language.UKRAINIAN to "Порт: "),
        "Wires Out: " to mapOf(Language.ENGLISH to "Wires Out: ", Language.DEUTSCH to "Ausgangskabel: ", Language.UKRAINIAN to "Вихідні дроти: "),
        "To component: " to mapOf(Language.ENGLISH to "To component: ", Language.DEUTSCH to "Zu Komponente: ", Language.UKRAINIAN to "До компонента: "),
        "1KHz" to mapOf(Language.ENGLISH to "1KHz", Language.DEUTSCH to "1KHz", Language.UKRAINIAN to "1кГц"),
        "NOT Gate" to mapOf(Language.ENGLISH to "NOT Gate", Language.DEUTSCH to "NICHT-Gatter", Language.UKRAINIAN to "Вентиль НЕ"),
        "add NOT logic gate" to mapOf(Language.ENGLISH to "add NOT logic gate", Language.DEUTSCH to "NICHT-Logikgatter hinzufügen", Language.UKRAINIAN to "додати логічний вентиль НЕ"),
        "AND Gate" to mapOf(Language.ENGLISH to "AND Gate", Language.DEUTSCH to "UND-Gatter", Language.UKRAINIAN to "Вентиль І"),
        "add AND logic gate" to mapOf(Language.ENGLISH to "add AND logic gate", Language.DEUTSCH to "UND-Logikgatter hinzufügen", Language.UKRAINIAN to "додати логічний вентиль І"),
        "OR Gate" to mapOf(Language.ENGLISH to "OR Gate", Language.DEUTSCH to "ODER-Gatter", Language.UKRAINIAN to "Вентиль АБО"),
        "add OR logic gate" to mapOf(Language.ENGLISH to "add OR logic gate", Language.DEUTSCH to "ODER-Logikgatter hinzufügen", Language.UKRAINIAN to "додати логічний вентиль АБО"),
        "NAND Gate" to mapOf(Language.ENGLISH to "NAND Gate", Language.DEUTSCH to "NAND-Gatter", Language.UKRAINIAN to "Вентиль I-HE"),
        "add NAND logic gate" to mapOf(Language.ENGLISH to "add NAND logic gate", Language.DEUTSCH to "NAND-Logikgatter hinzufügen", Language.UKRAINIAN to "додати логічний вентиль I-HE"),
        "NOR Gate" to mapOf(Language.ENGLISH to "NOR Gate", Language.DEUTSCH to "NOR-Gatter", Language.UKRAINIAN to "Вентиль АБО-HE"),
        "add NOR logic gate" to mapOf(Language.ENGLISH to "add NOR logic gate", Language.DEUTSCH to "NOR-Logikgatter hinzufügen", Language.UKRAINIAN to "додати логічний вентиль АБО-HE"),
        "XOR Gate" to mapOf(Language.ENGLISH to "XOR Gate", Language.DEUTSCH to "XOR-Gatter", Language.UKRAINIAN to "Вентиль ВИКЛ-АБО"),
        "add XOR logic gate" to mapOf(Language.ENGLISH to "add XOR logic gate", Language.DEUTSCH to "XOR-Logikgatter hinzufügen", Language.UKRAINIAN to "додати логічний вентиль ВИКЛ-АБО"),
        "XNOR Gate" to mapOf(Language.ENGLISH to "XNOR Gate", Language.DEUTSCH to "XNOR-Gatter", Language.UKRAINIAN to "Вентиль ВИКЛ-АБО-HE"),
        "add XNOR logic gate" to mapOf(Language.ENGLISH to "add XNOR logic gate", Language.DEUTSCH to "XNOR-Logikgatter hinzufügen", Language.UKRAINIAN to "додати логічний вентиль ВИКЛ-АБО-HE"),
        "Input pin" to mapOf(Language.ENGLISH to "Input pin", Language.DEUTSCH to "Eingangspin", Language.UKRAINIAN to "Вхідний контакт"),
        "add Input pin" to mapOf(Language.ENGLISH to "add Input pin", Language.DEUTSCH to "Eingangspin hinzufügen", Language.UKRAINIAN to "додати вхідний контакт"),
        "Button" to mapOf(Language.ENGLISH to "Button", Language.DEUTSCH to "Taste", Language.UKRAINIAN to "Кнопка"),
        "add Push Button (1-tick pulse)" to mapOf(Language.ENGLISH to "add Push Button (1-tick pulse)", Language.DEUTSCH to "Drucktaste hinzufügen (1-Takt-Impuls)", Language.UKRAINIAN to "додати кнопку (імпульс 1 такт)"),
        "Output pin" to mapOf(Language.ENGLISH to "Output pin", Language.DEUTSCH to "Ausgangspin", Language.UKRAINIAN to "Вихідний контакт"),
        "add Output pin" to mapOf(Language.ENGLISH to "add Output pin", Language.DEUTSCH to "Ausgangspin hinzufügen", Language.UKRAINIAN to "додати вихідний контакт"),
        "Clock" to mapOf(Language.ENGLISH to "Clock", Language.DEUTSCH to "Takt", Language.UKRAINIAN to "Тактовий генератор"),
        "add Clock" to mapOf(Language.ENGLISH to "add Clock", Language.DEUTSCH to "Takt hinzufügen", Language.UKRAINIAN to "додати тактовий генератор"),
        "RS Flip-Flop" to mapOf(Language.ENGLISH to "RS Flip-Flop", Language.DEUTSCH to "RS-Flipflop", Language.UKRAINIAN to "RS-тригер"),
        "add RS Flip-Flop" to mapOf(Language.ENGLISH to "add RS Flip-Flop", Language.DEUTSCH to "RS-Flipflop hinzufügen", Language.UKRAINIAN to "додати RS-тригер"),
        "JK Flip-Flop" to mapOf(Language.ENGLISH to "JK Flip-Flop", Language.DEUTSCH to "JK-Flipflop", Language.UKRAINIAN to "JK-тригер"),
        "add JK Flip-Flop" to mapOf(Language.ENGLISH to "add JK Flip-Flop", Language.DEUTSCH to "JK-Flipflop hinzufügen", Language.UKRAINIAN to "додати JK-тригер"),
        "D Flip-Flop" to mapOf(Language.ENGLISH to "D Flip-Flop", Language.DEUTSCH to "D-Flipflop", Language.UKRAINIAN to "D-тригер"),
        "add D Flip-Flop" to mapOf(Language.ENGLISH to "add D Flip-Flop", Language.DEUTSCH to "D-Flipflop hinzufügen", Language.UKRAINIAN to "додати D-тригер"),
        "T Flip-Flop" to mapOf(Language.ENGLISH to "T Flip-Flop", Language.DEUTSCH to "T-Flipflop", Language.UKRAINIAN to "T-тригер"),
        "add T Flip-Flop" to mapOf(Language.ENGLISH to "add T Flip-Flop", Language.DEUTSCH to "T-Flipflop hinzufügen", Language.UKRAINIAN to "додати T-тригер"),
        "Gates" to mapOf(Language.ENGLISH to "Gates", Language.DEUTSCH to "Gatter", Language.UKRAINIAN to "Вентилі"),
        "Wiring" to mapOf(Language.ENGLISH to "Wiring", Language.DEUTSCH to "Verkabelung", Language.UKRAINIAN to "З'єднання"),
        "Flip-Flops" to mapOf(Language.ENGLISH to "Flip-Flops", Language.DEUTSCH to "Flipflops", Language.UKRAINIAN to "Тригери")
    )

    fun getString(key: String): String {
        return translations[key]?.get(currentLanguage) ?: key
    }
}

 enum class Language{ENGLISH ,  DEUTSCH , UKRAINIAN}
