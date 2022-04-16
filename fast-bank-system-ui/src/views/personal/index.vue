<template>
  <div class="main-container">
    <div class="box-wrapper">
      <div class="flex">
        <a-card
          :bordered="false"
          class="card-border-radius personal-box"
          :body-style="{ padding: '10px' }"
        >
          <Scrollbar wrap-class="modal-dialog__wrap">
            <slot name="content"
              ><div class="info-wrapper">
                <div class="avatar-wrapper">
                  <div class="avatar" @mouseenter="avatarTouchStart">
                    <img :src="avatar" />
                  </div>
                  <div class="flex items-center justify-center camera-layer" @click="uploadAvatar">
                    <icon-camera style="color: #fff; font-size: 30px" />
                  </div>
                </div>
                <div class="text-xl">
                  {{ data.nickName }}
                </div>
                <div class="des-wrapper">
                  <i class="el-icon-edit"></i>
                  冰冻三尺，非一日之寒，成大事者不拘小节。
                </div>
                <div class="text-wrapper">
                  <div class="label"> 用户名： </div>
                  <div class="value"> {{ data.username }} </div>
                  <a-divider class="half-divider" />
                </div>
                <div class="text-wrapper">
                  <div class="label"> 性 别： </div>
                  <div class="value"> {{ data.gender === 0 ? '男' : '女' }} </div>
                  <a-divider class="half-divider" />
                </div>
                <div class="text-wrapper">
                  <div class="label"> 年 龄： </div>
                  <div class="value"> {{ data.age }}</div>
                  <a-divider class="half-divider" />
                </div>
                <div class="text-wrapper">
                  <div class="label"> 电 话： </div>
                  <div class="value"> {{ data.phone }} </div>
                  <a-divider class="half-divider" />
                </div>
                <div class="text-wrapper">
                  <div class="label"> 工作状态： </div>
                  <div class="value"> {{ data.workingState === 1 ? '就业' : '失业' }} </div>
                  <a-divider class="half-divider" />
                </div>
                <div class="text-wrapper">
                  <div class="label"> 邮 箱： </div>
                  <div class="value"> {{ data.email }} </div>
                  <a-divider class="half-divider" />
                </div>
                <div class="text-wrapper">
                  <div class="label"> 身 份： </div>
                  <div class="value"> {{ data.role === admin ? '管理员' : '普通用户' }} </div>
                  <a-divider class="half-divider" />
                </div>
                <div class="text-wrapper">
                  <div class="label"> 信用状态： </div>
                  <div class="value"> {{ data.creditStatus === 1 ? '未失信' : '失信' }} </div>
                  <a-divider class="half-divider" />
                </div>
                <div class="text-wrapper">
                  <div class="label"> 逾期次数： </div>
                  <div class="value"> {{ data.overdueNumber }} </div>
                  <a-divider class="half-divider" />
                </div>
                <div>
                  <a-button type="outline" @click="ChangePassword" class="abutton"
                    >修改密码</a-button
                  >
                  <a-button type="outline" @click="handleClick" class="abutton"
                    >修改基本信息</a-button
                  >
                  <a-modal
                    v-model:visible="visible"
                    title="基本信息"
                    @cancel="handleCancel"
                    @before-ok="handleBeforeOk"
                    @ok="handleConfirm"
                  >
                    <a-form :model="form">
                      <a-form-item field="nickName" label="呢称">
                        <a-input v-model="form.nickName" />
                      </a-form-item>
                      <a-form-item field="realName" label="姓名">
                        <a-input v-model="form.realName" disabled />
                      </a-form-item>
                      <a-form-item field="gender" label="性别">
                        <a-select v-model="form.gender" @change="changeType">
                          <a-option v-for="opt in optionItemsGender" :key="opt.value">
                            {{ opt.label }}
                          </a-option>
                        </a-select>
                      </a-form-item>
                      <a-form-item field="age" label="年龄">
                        <a-input-number v-model="form.age" />
                      </a-form-item>
                      <a-form-item field="phone" label="电话号码">
                        <a-input v-model="form.phone" />
                      </a-form-item>
                      <a-form-item field="bankCard" label="银行卡号">
                        <a-input v-model="form.bankCard" />
                      </a-form-item>
                      <a-form-item field="workingStatus" label="工作状态">
                        <a-select v-model="form.workingStatus" @change="changeType">
                          <a-option v-for="opt in optionItemsWorkingStatus" :key="opt.value">
                            {{ opt.label }}
                          </a-option>
                        </a-select>
                      </a-form-item>
                      <a-form-item field="email" label="邮箱">
                        <a-input v-model="form.email" />
                      </a-form-item>
                    </a-form> </a-modal
                ></div> </div
            ></slot>
          </Scrollbar>
        </a-card>
        <a-card
          :body-style="{ padding: '10px' }"
          :bordered="false"
          class="flex-1 card-border-radius wating-box"
          title="消息中心"
        >
          <template #extra>
            <a-select
              v-model="time"
              :style="{ width: '160px' }"
              placeholder="全部"
              @change="changeTime()"
            >
              <a-option v-for="item in timeFilter" :key="item.value">{{ item.label }}</a-option>
            </a-select>
          </template>
          <TableBody :style="{ height: '70vh' }">
            <template #default>
              <a-table
                :bordered="false"
                :loading="tableLoading"
                :data="dataList"
                :pagination="false"
                :rowKey="rowKey"
                table-layout-fixed
                :scroll="{ y: tableHeight, x: 1000 }"
                :style="{ height: '60vh' }"
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
              <TableFooter ref="tableFooterRef" :pagination="pagination" bordered="true" />
            </template>
          </TableBody>
        </a-card>
      </div>
    </div>
    <ModalDialog ref="modalDialogRef" title="修改密码" @confirm="onDataFormConfirm">
      <template #content>
        <a-form>
          <a-form-item
            :class="[item.required ? 'form-item__require' : 'form-item__no_require']"
            :label="item.label"
            v-for="item of formItems"
            :key="item.key"
          >
            <template v-if="item.type === 'input'">
              <a-input-password
                v-model="item.value.value"
                placeholder="请输入密码"
                allow-clear
                size="large"
              >
                <template #prefix>
                  <icon-lock />
                </template>
              </a-input-password>
            </template>
          </a-form-item>
        </a-form>
      </template>
    </ModalDialog>
  </div>
</template>

<script lang="ts">
import {
  selectAllOrByLikeNamePage,
  updateUserInfo,
  selectUserById,
  updateUserPassword,
} from '@/api/url'
import { usePagination, useTable, useTableColumn, useTableHeight } from '@/hooks/table'
import useUserStore from '@/store/modules/user'
import { Message } from '@arco-design/web-vue'
import { post, Response, get } from '@/api/http'
import { defineComponent, reactive, ref, getCurrentInstance, onMounted, computed } from 'vue'
import { UserState } from '@/store/types'
import { ModalDialogType, FormItem } from '@/types/components'
import { useLayoutStore } from '@/layouts'
const formItems = [
  {
    label: '旧密码',
    key: 'oldPassword',
    value: ref(''),
    type: 'input',
    required: true,
    placeholder: '请输入旧密码',
    validator: function () {
      if (!this.value.value) {
        Message.error(this.placeholder || '')
        return false
      }
      return true
    },
  },
  {
    label: '新密码',
    key: 'newPassword',
    value: ref(''),
    type: 'input',
    required: true,
    placeholder: '请输入新密码',
    validator: function () {
      if (!this.value.value) {
        Message.error(this.placeholder || '')
        return false
      }
      return true
    },
  },
]

export default defineComponent({
  name: 'Personal',
  setup() {
    const touched = ref(false)
    const uploaded = ref(false)
    const time = ref()
    const modalDialogRef = ref<ModalDialogType | null>(null)
    const optionItemsWorkingStatus = [
      {
        label: '失业',
        value: 0,
      },
      {
        label: '就业',
        value: 1,
      },
    ]
    const optionItemsGender = [
      {
        label: '男',
        value: 0,
      },
      {
        label: '女',
        value: 1,
      },
    ]
    const avatarTouchStart = () => {
      touched.value = true
    }
    const uploadAvatar = () => {
      uploaded.value = true
      setTimeout(() => {
        touched.value = false
        uploaded.value = false
      }, 1000)
    }
    const userStore = useUserStore()
    const pagination = usePagination(doRefresh)
    const table = useTable()
    const name = ref()
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
      { value: 7, label: '一周内' },
      { value: 30, label: '一月内' },
      { value: 90, label: '三月内' },
      { value: 365, label: '一年内' },
    ])

    function doRefresh() {
      post({
        url: selectAllOrByLikeNamePage,
        data: () => {
          return {
            name: name.value == undefined ? '' : name.value,
            current: pagination.page,
          }
        },
        headers: {
          'Content-Type': 'multipart/form-data; charset=UTF-8',
        },
      })
        .then((res) => {
          if (res.code === 200) {
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
      const dayObj = timeFilter.find((item) => {
        return item.label === time.value
      })
      post({
        url: selectAllOrByLikeNamePage,
        data: () => {
          return {
            day: dayObj?.value,
            current: pagination.page,
          }
        },
        headers: {
          'Content-Type': 'multipart/form-data; charset=UTF-8',
        },
      })
        .then((res) => {
          if (res.code === 200) {
            table.handleSuccess({ data: res.data?.records })
            pagination.setTotalSize(res.data?.total)
          }
        })
        .catch((e) => console.error(e))
    }
    onMounted(async () => {
      table.tableHeight.value = await useTableHeight(getCurrentInstance())
      doRefresh()
    })

    const visible = ref(false)
    const form = ref({
      nickName: userStore.nickname,
      gender: userStore.gender === 0 ? '男' : '女',
      age: userStore.age,
      phone: userStore.phone,
      workingStatus: userStore.workingState === 0 ? '失业' : '就业',
      email: userStore.email,
      bankCard: userStore.bankCard,
      realName: userStore.realName,
    })

    const getInfo = () => {
      get({
        url: selectUserById,
      })
        .then(({ data }: Response) => {
          const userInfo = reactive({
            ...data.userInfo,
            userId: data.userId,
            username: data.username,
            role: data.userRole.role,
            roleId: data.roleId,
          })
          userStore.saveUser(userInfo as UserState)
          Object.assign(data, userStore)
        })
        .catch((e: any) => console.log(e))
    }

    const handleClick = () => {
      visible.value = true
    }
    const handleBeforeOk = (done: () => void) => {
      console.log(form.value, '11211')
      window.setTimeout(() => {
        done()

        // prevent close
        // done(false)
      }, 1000)
    }
    const handleCancel = () => {
      visible.value = false
    }

    const handleConfirm = () => {
      post({
        url: updateUserInfo,
        data: () => {
          return {
            phone: form.value.phone,
            workingState: form.value.workingStatus === '失业' ? 0 : 1,
            nickname: form.value.nickName,
            email: form.value.email,
            bankCard: form.value.bankCard,
            realName: form.value.realName,
            age: form.value.age,
            gender: form.value.gender === '男' ? 0 : 1,
          }
        },
        headers: {
          'Content-Type': 'multipart/form-data; charset=UTF-8',
        },
      })
        .then((res) => {
          if (res.code === 200) {
            console.log('form ....')
            getInfo()
          }
        })
        .catch((error) => {
          Message.error(error.message)
        })
    }

    let data = computed(() => {
      return reactive({
        avatar: userStore.avatar,
        nickName: userStore.nickname,
        gender: userStore.gender,
        age: userStore.age,
        username: userStore.username,
        email: userStore.email,
        role: userStore.role,
        overdueNumber: userStore.overdueNumber,
        creditStatus: userStore.creditStatus,
        phone: userStore.phone,
        workingState: userStore.workingState,
        idCard: userStore.idCard,
        bankCard: userStore.bankCard,
        realName: userStore.realName,
      })
    })

    const ChangePassword = () => {
      formItems.forEach((it) => {
        it.value.value = ''
      })
      modalDialogRef.value?.toggle()
    }
    const store = useLayoutStore()
    const onDataFormConfirm = () => {
      if (formItems.every((it) => (it.validator ? it.validator() : true))) {
        post({
          url: updateUserPassword,
          data: () => {
            return {
              oldPassword: formItems[0]?.value.value,
              newPassword: formItems[1]?.value.value,
            }
          },
          headers: {
            'Content-Type': 'multipart/form-data; charset=UTF-8',
          },
        })
          .then((res) => {
            if (res.code === 200) {
              Message.success('修改成功')
              modalDialogRef.value?.toggle()
              userStore.logout().then(() => {
                ;(store as any).onLogout && (store as any).onLogout()
              })
            }
          })
          .catch((e) => Message.error(e.msg))
      }
    }
    return {
      touched,
      uploaded,
      optionItemsWorkingStatus,
      optionItemsGender,
      data,
      formItems,
      avatar: userStore.avatar,
      modalDialogRef,
      avatarTouchStart,
      uploadAvatar,
      changeTime,
      pagination,
      timeFilter,
      ...table,
      tableColumns,
      visible,
      form,
      handleClick,
      handleBeforeOk,
      handleCancel,
      handleConfirm,
      ChangePassword,
      onDataFormConfirm,
    }
  },
})
</script>
<style lang="less" scoped>
.box-wrapper {
  .table-bottom {
    position: absolute;
    bottom: 50%;
  }
  .abutton {
    position: relative;
    margin: 0 5%;
  }
  .personal-box {
    width: 33%;
    height: 80vh;
    .info-wrapper {
      text-align: center;
      .avatar-wrapper {
        display: inline-block;
        width: 6rem;
        height: 6rem;
        margin-top: 20px;
        position: relative;
        .avatar {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          transform-origin: bottom;
          transform: rotate(0deg);
          z-index: 1;
          transition: all 0.5s ease-in-out;
          & > img {
            width: 100%;
            height: 100%;
            border-radius: 50%;
            border: 2px solid rgb(245, 241, 7);
          }
        }
        .camera-layer {
          position: absolute;
          top: 0;
          bottom: 0;
          left: 0;
          right: 0;
          background: rgba(0, 0, 0, 0.6);
          border-radius: 50%;
        }
      }
      .des-wrapper {
        width: 70%;
        margin: 0 auto;
        font-size: 14px;
        padding: 15px;
      }
      .text-wrapper {
        font-size: 0.8rem;
        margin-top: 20px;
        width: 50%;
        margin: 0 auto;
        .label {
          display: inline-block;
          width: 60%;
          text-align: center;
        }
        .value {
          display: inline-block;
          width: 40%;
          margin-top: 3%;
          text-align: left;
        }
        .half-divider {
          left: 45px;
          width: calc(100% - 35px);
          min-width: auto;
          margin: 5px 0;
        }
      }
      .text-wrapper + .text-wrapper {
        margin-top: 15px;
      }
    }
  }
  .message-wrapper {
    border-bottom: 1px solid #f5f5f5;
    padding-bottom: 10px;

    .notify {
      width: 10px;
      height: 10px;
      border-radius: 50%;
    }
    .message-title {
      font-size: 14px;
    }
    .content {
      font-size: 12px;
      margin-top: 10px;
      line-height: 1rem;
    }
  }
  .message-wrapper + .message-wrapper {
    margin-top: 10px;
  }
  .wating-box {
    width: 30%;
    margin-left: 10px;
    .wating-item {
      padding: 10px;
      border-bottom: 1px solid #f5f5f5;
    }
  }
}
</style>


