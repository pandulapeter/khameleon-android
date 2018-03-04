package com.pandulapeter.khameleon.injection

import com.pandulapeter.khameleon.data.repository.*
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val userModule: Module = applicationContext {
    provide { UserRepository(get()) }
    provide { PreferenceRepository(get()) }
}

val chatModule: Module = applicationContext {
    provide { ChatRepository() }
}

val calendarModule: Module = applicationContext {
    provide { CalendarRepository() }
}

val songsModule: Module = applicationContext {
    provide { SongRepository() }
}