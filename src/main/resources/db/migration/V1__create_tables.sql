CREATE TABLE garage_sectors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sector VARCHAR(10) NOT NULL UNIQUE,
    base_price DECIMAL(10,2) NOT NULL,
    max_capacity INT NOT NULL,
    open_hour TIME NOT NULL,
    close_hour TIME NOT NULL,
    duration_limit_minutes INT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE garage_spots (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sector_id INT NOT NULL,
    lat DECIMAL(10,6),
    lng DECIMAL(10,6),
    occupied BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_spot_sector FOREIGN KEY (sector_id)
        REFERENCES garage_sectors(id)
        ON DELETE CASCADE

) ENGINE=InnoDB;

CREATE TABLE parking_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(10) NOT NULL,


    entry_time TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    exit_time  TIMESTAMP(3) NULL     DEFAULT NULL,

    sector VARCHAR(10) NOT NULL,
    spot_id INT NOT NULL,
    price_per_hour DECIMAL(10,2) NOT NULL,
    total_amount  DECIMAL(10,2) DEFAULT NULL,
    status VARCHAR(10) NOT NULL,

    CONSTRAINT fk_session_spot FOREIGN KEY (spot_id)
        REFERENCES garage_spots(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE incoming_events (
     id INT AUTO_INCREMENT PRIMARY KEY,
     event_type VARCHAR(10) NOT NULL,
     license_plate VARCHAR(10) NOT NULL,
     event_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     raw_payload JSON NOT NULL
) ENGINE=InnoDB;
