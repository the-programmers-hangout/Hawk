package me.ddivad.hawk.embeds

import dev.kord.core.entity.Guild
import dev.kord.rest.Image
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.x.emoji.Emojis
import me.ddivad.hawk.dataclasses.Configuration
import me.jakejmattson.discordkt.Discord

suspend fun EmbedBuilder.createConfigurationEmbed(configuration: Configuration, guild: Guild, discord: Discord) {
    val guildConfiguration = configuration[guild.id] ?: return
    val fullyConfigured = configuration.isFullyConfigured(guild)
        color = discord.configuration.theme
        title = "Configuration"
        thumbnail {
            url = guild.getIconUrl(Image.Format.PNG) ?: ""
        }

        description = "Fully Configured: ${if(fullyConfigured) Emojis.whiteCheckMark else Emojis.x}"

        field {
            name = "**General**"
            value = "Prefix: ${guildConfiguration.prefix}\n" +
                    "Logging Channel: ${guildConfiguration.loggingConfiguration.logChannel?.let { guild.getChannel(it).mention }}\n" +
                    "Alert Channel: ${guildConfiguration.loggingConfiguration.alertChannel?.let { guild.getChannel(it).mention }}\n"
        }

        field {
            name = "**Party**"
            value = "Enabled: ${guildConfiguration.partyModeConfiguration.enabled}\n" +
                    "Symbol: ${guildConfiguration.partyModeConfiguration.symbol}\n" +
                    "Channel Filter Enabled: ${guildConfiguration.partyModeConfiguration.channelFilterEnabled}\n" +
                    "Party Channels: ${guildConfiguration.partyModeConfiguration.channels.map {guild.getChannel(it).mention}.joinToString(", ") }"
        }
}