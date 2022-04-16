<template>
  <div class="main-container">
    <TableBody>
      <template #header>
        <TableHeader :show-filter="false" :title="'所有订单'">
          <template #top-right>
            <a-input
              v-role:admin="userRole"
              :style="{width: '240px'}"
              size="medium"
              placeholder="根据用户 id 筛选"
              @input="onInputUserId"
            />
            <a-select :style="{width:'200px'}" placeholder="根据产品类型筛选" @change="onChangeProductType">
              <a-option :value="''">所有订单</a-option>
              <a-option value="financial">理财产品订单</a-option>
              <a-option value="loan">贷款产品订单</a-option>
            </a-select>
            <a-button type="primary" @click="doSearch">搜索</a-button>
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
          size="small"
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
              <template v-if="item.key.endsWith('Id')" #cell="{ record }">
                {{ record[item.key] }}
                <a-button type="outline" size="mini" @click="copyInner">复制</a-button>
              </template>
              <template v-else-if="item.key === 'state'" #cell="{ record }">
                <div v-if="record[item.key] === 'FULFILLED'">
                  <a-tag size="medium" color="green">已完成</a-tag>
                </div>
                <div v-else-if="record[item.key] === 'REJECTED'">
                  <a-tag size="medium" color="red">已取消</a-tag>
                </div>
                <div v-else-if="record[item.key] === 'PENDING'">
                  <a-tag size="medium" color="orange">待支付</a-tag>
                </div>
              </template>
              <template v-else-if="item.key === 'ctime'" #cell="{ record }">
                <p v-date:LLLL="record[item.key]"></p>
              </template>
              <template v-else-if="item.key === 'actions'" #cell="{ record }">
                <a-space>
                  <a-button status="success" size="mini" @click="checkOrderInfo(record)">
                    详情
                  </a-button>
                </a-space>
              </template>
            </a-table-column>
          </template>
        </a-table>
      </template>
      <template #footer>
        <TableFooter :pagination="pagination"/>
      </template>
    </TableBody>
    <ModalDialog
      ref="modalDialogRef"
      :title="actionTitle"
      :showConfirm="(userRole === 'user' && payState === 'PENDING') ? true : false"
      confirmText="付款"
      backText="返回"
      @confirm="onDataFormConfirm()"
    >
      <template #content>
        <a-list>
          <a-list-item v-for="(attr,index) of productInfoAttrs" :key="index">
            <template #meta>
              {{ productInfoMap[attr] }}
            </template>
            <template #extra v-if="attr==='type'">
              {{ productInfo[attr] === 'loan' ? '贷款产品' : '理财产品' }}
            </template>
            <template #extra v-else-if="attr.toLowerCase().endsWith('time')">
              <p v-date:LLLL="productInfo[attr]"></p>
            </template>
            <template #extra v-else>
              {{ productInfo[attr] }}
            </template>
          </a-list-item>
        </a-list>
      </template>
    </ModalDialog>
  </div>
</template>

<script lang="ts">
import { get, post } from '@/api/http'
import { getOrderAll, getOrderByUser, getProductById } from '@/api/url'
import { usePagination, useRowKey, useTable, useTableColumn, useTableHeight } from '@/hooks/table'
import { ModalDialogType } from '@/types/components'
import { defineComponent, onMounted, ref, getCurrentInstance, computed } from 'vue'
import useUserStore from "@/store/modules/user"
import { filter } from "lodash"
import { Message } from '@arco-design/web-vue'

export default defineComponent({
  name: 'allProducts',
  setup() {
    const modalDialogRef = ref<ModalDialogType | null>(null)
    const table = useTable()
    const pagination = usePagination(doRefresh)
    const userStore = useUserStore()
    const rowKey = useRowKey('id')
    const actionTitle = ref('订单信息')
    const inputUserId = ref<string | null>(null)
    const productType = ref<null | 'financial' | 'loan'>(null)
    const tableColumns = useTableColumn([
      {
        title: '订单 ID',
        key: 'orderId',
        dataIndex: 'orderId',
      },
      {
        title: '用户 ID',
        key: 'userId',
        dataIndex: 'userId',
      },
      {
        title: '产品 ID',
        key: 'productId',
        dataIndex: 'productId',
      },
      {
        title: '状态',
        key: 'state',
        dataIndex: 'state',
      },
      {
        title: '创建时间',
        key: 'ctime',
        dataIndex: 'ctime',
      },
      {
        title: '操作',
        key: 'actions',
        dataIndex: 'actions',
      },
    ])
    const defaultCheckedKeys = ref([] as Array<string>)
    const defaultExpandedKeys = ref([] as Array<string>)
    // 详情弹窗
    const productInfo = ref<FinancialProduct | LoanProduct | null>(null)
    const productInfoAttrs = computed<string[]>(() => {
      if (productInfo.value === null) {
        return []
      }
      return Object.keys(productInfo.value)
    })
    const productInfoMap = {
      price: '价格',
      stock: '库存',
      ctime: '创建时间',
      startTime: '上架时间',
      endTime: '下架时间',
      productDescribe: '产品描述',
      loanProductId: '产品 ID',
      loanProductName: '产品名',
      interest: '利息',
      financialProductId: '产品 ID',
      financialProductName: '产品名',
      rate: '利率',
      type: '产品类别',
    }

    function getData(url: string, data: any = {}) {
      get({
        url,
        data: () => ({
          page: pagination.page,
          ...data,
        })
      })
        .then((res) => {
          table.handleSuccess({ data: res.data.records })
          pagination.setTotalSize((res as any).data.total)
        }, console.log)
    }

    function onInputUserId(userId: string) {
      userId = userId.trim()
      inputUserId.value = userId
    }

    function onChangeProductType(type: undefined | null | '' | 'financial' | 'loan') {
      if (type === '' || type === undefined) {
        type = null
      }
      productType.value = type
      doRefresh()
    }

    function doSearch() {
      doRefresh()
    }

    function doRefresh() {
      let data = { userId: inputUserId.value, productType: productType.value }
      if (userStore.isAdmin) {
        if (inputUserId.value !== null) {
          getData(getOrderByUser, data)
          return
        }
        getData(getOrderAll, data)
      } else {
        getData(getOrderByUser, data)
      }
    }

    const orderId = ref<string>()
    const payState = ref<string>()

    const onDataFormConfirm = () => {
      post({
        url: `/user/payment`,
        data: {
          orderId: orderId.value,
          money: productInfo.value?.price
        },
        headers: {
          'Content-Type': 'multipart/form-data; charset=UTF-8'
        }
      }).then(res => {
        if (res.code === 200) {
          Message.success('付款成功')
          modalDialogRef.value.toggle()
          doRefresh()
        }
      }).catch(e => Message.error(e.message))
    }


    async function checkOrderInfo(record: OrderTableData) {
      modalDialogRef.value.toggle()
      orderId.value = record.orderId
      payState.value = record.state
      let res = await get({
        url: getProductById + record.productId,
      });
      productInfo.value = res.data
      if (productInfo.value != null) {
        productInfo.value.type = record.productType
      }
    }

    function copyInner(event: Event) {
      let s = filter((event.target as DocumentType).parentNode?.childNodes, (e) => {
        return e.nodeType === 3 && e.textContent !== ''
      })[0]
      s.textContent = s.textContent?.trim() as string
      let range = document.createRange();
      let selection = window.getSelection();
      range.selectNode(s);
      selection?.removeAllRanges();
      selection?.addRange(range);
      document.execCommand("copy", false, undefined);
      document.execCommand("unselect", false, undefined);
    }

    onMounted(async () => {
      table.tableHeight.value = await useTableHeight(getCurrentInstance())
      doRefresh()
    })

    return {
      modalDialogRef,
      rowKey,
      tableColumns,
      actionTitle,
      pagination,
      checkOrderInfo,
      productType,
      inputUserId,
      defaultCheckedKeys,
      defaultExpandedKeys,
      ...table,
      userRole: userStore.role,
      payState,
      doSearch,
      onInputUserId,
      onChangeProductType,
      copyInner,
      productInfo,
      productInfoAttrs,
      productInfoMap,
      onDataFormConfirm
    }
  },
})
</script>
