const AuthRouter = (props: any) => {
  //   let userInfoRes =  userInfo();
  //   console.log(userInfoRes);

  //   if (userInfoRes.code === 0) {
  //     return props.children;
  //   }
  //   return <Redirect to="/login" />;
  return props.children;
};

export default AuthRouter;
