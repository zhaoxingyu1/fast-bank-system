<template>
  <div class="register">
    <a-card class="form-wrapper" :body-style="{ padding: state.device !== 'pc' ? '0px' : '20px' }" :bordered="false">
      <BaseInfo v-if="step === 1" :registerData="registerData" @next="onNext" @back="onBack"/>
      <CardInfo v-if="step === 2" :registerData="registerData" @register="next" @back="onBack"/>
    </a-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref, inject, computed, watch } from 'vue'
import store from '../../../store'
import BaseInfo from './baseInfo.vue'
import CardInfo from './cardInfo.vue'
export default defineComponent({
  name: 'RegusterForm',
  components: {
    BaseInfo,
    CardInfo
  },
  props: {
    step: {
      type: Number
    }
  },
  emits: ['changeState', 'changeStep'],
  setup(prop, { emit }) {
    let step = ref<number | undefined>(1)
    let registerData = reactive<RegisterDateType | Object>({
      role: 'user',
      realName: '',
      age: '',
      gender: '',
      idCard: '',
      phone: '',
      username: '',
      email: '',
      emailCode: '',
      password: ''
    })

    const changeStep = computed(() => prop.step)
    watch(changeStep, () => {
      if (prop.step === 1) {
        Object.keys(registerData).forEach((it:string) => {
          registerData[it] = ''
        })
      }
      step.value = prop.step
    })
    const onNext = (formData:Object) => {
      Object.assign(registerData, formData)
      step.value = 2
      emit('changeStep', step.value)
    }
    const next = () => {
      Object.assign(registerData, {role: 'user'})
      step.value = 1
      emit('changeStep', step.value)
      emit('changeState')
    }
    const onBack = (value:any) => {
      Object.assign(registerData, {role: 'user'})
      if (value === 1) {
        step.value = 1
        emit('changeStep', step.value)
      } else {
        emit('changeState')
      }
    }
    return {
      state: store?.state,
      step,
      registerData,
      onNext,
      onBack,
      next
    }
  }
})
</script>

<style scoped lang="less">
.register {
  .form-wrapper {
    height: 400px;
    padding: 0 25px;
    width: 90%;
    .title {
      font-size: 25px;
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
      width: 100%;
    }
  }
}
 @media screen and(max-width: 966px) {
    .register {
      background-image: url('../../assets/img_login_mobile_bg_01.jpg');
      background-size: cover;
      .form-wrapper {
        width: 95% !important;
        padding: 0 !important;
      }
    }
  }
</style>
