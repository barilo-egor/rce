package tgb.btc.lib.bean;


import lombok.Builder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "CONTACT")
@Builder
public class Contact extends BasePersist {
    @Column(name = "LABEL", length = 50, nullable = false)
    private String label;
    @Column(name = "URL", nullable = false)
    private String url;

    public Contact() {
    }

    public Contact(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "label='" + label + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Contact contact = (Contact) o;
        return Objects.equals(label, contact.label) && Objects.equals(url, contact.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), label, url);
    }
}
