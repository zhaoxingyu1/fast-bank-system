<template>
  <div class="main-container">
    <a-card
      title="工作台"
      :bodyStyle="{ padding: '10px' }"
      :headStyle="{ padding: '0 10px' }"
      size="small"
      :bordered="false"
      class="card-border-radius"
    >
      <a-row class="margin-top" wrap>
        <a-col :xs="24" :sm="16" :md="16" :lg="16" :xl="14">
          <div class="flex justify-center items-center">
            <div class="avatar-wrapper">
              <img :src="avatar" />
            </div>
            <div class="flex flex-col justify-around ml-3.5 flex-1">
              <div class="text-lg">{{ userStore.username }}</div>
            </div>
          </div>
        </a-col>
        <a-col :xs="12" :sm="8" :md="8" :lg="8" :xl="10">
          <div class="flex justify-end items-center h-full w-full mt-4">
            <div class="flex flex-col justify-around align-end item-action">
              <a-button  type="primary" @click="changeScriptControl">{{ isOpen ? '关闭防脚本模式' : '开启防脚本模式'  }}</a-button>
            </div>
            <div class="flex flex-col justify-around align-end item-action">
              <div class="text-gray">当前日期</div>
              <div class="text-lg mt-2">{{ currentDate }}</div>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-card>
    <div class="mt-3"></div>
    <a-row :gutter="[10, 10]">
      <a-col
        :xs="24"
        :sm="12"
        :md="8"
        :lg="6"
        :xl="6"
        :xxl="4"
        v-for="(item, index) of fastActions"
        :key="index"
      >
        <a-card
          @click="fastActionClick(item)"
          class="flex flex-col items-center justify-center fast-item-wrapper"
          :bordered="false"
        >
          <a-space direction="vertical" align="center">
            <component :is="item.icon" :style="{ color: item.color, fontSize: '28px' }" />
            <span class="mt-8 text-md">{{ item.title }}</span>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
    <div class="mt-3">
      <a-card
        :body-style="{ padding: '0px' }"
        :bordered="false"
        class="card-border-radius"
        title="贷款产品初筛申请"
      >
        <template #extra>
          <a-button  type="primary" @click="onRiskControl()">贷款初筛规则</a-button>
          <a-input-search :style="{width:'280px'}" placeholder="请输入用户名或产品名" v-model="name" @blur="doRefresh()" @press-enter="doRefresh()" @search="doRefresh()" style="margin: 0 10px"/>
          <a-select v-model="time" :style="{width:'160px'}" placeholder="全部" @change="changeTime()">
            <a-option v-for="item in timeFilter" :key="item.value">{{ item.label }}</a-option>
          </a-select>
        </template>
        <TableBody>
          <template #default>
            <a-table
              :bordered="false"
              :loading="tableLoading"
              :data="dataList"
              :pagination="false"
              :rowKey="rowKey"
              table-layout-fixed
              :scroll="{ y: tableHeight, x: 1000 }"
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
                </a-table-column>
              </template>
            </a-table>
          </template>
          <template #footer>
            <TableFooter ref="tableFooterRef" :pagination="pagination" />
          </template>
        </TableBody>
      </a-card>
    </div>
    <ModalDialog ref="modalDialogRef" title="贷款初筛规则" @confirm="onDataFormConfirm()">
      <template #content>
        <a-form>
          <a-form-item
            :row-class="[item.required ? 'form-item__require' : 'form-item__no_require']"
            v-for="item of formItems"
            :key="item.key"
            :label="item.label"
          >
            <template v-if="item.type === 'select'">
              <a-select v-model="item.value.value" :placeholder="item.placeholder">
                <a-option v-for="opt in item.optionItems" :key="opt.value">
                  {{ opt.label }}
                </a-option>
              </a-select>
            </template>
            <template v-if="item.type === 'input'">
              <a-input :placeholder="item.placeholder" v-model="item.value.value"></a-input>
            </template>
            <template v-if="item.type === 'number'">
              <a-input-number :placeholder="item.placeholder" v-model="item.value.value"  mode="button" />
            </template>
          </a-form-item>
        </a-form>
      </template>
    </ModalDialog>
  </div>
</template>

<script lang="ts">
  import { defineComponent, getCurrentInstance, computed, onMounted, ref, reactive, toRaw } from 'vue'
  import { useRouter } from 'vue-router'
  import { random } from 'lodash'
  import { post } from '@/api/http'
  import { selectAllOrByLikeNamePage, selectByTime, insertOrUpdate, getRiskControl, openScriptControl, closeScriptControl } from '@/api/url'
  import useUserStore from '@/store/modules/user'
  import {
    usePagination,
    useTable,
    useTableColumn,
    useTableHeight,
  } from '@/hooks/table'
  import { Message } from '@arco-design/web-vue'
  import { ModalDialogType, FormItem } from '@/types/components'
  const COLORS = ['#67C23A', '#E6A23C', '#F56C6C', '#409EFF']
  const date = new Date()
  const formItems = [
    {
      label: '年龄限制',
      key: 'ageRule',
      value: ref(''),
      type: 'input',
      required: true,
      placeholder: '请设置年龄限制',
      validator: function () {
        if (!this.value.value) {
          Message.error(this.placeholder || '')
          return false
        }
        return true
      },
    },
    {
      label: '信用状态',
      type: 'select',
      key: 'creditStatusRule',
      value: ref(undefined),
      required: true,
      placeholder: '请设置信用状态',
      optionItems: [
        {
          label: '失信',
          value: 0,
        },
        {
          label: '正常',
          value: 1,
        },
      ],
      reset: function () {
        this.value.vlue = undefined
      },
      validator: function () {
        if (!this.value.value) {
          Message.error(this.placeholder || '')
          return false
        }
        return true
      },
    },
    {
      label: '逾期次数',
      key: 'overdueNumberRule',
      value: ref(''),
      type: 'number',
      required: true,
      placeholder: '请设置逾期次数',
      validator: function () {
        if (!this.value.value) {
          Message.error(this.placeholder || '')
          return false
        }
        return true
      },
    },
    {
      label: '工作状态',
      key: 'workingStateRule',
      value: ref(undefined),
      type: 'select',
      required: true,
      placeholder: '请设置工作状态',
      optionItems: [
        {
          label: '失业',
          value: 0,
        },
        {
          label: '就业',
          value: 1,
        },
      ],
      reset: function () {
        this.value.vlue = undefined
      },
      validator: function () {
        if (!this.value.value) {
          Message.error(this.placeholder || '')
          return false
        }
        return true
      },
    },
  ] as FormItem[]
  export default defineComponent({
    name: 'WorkPlace',
    setup() {
      const userStore = useUserStore()
      const avatar = computed(() => userStore.avatar)
      const modalDialogRef = ref<ModalDialogType | null>(null)
      const table = useTable()
      const pagination = usePagination(doRefresh)
      const router = useRouter()
      const isOpen = ref(true)
      const fastActionClick = ({ path = '/' }) => {
        router.push(path)
      }
      const tableColumns = useTableColumn([
      {
        title: '用户名',
        key: 'username',
        dataIndex: 'username',
      },
      {
        title: '年龄',
        key: 'age',
        dataIndex: 'age',
      },
      {
        title: '信用状态',
        key: 'creditStatus',
        dataIndex: 'creditStatus',
      },
      {
        title: '逾期次数',
        key: 'overdueNumber',
        dataIndex: 'overdueNumber',
      },
      {
        title: '工作状态',
        key: 'workingState',
        dataIndex: 'workingState',
      },
      {
        title: '产品名',
        key: 'productName',
        dataIndex: 'productName',
      },
      {
        title: '产品类型',
        key: 'productType',
        dataIndex: 'productType',
      },
      {
        title: '通过状态',
        key: 'throughState',
        dataIndex: 'throughState',
      },
      {
        title: '理由',
        key: 'cause',
        dataIndex: 'cause',
      },
    ])
      const timeFilter = reactive([
        { value: 'all', label: '全部' },
        {value: 7, label: '一周内' },
        {value: 30, label: '一月内' },
        {value: 90, label: '三月内' },
        {value: 365, label: '一年内' }
      ])
      const name = ref()
      const time = ref()
      function doRefresh() {
        post({
          url: selectAllOrByLikeNamePage,
          data: () => {
            return {
              name: name.value == undefined ? '' : name.value,
              current: pagination.page
            }
          },
          headers: {
            'Content-Type': 'multipart/form-data; charset=UTF-8'
          }
        }).then((res) => {
            if (res.code === 200) {
              transferData(res.data?.records)
              table.handleSuccess({ data: res.data?.records })
              pagination.setTotalSize(res.data?.total)
            }
          })
          .catch(console.log)
      }
      const changeTime = () => {
        if (time.value === '全部') {
          return doRefresh()
        }
        const dayObj = timeFilter.find(item => {
          return item.label === time.value
        })
        post({
          url: selectAllOrByLikeNamePage,
          data: () => {
            return {
              day: dayObj?.value,
              current: pagination.page
            }
          },
          headers: {
            'Content-Type': 'multipart/form-data; charset=UTF-8'
          }
        }).then(res => {
          if (res.code === 200) {
            table.handleSuccess({ data: res.data?.records })
            pagination.setTotalSize(res.data?.total)
          }
        }).catch(e => Message.error(e.message))
      }
      const onRiskControl = () => {
        post({
          url: getRiskControl,
          headers: {
            'Content-Type': 'multipart/form-data; charset=UTF-8'
          }
        }).then(res => {
          if (res.code === 200) {
            if (res.data !== null) {
              formItems.forEach((it) => {
                const key = it.key
                if (it.key === 'creditStatusRule' || it.key === 'workingStateRule') {
                  it?.optionItems?.forEach(item => {
                    item.value === res.data[key] ? it.value.value = item.label : undefined
                  })
                } else {
                  it.value.value = res.data[key]
                }
              })
            }
            modalDialogRef.value?.toggle()
          }
        }).catch(e => Message.error(e.message))
      }
      const onDataFormConfirm = () => {
        if (formItems.every((it) => (it.validator ? it.validator() : true))) {
          modalDialogRef.value?.toggle()
          let form:any = reactive({})
          formItems.forEach(it => {
            if (it.key === 'creditStatusRule' || it.key === 'workingStateRule') {
              it.optionItems?.forEach(item => {
                item.label === it.value.value ? form[it.key] = item.value : undefined
              })
            } else {
              form[it.key] = it.value.value
            }
          })
          post({
            url: insertOrUpdate,
            data: form,
            headers: {
              'Content-Type': 'multipart/form-data; charset=UTF-8'
            }
          }).then(res => {
            if (res.code === 200) {
              Message.success('设置初筛规则成功')
            }
          }).catch(e => Message.error(e.message))
        }
      }
      const changeScriptControl = () => {
        isOpen.value = !isOpen.value
        if (isOpen.value) {
          post({
            url: openScriptControl,
             headers: {
            'Content-Type': 'multipart/form-data; charset=UTF-8'
            }
          }).then(res => {
            if(res.code === 200) {
              Message.success('开启成功')
            }
          }).catch(e => Message.error(e.message))
        } else {
          post({
            url: closeScriptControl,
             headers: {
            'Content-Type': 'multipart/form-data; charset=UTF-8'
            }
          }).then(res => {
            if(res.code === 200) {
              Message.success('关闭成功')
            }
          }).catch(e => Message.error(e.message))
        }
      }
      const transferData = (data: any) => {
        console.log(data, 'data')
        data.forEach((item:any) => {
          item['creditStatus'] = item['creditStatus'] === 0 ? '失信' : '正常'
          item['productType'] = '贷款产品'
          item['workingState'] = item['workingState'] === 0 ? '失业' : '工作中'
          item['throughState'] = item['throughState'] === 1 ? '通过' : '未通过'
        })
        console.log(data, 'data1')
      }
      onMounted(async () => {
        table.tableHeight.value = await useTableHeight(getCurrentInstance())
        doRefresh()
      })
      return {
        avatar,
        userStore,
        currentDate: date.getFullYear() + '/' + (date.getMonth() + 1) + '/' + date.getDate(),
        isOpen,
        fastActions: [
          {
            title: '首页',
            icon: 'icon-dashboard',
            path: '/',
            color: COLORS[random(0, COLORS.length)],
          },
          {
            title: '用户管理',
            path: '/user-management',
            icon: 'icon-user-group',
            color: COLORS[random(0, COLORS.length)],
          },
          {
            title: '产品中心',
            path: '/products',
            icon: 'icon-apps',
            color: COLORS[random(0, COLORS.length)],
          },
          {
            title: '订单中心',
            path: '/order',
            icon: 'icon-stamp',
            color: COLORS[random(0, COLORS.length)],
          },
          {
            title: '个人中心',
            path: '/personal',
            icon: 'icon-user',
            color: COLORS[random(0, COLORS.length)],
          },
        ],
        ...table,
        tableColumns,
        pagination,
        name,
        time,
        timeFilter,
        formItems,
        modalDialogRef,
        fastActionClick,
        doRefresh,
        changeTime,
        onRiskControl,
        onDataFormConfirm,
        changeScriptControl
      }
    },
  })
</script>

<style lang="less" scoped>
  .avatar-wrapper {
    width: 3rem;
    height: 3rem;
    max-width: 3rem;
    max-height: 3rem;
    min-width: 3rem;
    min-height: 3rem;
    & > img {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      border: 2px solid yellowgreen;
    }
  }
  .item-action {
    position: relative;
    padding: 0 30px;
  }
  .item-action::after {
    position: absolute;
    top: 20%;
    right: 0;
    height: 60%;
    content: '';
    display: block;
    width: 1px;
    background-color: var(--border-color);
  }
  div.item-action:last-child::after {
    width: 0;
  }
  .fast-item-wrapper {
    height: 80px;
    border-radius: 8px;
    .anticon {
      font-size: 20px;
    }
  }
  .fast-item-wrapper:hover {
    cursor: pointer;
    box-shadow: 0px 0px 10px #ddd;
  }
</style>
