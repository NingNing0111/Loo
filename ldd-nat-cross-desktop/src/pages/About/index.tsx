import Guide from '@/components/Guide';
import { trim } from '@/utils/format';
import { PageContainer } from '@ant-design/pro-components';
import styles from './index.less';

const AboutPage: React.FC = () => {
  return (
    <PageContainer ghost>
      <div className={styles.container}>
        <Guide name={trim('AboutPage')} />
      </div>
    </PageContainer>
  );
};

export default AboutPage;
