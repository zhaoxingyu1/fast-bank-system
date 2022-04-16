<template>
  <div class="main-container">
    <a-tabs type="text" size="large" @change="changPane()">
      <a-tab-pane v-for="item in productTypeArray" :key="item.value" :title="item.name">
        <a-row :gutter="[16, 16]">
          <a-col
            :xs="24"
            :sm="12"
            :md="12"
            :lg="6"
            :xl="6"
            :xxl="6"
            :xxxl="4"
            v-for="item of dataList"
            :key="item.id"
            class="col-item"
          >
            <a-space :size="large">
              <a-card hoverable="true" :loading="loading.value">
                <template #actions>
                  <a-button
                    type="primary"
                    v-if="(productType === 'financial' && item.bookState === 0)"
                    status="danger"
                    :disabled="(item.reStock === 0 || dayjs(item.endTime).isBefore(dayjs()))"
                    @click="onAppointOrBuy(item)"
                    >预约</a-button
                  >
                  <a-button
                    v-if="productType === 'financial' && item.bookState === 1 "
                    type="primary"
                    status="success"
                    @click="onCanAppoint(item)">
                      取消预约
                  </a-button>
                  <a-button
                    type="primary"
                    v-if="productType === 'loan'"
                    status="danger"
                    :disabled="(item.reStock === 0 || dayjs().isBefore(dayjs(item.startTime)) || dayjs(item.endTime).isBefore(dayjs()))"
                    @click="onAppointOrBuy(item)"
                    >购买</a-button
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
                      v-if="productType === 'financial'"
                      :style="{ height: '100%', 'object-fit': 'cover' }"
                      src="../../assets/123.png"
                    />
                    <img
                      v-else
                      :style="{ height: '100%', 'object-fit': 'cover' }"
                      src="../../assets/456.png"
                    />
                  </div>
                </template>
                <a-card-meta>
                  <template #title>
                    <div class="flex justify-between">
                      <span style="fontsize: 18px">{{ item.ProductName }}</span>
                      <span v-if="item.rate" style="fontsize: 14px; color: red"
                        >{{ item.rate }}%</span
                      >
                      <span v-if="item.interest" style="fontsize: 14px; color: red"
                        >{{ item.interest }}%</span
                      >
                    </div>
                  </template>
                  <template #description>
                    <a-typography-paragraph
                      v-if="item.productDescribe"
                      :ellipsis="{
                        rows: 2,
                        expandable: false,
                        ellipsisStr: '...',
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
                    <a-typography-text v-else style="fontSize: 10px"
                      >{{ item.endTime }}结束</a-typography-text
                    >
                    <div style="fontSize: 18px; color: red">￥{{ item.price }}</div>
                    <a-progress :percent="((item.stock - item.reStock)/item.stock).toFixed(2)" style="width: 120px" />
                  </template>
                </a-card-meta>
              </a-card>
            </a-space>
          </a-col>
        </a-row>
        <br />
        <TableFooter :pagination="pagination" :isBordered="true" />
      </a-tab-pane>
    </a-tabs>
    <ModalDialog ref="modalDialogRef" :title="actionTitle" @confirm="onDataFormConfirm(productInfo)" class="modal">
      <template #content>
        <div style="maxHeight: 400px">
          <a-list>
            <a-list-item v-for="(attr,index) of productInfoAttrs" :key="index">
              <template #meta>
                <a-typography-text style="width: 200px"> {{ productInfoMap[attr] }} </a-typography-text>
              </template>
              <template #extra v-if="attr==='type'">
                {{ productInfo[attr] === 'loan' ? '贷款产品' : '理财产品' }}
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
  import dayjs from 'dayjs'
  import { userGetAppointmentProduct, create } from '@/api/url'
  import { usePagination, useTable, useTableHeight } from '@/hooks/table'
  import { defineComponent, onMounted, computed, reactive, ref, getCurrentInstance } from 'vue'
  import { ModalDialogType } from '@/types/components'
  import { Message, Modal } from '@arco-design/web-vue'
  import useAppiontStore from '@/store/modules/appiont-product'
  export default defineComponent({
    name: 'shopMall',
    setup() {
      const appiontStore = useAppiontStore()
      const modalDialogRef = ref<ModalDialogType | null>(null)
      const actionTitle = ref('添加产品')
      const pagination = usePagination(doRefresh)
      const table = useTable()
      const productType = ref('financial')
      const productTypeArray = reactive([
        { value: 'financial', name: '理财产品' },
        { value: 'loan', name: '贷款产品' },
      ])
      let loading = ref(true)
      const productInfoMap = {
        loanProductId: '产品 ID',
        loanProductName: '产品名',
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
      async function doRefresh() {
        await getAppointment()
        loading = ref(true)
        get({
          url: `/product/${productType.value}/findAllByPage/${pagination.page}`,
        })
          .then((res) => {
            if (res.code === 200) {
              res.data.forEach((ele:any) => {
                if (appiontStore.filterAppiontId(ele?.productEntity.financialProductId)) {
                  ele['bookState'] = 1
                }
              })
              const data = tranferData(res.data)
              table.handleSuccess({ data })
              pagination.setTotalSize( res.data.length)
            }
          })
          .catch((e) => console.error(e))
          .finally(() => (loading = ref(false)))
      }
      const getAppointment = async () => {
        get({
          url: userGetAppointmentProduct,
        })
          .then((res) => {
            const dataList = ref([] as Array<string>)
            res.data.forEach((item:any) => {
              dataList.value.push(item?.productEntity?.financialProductId)
            })
            appiontStore.saveAppiont(dataList.value)
          })
          .catch((e) => console.error(e))
      }
      const changPane = () => {
        productType.value = productType.value === 'financial' ? 'loan' : 'financial'
        doRefresh()
      }
      const onDataFormConfirm = (item:any) => {
        if (productType.value === 'financial') {
          get({
            url: `product/userProduct/userAppointment/financial/${item['financialProductId']}`
          }).then(res => {
            if (res.code === 200) {
              modalDialogRef.value?.toggle()
              Message.success('预约成功')
              doRefresh()
            }
          }).catch(e => Message.error(e.message))
        } else {
          post({
            url: create,
            data: {
              productId: item['loanProductId']
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
          }).catch(e => Message.error(e.message))
        }
      }
      async function onAppointOrBuy(item: any) {
        await get({
          url: `product/getbyid/${item.id}`
        }).then(res => {
          if (res.code === 200) {
            productInfo.value = res.data
            if (productInfo.value != null) {
              productInfo.value.type = productType.value
            }
            actionTitle.value = productType.value === 'loan' ? '购买产品' : '预约产品'
            modalDialogRef.value?.toggle()
          }
        }).catch(e => Message.error(e.message))
      }
      const tranferData = (res:any) => {
        let data:any[] = []
        res.forEach((item:any) => {
          let dataItem = reactive<any>({})
          const product = item.productEntity
          dataItem['id'] = product[`${productType.value}ProductId`]
          dataItem['ProductName'] = product[`${productType.value}ProductName`]
          dataItem.startTime = dayjs(new Date(product.startTime)).format("YYYY-MM-DD HH:mm")
          dataItem.endTime =  dayjs(new Date(product.endTime)).format("YYYY-MM-DD HH:mm")
          dataItem['price'] = product.price.toFixed(2)
          dataItem['stock'] = product.stock
          dataItem['productDescribe'] = product.productDescribe
          dataItem['rate'] = productType.value === 'loan' ? product?.interest.toFixed(2) : product?.rate.toFixed(2)
          dataItem['reStock'] = item.stock
          dataItem['bookState'] = item.bookState || 0
          data.push(dataItem)
        })
        return data
      }
      const onCanAppoint = (item:any) => {
        Modal.confirm({
          title: '提示',
          content: '确定要取消预约吗？',
          cancelText: '取消',
          okText: '确定',
          onOk: () => {
             get({
              url: `product/userProduct/userCancelAppointment/financial/${item.id}`
            }).then(res => {
              if (res.code === 200) {
                Message.success('操作成功')
                doRefresh()
              }
            }).catch(e => Message.error(e.message))
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
        productTypeArray,
        productType,
        loading,
        dayjs,
        productInfo,
        productInfoMap,
        productInfoAttrs,
        modalDialogRef,
        actionTitle,
        onDataFormConfirm,
        changPane,
        onAppointOrBuy,
        onCanAppoint
      }
    },
  })
</script>

<style lang="less" scoped>
  .main-container {
    width: 82%;
    margin: 0 auto;
    background-color: #fff;
    border-radius: 4px;
    .arco-tabs {
      :deep(.arco-tabs-content) {
        padding: 10px 12px;
      }
    }
  }
  @media screen and(max-width: 966px) {
    .main-container {
      width: 100%;
    }
  }
</style>
