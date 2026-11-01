package com.example.data.model

enum class EscapeMode(val title: String, val description: String) {
    LOGIC_PUZZLE(
        "Logic Puzzle",
        "Solve 3 multi-step logic & math puzzles with a mindful 15s cool-off to exit."
    ),
    MASTER_PIN(
        "Master Passcode / Parental PIN",
        "Exit using a pre-configured 4-6 digit emergency passcode or parental PIN."
    ),
    OVERRIDE_PHRASE(
        "Solemn Override Phrase",
        "Type a strict commitment breaking phrase after an emergency countdown."
    ),
    STRICT_UNBREAKABLE(
        "Strict Zen Lock",
        "No early exit permitted under any circumstance until the timer expires."
    )
}
