package maxipool.pgstream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class PgSqlListener {

  private static final String QUERY = "LISTEN stream_me_items_change";
  private static final TypeReference<MyNotification> MY_NOTIFICATION_TYPE_REF = new TypeReference<>() {
  };

  private final JdbcTemplate jdbcTemplate;
  private final ObjectMapper mapper;

  @PostConstruct
  public void postConstruct() {
    Thread.startVirtualThread(() -> createNotificationHandler(this::getPgNotificationConsumer));
  }

  private void getPgNotificationConsumer(PGNotification notification) {
    try {
      var myNotification = mapper.readValue(notification.getParameter(), MY_NOTIFICATION_TYPE_REF);
      log.info("Notifications: channel name '{}' entity: '{}'", notification.getName(), myNotification);
    } catch (JsonProcessingException e) {
      log.error("Error converting JSON to MyNotification: {}", notification.getParameter(), e);
    }
  }

  private void createNotificationHandler(Consumer<PGNotification> consumer) {
    jdbcTemplate
        .execute((Connection c) -> {
          c.createStatement().execute(QUERY);

          // PGConnection is needed to access the getNotifications() method.
          var pgConnection = c.unwrap(PGConnection.class);
          while (!Thread.currentThread().isInterrupted()) {
            var nts = pgConnection.getNotifications(10000);
            if (nts == null) {
              continue;
            }
            for (var nt : nts) {
              consumer.accept(nt);
            }
          }
          return null;
        });
  }

}
