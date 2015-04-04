package com.kmecpp.mcanalytics;

import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import com.kmecpp.mcanalytics.Statistic.PlayerStat;

public class EventListener implements Listener {

	public static LinkedHashMap<Statistic, Integer> statistics = new LinkedHashMap<Statistic, Integer>();
	public static LinkedHashMap<PlayerInfo, LinkedHashMap<PlayerStat, Integer>> playerStats = new LinkedHashMap<PlayerInfo, LinkedHashMap<PlayerStat, Integer>>();

	public EventListener() {
		Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
		for (Statistic stat : Statistic.values()) {
			if (stat != Statistic.KEY) {
				statistics.put(stat, Integer.valueOf(-1));
			}
		}
		Bukkit.getScheduler().runTaskTimerAsynchronously(Main.plugin, new Runnable() {

			public void run() {
				EventListener.incrementStatistic(Statistic.SECONDS_ONLINE);
			}
		}, 0L, 20L);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		incrementStatistic(Statistic.PLAYER_JOINS);
		if (!e.getPlayer().hasPlayedBefore()) {
			statistics.put(Statistic.UNIQUE_PLAYERS, Integer.valueOf(Bukkit.getOfflinePlayers().length));
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		incrementStatistic(Statistic.BLOCKS_BROKEN);
		incrementPlayerStat(e.getPlayer(), Statistic.PlayerStat.BLOCKS_BROKEN);
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		incrementStatistic(Statistic.BLOCKS_PLACED);
		incrementPlayerStat(e.getPlayer(), Statistic.PlayerStat.BLOCKS_PLACED);
	}

	@EventHandler
	public void onAsyncChat(AsyncPlayerChatEvent e) {
		incrementStatistic(Statistic.CHAT_MESSAGES);
		incrementPlayerStat(e.getPlayer(), Statistic.PlayerStat.CHAT_MESSAGES);
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		String[] commands = { "/r ", "/reply ", "/t", "/tell ", "/m ", "/msg " };
		for (String command : commands) {
			if (e.getMessage().toLowerCase().startsWith(command.toLowerCase())) {
				incrementStatistic(Statistic.CHAT_MESSAGES);
				incrementPlayerStat(e.getPlayer(), Statistic.PlayerStat.CHAT_MESSAGES);
				break;
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		incrementStatistic(Statistic.ITEMS_DROPPED);
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		incrementStatistic(Statistic.INVENTORIES_OPENED);
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		incrementStatistic(Statistic.CHUNKS_LOADED);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		Player killer = e.getEntity().getKiller();
		if ((e.getEntity() instanceof Player)) {
			Player playerKilled = (Player) e.getEntity();
			incrementStatistic(Statistic.PLAYERS_KILLED);
			incrementPlayerStat(playerKilled, Statistic.PlayerStat.DEATHS);
			if (killer instanceof Player) {
				incrementPlayerStat(killer, Statistic.PlayerStat.PLAYERS_KILLED);
			}
		} else {
			incrementStatistic(Statistic.MOBS_KILLED);
			if (killer instanceof Player) {
				incrementPlayerStat(killer, Statistic.PlayerStat.MOBS_KILLED);
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (((int) e.getFrom().getX() != (int) e.getTo().getX()) || ((int) e.getFrom().getY() != (int) e.getTo().getY()) || ((int) e.getFrom().getZ() != (int) e.getTo().getZ())) {
			incrementStatistic(Statistic.BLOCKS_TRAVELED);
			incrementPlayerStat(e.getPlayer(), Statistic.PlayerStat.BLOCKS_TRAVELED);
		}
	}

	public static void incrementStatistic(Statistic statistic) {
		if (((Integer) statistics.get(statistic)).intValue() != -1) {
			statistics.put(statistic, Integer.valueOf(((Integer) statistics.get(statistic)).intValue() + 1));
		} else {
			statistics.put(statistic, Integer.valueOf(1));
		}
	}

	public static void incrementPlayerStat(Player player, Statistic.PlayerStat statistic) {
		UUID uuid = player.getUniqueId();
		PlayerInfo playerInfo = new PlayerInfo(uuid, player.getName());
		if ((playerStats.containsKey(playerInfo)) && ((playerStats.get(playerInfo)).containsKey(statistic)) && ((playerStats.get(playerInfo)).get(statistic)).intValue() != -1) {
			LinkedHashMap<Statistic.PlayerStat, Integer> newValue = playerStats.get(playerInfo);
			newValue.put(statistic, Integer.valueOf(((Integer) newValue.get(statistic)).intValue() + 1));
			playerStats.put(new PlayerInfo(uuid, player.getName()), newValue);
		} else {
			LinkedHashMap<Statistic.PlayerStat, Integer> newValue = new LinkedHashMap<Statistic.PlayerStat, Integer>();
			if (playerStats.containsKey(playerInfo)) {
				newValue = playerStats.get(playerInfo);
			}
			newValue.put(statistic, Integer.valueOf(1));
			playerStats.put(new PlayerInfo(uuid, player.getName()), newValue);
		}
	}

	public static void clearStatistics() {
		for (Statistic stat : statistics.keySet()) {
			statistics.put(stat, Integer.valueOf(-1));
		}
	}

	public static void clearPlayerStatistics() {
		playerStats.clear();
	}
}