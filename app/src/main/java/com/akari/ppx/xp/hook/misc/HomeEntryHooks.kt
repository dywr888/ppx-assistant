@file:Suppress("unused")

package com.akari.ppx.xp.hook.misc

import android.view.View
import com.akari.ppx.utils.findClass
import com.akari.ppx.utils.hookAfterAllConstructors
import com.akari.ppx.utils.hookAfterMethod
import com.akari.ppx.xp.Init.cl
import com.akari.ppx.xp.hook.SwitchHook

/**
 * The bottom bar is built by MainBottomBarController and later re-shaped by a
 * server-driven tab config that swaps indicator views by position, so specs are
 * hidden at their common source instead: the indicator factory b.a(...) is the
 * only place that creates a tab view, and it receives the spec tag (args[1])
 * plus the resolved label (args[2]).
 */
private fun hideBottomTabs(vararg tags: String) {
    "com.sup.android.base.main.bottom.b".findClass(cl).hookAfterMethod(
        "a",
        android.content.Context::class.java,
        String::class.java,
        String::class.java,
        Int::class.javaPrimitiveType
    ) { param ->
        val tag = param.args[1]?.toString()
        val label = param.args[2]?.toString()
        if (tag in tags || label in tags) {
            (param.result as? View)?.visibility = View.GONE
        }
    }
}

class TopicTabHook : SwitchHook("hide_bottom_topic_tab") {
    override fun onHook() = hideBottomTabs("discovery", "话题")
}

class MessageTabHook : SwitchHook("hide_bottom_message_tab") {
    override fun onHook() = hideBottomTabs("message", "消息")
}

/** The home top-bar game icon is created solely by MiniGameHelper (dex
 * `feedui.util.aa`; constructor receives the stub and icon view ids). */
class HomeGameEntryHook : SwitchHook("hide_home_game_entry") {
    override fun onHook() {
        "com.sup.superb.feedui.util.aa".findClass(cl).hookAfterAllConstructors { param ->
            val container = param.args[0] as? View ?: return@hookAfterAllConstructors
            val stubId = param.args[1] as? Int ?: return@hookAfterAllConstructors
            val iconId = param.args[2] as? Int ?: return@hookAfterAllConstructors
            container.findViewById<View>(stubId)?.visibility = View.GONE
            container.findViewById<View>(iconId)?.visibility = View.GONE
        }
    }
}
