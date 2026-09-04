package com.rork.grievai.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Mirrors the columns of the `profiles` table in Supabase (see schema_v2). */
@Serializable
data class ProfileDto(
    val id: String,
    @SerialName("institution_id") val institutionId: String,
    val name: String,
    val email: String,
    @SerialName("id_card_number") val idCardNumber: String,
    val role: UserRole,
    val department: String,
    @SerialName("enrollment_number") val enrollmentNumber: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("join_date") val joinDate: String
)

/** Mirrors the `universities` table — used to resolve institutionName for display. */
@Serializable
data class UniversityDto(
    val id: String,
    val name: String,
    @SerialName("join_code") val joinCode: String
)

fun ProfileDto.toUser(institutionName: String = "",institutionJoinCode: String = ""): User = User(
    id = id,
    name = name,
    email = email,
    idCardNumber = idCardNumber,
    institutionId = institutionId,
    institutionName = institutionName,
    institutionJoinCode = institutionJoinCode,
    role = role,
    department = department,
    enrollmentNumber = enrollmentNumber,
    avatarUrl = avatarUrl,
    joinDate = joinDate
)