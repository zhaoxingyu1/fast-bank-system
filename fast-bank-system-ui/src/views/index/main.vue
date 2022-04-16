<template>
  <div class="main-container">
    <a-row :gutter="[10, 10]" v-if="isReady">
      <a-col
        :xs="24"
        :sm="12"
        :md="12"
        :lg="12"
        :xl="12"
        :xxl="12"
        v-for="(item, index) of dataList"
        :key="index"
        class="item-wrapper"
      >
        <DataItem :data-model="item">
          <template v-if="index === 1" #extra="{ extra }">
            <div class="mt-4 text-xs" style="position: relative">
              <div> 一周内新增：{{ extra.data }} 人</div>
              <div class="stack-avatar-wrapper"> </div>
            </div>
          </template>
          <template v-else-if="index === 2" #extra="{ extra }">
            <div class="p-4">
              <a-progress :percent="extra.data" />
            </div>
          </template>
          <template v-else-if="index === 3" #extra>
            <OrderChart ref="mOrderChart" />
          </template>
        </DataItem>
      </a-col>
    </a-row>
    <div class="mt-2"></div>
    <a-row>
      <a-space direction="vertical" style="width: 100%">
        <FullYearSalesChart ref="fullYearSalesChart" />
      </a-space>
    </a-row>
  </div>
</template>

<script lang="ts">
  import DataItem from './components/DataItem.vue'
  import OrderChart from './components/chart/OrderChart.vue'
  import FullYearSalesChart from './components/chart/FullYearSalesChart.vue'
  import { computed, defineComponent, onMounted, reactive, ref, watch } from 'vue'
  import { useLayoutStore } from '@/layouts'
  import {userCount, getUserFlow, thisMonthSell, thisMonthOrder } from '@/api/url'
  import { post, get } from '@/api/http'
import { Message } from '@arco-design/web-vue'
  export default defineComponent({
    name: 'Home',
    components: {
      DataItem,
      OrderChart,
      FullYearSalesChart,
    },
    setup() {
      const layoutStore = useLayoutStore()
      const mOrderChart = ref<InstanceType<typeof OrderChart>>()
      const fullYearSalesChart = ref<InstanceType<typeof FullYearSalesChart>>()
      let isReady = ref(false)
      let dataList = ref([
        {
          id: 'visited',
          title: '今日访问量',
          data:0,
          prefix: '+',
          icon: 'icon-face-smile-fill',
          color: '#1890ff'
        },
        {
          id: 'newAdd',
          title: '新增用户',
          data: 0,
          prefix: '+',
          bottomTitle: '总用户量',
          totalSum: '200万+',
          icon: 'icon-heart-fill',
          color: '#ff0000',
          extra: {
            data: 0
          },
        },
        {
          id: 'sales',
          title: '当月销售额',
          data: '50000',
          prefix: '￥',
          bottomTitle: '累计销售额',
          totalSum: '2000万+',
          color: '#18e3ff',
          icon: 'icon-star-fill',
          extra: {
            data: 0.8,
          },
        },
        {
          id: 'order',
          title: '当月订单量',
          data: '189',
          suffix: '笔',
          bottomTitle: '累计订单量',
          totalSum: '1万+',
          color: '#bbc314',
          icon: 'icon-sun-fill',
          extra: {
            data: 80,
          },
        },
      ])
      const onResize = () => {
        setTimeout(() => {
          mOrderChart.value?.updateChart()
          fullYearSalesChart.value?.updateChart()
        }, 500)
      }
      const collapse = computed(() => {
        return layoutStore.state.isCollapse
      })
      watch(collapse, () => {
        onResize()
      })
      // 一天内新增
      const getUserCount = async() => {
        return post({
          url: userCount,
          data: {
            day: 1
          },
           headers: {
          'Content-Type': 'multipart/form-data; charset=UTF-8'
          }
        }).then(res => {
          if(res.code === 200) {
            dataList.value[1].data = res.data
          }
        }).catch(e => Message.error(e.message))
      }
      // 一周内新增
      const getUserCountByWeek = async() => {
        return post({
          url: userCount,
          data: {
            day: 7
          },
           headers: {
          'Content-Type': 'multipart/form-data; charset=UTF-8'
          }
        }).then(res => {
          if(res.code === 200) {
            dataList.value[1].extra = { data: res.data}
          }
        }).catch(e => Message.error(e.message))
      }
      // 今日浏览量
      const getUserFlowData = async() => {
        return post({
          url: getUserFlow,
           headers: {
            'Content-Type': 'multipart/form-data; charset=UTF-8'
          }
        }).then(res => {
          if(res.code === 200) {
            dataList.value[0].data = res.data
          }
        }).catch(e => Message.error(e.message))
      }
      // 这月的销售额
      const getThisMonthSell = async() => {
        return get({
          url: thisMonthSell
        }).then(res => {
          if (res.code === 200) {
            console.log(res)
            dataList.value[2].data = res.data
          }
        }).catch(e => Message.error(e.message))
      }
      // 这月的订单量
      const getThisMonthOrder= async() => {
        return get({
          url: thisMonthOrder
        }).then(res => {
          if (res.code === 200) {
            console.log(res)
            dataList.value[3].data = res.data
          }
        }).catch(e => Message.error(e.message))
      }
      onMounted( async() => {
        await getUserCount()
        await getUserFlowData()
        await getUserCountByWeek()
        await getThisMonthSell()
        await getThisMonthOrder()
        isReady.value = true
        console.log(dataList.value)
      })
      return {
        collapse,
        mOrderChart,
        fullYearSalesChart,
        dataList,
        isReady
      }
    },
  })
</script>
