<template>
  <a-card
    :body-style="{ padding: '10px' }"
    :head-style="{ padding: '0 10px' }"
    title="半年销售额分析图（数据为模拟，只为演示效果）"
    :bordered="false"
    class="card-border-radius"
  >
    <div class="chart-item-container">
      <a-skeleton animation v-if="loading">
        <a-skeleton-line :rows="4"/>
      </a-skeleton>
      <template v-else>
        <div ref="fullYearSalesChart" class="chart-item"></div>
      </template>
    </div>
  </a-card>
</template>
<script lang="ts">
import useEcharts from '@/hooks/useEcharts'
import { defineComponent, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { dispose, graphic } from 'echarts/core'
import { random } from 'lodash'
import { oneYear } from '@/api/url'
import { post, get } from '@/api/http'
import { Message } from '@arco-design/web-vue'

let data = ref([])
const months = [
  '一月',
  '二月',
  '三月',
  '四月',
  '五月',
  '六月',
  '七月',
  '八月',
  '九月',
  '十月',
  '十一月',
  '十二月',
]
export default defineComponent({
  name: 'FullYearSalesChart',
  setup() {
    const loading = ref(true)
    const fullYearSalesChart = ref<HTMLDivElement | null>(null)
    let interval: any = null
    // 这月的销售额
    const getOneYear = async () => {
      return get({
        url: oneYear
      }).then(res => {
        if (res.code === 200) {
          console.log(res)
          data = res.data
        }
      }).catch(e => Message.error(e.message))
    }
    const init = () => {
      const option = {
        color: ['rgba(64, 58, 255)'],
        grid: {
          top: '10%',
          left: '2%',
          right: '2%',
          bottom: '5%',
          containLabel: true,
        },
        legend: {
          data: ['全年销售额'],
        },
        tooltip: {
          trigger: 'axis',
        },
        xAxis: {
          type: 'category',
          data: months,
        },
        yAxis: {
          type: 'value',
          max: 100,
        },
        series: [
          {
            type: 'bar',
            name: '全年销售额',
            stack: '总量',
            data: data,
            smooth: true,
            label: {
              show: true,
              formatter(val: any) {
                return val.data + '万'
              },
            },
            itemStyle: {
              color: new graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 0.5, color: '#188df0' },
                { offset: 1, color: '#188df0' },
              ]),
            },
          },
        ],
      }
      setTimeout(() => {
        loading.value = false
        setTimeout(() => {
          nextTick(() => useEcharts(fullYearSalesChart.value as HTMLDivElement).setOption(option))
          interval = setInterval(async () => {
            await getOneYear()
            const option = {
              series: [
                {
                  data: data,
                },
              ],
            }
            useEcharts(fullYearSalesChart.value as HTMLDivElement).setOption(option)
          }, 1000 * 60)
        }, 100)
      }, 300)
    }
    const updateChart = () => {
      useEcharts(fullYearSalesChart.value as HTMLDivElement).resize()
    }
    onMounted(async () => {
      await getOneYear()
      init()
    })
    onBeforeUnmount(() => {
      dispose(fullYearSalesChart.value as HTMLDivElement)
      clearInterval(interval)
    })
    return {
      loading,
      fullYearSalesChart,
      updateChart,
    }
  },
})
</script>

<style lang="less" scoped>
.chart-item-container {
  width: 100%;

  .chart-item {
    height: 30vh;
  }
}
</style>
