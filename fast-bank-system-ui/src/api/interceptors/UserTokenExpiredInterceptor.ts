import { StoreType } from './../../types/store'
import { AxiosResponse } from 'axios'
import { Message } from '@arco-design/web-vue'

export default function (response: AxiosResponse, store: StoreType): AxiosResponse {
  if (response.status === 511) {
    console.log(123)
    Message.error('当前用户登录已过期或未登录，请重新登录')
    setTimeout(() => {
      ;(store as any).onLogout && (store as any).onLogout()
    }, 1500)
  }
  return response
}
