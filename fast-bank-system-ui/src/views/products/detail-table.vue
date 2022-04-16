<template>
  <div class="modal-content">
    <TableBody>
      <template #default>
        <a-table
          :bordered="true"
          :loading="tableData.tableLoading"
          :data="tableData.dataList"
          :pagination="false"
          :rowKey="tableData.rowKey"
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
              <template v-if="item.key === 'creditStatus'" #cell="{ record }">
                {{ record.creditStatus === 0 ? '失信' : '正常' }}
              </template>
              <template v-if="item.key === 'state'" #cell="{ record }">
                {{ record.state}}
              </template>
              <template v-if="item.key === 'ctime'" #cell="{ record }">
                <span v-date:YYYY-MM-DD HH:mm> {{ record.ctime}}</span>
              </template>
            </a-table-column>
          </template>
        </a-table>
      </template>
    </TableBody>
  </div>
</template>

<script lang="ts">
  import { usePagination, useRowKey, useTable, useTableColumn, useTableHeight } from '@/hooks/table'
  import { defineComponent, nextTick, onMounted, ref, getCurrentInstance, toRef, reactive, onBeforeMount, computed } from 'vue'
  export default defineComponent({
    name: 'DetailTable',
    emits: ['doRefresh'],
    props: {
      tableData: {
        type: Object,
        required: true
      }
    },
    setup(props, { emit }) {
      const table = useTable()
      const pagination = usePagination(() => { emit("doRefresh", props.tableData) })
      const tableColumns = useTableColumn([
        table.indexColumn,
        {
          title: '用户名称',
          key: `name`,
          dataIndex: `name`,
          width: 100
        },
        {
          title: '信用状态',
          key: `creditStatus`,
          dataIndex: `creditStatus`,
          width: 100
        },
        {
          title: '订单状态',
          key: `state`,
          dataIndex: `state`,
          width: 100
        },
        {
          title: '时间',
          key: 'ctime',
          dataIndex: 'ctime',
          width: 120
        }
      ])
      const data = computed(() => props.tableData)
      const init = () => {
        console.log(props.tableData, 'data')
        if (props.tableData) {
          // table.handleSuccess({data})
        }
        console.log(table.dataList, 'props.tableData')
      }
      onMounted(async() => {
        table.tableHeight.value = await useTableHeight(getCurrentInstance())
        init()
      })
      return {
        ...table,
        pagination,
        tableColumns,
        props
      }
    }
  })
</script>

<style lang="less" scoped>
  .modal-content {
    max-height: 400px;
  }
</style>