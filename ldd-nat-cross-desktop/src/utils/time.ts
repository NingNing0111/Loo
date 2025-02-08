export const formatTimestamp = (timestamp: number): string => {
  const date = new Date(timestamp);

  const year = date.getFullYear();
  // getMonth 返回的月份范围是 0-11，需要 +1 后再格式化
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  const hour = date.getHours().toString().padStart(2, '0');
  const minute = date.getMinutes().toString().padStart(2, '0');
  const second = date.getSeconds().toString().padStart(2, '0');

  return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
};

/**
 * 将几秒转换为 HH:mm:ss格式
 * @param seconds 秒
 */
export const formatSeconds2HMS = (seconds: number): string => {
  const hrs = String(Math.floor(seconds / 3600)).padStart(2, '0');
  const mins = String(Math.floor((seconds % 3600) / 60)).padStart(2, '0');
  const secs = String(seconds % 60).padStart(2, '0');
  return `${hrs}:${mins}:${secs}`;
};
