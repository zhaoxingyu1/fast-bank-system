<template>
  <div>
    <a-row>
      <a-col>
        <TableBody>
          <template #header>
            <TableHeader ref="tableHeaderRef" :show-filter="false" :title="'用户管理'">
              <template #top-right>
                <a-input-search :style="{width:'280px'}" placeholder="请输入用户信息" v-model="name" @blur="doRefresh()" @press-enter="doRefresh()" @search="doRefresh()"/>
                <a-radio-group v-model="creditStatus" type="button" @change="onChangeStatus()">
                  <a-radio value="-1">全部用户</a-radio>
                  <a-radio value="0">失信用户</a-radio>
                  <a-radio value="1">正常用户</a-radio>
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
                  <template v-if="item.key === 'index'" #cell="{ rowIndex }">
                    {{ rowIndex + 1 }}
                  </template>
                  <template v-else-if="item.key === 'gender'" #cell="{ record }">
                    <a-tag :color="record.gender === 1 ? 'green' : 'red'">
                      {{ record.gender === 0 ? '男' : '女' }}
                    </a-tag>
                  </template>
                  <template v-else-if="item.key === 'avatar'" #cell="{}">
                    <a-avatar size="30" :style="{ backgroundColor: 'var(--color-primary-light-1)' }">
                      <IconUser />
                    </a-avatar>
                  </template>
                  <template v-else-if="item.key === 'actions'" #cell="{ record }">
                    <a-button status="danger " @click="onDeleteItem(record)" size="mini">
                      {{ record.creditStatus === 1 ? '冻结' : '解冻' }}
                    </a-button>
                  </template>
                  <template v-else-if="item.key === 'creditStatus'" #cell="{ record }">
                    <a-tag color="blue" size="small" v-if="record.creditStatus === 1">正常</a-tag>
                    <a-tag color="red" size="small" v-else>失信</a-tag>
                  </template>
                </a-table-column>
              </template>
            </a-table>
          </template>
          <template #footer>
            <TableFooter ref="tableFooterRef" :pagination="pagination" />
          </template>
        </TableBody>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts">
  import { post,get, Response } from '@/api/http'
  import { selectAllUserOrByLikeName, selectAllByInfo, selectUserInfoByCreditStatus, updateUserCreditStatus } from '@/api/url'
  import {
    usePagination,
    useRowKey,
    useRowSelection,
    useTable,
    useTableColumn,
    useTableHeight,
  } from '@/hooks/table'
  import { Message, Modal } from '@arco-design/web-vue'
  import { defineComponent, getCurrentInstance, onMounted, ref, reactive } from 'vue'
  export default defineComponent({
    name: 'UserList',
    setup() {
      const table = useTable()
      const rowKey = useRowKey('id')
      const pagination = usePagination(doRefresh)
      const tableColumns = useTableColumn([
        table.indexColumn,
        {
          title: '头像',
          key: 'avatar',
          dataIndex: 'avatar',
          width: 50,
        },
        {
          title: '昵称',
          key: 'nickname',
          dataIndex: 'nickname',
          width: 80,
        },
        {
          title: '真实名字',
          key: 'realName',
          dataIndex: 'realName',
          width: 80,
        },
        {
          title: '性别',
          key: 'gender',
          dataIndex: 'gender',
          width: 80,
        },
        {
          title: '年龄',
          key: 'age',
          dataIndex: 'age',
          width: 50,
        },
        {
          title: '邮箱',
          key: 'email',
          dataIndex: 'email',
          width: 120,
        },
        {
          title: '电话',
          key: 'phone',
          dataIndex: 'phone',
          width: 120,
        },
        {
          title: '信用状态',
          key: 'creditStatus',
          dataIndex: 'creditStatus',
          width: 80,
        },
        {
          title: '操作',
          key: 'actions',
          fixed: 'right',
          width: 140,
          dataIndex: '',
        },
      ])
      const expandAllFlag = ref(true)
      const creditStatus = ref('-1')
      const userDetailInfo = ref()
      const name = ref('')
      function doRefresh() {
        post({
          url: selectAllByInfo,
          data: () => {
            return {
              info: name.value == undefined ? '' : name.value,
              current: pagination.page,
            }
          },
          headers: {
            'Content-Type': 'multipart/form-data; charset=UTF-8'
          }
        })
          .then((res) => {
            console.log(res)
            if (res.code === 200) {
              table.handleSuccess({ data: res.data?.records })
              pagination.setTotalSize(res.data?.total)
            }
          })
          .catch(console.log)
      }
      function onDeleteItem(item: any) {
        Modal.confirm({
          title: '提示',
          content: '确定要对此用户进行操作吗？',
          cancelText: '取消',
          okText: '确定',
          onOk: () => {
             post({
              url: updateUserCreditStatus,
              data: {
                userInfoId: item.userInfoId,
                creditStatus: item.creditStatus === 1 ? 0 : 1
              },
              headers: {
                'Content-Type': 'multipart/form-data; charset=UTF-8'
              }
            }).then(res => {
              if (res.code === 200) {
                doRefresh()
                Message.success('操作成功')
              }
            }).catch(e => Message.error(e.message))

          },
        })
      }
      const onChangeStatus = () => {
        if (creditStatus.value === '-1') {
          return doRefresh()
        }
        get({
          url: selectUserInfoByCreditStatus,
          data: () => {
            return {
              creditStatus: creditStatus.value === '1' ? 1 : 0,
              current: pagination.page,
            }
          },
        }).then((res) => {
          console.log(res)
          if (res.code === 200) {
              table.handleSuccess({ data: res.data?.records })
              pagination.setTotalSize(res.data?.total)
            }
        }).catch(e => Message.error(e.message))
      }
      onMounted(async () => {
        table.tableHeight.value = await useTableHeight(getCurrentInstance())
        doRefresh()
      })
      return {
        ...table,
        rowKey,
        expandAllFlag,
        tableColumns,
        pagination,
        name,
        creditStatus,
        userDetailInfo,
        onDeleteItem,
        onChangeStatus,
        doRefresh
      }
    },
  })
</script>
