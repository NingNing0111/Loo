// 全局共享数据示例
import { useState } from 'react';

const useUser = () => {
  const [loginUser, setLoginUser] = useState<API.LoginUserVO>();
  const setJwt = (jwt: string) => {
    localStorage.setItem('jwt', jwt);
  };
  return {
    loginUser,
    setLoginUser,
    setJwt,
  };
};

export default useUser;
