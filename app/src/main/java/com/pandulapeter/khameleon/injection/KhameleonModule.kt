package com.pandulapeter.khameleon.injection

import com.pandulapeter.khameleon.data.repository.MessageRepository
import com.pandulapeter.khameleon.data.repository.UserRepository
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val userModule: Module = applicationContext {
    provide { UserRepository(get()) }
}

val chatModule: Module = applicationContext {
    provide { MessageRepository() }
}