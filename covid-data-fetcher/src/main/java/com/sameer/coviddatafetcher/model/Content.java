package com.sameer.coviddatafetcher.model;

import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "content")
@Data
public class Content {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String template;
  private String content;
}
