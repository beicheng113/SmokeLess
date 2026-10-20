package com.example.smokeless.util

/**
 * Short, non-judgmental lines shown once after a breathing round.
 * The list lives in code; no API, no network, no tracking.
 */
object CravingPhrases {

    private val phrases = listOf(
        "烟瘾的平均寿命只有几分钟。",
        "你已经撑过了这一轮。",
        "这种冲动会来，也会走。",
        "慢下来的呼吸，会带着你走过去。",
        "这一刻不需要做更多。",
        "你只需要继续呼吸。",
        "刚才那一分钟，已经属于你了。",
        "想抽的感觉，正在慢慢退潮。",
        "你没有错过什么，你只是在继续。",
        "这一轮结束了，下一次也可以这样。",
    )

    fun random(): String = phrases.random()
}
