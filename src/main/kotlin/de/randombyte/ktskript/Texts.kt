package de.randombyte.ktskript

import de.randombyte.ktskript.utils.*

object Texts {
    val motivationalSpeech = listOf(
            "[${KtSkriptPlugin.NAME}] ".yellow() + "Metrics are disabled for this plugin or globally! Please consider enabling metrics.".aqua(),
            "Metrics are anonymous usage data (how many players are on the server, which minecraft version the server is on, etc.)".green(),
            "With that data the developer can check how many servers use the plugin. Plugins with many users motivate me more to release new updates. :)".gold(),
            "To disable this message, go to the Sponge global config, and enable metrics collection for at least this plugin, thanks! ;)".lightPurple())

    const val configComment = "Since you are already editing configs, how about enabling metrics for at least this plugin? ;)\n" +
            "Go to the 'config/sponge/global.conf', scroll to the 'metrics' section and enable metrics.\n" +
            "Anonymous metrics data collection enables the developer to see how many people and servers are using this plugin.\n" +
            "Seeing that my plugin is being used is a big factor in motivating me to provide future support and updates.\n" +
            "If you really don't want to enable metrics and don't want to receive any messages anymore, you can disable this config option ;("
}