package com.positivenews.app.data

/**
 * Lightweight on-device lexicon scorer. No ML, no API — runs offline against
 * the RSS title + summary so we can filter CNN/BBC headlines down to the
 * uplifting ones without leaking any reading data off-device.
 *
 * Score = (positive hits) - (negative hits * 2). Negatives weigh more because
 * mainstream RSS skews negative; a single hard-negative word should be enough
 * to drop a headline even when several mildly-positive words appear.
 */
object PositivityFilter {

    private val POSITIVE: Set<String> = setOf(
        "breakthrough", "rescue", "rescued", "saves", "saved", "saving",
        "hero", "heroic", "heroes", "celebrate", "celebrates", "celebration",
        "record", "milestone", "first ever", "discovery", "discovered",
        "cure", "cured", "healed", "recovery", "recovered", "thrive", "thriving",
        "soars", "soared", "surges", "wins", "won", "winning", "victory", "champion",
        "achievement", "achieves", "achieved", "achieve", "triumph",
        "donate", "donated", "donation", "raises", "raised", "fundraiser",
        "kindness", "generous", "generosity", "uplift", "uplifting",
        "love", "wholesome", "joy", "joyful", "hopeful", "hope",
        "smile", "smiling", "laughs", "laughter",
        "reunite", "reunited", "reunion",
        "innovat", "invent", "invention", "launch", "launched",
        "improve", "improved", "improvement", "progress", "growing", "boost",
        "thank", "thanks", "gratitude", "grateful",
        "adopt", "adopted", "adoption", "born", "newborn", "baby",
        "graduate", "graduated", "graduation", "scholarship",
        "peace", "peaceful", "treaty", "agreement", "deal reached",
        "clean energy", "renewable", "reforest", "restored", "restoration",
        "conservation", "protected", "protects", "protected species",
        "free of", "freed", "rescues", "found alive", "survived", "survivor",
        "best", "amazing", "incredible", "remarkable", "stunning", "delighted",
        "vaccine", "treatment approved", "approved", "cleared", "milestone",
        "olympic", "gold medal", "world cup", "champion",
    )

    private val NEGATIVE: Set<String> = setOf(
        "kill", "killed", "killing", "killer", "murder", "murders", "murdered",
        "dead", "death", "deaths", "dying", "died", "fatal", "fatality", "fatalities",
        "shoot", "shooting", "shot", "shooter", "gunman", "gunfire",
        "stab", "stabbed", "stabbing", "assault", "assaulted", "rape", "raped",
        "war", "warfare", "battle", "missile", "airstrike", "strike kills",
        "bomb", "bombing", "bomber", "explosion", "explode", "blast", "blasts",
        "attack", "attacks", "attacked", "attacker", "terror", "terrorist", "terrorism",
        "hostage", "kidnap", "kidnapped", "abduct", "abducted",
        "crash", "crashes", "crashed", "collide", "collision", "wreck",
        "fire kills", "wildfire", "burns down", "burned down",
        "flood", "flooding", "hurricane", "tornado", "earthquake", "tsunami",
        "disaster", "catastrophe", "tragedy", "tragic",
        "outbreak", "epidemic", "pandemic", "virus spreads",
        "abuse", "abused", "abuser", "scandal",
        "arrest", "arrested", "charged", "indicted", "convicted", "jailed",
        "prison", "guilty", "fraud", "fraudster", "scam", "scammer", "scammed",
        "lawsuit", "sued", "suing",
        "crisis", "collapse", "collapsed", "plunge", "plunges", "plummet", "plummets",
        "recession", "layoff", "layoffs", "fired", "bankrupt", "bankruptcy",
        "hate", "racist", "racism", "discrimination",
        "protest turns violent", "riot", "rioting", "rioters",
        "missing", "vanished", "feared dead",
        "ban", "banned", "banning", "shutdown", "shut down",
        "warning", "warned", "threat", "threats", "threatens", "threatened",
        "drug bust", "overdose", "overdoses",
        "cancer", "tumor",
        "controversy", "controversial", "outrage", "outraged", "furious",
        "slams", "slammed", "blasts", "rips into",
    )

    fun score(title: String, description: String): Int {
        val text = (title + " " + description).lowercase()
        val pos = POSITIVE.count { text.contains(it) }
        val neg = NEGATIVE.count { text.contains(it) }
        return pos - neg * 2
    }

    /** Threshold for "positive enough to show". */
    const val THRESHOLD = 1
}
