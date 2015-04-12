package com.kmecpp.mcanalytics;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.mcanalytics.util.NetworkingUtil;
import com.kmecpp.mcanalytics.util.SQLUtil;

public class Main extends JavaPlugin {

	public static Main plugin;

	@Override
	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		getCommand("mcanalytics").setExecutor(new Commands());
		new EventListener();
		initializeDatabase();
		Bukkit.getScheduler().runTaskTimer(plugin, new TPS(), 100L, 1L);
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			public void run() {
				saveStatistics();
			}
		}, 0L, 1200L);
	}

	@Override
	public void onDisable() {
		Bukkit.broadcastMessage(ChatColor.GREEN + "Finializing MCAnalytics statistics data...");
		saveStatistics();
	}

	public static void saveStatistics() {
		try {
			NetworkingUtil.submitStatistics();
		} catch (Exception e) {
			Bukkit.getLogger().warning("[" + Main.plugin.getDescription().getName() + "]" + "Failed to submit global statistics! (Check your config or website)");
			e.printStackTrace();
		}
		try {
			SQLUtil.saveStatistics();
		} catch (Exception e) {
			Bukkit.getLogger().warning("[" + Main.plugin.getDescription().getName() + "]" + "Failed to save player statistics");
			e.printStackTrace();
		}
	}

	public static void initializeDatabase() {
		try {
			SQLUtil.getConnection().close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}