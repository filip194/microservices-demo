package com.microservices.demo.analytics.service.dataaccess.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "twitter_analytics")
public class AnalyticsEntity implements BaseEntity<UUID>
{
    @Id
    @NotNull
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @NotNull
    @Column(name = "word")
    private String word;

    @NotNull
    @Column(name = "word_count")
    private Long wordCount;

    @NotNull
    @Column(name = "record_date")
    private LocalDateTime recordDate;

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final AnalyticsEntity that = (AnalyticsEntity) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }
}

