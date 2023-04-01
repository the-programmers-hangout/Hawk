package me.ddivad.hawk.dataclasses

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Guild
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data
import me.jakejmattson.discordkt.dsl.edit

@Serializable
data class Configuration(
    val ownerId: Snowflake? = null,
    var prefix: String = "hawk!",
    val guildConfigurations: MutableMap<Snowflake, GuildConfiguration> = mutableMapOf()
) : Data() {
    operator fun get(id: Snowflake) = guildConfigurations[id]
    fun hasGuildConfig(guildId: Snowflake) = guildConfigurations.containsKey(guildId)

    fun setup(guild: Guild) {
        if (guildConfigurations[guild.id] != null) return

        val newConfiguration = GuildConfiguration(
            guild.id,
        )
        edit {
            guildConfigurations[guild.id] = newConfiguration
        }
    }

    fun isFullyConfigured(guild: Guild): Boolean {
        val guildConfiguration = guildConfigurations[guild.id] ?: return false

        if (guildConfiguration.loggingConfiguration.logChannel == null ||
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
    var disallowedNicknameSymbols: MutableList<String> = mutableListOf(),
    var partyModeConfiguration: PartyModeConfiguration = PartyModeConfiguration(),
    var loggingConfiguration: LoggingConfiguration = LoggingConfiguration(),
    var reactionRoles: MutableList<ReactionRole> = mutableListOf()
)

@Serializable
data class PartyModeConfiguration(
    var enabled: Boolean = false,
    var symbol: String = "",
    var symbolStrip: String = "",
    var channels: MutableList<Snowflake> = mutableListOf(),
    var channelFilterEnabled: Boolean = false,
    var theme: PartyModeThemes = PartyModeThemes.SYMBOL
)

@Serializable
enum class PartyModeThemes {
    SYMBOL, FURRY
}

@Serializable
data class LoggingConfiguration(
    var logChannel: Snowflake? = null,
    var alertChannel: Snowflake? = null
)

@Serializable
data class ReactionRole(
    val id: Int,
    val description: String,
    val roles: MutableList<Snowflake>,
    var messageId: Snowflake?,
    val channel: Snowflake
)