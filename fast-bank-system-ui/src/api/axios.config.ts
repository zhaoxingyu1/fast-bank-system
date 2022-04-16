import Axios, { AxiosResponse } from 'axios'
import qs from 'qs'
import Cookies from 'js-cookie'
import { USER_TOKEN_KEY } from '../store/keys'

export const baseURL = 'http://localhost:10010'
// export const baseURL = 'http://192.168.137.226:10010'

export const CONTENT_TYPE = 'Content-Type'

export const FORM_URLENCODED = 'application/x-www-form-urlencoded; charset=UTF-8'

export const APPLICATION_JSON = 'application/json; charset=UTF-8'

export const APPLICATION = 'multipart/form-data; charset=UTF-8'

export const TEXT_PLAIN = 'text/plain; charset=UTF-8'


const service = Axios.create({
  baseURL: '/api',
  timeout: 10 * 60 * 1000,
  withCredentials: true, // 跨域请求时发送cookie
})

service.interceptors.request.use(
  (config) => {
    !config.headers && (config.headers = {})
    if (!config.headers[CONTENT_TYPE]) {
      config.headers[CONTENT_TYPE] = APPLICATION_JSON
    }
    if (config.headers[CONTENT_TYPE] === APPLICATION) {
      const fd = new FormData()
      if (Object.prototype.toString.call(config.data).indexOf('Object') !== -1) {
        for (const key in config.data) {
          fd.set(key, config.data[key])
        }
        config.data = fd
      }
    }
    if (config.headers[CONTENT_TYPE] === FORM_URLENCODED) {
      config.data = qs.stringify(config.data)
    }
    const token = Cookies.get(USER_TOKEN_KEY)
    if (token) {
      config.headers['token'] = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error.response)
  }
)

service.interceptors.response.use(
  (response: AxiosResponse): AxiosResponse => {
    if (response.status === 200) {
      if (response.config.url === '/user/login') {
        const token = response.headers.token
        Cookies.set('token', token)
      }
      return response
    } else {
      throw new Error(response.status.toString())
    }
  },
  (error) => {
    if (import.meta.env.MODE === 'development') {
      console.log(error)
    }
    return Promise.reject({ code: 500, msg: '服务器异常，请稍后重试…' })
  }
)

export default service
