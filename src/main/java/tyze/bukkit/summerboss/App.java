package tyze.bukkit.summerboss;

import org.bukkit.plugin.java.JavaPlugin;
import tyze.bukkit.summerboss.SummerBoss;

public class App extends JavaPlugin {
    private SummerBoss eventSummerBoss;

    @Override
    public void onEnable() {
        getLogger().info("暑期 BOSS 插件啟動");
        eventSummerBoss = new SummerBoss();
        getServer().getPluginManager().registerEvents(eventSummerBoss, this);
    }

    @Override
    public void onDisable() {
        eventSummerBoss.disable();
        getLogger().info("暑期 BOSS 插件關閉");
    }
}