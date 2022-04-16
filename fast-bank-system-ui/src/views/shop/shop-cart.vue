<template>
  <div class="main-container">
    <a-row :gutter="[10, 10]">
      <a-col
        :xs="24"
        :sm="12"
        :md="12"
        :lg="6"
        :xl="6"
        :xxl="6"
        :xxxl="4"
        v-for="item of dataList"
        :key="item.financialProductId"
        class="col-item"
      >
        <a-card hoverable>
          <template #actions>
            <a-button
              type="primary"
              status="success"
              @click="onCanAppoint(item)">
                取消预约
            </a-button>
            <a-button
              type="primary"
              status="danger"
              :disabled="(dayjs().isBefore(dayjs(item.startTime)) || dayjs(item.endTime).isBefore(dayjs()))"
              @click="onseckill(item)"
              >秒杀</a-button
            >
          </template>
          <template #cover>
            <div
              :style="{
                height: '204px',
                overflow: 'hidden',
              }"
            >
              <img
                :style="{ height: '100%', 'object-fit': 'cover' }"
                src="../../assets/123.png"
              />
            </div>
          </template>
          <a-card-meta>
            <template #title>
              <div class="flex justify-between">
                <span style="fontsize: 18px">{{ item.financialProductName }}</span>
                <span style="fontsize: 14px; color: red"
                  >{{ item.rate }}%</span
                >
              </div>
            </template>
            <template #description>
              <a-typography-paragraph
                v-if="item.productDescribe"
                :ellipsis="{
                  rows: 2,
                  expandable: false,
                  ellipsisStr: '...'
                }"
                style="color: #c9cdd4;fontSize: 10px"
              >
                {{ item.productDescribe }}
              </a-typography-paragraph>
              <div v-else style="height: 37px; color: #c9cdd4">该产品未添加任何描述</div>
            </template>
            <template #avatar>
              <a-typography-text v-if="item.reStock === 0" style="fontSize: 12px;color: #c9cdd4">已售罄</a-typography-text>
                <a-typography-text
                  v-else-if="dayjs().isBefore(dayjs(item.startTime))"
                  style="fontSize: 10px"
                  >还未到时间</a-typography-text
                >
                <a-typography-text
                  v-else-if="dayjs(item.endTime).isBefore(dayjs())"
                  style="fontSize: 10px"
                  >已结束</a-typography-text
                >
                 <!-- todo 切换为自定义指令 -->
                <a-typography-text v-else style="fontSize: 10px"
                  >{{ dayjs(new Date(item.endTime)).format("YYYY-MM-DD HH:mm") }}</a-typography-text
                >
                <div style="fontSize: 18px; color: red">￥{{ item.price }}</div>
            </template>
          </a-card-meta>
        </a-card>
      </a-col>
    </a-row>
    <br />
    <TableFooter :pagination="pagination" />
    <ModalDialog ref="modalDialogRef" :title="actionTitle" @confirm="onDataFormConfirm(productInfo)" class="modal">
      <template #content>
        <div style="maxHeight: 400px">
          <a-list>
            <a-list-item v-for="(attr,index) of productInfoAttrs" :key="index">
              <template #meta>
                <a-typography-text style="width: 200px"> {{ productInfoMap[attr] }} </a-typography-text>
              </template>
              <template #extra v-if="attr==='type'">
                 贷款产品
              </template>
              <template #extra v-else-if="attr === 'productDescribe'">
                <a-typography-paragraph
                  :ellipsis="{
                    rows: 1,
                    showTooltip: true,
                    ellipsisStr: '...'
                  }"
                  style="width: 350px"
                >
                  {{ productInfo[attr] }}
                </a-typography-paragraph>
              </template>
              <template #extra v-else-if="attr === 'startTime' || attr === 'endTime' || attr === 'ctime'">
                {{ dayjs(new Date(productInfo[attr])).format("YYYY-MM-DD HH:mm")  }}
              </template>
              <template #extra v-else>
                {{ productInfo[attr] }}
              </template>
            </a-list-item>
          </a-list>
        </div>
      </template>
    </ModalDialog>
  </div>
</template>

<script lang="ts">
  import { post, get } from '@/api/http'
  import { userGetAppointmentProduct, seckill } from '@/api/url'
  import { usePagination, useTable, useTableHeight } from '@/hooks/table'
  import { defineComponent, onMounted, ref, reactive, getCurrentInstance, computed } from 'vue'
  import { ModalDialogType } from '@/types/components'
  import { Message, Modal } from '@arco-design/web-vue'
  import dayjs from 'dayjs'
  export default defineComponent({
    name: 'CardList',
    setup() {
      const pagination = usePagination(doRefresh)
      const table = useTable()
      const modalDialogRef = ref<ModalDialogType | null>(null)
      const productInfoMap = {
        financialProductId: '产品 ID',
        financialProductName: '产品名',
        type: '产品类别',
        price: '价格',
        stock: '库存',
        ctime: '创建时间',
        startTime: '上架时间',
        endTime: '下架时间',
        productDescribe: '产品描述',
        interest: '利息',
        rate: '利率',
      }
      const productInfo = ref<FinancialProduct | LoanProduct | null>(null)
      const productInfoAttrs = computed<string[]>(() => {
        if (productInfo.value === null) {
          return []
        }
        return Object.keys(productInfo.value)
      })
      function doRefresh() {
        get({
          url: userGetAppointmentProduct,
        })
          .then((res) => {
            console.log(res, 'res')
            let data = ref([] as Array<FinancialProduct>)
            res.data.forEach((item:any) => {
              data.value.push(item?.productEntity)
            })

            table.handleSuccess({ data: data.value })
            pagination.setTotalSize( res.data.length)
          })
          .catch((e) => console.error(e))
      }
      const onseckill = (item:any) => {
        console.log(item)
        get({
          url: `product/getbyid/${item.financialProductId}`
        }).then(res => {
          if (res.code === 200) {
            productInfo.value = res.data
            if (productInfo.value != null) {
              productInfo.value.type = ''
            }
            modalDialogRef.value?.toggle()
          }
        }).catch(e => Message.error(e.message))
      }
      const onDataFormConfirm = (item:any) => {
        post({
          url: seckill,
          data: {
            productId: item['financialProductId']
          },
          headers: {
            'Content-Type': 'multipart/form-data; charset=UTF-8'
          }
        }).then(res => {
          if (res.code === 200) {
            modalDialogRef.value?.toggle()
            Message.success('购买成功')
            doRefresh()
          }
        }).catch(e => {
          Message.error(e.message)
        })
      }
      const onCanAppoint = (item:any) => {
        Modal.confirm({
          title: '提示',
          content: '确定要取消预约吗？',
          cancelText: '取消',
          okText: '确定',
          onOk: () => {
             get({
              url: `product/userProduct/userCancelAppointment/financial/${item.financialProductId}`
            }).then(res => {
              if (res.code === 200) {
                Message.success('操作成功')
                doRefresh()
              }
            }).catch(e => Message.error(e.msg))
          },
        })

      }
      onMounted(async () => {
        table.tableHeight.value = await useTableHeight(getCurrentInstance())
        doRefresh()
      })
      return {
        ...table,
        pagination,
        productInfo,
        productInfoMap,
        productInfoAttrs,
        modalDialogRef,
        onseckill,
        onDataFormConfirm,
        onCanAppoint,
        dayjs
      }
    },
  })
</script>

<style lang="less" scoped>
  .main-container {
    width: 80%;
    margin: 0 auto;
    background-color: #fff;
    border-radius: 4px;
    padding: 10px 12px;
  }
  .goods-title {
    font-size: 16px;
  }
  @media screen and(max-width: 966px) {
    .main-container {
      width: 100%;
    }
  }
</style>
