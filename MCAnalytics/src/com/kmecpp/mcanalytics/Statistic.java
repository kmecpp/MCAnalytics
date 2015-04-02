package com.kmecpp.mcanalytics;

public enum Statistic {
	KEY("key", "Key"),
	SERVER_TPS("serverTps", "Server TPS"),
	UNIQUE_PLAYERS("uniquePlayers", "Unique Players"),
	PLAYER_JOINS("playerJoins", "Player Joins"),
	PLAYERS_KILLED("playersKilled", "Players Killed"),
	MOBS_KILLED("mobsKilled", "Mobs Killed"),
	CHAT_MESSAGES("chatMessages", "Chat Messages"),
	BLOCKS_BROKEN("blocksBroken", "Blocks Broken"),
	BLOCKS_PLACED("blocksPlaced", "Blocks Placed"),
	BLOCKS_TRAVELED("blocksTraveled", "Blocks Traveled"),
	ITEMS_DROPPED("itemsDropped", "Items Dropped"),
	INVENTORIES_OPENED("inventoriesOpened", "Inventories Opened"),
	CHUNKS_LOADED("chunksLoaded", "Chunks Loaded"),
	SECONDS_ONLINE("secondsOnline", "Seconds Online"),
	STATS_COLLECTED("statsCollected", "Stats Collected");

	private final String key;
	private final String prettyKey;

	public static enum PlayerStat {
		PLAYER_NAME("playerName", "Name"),
		CHAT_MESSAGES("chatMessages", "Chat Messages"),
		PLAYERS_KILLED("playersKilled", "Players Killed"),
		MOBS_KILLED("mobsKilled", "Mobs Killed"),
		BLOCKS_BROKEN("blocksBroken", "Blocks Broken"),
		BLOCKS_PLACED("blocksPlaced", "Blocks Placed"),
		BLOCKS_TRAVELED("blocksTraveled", "Blocks Traveled"),
		DEATHS("playerDeaths", "Deaths");

		private final String key;
		private final String prettyKey;

		private PlayerStat(String key, String prettyKey) {
			this.key = key;
			this.prettyKey = prettyKey;
		}

		public String getValue() {
			return this.key;
		}

		public String getPrettyValue() {
			return this.prettyKey;
		}
	}

	private Statistic(String key, String prettyKey) {
		this.key = key;
		this.prettyKey = prettyKey;
	}

	public String getValue() {
		return this.key;
	}

	public String getPrettyValue() {
		return this.prettyKey;
	}
}
