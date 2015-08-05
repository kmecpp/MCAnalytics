package com.kmecpp.mcanalytics;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.kmecpp.mcanalytics.metrics.Metrics;
import com.kmecpp.mcanalytics.util.NetworkingUtil;
import com.kmecpp.mcanalytics.util.SQLUtil;

public class Main extends JavaPlugin {

	public static Main plugin;

	@Override
	public void onLoad() {
		plugin = this;
	}

	@Override
	public void onEnable() {
		//Configuration
		saveDefaultConfig();

		//Commands/Listeners
		getCommand("mcanalytics").setExecutor(new Commands());
		new EventListener();

		//Database
		try {
			SQLUtil.getConnection().close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		//Tasks
		Bukkit.getScheduler().runTaskTimer(plugin, new TPS(), 100L, 1L);
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				saveStatistics();
			}
		}, 0L, 1200L);

		//Metrics
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
		}
	}

	@Override
	public void onDisable() {
		Bukkit.broadcastMessage(ChatColor.GREEN + "Finalizing " + Main.plugin.getName() + " statistics data...");
		saveStatistics();
	}

	public static void saveStatistics() {
		try {
			NetworkingUtil.submitStatistics();
		} catch (Exception e) {
			Main.log(Level.WARNING, "Failed to submit global statistics! (Check your config or website)");
			e.printStackTrace();
		}
		try {
			SQLUtil.saveStatistics();
		} catch (Exception e) {
			Main.log(Level.WARNING, "Failed to save player statistics!");
			e.printStackTrace();
		}
	}

	public static void log(Level level, String message) {
		Bukkit.getLogger().log(level, "[" + Main.plugin.getName() + "] " + message);
	}
}