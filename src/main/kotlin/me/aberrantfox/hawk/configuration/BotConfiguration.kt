package me.aberrantfox.hawk.configuration

import me.jakejmattson.discordkt.api.dsl.data.Data

data class BotConfiguration(
        val guild: String = "<insert-guild>",
        var owner: String = "<insert-id>",
        var botPrefix: String = "hawk!",
        var nickSymbol: String = "\uD83D\uDD28 ",
        var enabled: Boolean = true,
        var staffRole: String = "Staff",
        var stripString: String = "\uD83D\uDD28",
        var mode: String = "suffix",
        var partyMode: Boolean = false,
        var partySuffix: String = "\ud83e\udd73 ",
        var partyStrip: String = "\ud83e\udd73"
): Data("data/configuration.json")