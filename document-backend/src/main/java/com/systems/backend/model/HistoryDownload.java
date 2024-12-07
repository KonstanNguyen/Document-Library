package com.systems.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.Hibernate;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Table(name = "History_Download")
public class HistoryDownload {
    @Embeddable
    @Data
    public static class HistoryDownloadId implements java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 7789561099938143923L;

        @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
        @JoinColumn(name = "account_id")
        protected Account account;

        @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
        @JoinColumn(name = "document_id")
        protected Document document;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
            HistoryDownloadId entity = (HistoryDownloadId) o;
            return Objects.equals(this.account, entity.account) &&
                    Objects.equals(this.document, entity.document);
        }

        @Override
        public int hashCode() {
            return Objects.hash(account, document);
        }

    }

    @EmbeddedId
    private HistoryDownloadId historyDownloadId;

    @Column(name = "date", nullable = false)
    @JsonFormat(pattern="HH:mm:ss dd-MM-yyyy")
    private LocalDateTime date;
}