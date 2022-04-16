<template>
  <div class="login">
    <div class="title">忘记密码</div>
    <a-form>
      <div class="item-wrapper mt-4">
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
        <a-input-password v-model="formData.newPassword" placeholder="请输入密码" allow-clear size="large">
          <template #prepend>
            密码
          </template>
        </a-input-password>
      </a-form-item>
    </div>
    </a-form>
    <div class="flex-sub"></div>
    <div class="mt-8 flex justify-between">
      <a-button class="loginButton" :loading="loading" @click="onBack">
        返回
      </a-button>
      <a-button type="primary" class="loginButton" :loading="loading" @click="onLogin">
        确认修改
      </a-button>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, computed, watch, } from 'vue'
import { retrievePassword, sendEmail } from '@/api/url'
import { post } from '@/api/http'
import { Message } from '@arco-design/web-vue'

export default defineComponent({
  name: 'ForgetPassword',
  emits: ['changePassword'],
  setup(props, { emit }) {
    const loading = ref<Boolean>(false)
    const time = ref(60)
    const formData = reactive({})
    let timer: NodeJS.Timer
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
      let email
      Object.keys(formData).forEach((it) => {
        email = (formData as any)[it]
      })
      post({
        url: sendEmail,
        data: {
          email
        },
        headers: {
          'Content-Type': 'multipart/form-data; charset=UTF-8'
        }
      }).then(res => {
        if (res.code === 200) {
          Message.success('发送成功,请注意查收')
        }
      }).catch(e => Message.error(e.message))
    }
    const onLogin = () => {
      loading.value = true
      post({
        url: retrievePassword,
        data: formData,
        headers: {
          'Content-Type': 'multipart/form-data; charset=UTF-8'
        }
      }).then((res) => {
          console.log(res)
        if (res.code === 200) {
          loading.value = false
          emit('changePassword')
          Message.success('修改成功')
        }
      }).catch((error) => {
        console.log(error, 'e')
        Message.error(error.msg)
      })
    }
    const onBack = () => { emit('changePassword') }
    return {
      time,
      loading,
      formData,
      onLogin,
      onBack,
      send
    }
  }
})

</script>

<style scoped lang="less">
.login {
  .form-wrapper {
    height: 400px;
    padding: 0 25px;
    align-self: center;
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
    .loginButton {
      width: 30%;
    }
  }
}
 @media screen and(max-width: 966px) {
    .login {
      background-image: url('../../assets/img_login_mobile_bg_01.jpg');
      background-size: cover;
      .form-wrapper {
        width: 95% !important;
        padding: 0px !important;
      }
    }
  }
</style>
