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
