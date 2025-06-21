package vn.id.tozydev.paperinfra.examples.shadow;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

import kotlin.time.Clock;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {
  @Override
  public void onEnable() {
    getComponentLogger().info(text("Paper Infra Example Plugin enabled!", GREEN));
    getComponentLogger()
        .info(
            text("Current time: ", GRAY)
                .append(text(Clock.System.INSTANCE.now().toString(), GOLD)));
  }

  @Override
  public void onDisable() {
    getComponentLogger().info(text("Paper Infra Example Plugin disabled!", RED));
  }
}
