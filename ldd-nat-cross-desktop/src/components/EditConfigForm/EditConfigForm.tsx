import {
  ModalForm,
  ProFormDigit,
  ProFormSelect,
  ProFormText,
} from '@ant-design/pro-components';
import { Button, Tag } from 'antd';
import { ReactNode } from 'react';

interface Props {
  type: 'server' | 'proxy';
  btnName: string;
  btnIcon?: ReactNode;
  title: string;
  keyHost: string;
  keyPort: string;
  onFinish: (formData: any) => Promise<any>;
  initialValues: any;
  size?: 'small' | 'middle' | 'large';
}

const EditConfigForm: React.FC<Props> = (props) => {
  return (
    <ModalForm
      layout="horizontal"
      autoFocusFirstInput
      title={props.title}
      trigger={
        <Button size={props.size} type="primary" icon={props.btnIcon}>
          {props.btnName}
        </Button>
      }
      onFinish={async (formData) => {
        await props.onFinish(formData);
        return true;
      }}
      initialValues={props.initialValues}
    >
      <ProFormText
        disabled
        label="标签"
        placeholder="请输入标签"
        vertical
        width="md"
      />
      <ProFormText
        label="主机名"
        name={props.keyHost}
        placeholder="请输入主机名称"
        vertical
        width="md"
      />
      <ProFormDigit
        label="端口"
        name={props.keyPort}
        placeholder="请输入端口"
        min={1024}
        max={65535}
        fieldProps={{ precision: 0 }}
        vertical
        width="md"
      />
      {props.type === 'server' && (
        <ProFormText.Password
          vertical
          width="md"
          name="password"
          label="接入密码"
          placeholder="请输入服务端密码"
        />
      )}
      {props.type === 'proxy' && (
        <>
          <ProFormDigit
            label="映射端口"
            name="openPort"
            placeholder="请输入映射端口"
            min={1024}
            max={65535}
            fieldProps={{ precision: 0 }}
            vertical
            width="md"
          />
          <ProFormSelect
            vertical
            width="md"
            name="protocol"
            label="代理协议"
            placeholder="请选择代理协议"
            valueEnum={{
              tcp: <Tag color="success">TCP</Tag>,
              udp: <Tag color="warning">UDP</Tag>,
            }}
          />
        </>
      )}
    </ModalForm>
  );
};

export default EditConfigForm;
