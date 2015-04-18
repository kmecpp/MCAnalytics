package com.kmecpp.mcanalytics.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.kmecpp.mcanalytics.EventListener;
import com.kmecpp.mcanalytics.Main;
import com.kmecpp.mcanalytics.PlayerInfo;
import com.kmecpp.mcanalytics.Statistic;
import com.kmecpp.mcanalytics.Statistic.PlayerStat;

public class SQLUtil {

	public static Long saveStatistics() {
		try {
			long startTime = System.currentTimeMillis();
			Connection connection = getConnection();
			for (Entry<PlayerInfo, LinkedHashMap<Statistic.PlayerStat, Integer>> playerEntry : EventListener.playerStats.entrySet()) {
				StringBuilder postQuery = new StringBuilder();
				StringBuilder postQueryValues = new StringBuilder();
				postQuery.append("REPLACE INTO USERDATA ('UUID', '" + Statistic.PlayerStat.PLAYER_NAME.toString() + "'");
				postQueryValues.append(" VALUES (");
				postQueryValues.append("'" + ((PlayerInfo) playerEntry.getKey()).getUniqueId() + "', '" + ((PlayerInfo) playerEntry.getKey()).getName() + "'");
				for (Statistic.PlayerStat statistic : Statistic.PlayerStat.values()) {
					String getQuery = "SELECT UUID," + statistic.toString() + " FROM USERDATA WHERE `UUID`= '" + playerEntry.getKey().getUniqueId().toString() + "'";
					Integer currentValue = 0;
					try {
						currentValue = Integer.valueOf(connection.createStatement().executeQuery(getQuery).getInt(2));
					} catch (Exception e) {
						e.printStackTrace();
					}
					postQuery.append(", '" + statistic.toString() + "'");

					int newAmount = 0;
					if ((playerEntry.getValue()).containsKey(statistic)) {
						newAmount = ((Integer) (playerEntry.getValue()).get(statistic)).intValue();
					}
					postQueryValues.append(", '" + (currentValue.intValue() + newAmount) + "'");
				}
				postQuery.append(")");
				postQueryValues.append(");");
				String post = postQuery.toString() + postQueryValues.toString();
				connection.createStatement().executeUpdate(post);
			}
			connection.close();
			return Long.valueOf(System.currentTimeMillis() - startTime);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			EventListener.clearPlayerStatistics();
		}
		return null;
	}

	public static LinkedHashMap<Statistic.PlayerStat, Object> getPlayerStatistics(PlayerInfo playerInfo) throws SQLException {
		LinkedHashMap<Statistic.PlayerStat, Object> playerStats = new LinkedHashMap<PlayerStat, Object>();
		Connection connection = getConnection();
		for (Statistic.PlayerStat statistic : Statistic.PlayerStat.values()) {
			String getQuery = null;
			if (playerInfo.getUniqueId() != null) {
				getQuery = "SELECT UUID," + statistic.toString() + " FROM USERDATA WHERE `UUID`= '" + playerInfo.getUniqueId() + "'";
			} else if (playerInfo.getName() != null) {
				getQuery = "SELECT " + Statistic.PlayerStat.PLAYER_NAME.toString() + "," + statistic.toString() + " FROM USERDATA WHERE UPPER(" + Statistic.PlayerStat.PLAYER_NAME.toString() + ")= '" + playerInfo.getName().toUpperCase() + "'";
			} else {
				return null;
			}
			Object value = connection.createStatement().executeQuery(getQuery).getObject(2);
			playerStats.put(statistic, value);
		}
		connection.close();
		return playerStats;
	}

	public static Connection getConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:" + Main.plugin.getDataFolder().getAbsolutePath() + File.separator + "userdata.db");
			connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS USERDATA (UUID VARCHAR(36) NOT NULL PRIMARY KEY, " + Statistic.PlayerStat.PLAYER_NAME.toString() + " VARCHAR(16) NOT NULL, " + Statistic.PlayerStat.CHAT_MESSAGES.toString() + " INT, " + Statistic.PlayerStat.BLOCKS_BROKEN.toString() + " INT, " + Statistic.PlayerStat.BLOCKS_PLACED.toString() + " INT, " + Statistic.PlayerStat.BLOCKS_TRAVELED.toString() + " INT, " + Statistic.PlayerStat.PLAYERS_KILLED.toString() + " INT, " + Statistic.PlayerStat.MOBS_KILLED.toString() + " INT, " + Statistic.PlayerStat.DEATHS.toString() + " INT);");
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}