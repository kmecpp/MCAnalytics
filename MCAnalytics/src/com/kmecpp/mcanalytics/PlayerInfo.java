package com.kmecpp.mcanalytics;

import java.util.UUID;

public class PlayerInfo {
	private UUID uuid;
	private String name;

	public PlayerInfo(UUID uuid, String playerName) {
		this.uuid = uuid;
		this.name = playerName;
	}

	public UUID getUniqueId() {
		return this.uuid;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.uuid.toString() + " : " + this.name;
	}

	@Override
	public boolean equals(Object object) {
		if ((object instanceof PlayerInfo)) {
			PlayerInfo playerInfo = (PlayerInfo) object;
			if ((playerInfo.getUniqueId().equals(this.uuid)) && (playerInfo.getName().equals(this.name))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = 31 + (this.uuid != null ? this.uuid.hashCode() : 0);
		hashCode += 31 * (this.uuid != null ? this.name.hashCode() : 0);
		return hashCode;
	}
}
