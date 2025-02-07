import {
  PageContainer,
  ProCard,
  ProFormUploadDragger,
} from '@ant-design/pro-components';
import { Button } from 'antd';

const ConfigPage: React.FC = () => {
  return (
    <PageContainer ghost>
      <ProCard
        title="导入"
        bordered
        headerBordered
        extra={<Button type="primary">添加</Button>}
      >
        <ProFormUploadDragger max={4} name="dragger" />
      </ProCard>

      <ProCard bordered headerBordered title="详情">
        <ProCard title="服务端配置" bordered headerBordered></ProCard>
        <ProCard title="本地代理配置" bordered headerBordered></ProCard>
      </ProCard>
    </PageContainer>
  );
};

export default ConfigPage;
