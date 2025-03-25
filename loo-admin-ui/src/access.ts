export default (initialState: API.LoginUserVO) => {
  // 在这里按照初始化数据定义项目中的权限，统一管理
  // 参考文档 https://umijs.org/docs/max/access
  const canAccess = initialState && initialState.role === 'admin';
  return {
    canAccess,
  };
};
