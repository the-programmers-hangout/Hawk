package me.aberrantfox.hawk.botdata

import me.jakejmattson.discordkt.api.dsl.data.Data

data class Messages(
    val USER_NICK_RESET: String = "Your nickname made it seem like you were a staff member. This has been automatically logged.",
    val STAFF_NICK_PROMPT: String = "You have updated your nickname and it does not contain the prefix. Here it is with the prefix: %1%"
): Data("data/messages.json")