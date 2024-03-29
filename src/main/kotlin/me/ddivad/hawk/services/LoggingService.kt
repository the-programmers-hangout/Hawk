package me.ddivad.hawk.services

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.runBlocking
import me.ddivad.hawk.dataclasses.Configuration
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.extensions.idDescriptor
import mu.KotlinLogging

@Service
class LoggingService(private val config: Configuration) {
    private val logger = KotlinLogging.logger {  }

    fun reactionRoleAdded(guild: Guild, member: Member, role: Role) = withLog(guild) {
        logger.info { buildGuildLogMessage(guild, "${role.name} (${role.id}) added to ${member.idDescriptor()}") }
        "**Info** :: Role **${role.name}** added to ${member.idDescriptor()}"
    }

    fun reactionRoleRemoved(guild: Guild, member: Member, role: Role) = withLog(guild) {
        logger.info { buildGuildLogMessage(guild, "${role.name} (${role.id}) removed from ${member.idDescriptor()}") }
        "**Info** :: Role **${role.name}** removed from ${member.idDescriptor()}"
    }

    fun nicknameApplied(guild: Guild, member: Member, nickname: String) = withLog(guild) {
        logger.info { buildGuildLogMessage(guild, "Nickname $nickname applied to ${member.idDescriptor()}") }
        "**Info** :: Nickname $nickname applied to ${member.idDescriptor()}"
    }

    fun blocklistedSymbolRemoved(guild: Guild, member: Member) = withAlert(guild) {
        logger.info { buildGuildLogMessage(guild, "Blocklisted symbols detected and removed from ${member.idDescriptor()}") }
        "**Info** :: Blocklisted symbols detected and removed from ${member.idDescriptor()} (${member.displayName})"
    }

    fun guildLog(guild: Guild, message: String) = withLog(guild) {
        logger.info { buildGuildLogMessage(guild, message) }
        "**Info** :: $message"
    }

    private fun withLog(guild: Guild, f: () -> String) =
        getLogConfig(guild).apply {
            runBlocking {
                log(guild, getLogConfig(guild), f())
            }
        }

    private fun withAlert(guild: Guild, f: () -> String) =
        getAlertConfig(guild).apply {
            runBlocking {
                alert(guild, getAlertConfig(guild), f())
            }
        }

    private fun getLogConfig(guild: Guild) = config[guild.id]!!.loggingConfiguration.logChannel!!
    private fun getAlertConfig(guild: Guild) = config[guild.id]!!.loggingConfiguration.alertChannel!!
    private suspend fun log(guild: Guild, logChannelId: Snowflake, message: String) =
        guild.getChannelOf<TextChannel>(logChannelId).createMessage(message)

    private suspend fun alert(guild: Guild, alertChannelId: Snowflake, message: String) =
        guild.getChannelOf<TextChannel>(alertChannelId).createMessage(message)
    private fun buildGuildLogMessage(guild: Guild, message: String) = "${guild.name} (${guild.id}): $message"
}

