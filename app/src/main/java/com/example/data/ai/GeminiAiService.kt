package com.example.data.ai

import android.content.Context
import com.example.data.models.MotorcycleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAiService(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

    // API Key resolution: Securely retrieves key injected into BuildConfig from .env by secrets-gradle-plugin
    private fun getApiKey(): String {
        return try {
            val buildConfigClass = Class.forName("com.example.BuildConfig")
            val field = buildConfigClass.getField("GEMINI_API_KEY")
            val key = field.get(null) as? String
            if (!key.isNullOrBlank() && key != "MY_GEMINI_API_KEY") {
                key.trim()
            } else {
                System.getenv("GEMINI_API_KEY") ?: ""
            }
        } catch (e: Throwable) {
            System.getenv("GEMINI_API_KEY") ?: ""
        }
    }

    /**
     * Calls Gemini 2.5 Flash / 1.5 Flash endpoint to generate response.
     */
    suspend fun generateContent(systemPrompt: String, userMessage: String): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalStateException("Gemini API key is not configured."))
        }

        // Try primary model gemini-2.5-flash, fallback to gemini-1.5-flash
        val models = listOf("gemini-2.5-flash", "gemini-1.5-flash", "gemini-2.0-flash")

        var lastError: Exception? = null
        for (model in models) {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", "$systemPrompt\n\nUser Question:\n$userMessage"))
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.7)
                        put("maxOutputTokens", 1024)
                    })
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val request = Request.Builder()
                    .url(url)
                    .post(jsonBody.toString().toRequestBody(mediaType))
                    .build()

                client.newCall(request).execute().use { response ->
                    val respBody = response.body?.string() ?: ""
                    if (!response.isSuccessful) {
                        lastError = RuntimeException("Gemini API HTTP ${response.code}: $respBody")
                        return@use // continue to next model
                    }

                    val json = JSONObject(respBody)
                    val candidates = json.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val content = candidates.getJSONObject(0).optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text", "")
                            if (text.isNotBlank()) {
                                return@withContext Result.success(text.trim())
                            }
                        }
                    }
                    lastError = RuntimeException("Invalid or empty response structure from Gemini API.")
                }
            } catch (e: Exception) {
                lastError = e
            }
        }

        Result.failure(lastError ?: RuntimeException("Unable to contact Gemini AI services."))
    }

    /**
     * Motorcycle Buying Advisor: provides tailored recommendations based on the user's inquiry
     * and the MotoScope database.
     */
    suspend fun askAdvisor(userQuery: String, bikeCatalogContext: String): Result<String> {
        val systemPrompt = """
            You are MotoScope AI, a world-class motorcycle automotive engineer, rider expert, and buyer's advisor.
            You have deep knowledge of motorcycle engineering, ergonomics, maintenance, mileage, performance, and riding comfort.
            
            App Catalog Bikes Context:
            $bikeCatalogContext
            
            Guidelines:
            1. Be warm, enthusiastic, concise, and structured.
            2. Match the user's rider profile (budget, height, experience level, daily commuting vs track vs touring).
            3. Mention specific bikes from the MotoScope catalog when relevant.
            4. Highlight key considerations like seat height, weight, mileage, and ABS safety.
            5. Use bullet points and clear headings for easy readability on mobile screens.
        """.trimIndent()

        return generateContent(systemPrompt, userQuery)
    }

    /**
     * Generates a head-to-head AI verdict for 2 or 3 motorcycles being compared.
     */
    suspend fun generateComparisonAiVerdict(
        bikeA: MotorcycleEntity,
        bikeB: MotorcycleEntity,
        bikeC: MotorcycleEntity? = null,
        priority: String
    ): Result<String> {
        val summaryA = "${bikeA.modelName}: ${bikeA.specs.displacementCc}cc, ${bikeA.specs.maxPowerHp} HP, ${bikeA.specs.maxTorqueNm} Nm, ${bikeA.specs.kerbWeightKg} kg, Seat: ${bikeA.specs.seatHeightMm}mm, Mileage: ${bikeA.specs.mileageKmpl} kmpl, Price: ${bikeA.priceDisplay}"
        val summaryB = "${bikeB.modelName}: ${bikeB.specs.displacementCc}cc, ${bikeB.specs.maxPowerHp} HP, ${bikeB.specs.maxTorqueNm} Nm, ${bikeB.specs.kerbWeightKg} kg, Seat: ${bikeB.specs.seatHeightMm}mm, Mileage: ${bikeB.specs.mileageKmpl} kmpl, Price: ${bikeB.priceDisplay}"
        val summaryC = if (bikeC != null) {
            "${bikeC.modelName}: ${bikeC.specs.displacementCc}cc, ${bikeC.specs.maxPowerHp} HP, ${bikeC.specs.maxTorqueNm} Nm, ${bikeC.specs.kerbWeightKg} kg, Seat: ${bikeC.specs.seatHeightMm}mm, Mileage: ${bikeC.specs.mileageKmpl} kmpl, Price: ${bikeC.priceDisplay}"
        } else "None"

        val prompt = """
            Motorcycles Head-to-Head Shootout:
            - Bike 1: $summaryA
            - Bike 2: $summaryB
            ${if (bikeC != null) "- Bike 3: $summaryC" else ""}
            
            User's Priority / Riding Mode: $priority
            
            Please deliver:
            1. Final Winner Verdict: Who takes the crown for this specific priority and why?
            2. Who Should Buy Which Bike: A direct 1-sentence recommendation for each bike.
            3. Key Trade-off: What you give up by choosing one over the other.
            Keep it punchy, insightful, and formatted with bullet points for mobile reading.
        """.trimIndent()

        val systemPrompt = "You are a professional motorcycle test journalist and dyno engineer delivering an authoritative comparison verdict."
        return generateContent(systemPrompt, prompt)
    }

    /**
     * Generates deep rider insights, ownership advice, and pros & cons for a specific motorcycle.
     */
    suspend fun analyzeMotorcycleInsights(bike: MotorcycleEntity): Result<String> {
        val bikeSpecs = """
            Model: ${bike.modelName} (${bike.modelYear})
            Category: ${bike.category}
            Price: ${bike.priceDisplay}
            Engine: ${bike.specs.engineType}, ${bike.specs.displacementCc}cc, ${bike.specs.maxPowerHp} HP @ ${bike.specs.maxPowerRpm} RPM, ${bike.specs.maxTorqueNm} Nm
            Weight & Seat: ${bike.specs.kerbWeightKg} kg, Seat Height: ${bike.specs.seatHeightMm} mm
            Brakes & Tech: ${bike.specs.absSystem}, Traction Control: ${bike.specs.hasTractionControl}, Quickshifter: ${bike.specs.hasQuickShifter}
            Fuel Tank & Mileage: ${bike.specs.fuelTankCapacityL} L, ${bike.specs.mileageKmpl} km/l
        """.trimIndent()

        val prompt = """
            Analyze this motorcycle for prospective buyers:
            $bikeSpecs
            
            Provide:
            - Rider Ergonomics & Seat Height Suitability (who can flat-foot easily)
            - City Commuting vs Highway Capability
            - Top 2 Strengths & 1 Limitation
            - Maintenance & Running Cost Expectations
            Keep your advice concise and clear with bullet points.
        """.trimIndent()

        val systemPrompt = "You are an automotive motorcycle mechanic and experienced road tester."
        return generateContent(systemPrompt, prompt)
    }
}
