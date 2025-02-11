use bytes::Bytes;
use std::{collections::HashMap, sync::Arc};
use tokio::sync::{mpsc, Mutex};

#[derive(Debug, Clone)]
pub struct LocalManager {
    pub senders: Arc<Mutex<HashMap<String, mpsc::Sender<Bytes>>>>,
}

impl LocalManager {
    pub fn new() -> Self {
        LocalManager {
            senders: Arc::new(Mutex::new(HashMap::new())),
        }
    }
    pub async fn put_sender(&self, visitor_id: String, sender: mpsc::Sender<Bytes>) {
        let mut senders = self.senders.lock().await;
        senders.insert(visitor_id, sender);
    }

    pub async fn get_sender(&self, visitor_id: &str) -> Option<mpsc::Sender<Bytes>> {
        let senders = self.senders.lock().await;
        senders.get(visitor_id).cloned()
    }

    pub async fn remove_sender(&self, visitor_id: &str) {
        let mut senders = self.senders.lock().await;
        senders.remove(visitor_id).unwrap();
    }
}
