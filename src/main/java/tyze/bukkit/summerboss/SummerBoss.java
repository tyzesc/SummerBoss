package tyze.bukkit.summerboss;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

public class SummerBoss implements Listener, CommandExecutor {
    private ScoreboardManager manager = Bukkit.getScoreboardManager();
    private Scoreboard scoreboard;
    private Objective objective;
    private Entity entityBoss;
    private YamlConfiguration config;
    private BukkitAPIHelper mmApi = new BukkitAPIHelper();
    private String bossName = "大象大象生出小象";

    private static String configFilePath = "plugins/SummerBoss/saves.yml";

    public SummerBoss() {
        Bukkit.getPluginCommand("summerboss").setExecutor(this);
        Bukkit.getPluginCommand("sboss").setExecutor(this);

        initializeScoreBoard();
        loadFile();
    }

    private void initializeScoreBoard() {
        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("積分", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length >= 1 && args[0].equals("create") && player.hasPermission("summerboss.create")) {
                spawnBoss(player.getLocation());
                return true;
            }
            if (args.length >= 1 && args[0].equals("save") && player.hasPermission("summerboss.create")) {
                saveFile();
                return true;
            }
            if (args.length >= 1 && args[0].equals("test") && player.hasPermission("summerboss.create")) {
                Bukkit.getLogger().info(" " + entityBoss.getLocation());
            }
            if (args.length >= 1 && args[0].equals("reset") && player.hasPermission("summerboss.reset")) {
                initializeScoreBoard();
            }
            if (args.length >= 1 && args[0].equals("off") && player.hasPermission("summerboss.off")) {
                player.setScoreboard(manager.getNewScoreboard());
            }

            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (isBoss(event.getEntity())) {
            Entity damager = event.getDamager();

            Player player = null;
            int damage = (int) Math.round(event.getFinalDamage());

            if (damager instanceof Player) {
                player = (Player) damager;

            }

            if (event.getCause().equals(DamageCause.PROJECTILE) && damager instanceof Arrow) {
                Arrow a = (Arrow) damager;
                player = (Player) a.getShooter();
                // Bukkit.getLogger().info(" " + a.toString() + " " + player);
            }

            if (player != null && damage != 0) {
                Score score = objective.getScore(player);
                score.setScore(score.getScore() + damage);

                player.setScoreboard(scoreboard);
            }
            event.setDamage(0);
        }
    }

    private void spawnBoss(Location location) {
        try {
            entityBoss = mmApi.spawnMythicMob(bossName, location);
            ((Skeleton) entityBoss).setHealth(2);
        } catch (Exception e) {
        }
    }

    private boolean isBoss(Entity entity) {
        if (entity == null)
            return false;

        ActiveMob mob = mmApi.getMythicMobInstance(entity);
        if (mob == null)
            return false;

        return mob.getType().equals(mmApi.getMythicMob(bossName));
    }

    public void disable() {
        saveFile();
    }

    private void saveFile() {
        File file = new File(configFilePath);
        if (config == null)
            config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection scores = config.createSection("scores");
        for (String entry : scoreboard.getEntries()) {
            scores.set(entry, objective.getScore(entry).getScore());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFile() {
        File file = new File(configFilePath);
        if (file.exists() == false)
            return;

        config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection scores = config.getConfigurationSection("scores");
        for (String entry : scores.getKeys(false)) {
            objective.getScore(entry).setScore(scores.getInt(entry));
        }
    }

}