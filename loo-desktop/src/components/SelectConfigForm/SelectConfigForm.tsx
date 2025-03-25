import { LocalProxyConfig, ServerConfig } from '@/models/types';
import { SelectOutlined } from '@ant-design/icons';
import { ModalForm, ProFormSelect } from '@ant-design/pro-components';
import { Button } from 'antd';

interface Props {
  title?: string;
  label?: string;
  placeholder?: string;
  triggerName: string;
  maxCount?: number;
  data: LocalProxyConfig[] | ServerConfig[];
  keyHost: 'host' | 'serverHost';
  keyPort: 'port' | 'serverPort';
  initialValue?: any;
  onFinish: (formData: any) => Promise<any>;
}

const SelectConfigForm: React.FC<Props> = (props) => {
  let options: any = props.data.map((item) => {
    return {
      label: item.label,
      value: item.id,
    };
  });
  return (
    <ModalForm
      title={props.title}
      style={{ paddingTop: 30 }}
      layout="horizontal"
      trigger={
        <Button type="primary" icon={<SelectOutlined />}>
          {props.triggerName}
        </Button>
      }
      onFinish={async (formData: { ids: number[] }) => {
        let ids = formData.ids;
        let selectedData = props.data.filter(
          (item) => item.id && ids.includes(item.id),
        );
        await props.onFinish(selectedData);
        return true;
      }}
    >
      <ProFormSelect
        options={options}
        name="ids"
        label={props.label}
        placeholder={props.placeholder}
        initialValue={props.initialValue}
        mode="multiple"
        fieldProps={{
          maxCount: props.maxCount,
        }}
      ></ProFormSelect>
    </ModalForm>
  );
};

export default SelectConfigForm;
