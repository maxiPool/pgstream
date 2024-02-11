package maxipool.pgstream;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

import static jakarta.persistence.GenerationType.SEQUENCE;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "stream_me_items")
public class StreamMeEntity {

  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "stream_me_items_sequence_gen")
  @SequenceGenerator(
      name = "stream_me_items_sequence_gen",
      sequenceName = "stream_me_items_sequence",
      allocationSize = 1)
  private Long id;

  @Column(name = "name")
  private String name;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    var that = (StreamMeEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
