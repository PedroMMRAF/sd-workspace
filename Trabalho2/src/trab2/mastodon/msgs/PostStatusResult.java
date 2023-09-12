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

    public long getCreationTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        LocalDateTime dateTime = LocalDateTime.parse(created_at, formatter);
        Instant instant = dateTime.toInstant(ZoneOffset.UTC);
        return instant.getEpochSecond() * 1000 + instant.getNano() / 1000000;
    }

    public String getText() {
        return content.substring(3, content.length() - 4);
    }

    public Message toMessage() {
        var m = new Message(getId(), account.username(), Domain.domain(), getText());
        m.setCreationTime(getCreationTime());
        return m;
    }
}