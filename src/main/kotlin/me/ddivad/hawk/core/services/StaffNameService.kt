package me.ddivad.hawk.core.services

import me.ddivad.hawk.core.dataclasses.Configuration
import me.jakejmattson.discordkt.api.annotations.Service

@Service
class StaffNameService (private val permissionsService: PermissionsService,
                        private val nicknameService: NicknameService,
                        private val configuration: Configuration) {

    suspend fun ensureStaffSymbol() {

    }

    suspend fun ensureNoStaffSymbol() {

    }
}