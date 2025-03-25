import { getHomeInfo } from '@/command/home';
import './index.less';

import { ReactComponent as SvgConfiguration } from '@/icons/home/Configuration.svg';
import { ReactComponent as SvgError } from '@/icons/home/Error.svg';
import { ReactComponent as SvgServer } from '@/icons/home/Server.svg';
import { ReactComponent as SvgSuccess } from '@/icons/home/Success.svg';
import { BasePageParam, DEFAULT_PAGE_PARAM, HomeCntInfo } from '@/models/types';

import { ConnectLog, pageLogs } from '@/command/log';
import OutlinkButton from '@/components/OutlinkButton/OutlinkButton';
import { formatTimestamp } from '@/utils/time';
import {
  PageContainer,
  ProCard,
  ProDescriptions,
  ProList,
  StatisticCard,
} from '@ant-design/pro-components';
import { Tag } from 'antd';
import { useEffect, useState } from 'react';

type StatisticCardType = {
  title: string;
  value: number;
  icon: React.ReactNode;
};

const HomePage: React.FC = () => {
  const [homeCntInfo, setHomeCntInfo] = useState<HomeCntInfo>();
  const [logPageParam, setLogPageParam] =
    useState<BasePageParam>(DEFAULT_PAGE_PARAM);
  const [connectLogList, setConnectLogList] = useState<ConnectLog[]>();
  const [logTotal, setLogTotal] = useState<number>(0);

  const loadHomeInfo = async () => {
    let res = await getHomeInfo();
    if (res.code === 0) {
      setHomeCntInfo(res.data);
    }
  };
  const loadConnectLogList = async () => {
    let res = await pageLogs(logPageParam.page, logPageParam.pageSize);
    if (res.code === 0) {
      let pageData = res.data;
      setConnectLogList(pageData.records);
      setLogTotal(pageData.total);
    }
  };
  useEffect(() => {
    loadHomeInfo();
    loadConnectLogList();
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
      title: '错误日志',
      value: homeCntInfo ? homeCntInfo.failedCnt : 0,
      icon: <SvgError className="icon-style" />,
    },
    {
      title: '正常日志',
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
    introduce: '一款使用方便、界面简洁的内网穿透工具',
  };

  // 功能特性
  const futureInfo = [
    {
      id: '1',
      name: '高性能及内存安全',
      description: '客户端基于Rust + Tokio实现，具有内存安全、高性能的特点',
    },
    {
      id: '2',
      name: '简单的配置管理',
      description:
        '提供友好简洁的服务端和代理端的配置信息管理界面，支持网络连接测试',
    },
    {
      id: '3',
      name: 'C/S端监控',
      description:
        '通过管理端界面对服务端和客户端进行监控，支持客户端强制下线的功能',
    },
    {
      id: '4',
      name: '尺寸小、跨平台',
      description: '使用Tauri进行打包发布，支持跨平台',
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
        <ProCard title="开发信息" colSpan="28%">
          <ProDescriptions column={1} dataSource={descriptionInfo}>
            <ProDescriptions.Item label="版本号" dataIndex="version" />

            <ProDescriptions.Item label="开发作者">
              <OutlinkButton
                href={descriptionInfo.author.blog}
                name={descriptionInfo.author.name}
                variant="link"
              />
            </ProDescriptions.Item>
            <ProDescriptions.Item label="联系方式" copyable>
              {descriptionInfo.author.email}
            </ProDescriptions.Item>
            <ProDescriptions.Item label="开源仓库" copyable>
              <OutlinkButton
                href={descriptionInfo.project}
                name="Github"
                variant="link"
              />
            </ProDescriptions.Item>

            <ProDescriptions.Item
              label="更新时间"
              dataIndex="lastUpdateTime"
              valueType="date"
            />
            <ProDescriptions.Item label="项目介绍" dataIndex="introduce" />
          </ProDescriptions>
        </ProCard>

        <ProCard title="功能特性" colSpan="32%">
          <ProList
            rowKey={'id'}
            dataSource={futureInfo}
            showActions="hover"
            metas={{
              title: {
                dataIndex: 'name',
                render: (dom, entity) => {
                  return <span>{`${entity.id}. ${entity.name}`}</span>;
                },
              },
              description: {
                dataIndex: 'description',
              },
            }}
            pagination={false}
          />
        </ProCard>

        <ProCard title="运行日志" colSpan="40%">
          <ProList
            rowKey={'id'}
            dataSource={connectLogList}
            showActions="hover"
            metas={{
              title: {
                dataIndex: 'logType',
                render: (dom, entity) => {
                  return (
                    <>
                      {entity.logType === 0 ? (
                        <Tag color="success">成功</Tag>
                      ) : (
                        <Tag color="red">失败</Tag>
                      )}
                      {
                        <span>
                          {formatTimestamp((entity.createdTime ?? 0) * 1000)}
                        </span>
                      }
                    </>
                  );
                },
              },
              description: {
                dataIndex: 'description',
              },
            }}
            pagination={{
              pageSize: logPageParam.pageSize,
              current: logPageParam.page,
              simple: true,
              total: logTotal,
              showSizeChanger: false,
              onChange: (page, pageSize) => {
                logPageParam.page = page;
                logPageParam.pageSize = pageSize;
                setLogPageParam(logPageParam);
                loadConnectLogList();
              },
            }}
          />
        </ProCard>
      </ProCard>
    </PageContainer>
  );
};

export default HomePage;
