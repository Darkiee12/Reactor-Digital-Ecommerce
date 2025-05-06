package com.ecommerce.nashtech.modules.image.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Table(name = "images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Builder
public class Image {
    @Id
    @Column("id")
    Long id;

    @Column("uuid")
    UUID uuid;

    @Column("name")
    String name;

    @Column("alt")
    String alt;

    @Column("object_key")
    String objectKey;

    @Column("mime_type")
    String mimeType;

    @Column("size")
    Long size;

    @Column("width")
    Integer width;

    @Column("height")
    Integer height;

    @Column("uploaded_at")
    Long uploadedAt;

}
