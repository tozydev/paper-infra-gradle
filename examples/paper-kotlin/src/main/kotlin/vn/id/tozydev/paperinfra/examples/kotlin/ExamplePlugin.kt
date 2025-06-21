package vn.id.tozydev.paperinfra.examples.kotlin

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import org.bukkit.plugin.java.JavaPlugin
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ExamplePlugin : JavaPlugin() {
    @OptIn(ExperimentalTime::class)
    override fun onEnable() {
        componentLogger.info(text("Paper Infra Example Plugin is enabled!", GREEN))
        componentLogger.info(
            text("Current time: ", GRAY)
                .append(text(Clock.System.now().toString(), GOLD)),
        )
    }

    override fun onDisable() {
        slF4JLogger.info("Paper Infra Example Plugin is disabled!")
    }
}
