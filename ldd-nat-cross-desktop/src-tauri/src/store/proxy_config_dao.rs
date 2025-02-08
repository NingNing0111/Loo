use std::error::Error;

use rusqlite::{params, ToSql};

use crate::{global::CONFIG_DB, model::dto::PageResult, utils::time::now_timestamp};

use super::{dao::BaseDAO, model::config::ProxyConfigDO};

const PROXY_CONFIG_TABLE: &str = "proxy_config";
const INIT_TABLE_DDL: &str = "
    CREATE TABLE IF NOT EXISTS proxy_config (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        host TEXT NOT NULL,
        port INTEGER NOT NULL,
        open_port INTEGER NOT NULL,
        protocol TEXT NOT NULL,
        created_time INTEGER NOT NULL 
    );
";
pub struct ProxyConfigDAO {
    dao: BaseDAO,
}

impl ProxyConfigDAO {
    pub fn new() -> Self {
        let base_dao = BaseDAO::new(CONFIG_DB, PROXY_CONFIG_TABLE)
            .expect(format!("connect to {} fail!", PROXY_CONFIG_TABLE).as_str());
        let _ = base_dao.execute_ddl(INIT_TABLE_DDL);
        ProxyConfigDAO { dao: base_dao }
    }

    /// 插入一条数据
    pub fn insert(&self, config: ProxyConfigDO) -> Result<usize, Box<dyn Error>> {
        let columns = ["host", "port", "open_port", "protocol", "created_time"];
        let now_timestamp = now_timestamp();
        let values: [&(dyn ToSql); 5] = [
            &config.host,
            &config.port,
            &config.open_port,
            &config.protocol,
            &now_timestamp,
        ];
        let i = self
            .dao
            .insert(&columns, &values)
            .expect(format!("Failed to insert data: {:?}", config).as_str());
        Ok(i)
    }

    // 插入多条数据
    /// 批量插入多条数据
    pub fn insert_batch(&mut self, configs: Vec<ProxyConfigDO>) -> Result<usize, Box<dyn Error>> {
        if configs.is_empty() {
            return Ok(0);
        }

        let sql = format!(
            "INSERT INTO {} (host, port, open_port, protocol, created_time) VALUES (?, ?, ?, ?, ?)",
            PROXY_CONFIG_TABLE
        );

        let tx = self.dao.conn.transaction()?; // 开启事务
        let mut count = 0;
        {
            let mut stmt = tx.prepare(&sql)?; // stmt 的生命周期限制在这个作用域
            for config in &configs {
                let now_timestamp = now_timestamp();
                stmt.execute(params![
                    &config.host,
                    &config.port,
                    &config.open_port,
                    &config.protocol,
                    &now_timestamp
                ])?;
                count += 1;
            }
        } // 这里 stmt 被释放

        tx.commit()?; // 提交事务
        Ok(count)
    }

    /// 根据ID删除
    pub fn delete_by_id(&self, config_id: i32) -> Result<usize, Box<dyn Error>> {
        let values: [&(dyn ToSql); 1] = [&config_id];
        let i = self
            .dao
            .delete("id = ?", &values)
            .expect(format!("Failed to delete data:{}", config_id).as_str());
        Ok(i)
    }

    /// 批量删除
    pub fn delete_by_ids_batch(&self, ids: Vec<i32>) -> Result<usize, Box<dyn Error>> {
        if ids.is_empty() {
            return Ok(0);
        }

        let placeholders: String = ids.iter().map(|_| "?").collect::<Vec<_>>().join(", ");
        let sql = format!(
            "DELETE FROM {} WHERE id IN ({})",
            PROXY_CONFIG_TABLE, placeholders
        );

        let params: Vec<&dyn ToSql> = ids.iter().map(|id| id as &dyn ToSql).collect();

        let rows_affected = self.dao.conn.execute(&sql, &params[..])?;

        Ok(rows_affected)
    }

    /// 更新配置
    pub fn update_by_id(&self, new_config: ProxyConfigDO) -> Result<usize, Box<dyn Error>> {
        let set_columns = ["host", "port", "open_port", "protocol", "created_time"];
        let now_timestamp = now_timestamp();
        let set_values: [&dyn ToSql; 5] = [
            &new_config.host,
            &new_config.port,
            &new_config.open_port,
            &new_config.protocol,
            &now_timestamp,
        ];
        let where_values: [&dyn ToSql; 1] = [&new_config.id];

        let i = self
            .dao
            .update(&set_columns, &set_values, "id = ?", &where_values)
            .expect(&format!("Failed to update data:{:?}", new_config));
        Ok(i)
    }

    /// 根据Id查找
    pub fn find_by_id(&self, config_id: i32) -> Result<Option<ProxyConfigDO>, Box<dyn Error>> {
        let sql = format!(
            "SELECT id, host, port, open_port, protocol, created_time FROM {} where id = ?",
            PROXY_CONFIG_TABLE
        );
        let params: [&dyn ToSql; 1] = [&config_id];
        let res = self
            .dao
            .query(sql.as_str(), &params, |row| {
                Ok(ProxyConfigDO {
                    id: row.get(0)?,
                    host: row.get(1)?,
                    port: row.get(2)?,
                    open_port: row.get(3)?,
                    protocol: row.get(4)?,
                    create_time: row.get(5)?,
                })
            })
            .expect(format!("Failed to query data by id:{}", config_id).as_str());
        let res = match res.get(0) {
            Some(e) => Some(e.clone()),
            None => None,
        };
        Ok(res)
    }

    /// 查找所有记录
    pub fn find_all(&self) -> Result<Vec<ProxyConfigDO>, Box<dyn Error>> {
        let sql = format!(
            "SELECT id, host, port, open_port, protocol, created_time FROM {}",
            PROXY_CONFIG_TABLE
        );
        let res = self
            .dao
            .query(&sql, &[], |row| {
                Ok(ProxyConfigDO {
                    id: row.get(0)?,
                    host: row.get(1)?,
                    port: row.get(2)?,
                    open_port: row.get(3)?,
                    protocol: row.get(4)?,
                    create_time: row.get(5)?,
                })
            })
            .expect("Failed to query all data.");
        Ok(res)
    }

    pub fn page(
        &self,
        page: i32,
        page_size: i32,
    ) -> Result<PageResult<ProxyConfigDO>, Box<dyn Error>> {
        let offset = (page - 1) * page_size; // 计算偏移量
        let sql = format!(
            "SELECT id, host, port, open_port, protocol, created_time
             FROM {} 
             ORDER BY id DESC 
             LIMIT ? OFFSET ?",
            PROXY_CONFIG_TABLE
        );

        let records = self.dao.query(&sql, &[&page_size, &offset], |row| {
            Ok(ProxyConfigDO {
                id: row.get(0)?,
                host: row.get(1)?,
                port: row.get(2)?,
                open_port: row.get(3)?,
                protocol: row.get(4)?,
                create_time: row.get(5)?,
            })
        })?;
        let total = self.count()?;

        Ok(PageResult::new(total, records))
    }

    pub fn reset_data(&self) -> Result<usize, Box<dyn Error>> {
        let drop_ddl = format!("DROP TABLE {};", PROXY_CONFIG_TABLE);
        let _ = self.dao.execute_ddl(&drop_ddl).expect("Drop table failed.");
        let _ = self
            .dao
            .execute_ddl(INIT_TABLE_DDL)
            .expect("Init table failed.");
        Ok(0)
    }

    /// 统计表中的总记录数
    pub fn count(&self) -> Result<i64, Box<dyn Error>> {
        let sql = format!("SELECT COUNT(*) FROM {}", PROXY_CONFIG_TABLE);

        let count: i64 = self
            .dao
            .query(&sql, &[], |row| row.get(0))?
            .into_iter()
            .next()
            .unwrap_or(0); // 获取查询结果

        Ok(count)
    }
}
