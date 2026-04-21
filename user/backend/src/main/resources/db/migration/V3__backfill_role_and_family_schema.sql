SET @role_column_exists = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'user'
      AND COLUMN_NAME = 'role'
);

SET @role_column_sql = IF(
    @role_column_exists = 0,
    'ALTER TABLE `user` ADD COLUMN `role` VARCHAR(20) NOT NULL DEFAULT ''vision'' AFTER `email`',
    'SELECT 1'
);
PREPARE stmt_role FROM @role_column_sql;
EXECUTE stmt_role;
DEALLOCATE PREPARE stmt_role;

CREATE TABLE IF NOT EXISTS family_user_binding (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  family_user_id BIGINT NOT NULL,
  vision_user_id BIGINT NOT NULL,
  relationship VARCHAR(50) DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

SET @family_binding_unique_exists = (
    SELECT COUNT(1)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'family_user_binding'
      AND INDEX_NAME = 'uk_family_user_binding_family'
);

SET @family_binding_unique_sql = IF(
    @family_binding_unique_exists = 0,
    'ALTER TABLE family_user_binding ADD UNIQUE KEY uk_family_user_binding_family (family_user_id)',
    'SELECT 1'
);
PREPARE stmt_family_unique FROM @family_binding_unique_sql;
EXECUTE stmt_family_unique;
DEALLOCATE PREPARE stmt_family_unique;

SET @family_binding_index_exists = (
    SELECT COUNT(1)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'family_user_binding'
      AND INDEX_NAME = 'idx_family_user_binding_vision'
);

SET @family_binding_index_sql = IF(
    @family_binding_index_exists = 0,
    'ALTER TABLE family_user_binding ADD KEY idx_family_user_binding_vision (vision_user_id)',
    'SELECT 1'
);
PREPARE stmt_family_index FROM @family_binding_index_sql;
EXECUTE stmt_family_index;
DEALLOCATE PREPARE stmt_family_index;

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
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

SET @care_profile_unique_exists = (
    SELECT COUNT(1)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'user_care_profile'
      AND INDEX_NAME = 'uk_user_care_profile_user'
);

SET @care_profile_unique_sql = IF(
    @care_profile_unique_exists = 0,
    'ALTER TABLE user_care_profile ADD UNIQUE KEY uk_user_care_profile_user (user_id)',
    'SELECT 1'
);
PREPARE stmt_care_unique FROM @care_profile_unique_sql;
EXECUTE stmt_care_unique;
DEALLOCATE PREPARE stmt_care_unique;
