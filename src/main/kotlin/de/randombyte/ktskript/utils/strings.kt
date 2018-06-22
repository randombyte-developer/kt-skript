package de.randombyte.ktskript.utils

import de.randombyte.byteitems.ByteItemsApi
import me.rojo8399.placeholderapi.PlaceholderService
import org.spongepowered.api.Sponge
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextAction
import org.spongepowered.api.text.serializer.TextSerializers

val String.t
    get() = TextSerializers.FORMATTING_CODE.deserialize(this)

fun String.toText(): Text = Text.of(this)

fun String.aqua(): Text = toText().aqua()
fun String.black(): Text = toText().black()
fun String.blue(): Text = toText().blue()
fun String.darkAqua(): Text = toText().darkAqua()
fun String.darkBlue(): Text = toText().darkBlue()
fun String.darkGray(): Text = toText().darkGray()
fun String.darkGreen(): Text = toText().darkGreen()
fun String.darkPurple(): Text = toText().darkPurple()
fun String.darkRed(): Text = toText().darkRed()
fun String.gold(): Text = toText().gold()
fun String.gray(): Text = toText().gray()
fun String.green(): Text = toText().green()
fun String.lightPurple(): Text = toText().lightPurple()
fun String.red(): Text = toText().red()
fun String.white(): Text = toText().white()
fun String.yellow(): Text = toText().yellow()

fun String.bold(): Text = toText().bold()
fun String.italic(): Text = toText().italic()
fun String.obfuscated(): Text = toText().obfuscated()
fun String.reset(): Text = toText().reset()
fun String.strikethrough(): Text = toText().strikethrough()
fun String.underline(): Text = toText().underline()

fun <T : TextAction<*>> String.action(action: T): Text = toText().action(action)

fun String.limit(limit: Int): String {
    val safeLimit = limit.coerceIn(0..length)
    return if (safeLimit < length) {
        substring(0, safeLimit) + "…"
    } else this
}

fun String.replace(vararg args: Pair<String, String>): String = replace(args.toMap())
fun String.replace(values: Map<String, String>): String {
    var string = this
    values.forEach { (argument, value) ->
        string = string.replace(argument, value)
    }
    return string
}

// API safe wrappers

/**
 * Tries to process the placeholders if PlaceholderAPI is loaded.
 */
fun String.tryReplacePlaceholders(source: Any? = null, observer: Any? = null): String {
    if (!PluginManager.getPlugin("placeholderapi").isPresent) return this

    val placeholderService = PlaceholderService::class.service

    val placeholders = placeholderService.defaultPattern.toRegex()
            .findAll(this)
            .map { matchResult -> matchResult.groupValues[1] }.toList()
    val replacements = placeholders.mapNotNull { placeholder ->
        val replacement = placeholderService.parse(placeholder, source, observer) ?: return@mapNotNull null
        val replacementString = if (replacement is Text) {
            TextSerializers.FORMATTING_CODE.serialize(replacement)
        } else {
            replacement.toString()
        }
        "%$placeholder%" to replacementString
    }.toMap()

    return this.replace(replacements)
}

fun String.tryAsByteItem(failMessage: String? = null): ItemStackSnapshot {
    if (!Sponge.getPluginManager().getPlugin("byte-items").isPresent) {
        // fall back to normal minecraft item types
        val itemType = Sponge.getRegistry().getType(ItemType::class.java, this)
                .orElseThrow { IllegalArgumentException("Couldn't find ItemType '$this'!") }
        return ItemStack.of(itemType, 1).createSnapshot()
    }

    val byteItemsApi = ByteItemsApi::class.service
    return if (failMessage != null) {
        byteItemsApi.getItemSafely(id = this, failMessage = failMessage)
    } else {
        byteItemsApi.getItemSafely(id = this)
    }
}