use std::error::Error;

use rusqlite::ToSql;

use crate::{global::LOG_DB, model::dto::PageResult, utils::time::now_timestamp};

use super::{dao::BaseDAO, model::log::ConnectLogDO};

const CONNECT_LOG_TABLE: &str = "connect_log";
const INIT_TABLE_DDL: &str = "
    CREATE TABLE IF NOT EXISTS connect_log (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        server_addr TEXT NOT NULL,
        proxy_addr TEXT NOT NULL,
        visitor_addr TEXT ,
        status INTEGER NOT NULL DEFAULT 0,
        connected_time INTEGER,
        disconnected_time INTEGER,
        created_time INTEGER
    );
";
pub struct ConnectLogDAO {
    dao: BaseDAO,
}

impl ConnectLogDAO {
    pub fn new() -> Self {
        let base_dao = BaseDAO::new(LOG_DB, CONNECT_LOG_TABLE)
            .expect(&format!("connect to {} fail!", CONNECT_LOG_TABLE));
        let _ = base_dao.execute_ddl(INIT_TABLE_DDL);
        ConnectLogDAO { dao: base_dao }
    }

    /// 插入一条数据
    pub fn insert(&self, log: ConnectLogDO) -> Result<i64, Box<dyn Error>> {
        let columns = [
            "server_addr",
            "proxy_addr",
            "visitor_addr",
            "status",
            "connected_time",
            "disconnected_time",
            "created_time",
        ];
        let now_timestamp = now_timestamp();
        let empty_timestamp: i64 = 0;
        let values: [&(dyn ToSql); 6] = [
            &log.server_addr,
            &log.proxy_addr,
            &log.visitor_addr,
            &now_timestamp,
            &empty_timestamp,
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

    /// 更新配置
    pub fn update_by_id(&self, new_log: ConnectLogDO) -> Result<usize, Box<dyn Error>> {
        let set_columns = [
            "server_addr",
            "proxy_addr",
            "visitor_addr",
            "status",
            "connected_time",
            "disconnected_time",
            "created_time",
        ];
        let set_values: [&dyn ToSql; 6] = [
            &new_log.server_addr,
            &new_log.proxy_addr,
            &new_log.visitor_addr,
            &new_log.connected_time,
            &new_log.disconnected_time,
            &new_log.created_time,
        ];
        let where_values: [&dyn ToSql; 1] = [&new_log.id];

        let i = self
            .dao
            .update(&set_columns, &set_values, "id = ?", &where_values)
            .expect(&format!("Failed to update data:{:?}", new_log));
        Ok(i)
    }

    /// 根据Id查找
    pub fn find_by_id(&self, id: i64) -> Result<Option<ConnectLogDO>, Box<dyn Error>> {
        let sql = format!(
            "SELECT id, server_addr, proxy_addr, visitor_addr, status, connected_time, disconnected_time, created_time FROM {} where id = ?",
            CONNECT_LOG_TABLE
        );
        let params: [&dyn ToSql; 1] = [&id];
        let res = self
            .dao
            .query(&sql, &params, |row| {
                Ok(ConnectLogDO {
                    id: row.get(0)?,
                    server_addr: row.get(1)?,
                    proxy_addr: row.get(2)?,
                    visitor_addr: row.get(3)?,
                    status: row.get(4)?,
                    connected_time: row.get(5)?,
                    disconnected_time: row.get(6)?,
                    created_time: row.get(7)?,
                })
            })
            .expect(&format!("Failed to query data by id:{}", id));
        let res = match res.get(0) {
            Some(e) => Some(e.clone()),
            None => None,
        };
        Ok(res)
    }

    /// 查找所有记录
    pub fn find_all(&self) -> Result<Vec<ConnectLogDO>, Box<dyn Error>> {
        let sql = format!(
            "SELECT id, server_addr, proxy_addr, visitor_addr, status, connected_time, disconnected_time,created_time FROM {}",
            CONNECT_LOG_TABLE
        );
        let res = self
            .dao
            .query(&sql, &[], |row| {
                Ok(ConnectLogDO {
                    id: row.get(0)?,
                    server_addr: row.get(1)?,
                    proxy_addr: row.get(2)?,
                    visitor_addr: row.get(3)?,
                    status: row.get(4)?,
                    connected_time: row.get(5)?,
                    disconnected_time: row.get(6)?,
                    created_time: row.get(7)?,
                })
            })
            .expect("Failed to query all data.");
        Ok(res)
    }

    pub fn page(
        &self,
        page: i32,
        page_size: i32,
    ) -> Result<PageResult<ConnectLogDO>, Box<dyn Error>> {
        let offset = (page - 1) * page_size; // 计算偏移量
        let sql = format!(
            "SELECT id, server_addr, proxy_addr, visitor_addr, status, connected_time, disconnected_time, created_time
             FROM {} 
             ORDER BY id DESC 
             LIMIT ? OFFSET ?",
            CONNECT_LOG_TABLE
        );

        let records = self.dao.query(&sql, &[&page_size, &offset], |row| {
            Ok(ConnectLogDO {
                id: row.get(0)?,
                server_addr: row.get(1)?,
                proxy_addr: row.get(2)?,
                visitor_addr: row.get(3)?,
                status: row.get(4)?,
                connected_time: row.get(5)?,
                disconnected_time: row.get(6)?,
                created_time: row.get(7)?,
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
    pub fn count_success(&self) -> Result<i64, Box<dyn Error>> {
        let sql = format!(
            "SELECT COUNT(*) FROM {} WHERE status = 1",
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
    pub fn count_fail(&self) -> Result<i64, Box<dyn Error>> {
        let sql = format!(
            "SELECT COUNT(*) FROM {} WHERE status = 0",
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
