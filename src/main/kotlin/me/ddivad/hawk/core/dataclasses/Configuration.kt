package me.ddivad.hawk.core.dataclasses

import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Role
import me.jakejmattson.discordkt.api.dsl.Data

data class Configuration(
        val ownerId: String = "insert-owner-id",
        var prefix: String = "hawk!",
        val guildConfigurations: MutableMap<Long, GuildConfiguration> = mutableMapOf()
) : Data("config/config.json") {
    operator fun get(id: Long) = guildConfigurations[id]
    fun hasGuildConfig(guildId: Long) = guildConfigurations.containsKey(guildId)

    fun setup(guild: Guild, prefix: String, adminRole: Role, staffRole: Role) {
        if (guildConfigurations[guild.id.longValue] != null) return

        val newConfiguration = GuildConfiguration(
                guild.id.value,
                prefix,
                staffRole.id.longValue,
                adminRole.id.longValue
        )
        guildConfigurations[guild.id.longValue] = newConfiguration
        save()
    }
}

data class GuildConfiguration(
        val id: String = "",
        var prefix: String = "hawk!",
        var staffRoleId: Long,
        var adminRoleId: Long,
        var enabled: Boolean = true,
        var nickSymbol: String = "\uD83D\uDD28 ",
        var stripString: String = "\uD83D\uDD28",
        var mode: String = "suffix",
        var partyMode: PartyModeConfiguration = PartyModeConfiguration()
)

data class PartyModeConfiguration(
        var enabled: Boolean = false,
        var suffix: String = "",
        var stripSuffix: String = "",
        var partyChannels: MutableList<String> = mutableListOf(),
        var filterEnabled: Boolean = false
)