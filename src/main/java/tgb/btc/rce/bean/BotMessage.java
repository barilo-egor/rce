package tgb.btc.rce.bean;

import lombok.Builder;
import tgb.btc.rce.enums.BotMessageType;
import tgb.btc.rce.enums.MessageType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "BOT_MESSAGE")
@Builder
public class BotMessage extends BasePersist {
    @Column(name = "TYPE")
    @Enumerated(value = EnumType.STRING)
    private BotMessageType type;
    @Column(name = "TEXT", length = 2000)
    private String text;
    @Column(name = "IMAGE", length = 500)
    private String image;
    @Column(name = "ANIMATION", length = 500)
    private String animation;
    @Column(name = "MESSAGE_TYPE", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MessageType messageType = MessageType.TEXT;

    public BotMessage() {
    }

    public BotMessage(BotMessageType type, String text, String image, String animation, MessageType messageType) {
        this.type = type;
        this.text = text;
        this.image = image;
        this.animation = animation;
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getAnimation() {
        return animation;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    public BotMessageType getType() {
        return type;
    }

    public void setType(BotMessageType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "BotMessage{" +
                "type=" + type +
                ", text='" + text + '\'' +
                ", image='" + image + '\'' +
                ", animation='" + animation + '\'' +
                ", messageType=" + messageType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BotMessage that = (BotMessage) o;
        return type == that.type && Objects.equals(text, that.text) && Objects.equals(image, that.image)
                && Objects.equals(animation, that.animation) && messageType == that.messageType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type, text, image, animation, messageType);
    }
}
