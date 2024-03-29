package me.ddivad.hawk.listeners

import dev.kord.core.event.message.MessageDeleteEvent
import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.services.LoggingService
import me.jakejmattson.discordkt.dsl.edit
import me.jakejmattson.discordkt.dsl.listeners

@Suppress("unused")
fun messageDeleteListener(configuration: Configuration, loggingService: LoggingService) = listeners {
    on<MessageDeleteEvent> {
        val guild = guild ?: return@on
        val guildConfiguration = configuration[guild.id] ?: return@on
        val reactionRole =
            guildConfiguration.reactionRoles.find { it.channel == channelId && it.messageId == messageId } ?: return@on
        loggingService.guildLog(guild.asGuild(), "Reaction role ${reactionRole.id} deleted. Removing from config")
        configuration.edit {
            guildConfiguration.reactionRoles.remove(reactionRole)
        }
    }
}