CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(200) NOT NULL,
  email VARCHAR(300) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(300) NOT NULL,
  description VARCHAR(500),
  available BOOLEAN,
  owner_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  author_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
  item_id BIGINT REFERENCES items (id) ON DELETE CASCADE,
  text VARCHAR(500) NOT NULL,
  created TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
  item_id BIGINT REFERENCES items (id) ON DELETE CASCADE,
  time_from TIMESTAMP NOT NULL,
  time_to TIMESTAMP NOT NULL,
  current_state VARCHAR(9) NOT NULL,
  items_owner_id BIGINT REFERENCES users (id) ON DELETE CASCADE
);