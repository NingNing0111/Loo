import { SettingInfo } from '@/models/types';
import { SettingTwoTone } from '@ant-design/icons';
import {
  ModalForm,
  ProForm,
  ProFormSelect,
  ProFormSwitch,
} from '@ant-design/pro-components';
import { useAntdConfigSetter } from '@umijs/max';
import { Button, Divider, theme } from 'antd';
import { useEffect } from 'react';
const { darkAlgorithm, defaultAlgorithm, compactAlgorithm } = theme;

interface Props {
  title: string;
  onFinish: (formData: any) => Promise<void>;
  initialValues: SettingInfo | undefined;
}

const SettingForm: React.FC<Props> = (props) => {
  const setAntConfig = useAntdConfigSetter();

  const loadTheme = (settingInfo: SettingInfo) => {
    let algorithm = [];
    if (settingInfo.theme === 'dark') {
      algorithm.push(darkAlgorithm);
    } else {
      algorithm.push(defaultAlgorithm);
    }
    if (settingInfo.compact) {
      algorithm.push(compactAlgorithm);
    }
    setAntConfig({
      theme: {
        algorithm,
      },
    });
  };

  const onFinish = async (formData: SettingInfo) => {
    await props.onFinish(formData);
    loadTheme(formData);
    return true;
  };

  useEffect(() => {
    if (props.initialValues) {
      loadTheme(props.initialValues);
    }
  }, []);

  return (
    <ModalForm
      title={props.title}
      layout="horizontal"
      trigger={<Button icon={<SettingTwoTone />}></Button>}
      autoFocusFirstInput
      onFinish={onFinish}
      initialValues={props.initialValues}
    >
      <Divider />
      <ProForm.Group
        title="外观设置"
        rowProps={{
          gutter: 12,
        }}
      >
        <ProFormSelect
          name="theme"
          label="主题"
          width="sm"
          valueEnum={{
            dark: '黑夜模式',
            light: '白天模式',
          }}
        />
        <ProFormSelect
          name="language"
          label="语言"
          width="sm"
          valueEnum={{
            zh: '中文简体',
            en: 'English',
          }}
        />
        <ProFormSwitch name="compact" label="紧凑布局" width="sm" />
      </ProForm.Group>
    </ModalForm>
  );
};

export default SettingForm;
