CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name  VARCHAR(255)                        NOT NULL,
    email VARCHAR(512)                        NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name        VARCHAR(255)                        NOT NULL,
    description VARCHAR(255),
    available   BOOLEAN                             NOT NULL,
    owner_id    BIGINT REFERENCES users (id),
    request_id  BIGINT,
    CONSTRAINT pk_items PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    description  VARCHAR(255),
    requester_id BIGINT,
    created      TIMESTAMP WITHOUT TIME ZONE         NOT NULL,
    FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT pk_requests PRIMARY KEY (id)
);

ALTER TABLE items
    ADD FOREIGN KEY (request_id) REFERENCES requests (id);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    available  BOOLEAN                             NOT NULL,
    booker_id  BIGINT REFERENCES users (id),
    item_id    BIGINT REFERENCES items (id),
    status     VARCHAR(255),
    CONSTRAINT pk_bookings PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    text      VARCHAR(255)                        NOT NULL,
    author_id BIGINT REFERENCES users (id),
    item_id   BIGINT REFERENCES items (id),
    created   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);