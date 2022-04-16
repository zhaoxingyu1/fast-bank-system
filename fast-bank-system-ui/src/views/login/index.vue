<template>
  <div class="login-container" :style=" { 'flex-direction': loginState === 'login' ? 'row' : 'row-reverse' }">
    <a-card class="form-div" :class="state.device === 'pc' ? 'form-pc' : 'form-mobile'" body-style="display: flex">
      <LoginForm class="login" @changeState=" loginState = 'register'" v-if="(state.device === 'pc' ? true : loginState === 'login')"/>
      <RegisterForm class="register" :step="step" @changeStep="(value) => step = value" @changeState=" loginState = 'login' " v-if="(state.device === 'pc' ? true : loginState !== 'login')" />
    </a-card>

    <div class="switch" :class="loginState === 'login' ? 'switch1' : 'switch2'" v-if="state.device === 'pc'">
      <div v-if="loginState === 'login'">
       <a-typography>
          <a-typography-title>
            你好,
          </a-typography-title>
          <a-typography-title>
            我的朋友
          </a-typography-title>
          <a-typography-title :heading="6">
            输入你的个人资料，开始与我们的旅程！
          </a-typography-title>
        </a-typography>
        <a-button type="primary" @click="loginState = 'register'" style="margin-top: 20px">注 册</a-button>
      </div>
      <div v-else>
        <a-typography>
          <a-typography-title>
            欢迎回来
          </a-typography-title>
          <a-typography-title :heading="6">
            为了与我们保持联系，请用您的个人信息登录
          </a-typography-title>
        </a-typography>
        <a-button type="primary" @click=" changeState" style="margin-top: 20px">登 录</a-button>
        <div class="switch__circle"></div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
  import { defineComponent, provide, ref } from 'vue'
   import store from '../../store'
  import setting from '../../setting'
  import useAppInfo from '@/hooks/useAppInfo'
  import LoginForm from './components/loginForm.vue'
  import RegisterForm from './components/registerForm.vue'
  export default defineComponent({
    name: 'Login',
    components: {
      LoginForm, RegisterForm
    },
    setup() {
      const projectName = setting.projectName
      const { version } = useAppInfo()
      const loginState = ref('login')
      const step = ref(1)
      const changeState = () => {
        loginState.value = 'login'
        step.value = 1
      }
      return {
        projectName,
        version,
        step,
        loginState,
        state: store.state,
        changeState
      }
    }
  })
</script>

<style lang="less" scoped>
  @keyframes scale-to {
    0% {
      transform: scale(0.2, 0.2);
    }
    100% {
      transform: scale(1, 1);
    }
  }
  .login-container {
    overflow: hidden;
    height: 100vh;
    width: 100vw;
    font-size: 12px;
    transition: 1.25s;
    position: relative;
    .form-div {
      height: 440px;
      box-shadow: 0 0 10px #cfcfcf;
      border-radius: 10px;
    }
    .form-pc {
      width: 60%;
      position: absolute;
      top: 20%;
      left: 20%;
    }
    .form-mobile {
      width: 90%;
      margin: 30% auto;
    }
    .switch {
      width: 30%;
      height: 440px;
      z-index: 200;
      position: absolute;
      left: 50%;
      top: 20%;
      box-shadow: 0 0 10px #cfcfcf;
      border-radius: 10px;
      display: flex;
      justify-content: center;
      align-items: center;
      background-color: #fff;
    }
    .login {
      flex: 1;
      background-color: #fff;
      display: flex;
    }
    .register {
      flex: 1;
      background-color: #fff;
      display: flex;
    }
  }
  @media screen and(max-width: 966px) {
    .info {
      display: none !important;
    }
  }
  @keyframes to-login {
    0% { left: 20%; }
    100% { left: 50%; }
  }
  @keyframes to-register {
    0% { left: 50%; }
    100% { left: 20%; }
  }
  .switch1 {
    animation: to-login 2s ease;
    animation-fill-mode:forwards;
  }
  .switch2 {
    animation: to-register 2s ease;
    animation-fill-mode:forwards;
  }
</style>
