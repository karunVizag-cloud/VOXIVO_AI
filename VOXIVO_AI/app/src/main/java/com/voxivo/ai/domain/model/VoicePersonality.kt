package com.voxivo.ai.domain.model

enum class VoicePersonality(
    val displayName: String,
    val pitch: Float,
    val speechRate: Float,
    val sampleGreeting: String
) {
    FRIENDLY(
        displayName = "Friendly",
        pitch = 1.15f,
        speechRate = 1.05f,
        sampleGreeting = "Hey there! Ready to crush your goals today?"
    ),
    PRO(
        displayName = "Professional",
        pitch = 1.0f,
        speechRate = 1.0f,
        sampleGreeting = "Good day. Here is your scheduled agenda."
    ),
    CALM(
        displayName = "Calm",
        pitch = 0.90f,
        speechRate = 0.90f,
        sampleGreeting = "Take a deep breath. Your next reminder is here."
    ),
    MOTIVATE(
        displayName = "Motivate",
        pitch = 1.25f,
        speechRate = 1.15f,
        sampleGreeting = "Let's make things happen! Time for action!"
    ),
    POLITE(
        displayName = "Polite",
        pitch = 1.05f,
        speechRate = 0.98f,
        sampleGreeting = "Kindly note, you have an upcoming scheduled task."
    ),
    FIRM(
        displayName = "Firm",
        pitch = 0.95f,
        speechRate = 1.02f,
        sampleGreeting = "Attention required. Complete your pending reminder now."
    )
}

enum class VoiceGender {
    FEMALE,
    MALE
}

enum class LanguageMode(val localeTag: String, val displayName: String) {
    ENGLISH("en-US", "English"),
    TELUGU("te-IN", "తెలుగు (Telugu)"),
    HINDI("hi-IN", "हिन्दी (Hindi)"),
    AUTO("en-US", "Auto-Detect")
}
