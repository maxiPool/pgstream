CREATE SEQUENCE IF NOT EXISTS stream_me_items_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE stream_me_items
(
  id   BIGINT NOT NULL,
  name VARCHAR(255),
  CONSTRAINT pk_stream_me_items PRIMARY KEY (id)
);

CREATE OR REPLACE FUNCTION notify_stream_me_items_changes()
  RETURNS TRIGGER AS
$$
BEGIN
  IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' OR TG_OP = 'DELETE' THEN
    PERFORM pg_notify('stream_me_items_change', json_build_object('action', TG_OP, 'new', NEW, 'old', OLD)::text);
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER stream_me_items_changes_trigger
  AFTER INSERT OR UPDATE OR DELETE
  ON stream_me_items
  FOR EACH ROW
EXECUTE FUNCTION notify_stream_me_items_changes();
