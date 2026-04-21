ALTER TABLE `user`
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'vision' AFTER email;

CREATE TABLE IF NOT EXISTS family_user_binding (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  family_user_id BIGINT NOT NULL,
  vision_user_id BIGINT NOT NULL,
  relationship VARCHAR(50) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_family_user_binding_family (family_user_id),
  KEY idx_family_user_binding_vision (vision_user_id)
);

CREATE TABLE IF NOT EXISTS user_care_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  subject_name VARCHAR(50) DEFAULT NULL,
  gender VARCHAR(20) DEFAULT NULL,
  age VARCHAR(20) DEFAULT NULL,
  vision_level VARCHAR(50) DEFAULT NULL,
  address VARCHAR(255) DEFAULT NULL,
  notes TEXT,
  emergency_name VARCHAR(50) DEFAULT NULL,
  emergency_relation VARCHAR(50) DEFAULT NULL,
  emergency_phone VARCHAR(20) DEFAULT NULL,
  emergency_backup_phone VARCHAR(20) DEFAULT NULL,
  medicine TEXT,
  disease_note TEXT,
  allergy TEXT,
  reminder TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_care_profile_user (user_id)
);
