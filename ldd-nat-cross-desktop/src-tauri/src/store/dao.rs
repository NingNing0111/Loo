use rusqlite::{Connection, Result, ToSql};

use crate::sys::get_db_path;

pub struct BaseDAO {
    table: String,
    conn: Connection,
}

impl BaseDAO {
    /// 创建新的 BaseDAO 实例，自动转义表名
    pub fn new(db_name: &str, table: &str) -> Result<Self> {
        let db_path = get_db_path(db_name);
        let conn = Connection::open(db_path)?;
        Ok(Self {
            table: table.to_string(),
            conn,
        })
    }

    /// 执行 DDL 语句
    pub fn execute_ddl(&self, ddl: &str) -> Result<usize> {
        self.conn.execute(ddl, [])
    }

    /// 插入数据，自动转义列名
    pub fn insert(&self, columns: &[&str], values: &[&dyn ToSql]) -> Result<usize> {
        let cols: Vec<String> = columns.iter().map(|col| col.to_string()).collect();
        let placeholders = vec!["?"; columns.len()].join(", ");
        let sql = format!(
            "INSERT INTO {} ({}) VALUES ({})",
            self.table,
            cols.join(", "),
            placeholders
        );
        self.conn.execute(&sql, values)
    }

    /// 更新数据
    pub fn update(
        &self,
        set_columns: &[&str],
        set_values: &[&dyn ToSql],
        where_clause: &str,
        where_values: &[&dyn ToSql],
    ) -> Result<usize> {
        let set_exprs: Vec<String> = set_columns
            .iter()
            .map(|col| format!("{} = ?", col))
            .collect();

        let sql = format!(
            "UPDATE {} SET {} WHERE {}",
            self.table,
            set_exprs.join(", "),
            where_clause
        );

        let mut params: Vec<&dyn ToSql> = Vec::new();
        params.extend(set_values.iter().map(|&v| v));
        params.extend(where_values.iter().map(|&v| v));

        self.conn.execute(&sql, &params[..]) // 传递切片
    }

    /// 删除数据，自动转义表名
    pub fn delete(&self, where_clause: &str, where_values: &[&dyn ToSql]) -> Result<usize> {
        let sql = format!("DELETE FROM {} WHERE {}", self.table, where_clause);
        self.conn.execute(&sql, where_values)
    }

    /// 通用查询方法
    pub fn query<T, F>(&self, sql: &str, params: &[&dyn ToSql], mapper: F) -> Result<Vec<T>>
    where
        F: Fn(&rusqlite::Row) -> Result<T>,
    {
        let mut stmt = self.conn.prepare(sql)?;
        let rows = stmt.query_map(params, mapper)?;

        rows.collect() // 使用 collect 简化结果处理
    }
}
