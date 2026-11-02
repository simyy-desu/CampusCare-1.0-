package com.example.campuscare10.repository

import com.example.campuscare10.datamodel.StudentProfiles
import com.example.campuscare10.supabase.supabase
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

class UserRepository {

    suspend fun registerStudent(profile: StudentProfiles, password: String) {
        val email = profile.email ?: throw IllegalArgumentException("Email cannot be null")

        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        // 2. Insert the profile into your custom table
        supabase.from("Studentprofiles").insert(profile)
    }
}