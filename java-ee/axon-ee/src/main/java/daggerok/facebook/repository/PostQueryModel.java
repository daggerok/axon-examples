package daggerok.facebook.repository;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
public class PostQueryModel implements Serializable {
  private static final long serialVersionUID = -6441700694856683345L;

  @Id
  @GeneratedValue(generator = "UUID2")
  @GenericGenerator(name = "UUID2", strategy = "org.hibernate.id.UUIDGenerator")
  UUID id;

  String postId;
  Long likes, dislikes;
  Instant createdAt, modifiedAt;
}
