package com.rork.grievai.data

import com.rork.grievai.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

/**
 * App-wide Supabase client. URL and anon key come from local.properties
 * (never hardcoded here, never committed to git) via BuildConfig fields
 * generated in app/build.gradle.kts.
 */
object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }

    val auth get() = client.auth
    val postgrest get() = client.postgrest
    val storage get() = client.storage
}