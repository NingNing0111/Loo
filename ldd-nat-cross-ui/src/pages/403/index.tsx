import { ReactComponent as Svg403 } from '@/icons/403/403.svg';
import { PageContainer } from '@ant-design/pro-components';
import { Button, Flex } from 'antd';

const Page403 = () => {
  const toLogout = () => {
    // 清空本地存储
    localStorage.clear();
    // 跳转到登录页
    window.location.href = '/login';
  };

  return (
    <PageContainer>
      <Flex justify="center" align="center" style={{ height: '100%' }} vertical>
        <Svg403 />
        <span>抱歉,你无权访问该页面</span>
        <Button style={{ marginTop: 60 }} type="primary" onClick={toLogout}>
          退出登录
        </Button>
      </Flex>
    </PageContainer>
  );
};

export default Page403;
