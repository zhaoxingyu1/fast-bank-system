<template>
  <div class="main-container">
    <TableBody>
      <template #header>
        <TableHeader :show-filter="false" :title="'所有产品'">
          <template #top-right>
            <a-input-search :style="{width:'280px'}" placeholder="请输入产品名模糊查询" v-model="name" @press-enter="findByname()" @search="findByname()"/>
            <a-button type="primary" size="small" @click="onAddItem()">
              <template #icon>
                <icon-plus />
              </template>
              <template #default>添加产品</template>
             </a-button>
            <a-radio-group v-model="productType" type="button" @change="doRefresh()">
              <a-radio value="financial">理财产品</a-radio>
              <a-radio value="loan">贷款产品</a-radio>
            </a-radio-group>
          </template>
        </TableHeader>
      </template>
      <template #default>
        <a-table
          :bordered="false"
          :loading="tableLoading"
          :data="dataList"
          :pagination="false"
          :row-key="rowKey"
        >
          <template #columns>
            <a-table-column
              v-for="item of tableColumns"
              :key="item.key"
              :align="item.align"
              :title="item.title"
              :width="item.width"
              :data-index="item.key"
              :fixed="item.fixed"
            >
              <template v-if="item.key === 'index'" #cell="{ rowIndex }">
                {{ rowIndex + 1 }}
              </template>
              <template v-else-if="item.key === 'productDescribe'"  #cell="{ record }">
                <a-typography-paragraph
                  :ellipsis="{
                    rows: 1,
                    showTooltip: true,
                  }"
                >
                  {{ record.productDescribe }}
                </a-typography-paragraph>
              </template>
              <template v-else-if="item.key === 'actions'" #cell="{ record }">
                <a-space>
                  <a-button status="primary" size="mini" @click="onDetailItem(record)"
                    >详情</a-button
                  >
                  <a-button status="success" size="mini" @click="onUpdateItem(record)"
                    >编辑</a-button
                  >
                  <a-button status="danger" size="mini" @click="onDeleteItem(record)"
                    >删除</a-button
                  >
                </a-space>
              </template>
            </a-table-column>
          </template>
        </a-table>
      </template>
      <template #footer>
        <TableFooter ref="tableFooterRef" :pagination="pagination" />
      </template>
    </TableBody>
    <ModalDialog ref="modalDialogRef" :title="actionTitle" @confirm="onDataFormConfirm(type)">
      <template #content>
        <a-form>
          <a-row>
            <a-form-item
              :class="[item.required ? 'form-item__require' : 'form-item__no_require']"
              :label="item.label"
              v-for="item of formItems"
              :key="item.key"
            >
              <template v-if="item.type === 'input'">
                <a-input :placeholder="item.placeholder" v-model="item.value.value">
                  <template #prepend v-if="item.key === 'roleCode'">
                    {{ ROLE_CODE_FLAG }}
                  </template>
                </a-input>
              </template>
              <template v-if="item.type === 'textarea'">
                <a-textarea
                  v-model="item.value.value"
                  :placeholder="item.placeholder"
                  :auto-size="{ minRows: 3, maxRows: 5 }"
                />
              </template>
              <template v-if="item.type === 'time'">
                <a-range-picker
                  showTime
                  v-model="item.value.value"
                  :disabled="type"
                  size="small"
                  :disabledDate="(current) => dayjs(current).isBefore(dayjs().subtract(1, 'day'))"
                  :disabledTime="getDisabledRangeTime"
                />
              </template>
            </a-form-item>
          </a-row>
        </a-form>
      </template>
    </ModalDialog>
    <ModalDialog ref="DetailModalDialogRef" :title="detaileTatile" :width="'1100px'" :showConfirm="false" backText="返回">
      <template #content>
        <DetailTable :tableData="detailTable" @doRefresh="onDetailItem()"/>
      </template>
    </ModalDialog>
  </div>
</template>

<script lang="ts">
  import { post, get } from '@/api/http'
  import dayjs, { Dayjs } from 'dayjs'
  import { usePagination, useRowKey, useTable, useTableColumn, useTableHeight } from '@/hooks/table'
  import { ModalDialogType, FormItem } from '@/types/components'
  import { Message, Modal } from '@arco-design/web-vue'
  import DetailTable from './detail-table.vue'
  import { defineComponent, nextTick, onMounted, ref, getCurrentInstance, reactive } from 'vue'
  import { getByProduct } from '@/api/url'
  import { orderState } from '@/types/state-enum'
  const formItems = [
    {
      label: '名称',
      key: 'name',
      value: ref(''),
      type: 'input',
      required: true,
      placeholder: '请输入产品名称',
      validator: function () {
        if (!this.value.value) {
          Message.error(this.placeholder || '')
          return false
        }
        return true
      },
    },
    {
      label: '时间',
      key: 'time',
      value: ref<Dayjs[]>([]),
      reset: function () {
        this.value.value = []
      },
      type: 'time',
      required: true,
      placeholder: '请输入开始时间',
      validator: function () {
        if (!this.value.value) {
          Message.error(this.placeholder || '')
          return false
        }
        return true
      },
    },
    {
      label: '价格',
      type: 'input',
      key: 'price',
      value: ref(''),
      required: true,
      placeholder: '请输入产品价格',
      validator: function () {
        if (!this.value.value) {
          Message.error(this.placeholder || '')
          return false
        }
        return true
      },
    },
    {
      label: '数量',
      key: 'stock',
      value: ref(''),
      type: 'input',
      required: true,
      placeholder: '请输入产品数量',
      validator: function () {
        if (!this.value.value) {
          Message.error(this.placeholder || '')
          return false
        }
        return true
      },
    },
    {
      label: '利率/利息',
      key: 'rate',
      value: ref(''),
      type: 'input',
      required: true,
      placeholder: '请输入利率或利息',
      validator: function () {
        if (!this.value.value) {
          Message.error(this.placeholder || '')
          return false
        }
        return true
      },
    },
    {
      label: '描述',
      key: 'productDescribe',
      value: ref(''),
      type: 'textarea',
      placeholder: '请输入产品描述',
    }
  ] as FormItem[]
  export default defineComponent({
    name: 'allProducts',
    components: { DetailTable },
    setup() {
      const modalDialogRef = ref<ModalDialogType | null>(null)
      const DetailModalDialogRef = ref<ModalDialogType | null>(null)
      const table = useTable()
      const detailTable = useTable()
      const pagination = usePagination(doRefresh)
      const actionTitle = ref('添加产品')
      const type = ref(false)
      const productType = ref('financial')
      const name = ref()
      const updateId = ref()
      const tableColumns = useTableColumn([
        table.indexColumn,
        {
          title: '产品名称',
          key: `ProductName`,
          dataIndex: `ProductName`,
        },
        {
          title: '产品价格',
          key: 'price',
          dataIndex: 'price',
        },
        {
          title: '产品总数',
          key: 'stock',
          dataIndex: 'stock',
        },
        {
          title: '剩余库存',
          key: 'reStock',
          dataIndex: 'stock',
        },
        {
          title: '利率/利息',
          key: 'rate',
          dataIndex: 'rate',
        },
        {
          title: '产品开始时间',
          key: 'startTime',
          dataIndex: 'startTime',
        },
        {
          title: '产品结束时间',
          key: 'endTime',
          dataIndex: 'endTime',
        },
        {
          title: '产品描述',
          key: 'productDescribe',
          dataIndex: 'productDescribe',
          width: 160
        },
        {
          title: '操作',
          key: 'actions',
          dataIndex: 'actions',
        },
      ])
      const defaultCheckedKeys = ref([] as Array<string>)
      const defaultExpandedKeys = ref([] as Array<string>)
      const detaileTatile = ref('预约详情')
      function doRefresh() {
        get({
          url: `/product/${productType.value}/findAllByPage/${pagination.page}`,
        })
          .then((res) => {
            if (res.code === 200) {
              const data = tranferData(res.data, 0)
              table.handleSuccess({ data })
              pagination.setTotalSize( res.data.length)
            }
          })
          .catch(e => Message.error(e.message))
      }
      function findByname() {
        get({
          url: `/product/${productType.value}/findByName/${name.value}`,
        })
          .then((res) => {
            if (res.code === 200) {
              console.log(res.data, 'res.data')
              const data = tranferData(res.data, 1)
              console.log(data, 'findByname')
              table.handleSuccess({ data })
              pagination.setTotalSize(res.data.length)
            }
          })
          .catch(e => Message.error(e.message))
      }
      function onAddItem() {
        actionTitle.value = '添加产品'
        modalDialogRef.value?.toggle()
        type.value = false
        formItems.forEach((it) => {
          if (it.reset) {
            it.reset()
          } else {
            it.value.value = ''
          }
        })
      }
      const onDetailItem = (item: any) => {
        // todo 管理员查看该产品预约详情
        console.log(item, type, 'log')
        if (productType.value === 'financial' && dayjs(dayjs()).isBefore(item.startTime)) {
          detaileTatile.value='预约详情'
          get({
            url: `product/userProduct/adminGetAppointment/${item.id}`
          }).then(res => {
            let data: any[] = []
            res.data.forEach((ele: any) => {
              const map = ele
              data.push({ name: map.userEntity?.username, creditStatus: map.userEntity?.userInfo?.creditStatus, state: orderState.BOOKING, ctime: map.userProductEntity?.mtime })
            })
            detailTable.handleSuccess({ data })
          }).catch(e => Message.error(e.message))
        } else {
          // 理财产品超过结束时间或者贷款产品都为查看已购买的
          detaileTatile.value='购买详情'
          get({
            url: getByProduct,
            data: {
              id: item.id
            }
          }).then(res => {
            if (res.code === 200) {
              let data: any[] = []
              res.data?.records.forEach((ele:any) => {
                const state = ele.state === 'PENDING' ? orderState.PENDING : ele.state === 'REJECTED' ? orderState.REJECTED : orderState.FULFILLED
                data.push({ name: ele.name, creditStatus: ele.auth ? 1 : 0, state, ctime: ele.mtime })
              })
              detailTable.handleSuccess({ data })
            }
            console.log(res, '1')
          }).catch(e => Message.error(e.message))
        }

        DetailModalDialogRef.value?.toggle()
      }
      function onUpdateItem(item: any) {
        actionTitle.value = '修改产品'
        modalDialogRef.value?.toggle()
        type.value = true
        nextTick(() => {
          console.log(item, 'item')
          formItems.forEach((it) => {
            const key = it.key
            if (it.key === 'name') {
              it.value.value = item[`ProductName`]
            } else if(it.key === 'time') {
              it.value.value = [ item.startTime, item.endTime ]
            } else{
              const propName = item[key]
              it.value.value = propName
            }
          })
          updateId.value = item.id
        })
      }
      async function onDeleteItem(item: any) {
        await Modal.confirm({
          title: '警告',
          content: '是否要删除此产品？',
          cancelText: '取消',
          okText: '删除',
          onOk: () => {
            let data:any = reactive({})
            data[`${productType.value}ProductName`] = item.ProductName
            data[`${productType.value}ProductId`] = item.id
            post({
              url: `product/${productType.value}/delete`,
              data,
              headers: {
                'Content-Type': 'multipart/form-data; charset=UTF-8'
              }
            }).then(res => {
              if (res.code === 200) {
                Message.success('删除成功')
                doRefresh()
              }
            }).catch(e => Message.error(e.message))
          }
        })
      }
      async function onDataFormConfirm(type: number) {
        if (formItems.every((it) => (it.validator ? it.validator() : true))) {
          modalDialogRef.value?.toggle()
          let form:any = reactive({})
          formItems.forEach(it => {
            if (it.key === 'name') {
              form[productType.value + 'ProductName'] = it.value
            } else if (it.key === 'time'){
              form['startTime'] = new Date(Date.parse(it.value.value[0])).getTime()
              form['endTime'] = new Date(Date.parse(it.value.value[1])).getTime()
            } else if (it.key === 'rate') {
              form[productType.value === 'loan' ? 'interest' : 'rate'] = it.value
            } else {
              form[it.key] = it.value
            }
            if (type) {
              form[productType.value + 'ProductId'] = updateId.value
            }
          })

          await post({
            url: `product/${productType.value}/${!type ? 'create' : 'update'}`,
            data: form,
            headers: {
              'Content-Type': 'multipart/form-data; charset=UTF-8'
            }
          }).then(res => {
            if (res.code === 200) {
              doRefresh()
              Message.success(!type ?'添加成功' : '修改成功')
            }
          }).catch(e => Message.error(e.message))
        }
      }
      const tranferData = (res:any, type:number) => {
        let data:any[] = []
        res.forEach((item:any) => {
          let dataItem = reactive<any>({})
          const product = type === 0 ? item.productEntity : item
          dataItem['id'] = product[`${productType.value}ProductId`]
          dataItem['ProductName'] = product[`${productType.value}ProductName`]
          dataItem.startTime = dayjs(new Date(product.startTime)).format("YYYY-MM-DD HH:mm")
          dataItem.endTime =  dayjs(new Date(product.endTime)).format("YYYY-MM-DD HH:mm")
          dataItem['price'] = product.price
          dataItem['stock'] = product.stock
          dataItem['productDescribe'] = product.productDescribe
          dataItem['rate'] = productType.value === 'loan' ? product?.interest.toFixed(2) : product?.rate.toFixed(2)
          dataItem['reStock'] = item.stock
          data.push(dataItem)
        })
        return data
      }
      const getDisabledTime = (data:any) => {
        console.log(data, 'getDisabledTime')
        dayjs(data).isBefore(dayjs().subtract(1, 'day'))
      }
      function range(start:any, end:any) {
        const result = [];
        for (let i = start; i < end; i++) {
          result.push(i);
        }
        return result;
      }
      const getDisabledRangeTime = (data:any, type:any) => {
        const d = new Date()
        const hour : number = d.getHours()
        const minutes : number = d.getMinutes()
        let flag = ref<boolean>(false)
        if (type === 'start') {
          flag.value = hour === new Date(data).getHours()
        }
        return {
          disabledHours:  () => type === 'start' ? range(0, hour) : '',
          disabledMinutes: () => flag.value && type === 'start' ? range(0, minutes) : ''
        }
      }
      onMounted(async () => {
        table.tableHeight.value = await useTableHeight(getCurrentInstance())
        doRefresh()
      })
      return {
        modalDialogRef,
        DetailModalDialogRef,
        tableColumns,
        formItems,
        dayjs,
        name,
        pagination,
        actionTitle,
        detaileTatile,
        productType,
        type,
        detailTable,
        defaultCheckedKeys,
        defaultExpandedKeys,
        ...table,
        onAddItem,
        onDataFormConfirm,
        findByname,
        onDeleteItem,
        onUpdateItem,
        doRefresh,
        onDetailItem,
        getDisabledTime,
        getDisabledRangeTime
      }
    },
  })
</script>
