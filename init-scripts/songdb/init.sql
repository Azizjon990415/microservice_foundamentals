CREATE TABLE songs (
                       id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      artist VARCHAR(255) NOT NULL,
                      album VARCHAR(255),
                      length VARCHAR(10),
                      resource_id VARCHAR(255) NOT NULL,
                      year VARCHAR(4)
);