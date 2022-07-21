package me.ddivad.hawk.services

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ButtonStyle
import dev.kord.x.emoji.DiscordEmoji
import dev.kord.x.emoji.Emojis
import kotlinx.coroutines.runBlocking
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.arguments.Argument
import me.jakejmattson.discordkt.arguments.OptionalArg
import me.jakejmattson.discordkt.commands.Command
import me.jakejmattson.discordkt.commands.CommandEvent
import me.jakejmattson.discordkt.commands.DiscordContext
import me.jakejmattson.discordkt.commands.Execution
import me.jakejmattson.discordkt.dsl.MenuBuilder

@KordPreview
@Service
class HelpService {
    suspend fun sendHelpEmbed(event: CommandEvent<*>, command: Command) = event.respond {
        color = event.discord.configuration.theme
        title = command.names.joinToString(", ")
        description = command.description

        val commandInvocation = "${event.prefix()}${command.names.first()}"
        val helpBundle = command.executions.map {
            """$commandInvocation ${it.generateStructure()}
                ${
                it.arguments.joinToString("\n") { arg ->
                    """- ${arg.name}: ${arg.description} (${arg.generateExample(event.context)})
                    """.trimMargin()
                }
            }
            """.trimMargin()
        }
        field {
            this.value = helpBundle.joinToString("\n\n") { it }
        }
    }

    private fun Argument<*, *>.generateExample(context: DiscordContext) =
        runBlocking { generateExamples(context) }
            .takeIf { it.isNotEmpty() }
            ?.random()
            ?: "<Example>"

    private fun Execution<*>.generateStructure() = arguments.joinToString(" ") {
        val type = it.name
        if (it.isOptional()) "[$type]" else type
    }
}