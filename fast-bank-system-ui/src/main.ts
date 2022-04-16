import { createApp } from 'vue'
import App from './App.vue'
import LayoutStore from './layouts'
import './styles/index.css'
import router from './router'
import { DeviceType } from './types/store'
import './utils/router'
import ArcoVue from '@arco-design/web-vue';
import ArcoVueIcon from '@arco-design/web-vue/es/icon'
import '@arco-design/web-vue/dist/arco.css'
import pinia from './store/pinia'
import directives from "@/directives";

// import '../mock'


function getScreenType() {
  const width = document.body.clientWidth
  if (width <= 768) {
    return DeviceType.MOBILE
  } else if (width < 992 && width > 768) {
    return DeviceType.PAD
  } else if (width < 1200 && width >= 992) {
    return DeviceType.PC
  } else {
    return DeviceType.PC
  }
}

const app = createApp(App)
  .use(ArcoVueIcon)
  .use(pinia)
  .use(router)
  .use(ArcoVue)
  .use(LayoutStore, {
    state: {
      device: getScreenType(),
    },
    actions: {
      onPersonalCenter() {
        router.push('/personal')
      },
      onLogout() {
        router.replace({ path: '/login' }).then(() => {
          window.location.reload()
        })
      },
    },
  })

router.isReady().then(() => {
  app.mount('#app')
})

for (const name of Object.keys(directives)) {
  console.log(name)
  app.directive(name, directives[name])
}
