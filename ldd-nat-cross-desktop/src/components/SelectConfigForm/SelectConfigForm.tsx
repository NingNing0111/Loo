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
  onFinish: (formData: any) => Promise<any>;
}

const SelectConfigForm: React.FC<Props> = (props) => {
  let options: any = props.data.map((item) => {
    if (props.keyHost === 'host' && props.keyPort === 'port') {
      let s = item as LocalProxyConfig;
      return {
        label: (
          <span>
            {s.protocol + '/' + s.host + ':' + s.port + ' -> ' + s.openPort}
          </span>
        ),
        value: s.id,
      };
    }
    let s = item as ServerConfig;
    return {
      label: <span>{s.serverHost + ':' + s.serverPort}</span>,
      value: s.id,
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
        mode="multiple"
        fieldProps={{
          maxCount: props.maxCount,
        }}
      ></ProFormSelect>
    </ModalForm>
  );
};

export default SelectConfigForm;
