package me.ddivad.hawk.dataclasses

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Role
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data

@Serializable
data class Configuration(
        val ownerId: Snowflake? = null,
        var prefix: String = "++",
        val guildConfigurations: MutableMap<Snowflake, GuildConfiguration> = mutableMapOf()
) : Data() {
    operator fun get(id: Snowflake) = guildConfigurations[id]
    fun hasGuildConfig(guildId: Snowflake) = guildConfigurations.containsKey(guildId)

    fun setup(guild: Guild) {
        if (guildConfigurations[guild.id] != null) return

        val newConfiguration = GuildConfiguration(
                guild.id,
        )
        guildConfigurations[guild.id] = newConfiguration
        save()
    }

    fun isFullyConfigured(guild: Guild): Boolean {
        val guildConfiguration = guildConfigurations[guild.id] ?: return false

        if (guildConfiguration.staffRoleId == null ||
            guildConfiguration.adminRoleId == null ||
            guildConfiguration.loggingConfiguration.logChannel == null ||
            guildConfiguration.loggingConfiguration.alertChannel == null
        ) {
            return false
        }
        return true
    }
}

@Serializable
data class GuildConfiguration(
    val id: Snowflake,
    var prefix: String = "hawk!",
    var staffRoleId: Snowflake? = null,
    var adminRoleId: Snowflake? = null,
    var disallowedNicknameSymbols: MutableList<String> = mutableListOf(),
    var partyModeConfiguration: PartyModeConfiguration = PartyModeConfiguration(),
    var loggingConfiguration: LoggingConfiguration = LoggingConfiguration()
)

@Serializable
data class PartyModeConfiguration(
    var enabled: Boolean = false,
    var symbol: String = "",
    var symbolStrip: String = "",
    var channels: MutableList<Snowflake> = mutableListOf(),
    var channelFilterEnabled: Boolean = false
)

@Serializable
data class LoggingConfiguration(
    var logChannel: Snowflake? = null,
    var alertChannel: Snowflake? = null
)