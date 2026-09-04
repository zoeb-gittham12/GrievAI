package com.rork.grievai.data

import android.util.Log
import com.rork.grievai.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

/**
 * Calls Google Gemini to classify a student complaint.
 * Admin AI Insights screens stay on MockRepository (hardcoded).
 */
object GeminiClassifier {

    // If this fails, try "gemini-1.5-flash" or "gemini-2.0-flash"
    private const val MODEL = "gemini-3.6-flash"
    private const val BASE =
        "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val http = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun classify(
        title: String,
        description: String,
        selectedCategory: String,
        selectedDepartment: String
    ): AIAnalysis {
        val apiKey = BuildConfig.GEMINI_API_KEY
        Log.d("Gemini", "key length = ${apiKey.length}")

        if (apiKey.isBlank()) {
            Log.e("Gemini", "GEMINI_API_KEY is empty — check local.properties + Gradle sync")
            return fallback(title, description, selectedCategory, selectedDepartment)
        }

        return try {
            val prompt = buildPrompt(title, description, selectedCategory, selectedDepartment)

            val requestBody = buildJsonObject {
                putJsonArray("contents") {
                    add(buildJsonObject {
                        putJsonArray("parts") {
                            add(buildJsonObject {
                                put("text", prompt)
                            })
                        }
                    })
                }
                putJsonObject("generationConfig") {
                    put("temperature", 0.2)
                    put("responseMimeType", "application/json")
                }
            }

            val responseText: String = http.post("$BASE?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            Log.d("Gemini", "raw response: ${responseText.take(800)}")

            val root = json.parseToJsonElement(responseText).jsonObject

            // Surface API error payloads (quota, invalid key, bad model, etc.)
            root["error"]?.let { err ->
                Log.e("Gemini", "API error object: $err")
                error("Gemini API error: $err")
            }

            val text = root["candidates"]
                ?.jsonArray
                ?.firstOrNull()
                ?.jsonObject
                ?.get("content")
                ?.jsonObject
                ?.get("parts")
                ?.jsonArray
                ?.firstOrNull()
                ?.jsonObject
                ?.get("text")
                ?.jsonPrimitive
                ?.content
                ?: error("Empty Gemini candidates/text")

            val cleaned = text
                .replace("```json", "")
                .replace("```", "")
                .trim()

            Log.d("Gemini", "cleaned JSON: ${cleaned.take(400)}")

            val parsed = json.decodeFromString(GeminiResult.serializer(), cleaned)

            AIAnalysis(
                category = parsed.category.ifBlank { selectedCategory },
                priority = parsePriority(parsed.priority),
                confidenceScore = parsed.confidence.coerceIn(0.5f, 0.99f),
                suggestedDepartment = parsed.suggestedDepartment.ifBlank {
                    selectedDepartment.ifBlank { selectedCategory }
                },
                insights = parsed.insight.ifBlank {
                    "Classified by Gemini based on complaint content."
                },
                topComplaintType = parsed.category.ifBlank { selectedCategory }
            )
        } catch (e: Exception) {
            Log.e("Gemini", "classify failed: ${e.message}", e)
            fallback(title, description, selectedCategory, selectedDepartment)
        }
    }

    private fun buildPrompt(
        title: String,
        description: String,
        selectedCategory: String,
        selectedDepartment: String
    ): String = """
        You are GrievAI, a university student grievance classifier.
        Analyze the complaint and return ONLY valid JSON with these exact keys:
        {
          "category": string,
          "priority": "LOW" | "MEDIUM" | "HIGH" | "CRITICAL",
          "confidence": number between 0 and 1,
          "suggestedDepartment": string,
          "insight": string (1-2 sentences for the admin)
        }

        Rules:
        - priority CRITICAL only for safety, harassment, health, discrimination, emergencies
        - priority HIGH for exams, fees, scholarships, network outages, academic blockers
        - prefer the student's selected category/department if they are reasonable
        - insight must be specific to THIS complaint, not generic

        Student-selected category: $selectedCategory
        Student-selected department: $selectedDepartment

        Title: $title
        Description: $description
    """.trimIndent()

    private fun parsePriority(raw: String): ComplaintPriority =
        when (raw.trim().uppercase()) {
            "CRITICAL" -> ComplaintPriority.CRITICAL
            "HIGH" -> ComplaintPriority.HIGH
            "MEDIUM" -> ComplaintPriority.MEDIUM
            else -> ComplaintPriority.LOW
        }

    private fun fallback(
        title: String,
        description: String,
        category: String,
        department: String
    ): AIAnalysis {
        val text = "$title $description $category".lowercase()
        val priority = when {
            listOf(
                "harassment", "ragging", "safety", "health", "hygien",
                "discriminat", "urgent", "emergency"
            ).any { it in text } -> ComplaintPriority.CRITICAL
            listOf(
                "exam", "fee", "scholarship", "wifi", "network", "broken", "delay"
            ).any { it in text } -> ComplaintPriority.HIGH
            listOf("facility", "transport", "hostel", "library")
                .any { it in text } -> ComplaintPriority.MEDIUM
            else -> ComplaintPriority.LOW
        }
        return AIAnalysis(
            category = category,
            priority = priority,
            confidenceScore = 0.75f,
            suggestedDepartment = department.ifBlank { category },
            insights = "Offline heuristic classification (Gemini unavailable). Keywords suggest ${priority.label} priority.",
            topComplaintType = category
        )
    }
}

@Serializable
private data class GeminiResult(
    val category: String = "",
    val priority: String = "LOW",
    val confidence: Float = 0.8f,
    val suggestedDepartment: String = "",
    val insight: String = ""
)