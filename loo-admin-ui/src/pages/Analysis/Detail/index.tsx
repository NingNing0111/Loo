import {
  analysis,
  lastSystemData,
} from '@/services/serverSystemInfoController';
import { ProCard } from '@ant-design/pro-components';
import { Button, Select } from 'antd';
import ReactECharts from 'echarts-for-react';
import { useEffect, useState } from 'react';

type TimeType = 'day' | 'month';
interface Props {
  serverName: string;
}

const getLineChartOption = (analysisList: API.AnalysisDataVO[]) => {
  const option = {
    title: {
      text: ``,
    },
    tooltip: {
      trigger: 'axis',
    },
    legend: {
      data: [
        'JVM最大可用内存',
        'JVM已分配的总内存',
        'JVM剩余内存',
        'JVM已用内存',
      ],
    },
    xAxis: {
      type: 'category',
      data: analysisList.map((item) => item.registerTime), // 后端返回的时间数据
      axisLabel: {
        rotate: 45,
        // formatter: (value: string) => value.split('T')[1],
        formatter: (value: string) => value,
      }, // 旋转角度，防止时间重叠
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: (value: number) => value + 'MB', // 显示 MB 单位
      },
    },
    series: [
      {
        name: 'JVM最大可用内存',
        type: 'line',
        data: analysisList.map((item) => {
          return ((item.jvmMaxMemory as number) / 1024 / 1024).toFixed(2);
        }),
        smooth: false,
      },
      {
        name: 'JVM已分配的总内存',
        type: 'line',
        data: analysisList.map((item) => {
          // return ((item.maxMemory as number) / 1024 / 1024).toFixed(2);
          return ((item.jvmTotalMemory as number) / 1024 / 1024).toFixed(2);
        }),
        smooth: false,
      },

      {
        name: 'JVM已用内存',
        type: 'line',
        data: analysisList.map((item) => {
          return (
            ((item.jvmTotalMemory as number) - (item.jvmFreeMemory as number)) /
            1024 /
            1024
          ).toFixed(2);
        }),
        smooth: false,
      },

      {
        name: 'JVM剩余内存',
        type: 'line',
        data: analysisList.map((item) => {
          return ((item.jvmFreeMemory as number) / 1024 / 1024).toFixed(2);
        }),
        smooth: false,
      },
    ],
  };
  return option;
};

const getPieChartOption = (analysis: API.AnalysisDataVO) => {
  let freeMemory = 0;
  let usedMemory = 0;
  let maxMemory = 0;
  if (analysis.jvmFreeMemory) {
    freeMemory = analysis.jvmFreeMemory / 1024 / 1024;
  }
  if (analysis.jvmTotalMemory && analysis.jvmFreeMemory) {
    usedMemory =
      (analysis.jvmTotalMemory - analysis.jvmFreeMemory) / 1024 / 1024;
  }
  if (analysis.jvmMaxMemory) {
    maxMemory = analysis.jvmMaxMemory / 1024 / 1024;
  }

  const option = {
    title: {
      text: 'JVM 内存使用占比',
      left: 'center',
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} MB ({d}%)',
    },
    legend: {
      orient: 'vertical',
      left: 'left',
    },
    series: [
      {
        name: '内存占比',
        type: 'pie',
        radius: '50%',
        data: [
          {
            value: usedMemory.toFixed(2),
            name: 'JVM已使用内存',
          },
          {
            value: freeMemory.toFixed(2),
            name: 'JVM剩余内存',
          },
          {
            value: (maxMemory - usedMemory - freeMemory).toFixed(2),
            name: '未使用内存',
          },
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
        },
      },
    ],
  };

  return option;
};

const getPillarChartOption = (analysisList: API.AnalysisDataVO[]) => {
  const option = {
    title: {
      text: 'GC 统计信息',
      left: 'center',
    },
    tooltip: {
      trigger: 'axis',
    },
    legend: {
      data: ['GC 总次数', 'GC 总耗时'],
      bottom: 0,
    },
    xAxis: {
      type: 'category',
      data: analysisList.map((item) => item.registerTime), // 后端返回的时间数据
      axisLabel: {
        rotate: 45,
        // formatter: (value: string) => value.split('T')[1],
        formatter: (value: string) => value,
      },
    },
    yAxis: [
      {
        type: 'value',
        name: 'GC 次数',
        position: 'left',
      },
      {
        type: 'value',
        name: 'GC 耗时 (ms)',
        position: 'right',
        splitLine: { show: false },
      },
    ],
    series: [
      {
        name: 'GC 总次数',
        type: 'bar',
        data: analysisList.map((item) => item.gcCount), // GC 次数
        yAxisIndex: 0,
      },
      {
        name: 'GC 总耗时',
        type: 'bar',
        data: analysisList.map((item) => item.gcTime), // GC 耗时
        yAxisIndex: 1,
      },
    ],
  };
  return option;
};

const getDiskPieChartOption = (analysis: API.AnalysisDataVO) => {
  let diskUsed = 0;
  let diskFree = 0;
  if (analysis.diskFree && analysis.diskTotal) {
    diskUsed = analysis.diskTotal - analysis.diskFree;
    diskFree = analysis.diskFree;
  }
  const option = {
    title: {
      text: '磁盘使用情况',
      left: 'center',
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} GB ({d}%)', // 显示名称、数值和百分比
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: ['已用容量', '剩余容量'],
    },
    series: [
      {
        name: '磁盘使用情况',
        type: 'pie',
        radius: '55%', // 控制饼图大小
        center: ['50%', '50%'], // 让饼图居中
        data: [
          {
            value: (diskUsed / 1024 / 1024 / 1024).toFixed(2),
            name: '已用容量',
          }, // diskUsed = diskTotal - diskFree
          {
            value: (diskFree / 1024 / 1024 / 1024).toFixed(2),
            name: '剩余容量',
          }, // diskFree
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
        },
        label: {
          formatter: '{b}: {d}%', // 只显示百分比
        },
      },
    ],
  };

  return option;
};

const AnalysisDetail = (props: Props) => {
  const [analysisList, setAnalysisList] = useState<API.AnalysisDataVO[]>([]);
  const [loading, setLoading] = useState(false);
  const [lastAnalysis, setLastAnalysis] = useState<API.AnalysisDataVO>();
  const [timeType, setTimeType] = useState<TimeType>('day');

  const loadAnalysisData = async (fTimeType: TimeType) => {
    setLoading(true);
    let res = await analysis({
      serverName: props.serverName,
      timeType: fTimeType,
    } as any);

    if (fTimeType === 'month') {
      res = res.map((item: API.AnalysisDataVO) => {
        return {
          ...item,
          registerTime: item.registerTime
            ? item.registerTime.split('T')[0]
            : '',
        };
      });
    }
    if (fTimeType === 'day') {
      res = res.map((item: API.AnalysisDataVO) => {
        return {
          ...item,
          registerTime: item.registerTime
            ? item.registerTime.split('T')[1]
            : '',
        };
      });
    }

    setAnalysisList(res);
    setLoading(false);
  };

  const loadLastAnalysisData = async () => {
    setLoading(true);
    let res = await lastSystemData({ serverName: props.serverName } as any);
    if (res) {
      setLastAnalysis(res);
    } else {
      setLastAnalysis({
        jvmMaxMemory: 0,
        jvmTotalMemory: 0,
        jvmFreeMemory: 0,
        jvmUsableMemory: 0,
        gcCount: 0,
        gcTime: 0,
        diskTotal: 0,
        diskFree: 0,
      });
    }
    setLoading(false);
  };

  useEffect(() => {
    loadAnalysisData(timeType);
    loadLastAnalysisData();
  }, []);

  return (
    <>
      <ProCard
        collapsible
        ghost
        title={`服务名称:[${props.serverName}]`}
        extra={
          <>
            <Select
              onChange={async (value: TimeType) => {
                setTimeType(value);
                await loadAnalysisData(value);
                console.log('执行了请求数据');
              }}
              value={timeType}
              style={{ width: 120, marginRight: 10 }}
            >
              <Select.Option value="day">24h</Select.Option>
              <Select.Option value="month">1个月内</Select.Option>
            </Select>
            <Button
              onClick={async () => {
                await loadAnalysisData(timeType);
                await loadLastAnalysisData();
              }}
              type="primary"
            >
              刷新
            </Button>
          </>
        }
        bordered
        headerBordered
      >
        <ProCard.Group>
          <ProCard bordered colSpan="50%">
            {analysisList.length > 0 && (
              <ReactECharts
                option={getLineChartOption(analysisList)}
                showLoading={loading}
              />
            )}
          </ProCard>

          <ProCard bordered colSpan="50%">
            {analysisList.length > 0 && (
              <ReactECharts
                option={getPillarChartOption(analysisList)}
                showLoading={loading}
              />
            )}
          </ProCard>
        </ProCard.Group>

        <ProCard.Group>
          <ProCard bordered colSpan="50%">
            {lastAnalysis && (
              <ReactECharts
                option={getPieChartOption(lastAnalysis)}
                showLoading={loading}
              />
            )}
          </ProCard>
          <ProCard bordered colSpan="50%">
            {lastAnalysis && (
              <ReactECharts
                option={getDiskPieChartOption(lastAnalysis)}
                showLoading={loading}
              />
            )}
          </ProCard>
        </ProCard.Group>
      </ProCard>
    </>
  );
};

export default AnalysisDetail;
