package me.aberrantfox.hawk

import com.google.gson.Gson
import me.aberrantfox.hawk.configuration.BotConfiguration
import me.aberrantfox.kjdautils.api.getInjectionObject
import me.aberrantfox.kjdautils.api.startBot
import me.aberrantfox.kjdautils.extensions.jda.fullName
import java.awt.Color
import java.util.*
import kotlin.system.exitProcess

data class Properties(val author: String, val version: String, val kutils: String, val repository: String)
private val propFile = Properties::class.java.getResource("/properties.json").readText()
val project: Properties = Gson().fromJson(propFile, Properties::class.java)
val startTime = Date()

fun main(args: Array<String>) {
    val token = args.firstOrNull()

    if(token == null || token == "UNSET") {
        println("Please specify bot_Token ")
        exitProcess(-1)
    }

    startBot(token, "me.aberrantfox.hawk.") {
        configure {
            val configuration: BotConfiguration = discord.getInjectionObject<BotConfiguration>()!!
            prefix = configuration.botPrefix
            allowMentionPrefix = true

            colors {
                infoColor = Color.CYAN
                failureColor = Color.RED
                successColor = Color.GREEN
            }


            mentionEmbed {
                with(project) {
                    val self = it.guild.jda.selfUser
                    val kotlinVersion = KotlinVersion.CURRENT
                    val requiredRole = configuration.staffRole ?: "<Not Configured>"
                    val milliseconds = Date().time - startTime.time
                    val seconds = (milliseconds / 1000) % 60
                    val minutes = (milliseconds / (1000 * 60)) % 60
                    val hours = (milliseconds / (1000 * 60 * 60)) % 24
                    val days = (milliseconds / (1000 * 60 * 60 * 24))

                    color = infoColor
                    thumbnail = self.effectiveAvatarUrl
                    addField(self.fullName(), "A bot to add and maintain a symbol as a prefix or suffix in staff names.")
                    addInlineField("Prefix", configuration.botPrefix)
                    addInlineField("Ping", "${discord.jda.gatewayPing}ms")
                    addField("Uptime", "$days day(s), " +
                            "$hours hour(s), " +
                            "$minutes minute(s) " +
                            "and $seconds second(s)")

                    addField("Build Info", "```" +
                            "Version: $version\n" +
                            "KUtils: $kutils\n" +
                            "Kotlin: $kotlinVersion" +
                            "```")

                    addInlineField("Source", repository)
                }
            }

        }
    }
}
