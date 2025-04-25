-- Cập nhật cấu trúc bảng users
-- Thêm giá trị mặc định cho trường enabled nếu chưa có
ALTER TABLE users MODIFY enabled BOOLEAN DEFAULT TRUE;

-- Tạo hoặc cập nhật bảng user_roles nếu chưa có
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- Đảm bảo các ràng buộc trên trường còn lại
ALTER TABLE users 
MODIFY COLUMN username VARCHAR(255) NOT NULL,
MODIFY COLUMN password VARCHAR(255) NOT NULL,
MODIFY COLUMN email VARCHAR(255) NOT NULL,
MODIFY COLUMN full_name VARCHAR(255) NOT NULL;

-- Cập nhật trường enabled từ active nếu cần
-- Bỏ comment dòng sau nếu muốn sao chép giá trị từ active sang enabled
-- UPDATE users SET enabled = active WHERE enabled IS NULL;

-- Hoặc cập nhật trường active thành enabled nếu đã có
-- ALTER TABLE users CHANGE COLUMN active enabled BOOLEAN DEFAULT TRUE;

-- Hoặc bạn có thể truncate và chạy lại từ đầu (cẩn thận vì sẽ mất dữ liệu)
-- DROP TABLE IF EXISTS user_roles;
-- DROP TABLE IF EXISTS users; 