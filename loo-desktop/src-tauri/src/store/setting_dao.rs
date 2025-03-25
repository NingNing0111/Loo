use std::error::Error;

use rusqlite::ToSql;

use crate::global::USER_DB;

use super::{dao::BaseDAO, model::setting::SettingInfoDO};

const SETTING_TABLE: &str = "setting_info";
const INIT_TABLE_DDL: &str = "
    CREATE TABLE IF NOT EXISTS setting_info (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        theme TEXT NOT NULL,
        language TEXT NOT NULL,
        compact INTEGER NOT NULL
    );
";
const DEFAULT_THEME: &str = "system";
const DEFAULT_LANGUAGE: &str = "en";
const DEFAULT_COMPACT: bool = false;
pub struct SettingInfoDAO {
    dao: BaseDAO,
}

impl SettingInfoDAO {
    pub fn new() -> Self {
        let base_dao = BaseDAO::new(USER_DB, SETTING_TABLE)
            .expect(format!("connect to {} fail!", SETTING_TABLE).as_str());
        let _ = base_dao.execute_ddl(INIT_TABLE_DDL);
        let setting_dao = SettingInfoDAO { dao: base_dao };
        let s = setting_dao.count().unwrap();
        if s == 0 {
            let _ = setting_dao.reset_data();
        }
        setting_dao
    }

    // 插入 根本不会用到 不设置为pub
    fn insert(&self, config: SettingInfoDO) -> Result<usize, Box<dyn Error>> {
        let columns = ["theme", "language", "compact"];
        let values: [&(dyn ToSql); 3] = [&config.theme, &config.language, &config.compact];
        let i = self
            .dao
            .insert(&columns, &values)
            .expect(format!("Failed to insert data: {:?}", config).as_str());
        Ok(i)
    }

    /// 更新配置
    pub fn update(&self, new_setting: SettingInfoDO) -> Result<usize, Box<dyn Error>> {
        let set_columns = ["theme", "language", "compact"];
        let set_values: [&dyn ToSql; 3] = [
            &new_setting.theme,
            &new_setting.language,
            &new_setting.compact,
        ];
        let id: i32 = 1;
        let where_values: [&dyn ToSql; 1] = [&id];

        let i = self
            .dao
            .update(&set_columns, &set_values, "id = ?", &where_values)
            .expect(&format!("Failed to update data:{:?}", new_setting));
        Ok(i)
    }

    /// 根据Id查找 id为1
    pub fn get(&self, config_id: i32) -> Result<Option<SettingInfoDO>, Box<dyn Error>> {
        let sql = format!(
            "SELECT theme, language, compact FROM {} where id = ?",
            SETTING_TABLE
        );
        let params: [&dyn ToSql; 1] = [&config_id];
        let res = self
            .dao
            .query(sql.as_str(), &params, |row| {
                Ok(SettingInfoDO {
                    theme: row.get(0)?,
                    language: row.get(1)?,
                    compact: row.get(2)?,
                })
            })
            .expect(format!("Failed to query data by id:{}", config_id).as_str());
        let res = match res.get(0) {
            Some(e) => Some(e.clone()),
            None => None,
        };
        Ok(res)
    }

    pub fn reset_data(&self) -> Result<usize, Box<dyn Error>> {
        let drop_ddl = format!("DROP TABLE {};", SETTING_TABLE);
        let _ = self.dao.execute_ddl(&drop_ddl).expect("Drop table failed.");
        let _ = self
            .dao
            .execute_ddl(INIT_TABLE_DDL)
            .expect("Init table failed.");
        let _ = self.insert(SettingInfoDO {
            theme: String::from(DEFAULT_THEME),
            language: String::from(DEFAULT_LANGUAGE),
            compact: DEFAULT_COMPACT,
        });
        Ok(0)
    }

    pub fn count(&self) -> Result<i64, Box<dyn Error>> {
        let sql = format!("SELECT COUNT(*) FROM {}", SETTING_TABLE);

        let count: i64 = self
            .dao
            .query(&sql, &[], |row| row.get(0))?
            .into_iter()
            .next()
            .unwrap_or(0); // 获取查询结果

        Ok(count)
    }
}
