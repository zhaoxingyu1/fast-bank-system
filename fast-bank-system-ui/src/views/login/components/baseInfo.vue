<template>
  <div class="base-info">
    <div class="form-wrapper">
      <div class="title">
        基础信息
      </div>
      <a-form>
        <div class="item-wrapper">
          <a-form-item hide-label="true">
            <a-input v-model="formData.realName" placeholder="请输入真实姓名" allow-clear size="large">
              <template #prepend>
                姓名
              </template>
            </a-input>
           </a-form-item>
        </div>
        <div class="item-wrapper">
           <a-form-item hide-label="true">
            <a-input v-model="formData.age" placeholder="请输入年龄" allow-clear size="large">
              <template #prepend>
                年龄
              </template>
            </a-input>
           </a-form-item>
        </div>
        <div class="item-wrapper">
          <a-form-item label="性别">
            <a-select v-model="gender" placeholder="请输入性别" size="large" @change="changeGender">
              <a-option v-for="opt in genderList" :key="opt.value">
                {{ opt.label }}
              </a-option>
            </a-select>
          </a-form-item>
        </div>
        <div class="item-wrapper">
          <a-form-item hide-label="true">
            <a-input v-model="formData.idCard" placeholder="请输入身份证" allow-clear size="large">
              <template #prepend>
                身份证
              </template>
            </a-input>
          </a-form-item>
        </div>
        <div class="item-wrapper">
          <a-form-item hide-label="true">
            <a-input v-model="formData.phone" placeholder="请输入电话号码" allow-clear size="large">
              <template #prepend>
                手机
              </template>
            </a-input>
          </a-form-item>
        </div>
      </a-form>
      <div class="mt-4 flex justify-between">
        <a-button class="register" @click="onBack()">
          返回
        </a-button>
        <a-button type="primary" class="register" @click="onNext()">
          下一步
        </a-button>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { computed, defineComponent, onMounted, reactive, ref, watch } from 'vue'
import { mapKeys } from "lodash"
import { Message } from '@arco-design/web-vue'
export default defineComponent({
  name: 'BaseInfo',
  props: {
    registerData: {
      type: Object,
      require: true
    }
  },
  emits: ['next', 'back'],
  setup(props, { emit }) {
    let formData = reactive<BaseInfoType>({
      realName: '',
      age: '',
      gender: '',
      idCard: '',
      phone: ''
    })
    const genderList = reactive([
      { value: 0, label: '男' },
      { value: 1, label: '女' }
    ])
    const gender = ref()
    const changeGender = () => {
      const formGender = genderList.find(item => {
        return item.label === gender.value
      })
      Object.assign(formData, { gender: formGender?.value })
    }
    const onNext = () => {
      if (formData?.realName !== '' && formData.age !== '' && formData.gender !== '' && formData.idCard !== '' && formData.phone !== '') {
        emit('next', formData)
      } else {
        Message.error('基础信息都不能为空，请先填写完基础信息')
      }
    }
    const onBack = () => {
      mapKeys(formData, (value:string | number, key:string) => {
        return formData[key] = ''
      })
      gender.value = ''
      console.log(formData, 'formData')
      emit('back', 0)
    }
    onMounted(() => {
      console.log(formData, props.registerData, 12323123)
      Object.assign(formData, props.registerData)
      gender.value = genderList.find(item => {
        return item.value === (formData as any).gender
      })?.label
    })
    return {
      formData,
      genderList,
      gender,
      onNext,
      onBack,
      changeGender
    }
  }
})
</script>

<style scoped lang="less">
.base-info {
  .form-wrapper {
    height: 400px;
    padding: 0 20px;
    // width: 90%;
    .title {
      font-size: 20px;
      font-weight: bold;
      margin-bottom: 20px;
    }
    .item-wrapper {
      .arco-input-outer {
        :deep(.arco-input-append) {
          background-color: #fff;
        }
      }
    }
    .register {
      width: 30%;
    }
  }
}
 @media screen and(max-width: 966px) {
    .base-info {
      background-image: url('../../assets/img_login_mobile_bg_01.jpg');
      background-size: cover;
      .form-wrapper {
        width: 95% !important;
        padding: 0 !important;
      }
    }
  }
</style>