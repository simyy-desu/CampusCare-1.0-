package com.example.campuscare10.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.gotrue.Auth

const val SUPABASE_URL = "https://xphmrjqtuzdujfdpdrzs.supabase.co"
const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhwaG1yanF0dXpkdWpmZHBkcnpzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODgyNTQxODQsImV4cCI6MjEwMzgzMDE4NH0.HNQXnX89FlANdBaTB8WWUabjVq-93_mLMqrE8vhiwGo"

val supabase = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_KEY
) {
    install(Postgrest)
    install(Storage)
    install(Auth)
}