<template>
  <div class="card-info">
    <div class="form-wrapper">
      <div class="title">
        创建账号
      </div>
      <a-form>
        <div class="item-wrapper mt-2">
          <a-form-item hide-label="true">
            <a-input v-model="formData.username" placeholder="请输入用户名" allow-clear size="large">
              <template #prepend>
                账号
              </template>
            </a-input>
          </a-form-item>
        </div>
        <div class="item-wrapper">
          <a-form-item hide-label="true">
            <a-input v-model="formData.email" placeholder="请输入邮箱" allow-clear size="large">
              <template #prepend>
                邮箱
              </template>
            </a-input>
          </a-form-item>
        </div>
        <div class="item-wrapper">
          <a-form-item hide-label="true">
            <a-input v-model="formData.emailCode" placeholder="请输入验证吗" allow-clear size="large" >
              <template #append>
                <a-button @click="send" :disabled="time < 60">{{ time < 60? time : '获取验证码'}}</a-button>
              </template>
            </a-input>
          </a-form-item>
        </div>
        <div class="item-wrapper">
          <a-form-item hide-label="true">
            <a-input-password v-model="formData.password" placeholder="请输入密码" allow-clear size="large">
              <template #prepend>
                密码
              </template>
            </a-input-password>
          </a-form-item>
        </div>
      </a-form>
      <div class="flex-sub"></div>
      <div class="mt-10 flex justify-between">
        <a-button class="register" @click="onBack">
          上一步
        </a-button>
        <a-button type="primary" class="register" :loading="loading" @click="onRegister">
          注册
        </a-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, inject, onMounted, reactive, ref, watch } from 'vue'
import { post } from '@/api/http'
import { register, sendEmail } from '@/api/url'
import { Message } from '@arco-design/web-vue'
export default defineComponent({
  name: 'BaseInfo',
  props: {
    registerData: {
      type: Object,
      require: true
    }
  },
  emits: ['register', 'back'],
  setup(props, { emit }) {
    const formData = reactive<CardInfoType>({
      username: '',
      email: '',
      emailCode: '',
      password: ''
    })
    const data = reactive({})
    const loading = ref(false)
    let time = ref(60)
    let timer: NodeJS.Timer
    const loginState = computed(() => inject('loginState'))
    watch(loginState, () => {
    })
    const setTime = () => {
      timer = setInterval(() => {
        if (time.value > 0 && time.value <= 60) {
          time.value --
          if (time.value === 0) {
            clearInterval(timer)
            time.value = 60
          }
        }
      }, 1000)
    }
    const send = () => {
      setTime()
      post({
        url: sendEmail,
        data: {
          email: formData.email
        },
        headers: {
          'Content-Type': 'multipart/form-data; charset=UTF-8'
        }
      }).then(res => {
        if (res.code === 200) {
          Message.success('发送成功,请注意查收')
        }
      }).catch(e =>  Message.error(e.message))
    }
    const onRegister = () => {
      console.log(formData)
      if (formData.username !== '' && formData.email !== '' && formData.emailCode !== '' && formData.password !== '') {
        loading.value = true
        post({
        url: register,
        data: formData,
        headers: {
          'Content-Type': 'multipart/form-data; charset=UTF-8'
        }
      })
        .then(() => {
          Message.success('注册成功，请登录')
          emit('register')
        })
        .catch((error) => {
          Message.error(error.message)
        }).finally(() => {
          loading.value = false
        })
      } else {
        Message.error('该数据均为必填项，请先填写内容')
      }

    }
    const onBack = () => {
      emit('back', 1)
    }
    onMounted(() => {
      Object.assign(formData, props.registerData)
      console.log(formData, 'mo')
    })
    return {
      formData,
      loading,
      time,
      onRegister,
      onBack,
      send
    }
  }
})
</script>


<style scoped lang="less">
.card-info {
  .form-wrapper {
    height: 400px;
    padding: 0 25px;
    // width: 90%;
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
      width: 30%;
    }
  }
}
 @media screen and(max-width: 966px) {
    .card-info {
      background-image: url('../../assets/img_login_mobile_bg_01.jpg');
      background-size: cover;
      .form-wrapper {
        width: 95% !important;
        padding: 0 !important;
      }
    }
  }
</style>