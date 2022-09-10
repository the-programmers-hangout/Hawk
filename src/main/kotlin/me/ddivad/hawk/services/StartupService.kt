package me.ddivad.hawk.services

import dev.kord.core.behavior.getChannelOfOrNull
import dev.kord.core.entity.channel.TextChannel
import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.embeds.createReactionRoleMenu
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.extensions.createMenu

@Service
class StartupService(
    private val configuration: Configuration,
    private val discord: Discord,
    private val loggingService: LoggingService
) {
    suspend fun refreshReactionRoleInteractions() {
        configuration.guildConfigurations.forEach { config ->
            val guild = config.value.let { discord.kord.getGuild(config.key) } ?: return@forEach
            val guildConfig = configuration[guild.id] ?: return@forEach
            if (guildConfig.reactionRoles.isEmpty()) return@forEach

            guildConfig.reactionRoles.forEach {
                val channel = guild.getChannelOfOrNull<TextChannel>(it.channel) ?: return
                val message = channel.getMessageOrNull(it.messageId!!) ?: return
                message.delete()
                it.messageId = channel.createMenu {
                    createReactionRoleMenu(discord, guild, it)
                }.id
            }
            configuration.save()
        }
    }
}