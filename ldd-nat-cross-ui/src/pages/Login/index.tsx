import { auth } from '@/services/authController';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { LoginForm, ProFormText } from '@ant-design/pro-components';
import { history, useModel, useNavigate } from '@umijs/max';
import { useEffect } from 'react';

const LoginPage: React.FC = () => {
  const { setLoginUser, setJwt } = useModel('global');
  const navigate = useNavigate();

  const handleSubmit = async (values: API.AuthVO) => {
    const res = await auth(values);
    if (res) {
      setLoginUser(res);
      setJwt(res.token);
      history.push('/');
      navigate(0);
    }
  };

  const initUserInfo = async () => {
    localStorage.removeItem('jwt');
    console.log('清理数据缓存');
  };

  useEffect(() => {
    initUserInfo();
  }, []);

  return (
    <LoginForm
      contentStyle={{
        minWidth: 280,
        maxWidth: '75vw',
      }}
      logo={<img alt="logo" src="/logo.svg" />}
      title="Admin Login"
      subTitle={' '}
      initialValues={{
        autoLogin: true,
      }}
      onFinish={async (values: API.AuthVO) => {
        await handleSubmit(values);
      }}
    >
      <ProFormText
        name="username"
        fieldProps={{
          size: 'large',
          prefix: <UserOutlined />,
        }}
        placeholder={'请输入用户名'}
        rules={[
          {
            required: true,
            message: '用户名是必填项！',
          },
        ]}
      />
      <ProFormText.Password
        name="password"
        fieldProps={{
          size: 'large',
          prefix: <LockOutlined />,
        }}
        placeholder={'请输入密码'}
        rules={[
          {
            required: true,
            message: '密码是必填项！',
          },
        ]}
      />
    </LoginForm>
  );
};

export default LoginPage;
