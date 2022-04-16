<template>
  <div class="login">
    <a-card class="form-wrapper" :body-style="{ padding: state.device !== 'pc' ? '7px' :'20px' }" :bordered="false">
      <div v-if="isLogin" class="login-form">
        <div class="title">账号登录</div>
        <a-form>
          <div class="item-wrapper mt-4">
            <a-form-item hide-label="true" :rules="[{ required: true, message: '用户名不能为空' }]">
              <a-input v-model="username" placeholder="请输入用户名" allow-clear size="large">
                <template #prefix>
                  <icon-mobile />
                </template>
              </a-input>
            </a-form-item>
          </div>
          <div class="item-wrapper mt-1">
            <a-form-item hide-label="true" :rules="[{required: true, message: '密码不能为空'}]">
              <a-input-password v-model="password" placeholder="请输入密码" allow-clear size="large">
                <template #prefix>
                  <icon-lock />
                </template>
              </a-input-password>
            </a-form-item>
          </div>
        </a-form>
        <div class="flex-sub"></div>
        <div class="mt-4">
          <a-button type="primary" class="loginButton" :loading="loading" @click="onLogin">
            登录
          </a-button>
        </div>
        <div class="my-width flex-sub mt-4 mb-8">
          <div class="flex" :class="state.device !== 'pc' ? 'justify-between' : 'justify-end'">
            <a-link :underline="false" type="primary" v-if="state.device !== 'pc' " @click="onChange">我要注册</a-link>
            <a-link :underline="false" type="primary" @click="forgetPassword">忘记密码？</a-link>
          </div>
        </div>
        <a-divider orientation="center">第三方登录</a-divider>
        <div class="text-center text-lg">
          <icon-alipay-circle />
          <icon-github class="mr-6 ml-6" />
          <icon-weibo-circle-fill />
        </div>
      </div>
      <ForgetPassword v-else @changePassword="isLogin = true" />
    </a-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { post, Response, get } from '@/api/http'
import { login, selectUserById, retrievePassword } from '@/api/url'
import { Message } from '@arco-design/web-vue'
import { UserState } from '@/store/types'
import store from '../../../store'
import { useLayoutStore } from '@/layouts/index'
import { LayoutMode } from '@/types/store'
import useUserStore from '@/store/modules/user'
import { USER_TOKEN_KEY } from '@/store/keys'
import Cookies from 'js-cookie'
import ForgetPassword from './forgetPassword.vue'
export default defineComponent({
  name: 'LoginForm',
  components: {
    ForgetPassword
  },
  emits: ['changeState'],
  setup(props, { emit }) {
    const username = ref<string>()
    const password = ref<string>()
    const loading = ref(false)
    const router = useRouter()
    const route = useRoute()
    const userStore = useUserStore()
    const layoutstore = useLayoutStore()
    const isLogin = ref<Boolean>(true)
    let token = ref<string>()
    const loginIn =async () => {
      loading.value = true
      return post({
        url: login,
        data: {
          username: username.value,
          password: password.value,
        },
        headers: {
          'Content-Type': 'multipart/form-data; charset=UTF-8'
        }
      }).then((res) => {
        if (res.code === 200) {
          token.value = Cookies.get(USER_TOKEN_KEY)
        }
      }).catch((error) => {
        Message.error(error.message)
      })
    }
    const getInfo = () => {
      get({
        url: selectUserById
      }).then(({ data }: Response) => {
        const userInfo = reactive({...data.userInfo, 'userId': data.userId, 'username': data.username, 'role': data.userRole.role, 'roleId': data.roleId })
        userStore.saveUser(userInfo as UserState).then(() => {
          layoutstore.changeLayoutMode(data?.userRole.role === 'admin' ? LayoutMode.LTR : LayoutMode.TTB)
          router
            .replace({
              path: route.query.redirect ? (route.query.redirect as string) : '/',
            })
            .then(() => {
              Message.success('登录成功，欢迎：' + username.value)
            })
        })
      }).catch(e => Message.error(e.message)).finally(() => loading.value = false)
    }
    const forgetPassword = () => {
      isLogin.value = false
    }
    const onLogin = async() => {
      await loginIn()
       getInfo()
    }
    const onChange = () => { emit('changeState') }
    return {
      username,
      password,
      loading,
      state: store?.state,
      onLogin,
      onChange,
      isLogin,
      forgetPassword
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
    .login-form .title {
      font-size: 25px;
      font-weight: bold;
      margin-bottom: 20px;
    }
    .login-form .loginButton {
      width: 100%;
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
