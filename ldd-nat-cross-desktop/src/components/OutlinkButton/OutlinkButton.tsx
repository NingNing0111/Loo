import { open } from '@tauri-apps/plugin-shell';
import { Button } from 'antd';
import { ReactNode } from 'react';

interface Props {
  href: string;
  icon?: ReactNode;
  name?: string;
  variant?: 'outlined' | 'dashed' | 'solid' | 'filled' | 'text' | 'link';
}

const OutlinkButton: React.FC<Props> = (props) => {
  return (
    <Button
      icon={props.icon}
      onClick={async () => {
        await open(props.href);
      }}
      color="primary"
      variant={props.variant}
      size="small"
    >
      {props.name}
    </Button>
  );
};

export default OutlinkButton;
