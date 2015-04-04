package com.kmecpp.mcanalytics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.kmecpp.mcanalytics.util.NetworkingUtil;
import com.kmecpp.mcanalytics.util.SQLUtil;

public class Commands implements CommandExecutor {

	public final String invalid = ChatColor.RED + "That is not a valid command! Type /stats for a list of commands";

	@Override
	public boolean onCommand(final CommandSender out, Command cmd, String commandLabel, String[] args) {
		if ((commandLabel.equalsIgnoreCase("stats")) || (commandLabel.equalsIgnoreCase("mcstats")) || (commandLabel.equalsIgnoreCase("mca")) || (commandLabel.equalsIgnoreCase("+ Main.plugin.getName() +"))) {
			if (args.length == 0) {
				out.sendMessage("");
				out.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + Main.plugin.getName() + " Commands");
				out.sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "----------------------------------------");
				out.sendMessage("");
				out.sendMessage(ChatColor.GREEN + "/stats view    " + ChatColor.AQUA + "Views the current servers stats");
				out.sendMessage(ChatColor.GREEN + "/stats mystats " + ChatColor.AQUA + "Views your current stats");
				out.sendMessage(ChatColor.GREEN + "/stats check   " + ChatColor.AQUA + "Views the specified players stats");
				out.sendMessage(ChatColor.GREEN + "/stats local   " + ChatColor.AQUA + "Displays current statistic packet info");
				out.sendMessage(ChatColor.GREEN + "/stats submit  " + ChatColor.AQUA + "Pushes current local data up to the web server");
				out.sendMessage(ChatColor.GREEN + "/stats save    " + ChatColor.AQUA + "Saves individual player data to database");
				out.sendMessage(ChatColor.GREEN + "/stats reload  " + ChatColor.AQUA + "Reloads the configuration file");
				out.sendMessage(ChatColor.GREEN + "/stats info    " + ChatColor.AQUA + "Displays information about the plugin");
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("view")) {
					Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

						public void run() {
							try {
								String statistics = NetworkingUtil.getStatistics();
								out.sendMessage("");
								out.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "RubberVoltz Server Statistics");
								out.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "---------------------------------");
								out.sendMessage("");
								for (String stat : statistics.split("<br>")) {
									String[] parts = stat.split(":");
									out.sendMessage(ChatColor.GREEN + parts[0] + ChatColor.AQUA + parts[1]);
								}
							} catch (IOException e) {
								out.sendMessage(ChatColor.RED + "Unable to retrieve statistics!");
							}
						}
					});
				} else if (args[0].equalsIgnoreCase("mystats")) {
					if ((out instanceof Player)) {
						final Player player = (Player) out;
						Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

							public void run() {
								try {
									out.sendMessage("");
									out.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Your RubberVoltz Statistics");
									out.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "---------------------------------");
									out.sendMessage("");
									for (Entry<Statistic.PlayerStat, Object> entry : SQLUtil.getPlayerStatistics(new PlayerInfo(player.getUniqueId(), player.getName())).entrySet()) {
										player.sendMessage(ChatColor.GREEN + ((Statistic.PlayerStat) entry.getKey()).getPrettyValue() + ": " + ChatColor.AQUA + entry.getValue().toString());
									}
								} catch (Exception e) {
									out.sendMessage(ChatColor.RED + "Unable to retrieve your statistics! Please report this!");
								}
							}
						});
					} else {
						out.sendMessage(ChatColor.RED + "Only in game players can execute this command!");
					}
				} else if (args[0].equalsIgnoreCase("check")) {
					out.sendMessage(ChatColor.RED + "Usage: /stats check <player>");
				} else if (args[0].equalsIgnoreCase("local")) {
					out.sendMessage("");
					out.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Local Statistics (Data from the past " + EventListener.statistics.get(Statistic.SECONDS_ONLINE) + " seconds)");
					out.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "---------------------------------");
					out.sendMessage("");
					for (Statistic stat : EventListener.statistics.keySet()) {
						Integer value = EventListener.statistics.get(stat);
						if (value.intValue() == -1) {
							value = Integer.valueOf(0);
						}
						out.sendMessage(ChatColor.GREEN + stat.getPrettyValue() + ": " + ChatColor.AQUA + value);
					}
				} else if (args[0].equalsIgnoreCase("submit")) {
					if (out.isOp()) {
						try {
							NetworkingUtil.submitStatistics();
							out.sendMessage(ChatColor.GREEN + "Local statistics submitted successfully");
						} catch (IOException e) {
							out.sendMessage(ChatColor.RED + "Failed to submit statistics! Check console for details!");
							e.printStackTrace();
						}
					} else {
						out.sendMessage(ChatColor.RED + "You do not have permission to perform this command!");
					}
				} else if (args[0].equalsIgnoreCase("save")) {
					if (out.isOp()) {
						try {
							long timeTaken = SQLUtil.saveStatistics().longValue();
							out.sendMessage(ChatColor.GREEN + "Player statistics saved successfully to database! (" + timeTaken + "ms)");
						} catch (Exception e) {
							out.sendMessage(ChatColor.RED + "Failed to save statistics! Check console for details!");
							e.printStackTrace();
						}
					} else {
						out.sendMessage(ChatColor.RED + "You do not have permission to perform this command!");
					}
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (out.isOp()) {
						Main.plugin.reloadConfig();
						out.sendMessage(ChatColor.GREEN + Main.plugin.getName() + " configuration file reloaded!");
					} else {
						out.sendMessage(ChatColor.RED + "You do not have permission to perform this command!");
					}
				} else if (args[0].equalsIgnoreCase("info")) {
					out.sendMessage("");
					out.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + Main.plugin.getName() + " Information");
					out.sendMessage(ChatColor.YELLOW + "--------------------------------");
					out.sendMessage("");
					PluginDescriptionFile pdfFile = Main.plugin.getDescription();
					out.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Author:  " + ChatColor.AQUA + ChatColor.BOLD + pdfFile.getAuthors().get(0));
					out.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "Version: " + ChatColor.AQUA + ChatColor.BOLD + pdfFile.getVersion());
					out.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "GitHub:  " + ChatColor.AQUA + ChatColor.BOLD + "https://github.com/kmecpp/MCAnalytics");
				} else {
					out.sendMessage(this.invalid);
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("check")) {
					final String playerName = args[1];
					Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

						public void run() {
							ArrayList<String> statistics = new ArrayList<String>();
							try {
								for (Entry<Statistic.PlayerStat, Object> entry : SQLUtil.getPlayerStatistics(new PlayerInfo(null, playerName)).entrySet()) {
									statistics.add(ChatColor.GREEN + ((Statistic.PlayerStat) entry.getKey()).getPrettyValue() + ": " + ChatColor.AQUA + entry.getValue().toString());
								}
							} catch (Exception e) {
								out.sendMessage(ChatColor.RED + "That player does not exist!");
								return;
							}
							out.sendMessage("");
							out.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + playerName + "'s RubberVoltz Statistics");
							out.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "---------------------------------");
							out.sendMessage("");
							for (String statistic : statistics) {
								out.sendMessage(statistic);
							}
						}
					});
				} else {
					out.sendMessage(this.invalid);
				}
			} else {
				out.sendMessage(this.invalid);
			}
		}
		return false;
	}
}