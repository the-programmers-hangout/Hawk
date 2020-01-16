package me.aberrantfox.hawk

import me.aberrantfox.kjdautils.api.startBot
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    val token = args.firstOrNull()

    if(token == null || token == "UNSET") {
        println("Please specify bot_Token ")
        exitProcess(-1)
    }

    startBot(token) {
        configure {
            globalPath = "me.aberrantfox.hawk."
        }
    }
}
