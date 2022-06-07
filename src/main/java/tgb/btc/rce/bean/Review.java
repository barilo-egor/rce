package tgb.btc.rce.bean;


import lombok.Builder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "REVIEW")
@Builder
public class Review extends BasePersist {

    @Column(name = "TEXT")
    private String text;

    @Column(name = "CHAT_ID")
    private Long chatId;

    @Column(name = "USERNAME")
    private String username;

    public Review() {
    }

    public Review(String text, Long chatId, String username) {
        this.text = text;
        this.chatId = chatId;
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
