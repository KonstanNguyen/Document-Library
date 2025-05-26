package com.systems.backend.documents.models;

import com.systems.backend.download.models.HistoryDownload;
import com.systems.backend.ratings.models.Rating;
import com.systems.backend.users.models.DocUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id", nullable = false)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private DocUser author;

    @Nationalized
    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    @Nationalized
    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    private String content;

    @Lob
    @Column(name ="description")
    private String description;

//    @ColumnDefault("0")
    @Column(name ="views")
    private int views = 0;

//    @ColumnDefault("0")
    @Column(name = "status", nullable = false)
    private Short status = 0;

//    @ColumnDefault("getdate()")
    @Column(name = "create_at", nullable = false)
    @JsonFormat(pattern="HH:mm:ss dd-MM-yyyy")
    private LocalDateTime createAt = LocalDateTime.now();

//    @ColumnDefault("getdate()")
    @Column(name = "update_at", nullable = false)
    @JsonFormat(pattern="HH:mm:ss dd-MM-yyyy")
    private LocalDateTime updateAt = LocalDateTime.now();

    @OneToMany(mappedBy = "historyDownloadId.document", fetch = FetchType.LAZY)
    private Collection<HistoryDownload> historyDownloads;

    @OneToMany(mappedBy = "ratingId.document", fetch = FetchType.LAZY)
    private Collection<Rating> ratings;

}