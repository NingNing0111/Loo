import { open } from '@tauri-apps/plugin-shell';
import { Button } from 'antd';
import { ButtonShape, ButtonType } from 'antd/es/button';
import { ReactNode } from 'react';

interface Props {
  href: string;
  icon?: ReactNode;
  name?: string;
  variant?: 'outlined' | 'dashed' | 'solid' | 'filled' | 'text' | 'link';
  type?: ButtonType;
  shape?: ButtonShape;
}

const OutlinkButton: React.FC<Props> = (props) => {
  return (
    <Button
      icon={props.icon}
      onClick={async () => {
        await open(props.href);
      }}
      type={props.type}
      color="primary"
      variant={props.variant}
      size="small"
      shape={props.shape}
    >
      {props.name}
    </Button>
  );
};

export default OutlinkButton;
