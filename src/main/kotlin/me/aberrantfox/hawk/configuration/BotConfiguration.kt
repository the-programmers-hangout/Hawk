package me.aberrantfox.hawk.configuration

import me.aberrantfox.kjdautils.api.annotation.Data

@Data("data/configuration.json")
data class BotConfiguration(
        val guild: String = "<insert-guild>",
        var owner: String = "<insert-id>",
        var botPrefix: String = "hawk!",
        var nickSymbol: String = "\uD83D\uDD28 ",
        var enabled: Boolean = true,
        var staffRole: String = "Staff",
        var stripString: String = "\uD83D\uDD28",
        var mode: String = "suffix"
)