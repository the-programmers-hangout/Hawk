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
import me.jakejmattson.discordkt.commands.Execution
import me.jakejmattson.discordkt.dsl.MenuBuilder

@KordPreview
@Service
class HelpService {

//    suspend fun buildHelpEmbed(event: CommandEvent<*>) = event.respondMenu {
//        val container = event.discord.commands
//        fun joinNames(value: List<Command>) =
//            value.joinToString("\n") { it.names.first() }
//
//        val groupedCommands = container
//            .filter { it.hasPermissionToRun(event) }
//            .groupBy { it.category }
//            .toList()
//            .sortedByDescending { it.second.size }
//
//        val categoryNames = container
//            .filter { it.hasPermissionToRun(event) }
//            .groupBy { it.category }
//            .toList()
//            .sortedByDescending { it.second.size }
//            .map { it.first }
//            .toList()
//
//        if (groupedCommands.isNotEmpty()) {
//            groupedCommands.map { (category, commands) ->
//                val sorted = commands
//                    .sortedBy { it.names.first() }
//
//                page {
//                    title = "Help - $category Commands"
//                    description = """
//                        You have **${commands.size}** commands available based on permissions.
//
//                        Use `${event.prefix()}help` <command> for more information
//                    """.trimIndent()
//                    color = event.discord.configuration.theme
//
//                    field {
//                        name = "**Commands**"
//                        value = "```css\n${joinNames(sorted)}\n```"
//                        inline = true
//                    }
//                }
//            }
//
//            categoryNames.chunked(5).forEachIndexed { index, category ->
//                buttons {
//                    category.forEachIndexed { page, name ->
//                        button(name, getEmojiForCategory(name), ButtonStyle.Secondary) {
//                            loadPage(page + index * 5)
//                        }
//                    }
//                }
//            }
//        }
//    }



    suspend fun sendHelpEmbed(event: CommandEvent<*>, command: Command) = event.respond {
        color = event.discord.configuration.theme
        title = command.names.joinToString(", ")
        description = command.description

        val commandInvocation = "${event.prefix()}${command.names.first()}"
        val helpBundle = command.executions.map {
            """$commandInvocation ${it.generateStructure()}
                ${
                it.arguments.joinToString("\n") { arg ->
                    """- ${arg.name}: ${arg.description} (${arg.generateExample(event)})
                    """.trimMargin()
                }
            }
            """.trimMargin()
        }
        field {
            this.value = helpBundle.joinToString("\n\n") { it }
        }
    }

    private fun Argument<*>.generateExample(event: CommandEvent<*>) =
        runBlocking { generateExamples(event) }
            .takeIf { it.isNotEmpty() }
            ?.random()
            ?: "<Example>"

    private fun Execution<*>.generateStructure() = arguments.joinToString(" ") {
        val type = it.name
        if (it is OptionalArg) "[$type]" else type
    }
}