import { getHomeInfo } from '@/command/home';
import './index.less';

import { ReactComponent as SvgConfiguration } from '@/icons/home/Configuration.svg';
import { ReactComponent as SvgError } from '@/icons/home/Error.svg';
import { ReactComponent as SvgServer } from '@/icons/home/Server.svg';
import { ReactComponent as SvgSuccess } from '@/icons/home/Success.svg';
import { HomeCntInfo } from '@/models/types';

import {
  PageContainer,
  ProCard,
  ProDescriptions,
  ProList,
  StatisticCard,
} from '@ant-design/pro-components';
import { useEffect, useState } from 'react';

type StatisticCardType = {
  title: string;
  value: number;
  icon: React.ReactNode;
};

const HomePage: React.FC = () => {
  const [homeCntInfo, setHomeCntInfo] = useState<HomeCntInfo>();
  const loadHomeInfo = async () => {
    let res = await getHomeInfo();
    if (res.code === 0) {
      setHomeCntInfo(res.data);
    }
  };
  useEffect(() => {
    loadHomeInfo();
  }, []);

  // 卡片统计信息
  const cardInfos: StatisticCardType[] = [
    {
      title: '代理配置数',
      value: homeCntInfo ? homeCntInfo.proxyCnt : 0,
      icon: <SvgConfiguration className="icon-style" />,
    },
    {
      title: '服务端数',
      value: homeCntInfo ? homeCntInfo.serverCnt : 0,
      icon: <SvgServer className="icon-style" />,
    },
    {
      title: '连接错误数',
      value: homeCntInfo ? homeCntInfo.failedCnt : 0,
      icon: <SvgError className="icon-style" />,
    },
    {
      title: '连接成功数',
      value: homeCntInfo ? homeCntInfo.successedCnt : 0,
      icon: <SvgSuccess className="icon-style" />,
    },
  ];
  // 开发信息
  const descriptionInfo = {
    project: 'https://github.com/ningning0111/ldd-nat-cross',
    author: {
      blog: 'https://pgthinker.me',
      name: 'Pgthinker',
      email: 'zdncode@gmail.com',
      github: 'https://github.com/ningning0111',
    },
    lastUpdateTime: new Date(),
    version: '1.0.0',
    introduce: '这是项目介绍',
  };

  // 功能特性
  const futureInfo = [
    {
      id: '1',
      name: 'TCP/UDP穿透',
      description: '支持基本的端口穿透',
    },
    {
      id: '2',
      name: '支持服务端配置',
      description: '支持服务端配置',
    },
    {
      id: '3',
      name: '支持连接错误数',
      description: '支持连接错误数',
    },
    {
      id: '4',
      name: '支持连接成功数',
      description: '支持连接成功数',
    },
    {
      id: '5',
      name: '支持连接错误数',
      description: '支持连接错误数',
    },
    {
      id: '6',
      name: '支持连接成功数',
      description: '支持连接成功数',
    },
    {
      id: '7',
      name: '支持连接错误数',
      description: '支持连接错误数',
    },
    {
      id: '8',
      name: '支持连接成功数',
      description: '支持连接成功数',
    },
  ];

  // 日志内容
  const logInfo = [
    {
      id: '1',
      type: 'Future',
      version: '1.0.0',
      description: '初始化项目',
      issue: 'https://example.com',
      updateTime: new Date(),
    },
    {
      id: '2',
      type: 'Bug',
      version: '1.0.1',
      issue: 'https://example.com',
      description: '修复bug',
      updateTime: new Date(),
    },
  ];

  return (
    <PageContainer ghost>
      <StatisticCard.Group direction={'row'} bordered className="content-card">
        {cardInfos.map((item, index) => (
          <StatisticCard
            key={index}
            statistic={{
              title: item.title,
              value: item.value,
              icon: item.icon,
            }}
          />
        ))}
      </StatisticCard.Group>

      <ProCard
        split={'vertical'}
        bordered
        headerBordered
        className="content-card"
      >
        <ProCard title="开发信息">
          <ProDescriptions column={1} dataSource={descriptionInfo}>
            <ProDescriptions.Item label="作者">
              <a href={descriptionInfo.author.blog}>
                {descriptionInfo.author.name}
              </a>
            </ProDescriptions.Item>
            <ProDescriptions.Item label="邮箱" copyable>
              {descriptionInfo.author.email}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="仓库" copyable>
              <a href={descriptionInfo.project}>GitHub</a>
            </ProDescriptions.Item>
            <ProDescriptions.Item label="版本号" dataIndex="version" />

            <ProDescriptions.Item
              label="更新时间"
              dataIndex="lastUpdateTime"
              valueType="date"
            />
            <ProDescriptions.Item label="项目介绍" dataIndex="introduce" />
          </ProDescriptions>
        </ProCard>
        <ProCard title="功能特性">
          <ProList
            rowKey={'id'}
            dataSource={futureInfo}
            showActions="hover"
            metas={{
              title: {
                dataIndex: 'name',
              },
              description: {
                dataIndex: 'description',
              },
            }}
            pagination={{
              pageSize: 5,
            }}
          />
        </ProCard>
        <ProCard title="更新日志">
          <ProList
            rowKey={'id'}
            dataSource={logInfo}
            showActions="hover"
            metas={{
              title: {
                dataIndex: 'description',
              },
              description: {
                dataIndex: 'version',
              },
              extra: {
                dataIndex: 'updateTime',
                valueType: 'date',
              },
            }}
            pagination={{
              pageSize: 5,
            }}
          />
        </ProCard>
      </ProCard>
    </PageContainer>
  );
};

export default HomePage;
