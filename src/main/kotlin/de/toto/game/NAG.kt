package de.toto.game

/**
 * Numeric annotation glyphs
 *
 * @see <link>https://en.wikipedia.org/wiki/Numeric_Annotation_Glyphs</link>
 */
enum class NAG(val nag: String, val pgn: String) {

    GOOD_MOVE("$1", "!"),
    POOR_MOVE("$2", "?"),
    VERY_GOOD_MOVE("$3", "\u203C"), // ‼
    VERY_POOR_MOVE("$4", "??"), // "\u2047"
    INTERESTING_MOVE("$5", "!?"), // "\u2049"
    DUBIOUS_MOVE("$6", "?!"), // "\u2048"
    FORCED_MOVE("$7", "\u25A1"), // □
    SINGULAR_MOVE("$8", "\u25A1"), // □
    EVEN_POSITION("$10", "="),
    EVEN_POSITION2("$11", "="),
    UNCLEAR_POSITION("$13", "\u221E"), // ∞
    SLIGHT_ADVANTAGE_WHITE("$14", "+="),
    SLIGHT_ADVANTAGE_BLACK("$15", "=+"),
    ADVANTAGE_WHITE("$16", "+/-"),
    ADVANTAGE_BLACK("$17", "-/+"),
    DECISIVE_ADVANTAGE_WHITE("$18", "+-"),
    DECISIVE_ADVANTAGE_BLACK("$19", "-+"),
    ZUGZWANG_WHITE("$22", "\u2A00"), // ⨀
    ZUGZWANG_BLACK("$23", "\u2A00"), // ⨀
    DEVELOPMENT_ADVANTAGE_WHITE("$32", "\u27F3"), // ⟳
    DEVELOPMENT_ADVANTAGE_BLACK("$33", "\u27F3"), // ⟳
    INITIATIVE_WHITE("$36", "\u2192"), // →
    INITIATIVE_BLACK("$37", "\u2192"), // →
    ATTACK_WHITE("$40", "\u2191"), //↑
    ATTACK_BLACK("$41", "\u2191"), //↑
    COMPENSATION_WHITE("$44", "=/" + "\u221E"), // =/∞
    COMPENSATION_BLACK("$45", "\u221E" + "/="), // ∞/=
    COUNTERPLAY_WHITE("$132", "\u21C6"), // ⇆
    COUNTERPLAY_BLACK("$133", "\u21C6"), // ⇆
    WITH_THE_IDEA("$140", "\u2206"), // ∆
    BETTER_IS("$142", "\u2313"), // ⌓
    EDITORIAL_COMMENT("$145", "RR"),
    NOVELTY("$146", "N"),
    FILE("$239", "\u21D4"), // ⇔
    DIAGONAL("$240", "\u21D7"), // ⇗
    KING_SIDE("$242", "\u27EB"), // ⟫
    QUEEN_SIDE("$243", "\u27EA"), // ⟪
    WEAK_POINT("$244", "\u2715"), // ✕
    ENDING("$245", "\u22A5"), //⊥
    UNKNOWN("<?>", "<unknown de.toto.game.NAG>");

    override fun toString(): String {
        return pgn
    }

    val isPositionEval = nag.drop(1).toIntOrNull() in 10..135

}

fun getNag(nag: String): NAG {
    return NAG.values().firstOrNull { it.nag == nag } ?: NAG.UNKNOWN
}