package trab2.mastodon.msgs;

import trab2.api.Message;
import trab2.servers.Domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public record PostStatusResult(String id, String content, String created_at, MastodonAccount account) {
	public long getId() {
		return Long.valueOf(id);
	}

	long getCreationTime() {
		String dateString = "2023-05-19T18:31:53.846Z";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
		Instant instant = dateTime.toInstant(ZoneOffset.UTC);
		return instant.getEpochSecond();
	}

	public String getText() {
		return content;
	}

	public Message toMessage() {
		var m = new Message(getId(), account.username(), Domain.get(), getText());
		m.setCreationTime(getCreationTime());
		return m;
	}
}