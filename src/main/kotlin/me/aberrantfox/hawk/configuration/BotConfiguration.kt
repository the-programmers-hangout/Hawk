package me.aberrantfox.hawk.configuration

import me.aberrantfox.kjdautils.api.annotation.Data

@Data("data/configuration.json")
data class BotConfiguration(
        val guild: String = "244230771232079873",
        val owner: String = "222164217707364362",
        val nickPrefix: String = "\uD83D\uDD28 ",
        var enabled: Boolean = true,
        var staffRole: String = "Staff"
)