package tgb.btc.rce.bean;


import lombok.Builder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

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

    @Column(name = "IS_PUBLISHED")
    private Boolean isPublished;

    public Review() {
    }

    public Review(String text, Long chatId, String username, Boolean isPublished) {
        this.text = text;
        this.chatId = chatId;
        this.username = username;
        this.isPublished = isPublished;
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

    public Boolean getPublished() {
        return isPublished;
    }

    public void setPublished(Boolean published) {
        isPublished = published;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Review review = (Review) o;
        return Objects.equals(text, review.text) && Objects.equals(chatId, review.chatId) && Objects.equals(username, review.username) && Objects.equals(isPublished, review.isPublished);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text, chatId, username, isPublished);
    }

    @Override
    public String toString() {
        return "Review{" +
                "text='" + text + '\'' +
                ", chatId=" + chatId +
                ", username='" + username + '\'' +
                ", isPublished=" + isPublished +
                '}';
    }
}
