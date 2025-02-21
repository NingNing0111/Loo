use std::error::Error;

use rusqlite::ToSql;

use crate::{global::CONFIG_DB, model::dto::PageResult, utils::time::now_timestamp};

use super::{dao::BaseDAO, model::config::ServerConfigDO};

const SERVER_CONFIG_TABLE: &str = "server_config";
const INIT_TABLE_DDL: &str = "
    CREATE TABLE IF NOT EXISTS server_config (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        label TEXT NOT NULL,
        server_host TEXT NOT NULL,
        server_port INTEGER NOT NULL,
        password TEXT NOT NULL,
        created_time INTEGER NOT NULL 
    );
";
pub struct ServerConfigDAO {
    dao: BaseDAO,
}

impl ServerConfigDAO {
    pub fn new() -> Self {
        let base_dao = BaseDAO::new(CONFIG_DB, SERVER_CONFIG_TABLE)
            .expect(format!("connect to {} fail!", SERVER_CONFIG_TABLE).as_str());
        let _ = base_dao.execute_ddl(INIT_TABLE_DDL);
        ServerConfigDAO { dao: base_dao }
    }

    /// 插入一条数据
    pub fn insert(&self, config: ServerConfigDO) -> Result<usize, Box<dyn Error>> {
        match self.exist_label(config.label.clone()) {
            Ok(false) => {
                let columns = [
                    "label",
                    "server_host",
                    "server_port",
                    "password",
                    "created_time",
                ];
                let now_timestamp = now_timestamp();
                let values: [&(dyn ToSql); 5] = [
                    &config.label.as_str(),
                    &config.server_host.as_str(),
                    &config.server_port,
                    &config.password.as_str(),
                    &now_timestamp,
                ];
                let i = self
                    .dao
                    .insert(&columns, &values)
                    .expect(format!("Failed to insert data: {:?}", config).as_str());
                Ok(i)
            }
            _ => {
                let err_msg = format!(
                    "Failed to check label exist OR label already exist: {}",
                    config.label
                );
                log::error!("{}", err_msg);
                return Err(err_msg.into());
            }
        }
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

    /// 更新配置
    pub fn update_by_id(&self, new_config: ServerConfigDO) -> Result<usize, Box<dyn Error>> {
        let set_columns = ["server_host", "server_port", "password"];
        let set_values: [&dyn ToSql; 3] = [
            &new_config.server_host,
            &new_config.server_port,
            &new_config.password,
        ];
        let where_values: [&dyn ToSql; 1] = [&new_config.id];

        let i = self
            .dao
            .update(&set_columns, &set_values, "id = ?", &where_values)
            .expect(format!("Failed to update data:{:?}", new_config).as_str());
        Ok(i)
    }

    /// 根据Id查找
    pub fn find_by_id(&self, config_id: i32) -> Result<Option<ServerConfigDO>, Box<dyn Error>> {
        let sql = format!(
            "SELECT id, server_host, server_port, password, created_time,label FROM {} where id = ?",
            SERVER_CONFIG_TABLE
        );
        let params: [&dyn ToSql; 1] = [&config_id];
        let res = self
            .dao
            .query(sql.as_str(), &params, |row| {
                Ok(ServerConfigDO {
                    id: row.get(0)?,
                    server_host: row.get(1)?,
                    server_port: row.get(2)?,
                    password: row.get(3)?,
                    create_time: row.get(4)?,
                    label: row.get(5)?,
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
    pub fn find_all(&self) -> Result<Vec<ServerConfigDO>, Box<dyn Error>> {
        let sql = format!(
            "SELECT id, server_host, server_port, password, created_time,label FROM {}",
            SERVER_CONFIG_TABLE
        );
        let res = self
            .dao
            .query(&sql, &[], |row| {
                Ok(ServerConfigDO {
                    id: row.get(0)?,
                    server_host: row.get(1)?,
                    server_port: row.get(2)?,
                    password: row.get(3)?,
                    create_time: row.get(4)?,
                    label: row.get(5)?,
                })
            })
            .expect("Failed to query all data.");
        Ok(res)
    }

    pub fn exist_label(&self, label: String) -> Result<bool, Box<dyn Error>> {
        let sql = format!(
            "SELECT COUNT(*) FROM {} WHERE label = ?",
            SERVER_CONFIG_TABLE
        );
        let count: i64 = self
            .dao
            .query(&sql, &[&label], |row| row.get(0))?
            .into_iter()
            .next()
            .unwrap_or(0); // 获取查询结果
        Ok(count > 0)
    }

    pub fn page(
        &self,
        page: i32,
        page_size: i32,
    ) -> Result<PageResult<ServerConfigDO>, Box<dyn Error>> {
        let offset = (page - 1) * page_size; // 计算偏移量
        let sql = format!(
            "SELECT id, server_host, server_port, password, created_time ,label
             FROM {} 
             ORDER BY id DESC 
             LIMIT ? OFFSET ?",
            SERVER_CONFIG_TABLE
        );

        let records = self.dao.query(&sql, &[&page_size, &offset], |row| {
            Ok(ServerConfigDO {
                id: row.get(0)?,
                server_host: row.get(1)?,
                server_port: row.get(2)?,
                password: row.get(3)?,
                create_time: row.get(4)?, // created_time 可能为 NULL
                label: row.get(5)?,
            })
        })?;

        let total = self.count()?;

        Ok(PageResult::new(total, records))
    }

    pub fn reset_data(&self) -> Result<usize, Box<dyn Error>> {
        let drop_ddl = format!("DROP TABLE {};", SERVER_CONFIG_TABLE);
        let _ = self.dao.execute_ddl(&drop_ddl).expect("Drop table failed.");
        let _ = self
            .dao
            .execute_ddl(INIT_TABLE_DDL)
            .expect("Init table failed.");
        Ok(0)
    }

    /// 统计表中的总记录数
    pub fn count(&self) -> Result<i64, Box<dyn Error>> {
        let sql = format!("SELECT COUNT(*) FROM {}", SERVER_CONFIG_TABLE);

        let count: i64 = self
            .dao
            .query(&sql, &[], |row| row.get(0))?
            .into_iter()
            .next()
            .unwrap_or(0); // 获取查询结果

        Ok(count)
    }
}
