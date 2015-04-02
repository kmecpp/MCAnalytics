package com.kmecpp.mcanalytics;

import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.mcanalytics.util.NetworkingUtil;
import com.kmecpp.mcanalytics.util.SQLUtil;

public class Main extends JavaPlugin {
	public static Main plugin;

	public void onEnable() {
		plugin = this;
		saveDefaultConfig();

		getCommand("statistics").setExecutor(new Commands());

		new EventListener();

		Bukkit.getScheduler().runTaskTimer(plugin, new TPS(), 100L, 1L);
		try {
			SQLUtil.getConnection().close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			public void run() {
				try {
					SQLUtil.saveStatistics();
					NetworkingUtil.submitStatistics();
				} catch (IOException e) {
					Bukkit.getLogger().warning("[" + Main.plugin.getDescription().getName() + "]" + "Failed to submit statistics!");
				}
			}
		}, 0L, 1200L);
	}

	public void onDisable() {
		Bukkit.broadcastMessage(ChatColor.GREEN + "Finializing MCAnalytics statistics data...");
		try {
			NetworkingUtil.submitStatistics();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}