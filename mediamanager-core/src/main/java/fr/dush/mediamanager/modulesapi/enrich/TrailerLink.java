package fr.dush.mediamanager.modulesapi.enrich;

import lombok.Data;

@Data
public class TrailerLink {

	private String name;

	private String source;

	private String quality;

	private String url;

	@Override
	public String toString() {
		return new StringBuffer(name).append(" - ").append(quality).append(" (").append(source).append(") : ").append(url).toString();
	}
}
