use std::error::Error;

use rusqlite::ToSql;

use crate::{global::LOG_DB, model::dto::PageResult, utils::time::now_timestamp};

use super::{dao::BaseDAO, model::log::LogDO};

const CONNECT_LOG_TABLE: &str = "app_log";
const INIT_TABLE_DDL: &str = "
    CREATE TABLE IF NOT EXISTS app_log (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        operation INTEGER NOT NULL,
        log_type INTEGER NOT NULL DEFAULT 0,
        description TEXT,
        created_time INTEGER
    );
";
pub struct LogDAO {
    dao: BaseDAO,
}

impl LogDAO {
    pub fn new() -> Self {
        let base_dao = BaseDAO::new(LOG_DB, CONNECT_LOG_TABLE)
            .expect(&format!("connect to {} fail!", CONNECT_LOG_TABLE));
        let _ = base_dao.execute_ddl(INIT_TABLE_DDL);
        LogDAO { dao: base_dao }
    }

    /// 插入一条数据
    pub fn insert(&self, log: LogDO) -> Result<i64, Box<dyn Error>> {
        let columns = ["operation", "log_type", "description", "created_time"];
        let now_timestamp = now_timestamp();
        let values: [&(dyn ToSql); 4] = [
            &log.operation,
            &log.log_type,
            &log.description,
            &now_timestamp,
        ];
        let _ = self
            .dao
            .insert(&columns, &values)
            .expect(format!("Failed to insert data: {:?}", log).as_str());

        let id = self.dao.conn.last_insert_rowid();

        Ok(id)
    }

    /// 根据ID删除
    pub fn delete_by_id(&self, id: i32) -> Result<usize, Box<dyn Error>> {
        let values: [&(dyn ToSql); 1] = [&id];
        let i = self
            .dao
            .delete("id = ?", &values)
            .expect(&format!("Failed to delete data:{}", id));
        Ok(i)
    }

    pub fn page(&self, page: i32, page_size: i32) -> Result<PageResult<LogDO>, Box<dyn Error>> {
        let offset = (page - 1) * page_size; // 计算偏移量
        let sql = format!(
            "SELECT id, operation, log_type, description, created_time
             FROM {} 
             ORDER BY id DESC 
             LIMIT ? OFFSET ?",
            CONNECT_LOG_TABLE
        );

        let records = self.dao.query(&sql, &[&page_size, &offset], |row| {
            Ok(LogDO {
                id: row.get(0)?,
                operation: row.get(1)?,
                log_type: row.get(2)?,
                description: row.get(3)?,
                created_time: row.get(4)?,
            })
        })?;
        let total = self.count()?;

        Ok(PageResult::new(total, records))
    }

    pub fn reset_data(&self) -> Result<usize, Box<dyn Error>> {
        let drop_ddl = format!("DROP TABLE {};", CONNECT_LOG_TABLE);
        let _ = self.dao.execute_ddl(&drop_ddl).expect("Drop table failed.");
        let _ = self
            .dao
            .execute_ddl(INIT_TABLE_DDL)
            .expect("Init table failed.");
        Ok(0)
    }

    /// 统计表中的总记录数
    pub fn count(&self) -> Result<i64, Box<dyn Error>> {
        let sql = format!("SELECT COUNT(*) FROM {}", CONNECT_LOG_TABLE);

        let count: i64 = self
            .dao
            .query(&sql, &[], |row| row.get(0))?
            .into_iter()
            .next()
            .unwrap_or(0); // 获取查询结果

        Ok(count)
    }
    /// 统计表中成功的状态数
    pub fn count_normal(&self) -> Result<i64, Box<dyn Error>> {
        let sql = format!(
            "SELECT COUNT(*) FROM {} WHERE log_type = 0 ",
            CONNECT_LOG_TABLE
        );

        let count: i64 = self
            .dao
            .query(&sql, &[], |row| row.get(0))?
            .into_iter()
            .next()
            .unwrap_or(0); // 获取查询结果

        Ok(count)
    }

    /// 统计表中失败的状态数
    pub fn count_err(&self) -> Result<i64, Box<dyn Error>> {
        let sql = format!(
            "SELECT COUNT(*) FROM {} WHERE log_type = 1 ",
            CONNECT_LOG_TABLE
        );

        let count: i64 = self
            .dao
            .query(&sql, &[], |row| row.get(0))?
            .into_iter()
            .next()
            .unwrap_or(0); // 获取查询结果

        Ok(count)
    }
}
