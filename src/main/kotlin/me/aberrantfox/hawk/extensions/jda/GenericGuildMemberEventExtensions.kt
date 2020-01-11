package me.aberrantfox.hawk.extensions.jda


import net.dv8tion.jda.api.events.guild.member.update.GenericGuildMemberUpdateEvent

fun <T> GenericGuildMemberUpdateEvent<T>.isSelf() = this.member.id == jda.selfUser.id