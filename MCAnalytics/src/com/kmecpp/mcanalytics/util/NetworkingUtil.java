package com.kmecpp.mcanalytics.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import com.kmecpp.mcanalytics.EventListener;
import com.kmecpp.mcanalytics.Main;
import com.kmecpp.mcanalytics.Statistic;

public class NetworkingUtil {
	public static String createConnection(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		connection.setReadTimeout(5000);
		connection.setConnectTimeout(5000);
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
		connection.addRequestProperty("Content-Type", "application/json");
		connection.addRequestProperty("Content-Encoding", "gzip");
		connection.addRequestProperty("Accept", "application/json");
		connection.addRequestProperty("Connection", "close");
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	public static String getStatistics() throws IOException {
		String configUrl = Main.plugin.getConfig().getString("url");
		if (!configUrl.equalsIgnoreCase("null")) {
			URL url = new URL(configUrl + "?color=null");
			return createConnection(url);
		}
		Main.log(Level.SEVERE, "URL value not set in config!");
		return null;
	}

	public static void submitStatistics() throws IOException {
		String configUrl = Main.plugin.getConfig().getString("url");
		if (!configUrl.equalsIgnoreCase("null")) {
			EventListener.statistics.put(Statistic.STATS_COLLECTED, 1);
			URL url = new URL(configUrl + getUrlParams());
			createConnection(url);
			EventListener.clearStatistics();
		} else {
			Main.log(Level.SEVERE, "URL value not set in config!");
		}
	}

	public static String getUrlParams() {
		StringBuilder sb = new StringBuilder();
		sb.append("?");
		for (Statistic stat : Statistic.values()) {
			if (stat != Statistic.KEY) {
				sb.append(stat.getValue() + "=" + EventListener.statistics.get(stat) + "&");
			} else {
				sb.append(Statistic.KEY.toString().toLowerCase() + "=" + Main.plugin.getConfig().getString("authKey") + "&");
			}
		}
		return sb.toString();
	}
}