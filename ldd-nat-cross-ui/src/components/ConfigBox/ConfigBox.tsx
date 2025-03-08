import {
  ModalForm,
  ProFormSelect,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { Button } from 'antd';
import { ReactNode } from 'react';

interface Props {
  type: 'add' | 'update';
  title: string;
  btnIcon?: ReactNode;
  btnName: string;
  serverOptions?: { label: string; value: string }[];
  onFinish: (formData: any) => Promise<void>;
  trigger?: () => Promise<void>;
  initialValues?: any;
  btnSize?: 'small' | 'large' | 'middle';
}
const AddConfigBox: React.FC<Props> = (props) => {
  return (
    <ModalForm
      layout="horizontal"
      autoFocusFirstInput
      title={props.title}
      trigger={
        <Button
          type="primary"
          icon={props.btnIcon}
          onClick={props.trigger}
          size="small"
        >
          {props.btnName}
        </Button>
      }
      onFinish={async (formData) => {
        await props.onFinish(formData);
        return true;
      }}
    >
      <ProFormSelect
        label="服务名"
        initialValue={props.initialValues?.serverName}
        placeholder="请选择服务名"
        name="serverName"
        options={props.serverOptions}
        disabled={props.type === 'update'}
      />
      <ProFormTextArea
        label="黑名单"
        placeholder="使用英文逗号分隔(,)"
        name="blackListStr"
        fieldProps={{
          autoSize: false,
        }}
        initialValue={
          props.initialValues?.blackList
            ? props.initialValues.blackList.join(',')
            : ''
        }
      />
      <ProFormTextArea
        initialValue={
          props.initialValues?.whiteList
            ? props.initialValues.whiteList.join(',')
            : ''
        }
        label="白名单"
        placeholder="使用英文逗号分隔(,)"
        name="whiteListStr"
      />
    </ModalForm>
  );
};

export default AddConfigBox;
