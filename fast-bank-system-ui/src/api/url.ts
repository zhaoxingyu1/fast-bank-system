import { baseURL } from './axios.config'

export const baseAddress = baseURL

// export const test = '/test'

export const login = '/user/login'

export const register = '/user/registerUser'

export const updateUserPassword = '/user/updateUserPassWd'

export const selectUserById = '/user/selectUserById'

export const deleteUserById = '/user/deleteUserById'

export const updateUserById = '/user/updateUserById'

export const updateUserInfo = '/user/updateUserInfo'

export const sendEmail = '/user/sendEmail'

export const selectAllUserOrByLikeName = '/user/admin/selectAllUserOrByLikeName'

export const selectUserInfoByCreditStatus = '/user/admin/selectUserInfoByCreditStatus'

export const updateUserCreditStatus = '/user/admin/updateUserCreditStatus'

export const selectAllByInfo = '/user/admin/selectAllByInfo'

export const userGetAppointmentProduct = '/product/userProduct/userGetAppointmentProduct'

export const selectAllOrByLikeNamePage = '/user/applicationRecord/selectAllOrByLikeNamePage'

export const selectByTime = '/user/applicationRecord/selectByTime'

export const getRiskControl = '/user/admin/getRiskControl'

export const insertOrUpdate = '/user/admin/insertOrUpdate'


export const getOrderByUser = '/order/getByUser'

export const getOrderAll = '/order/getAll'

export const getProductById = '/product/getbyid/'

export const getByProduct = '/order/getByProduct'

export const create = '/order/create'

export const seckill ='/order/seckill'

export const retrievePassword = '/user/retrievePassword'

export const openScriptControl = '/user/admin/openScriptControl'

export const closeScriptControl = '/user/admin/closeScriptControl'

export const userCount = '/user/admin/userCount'

export const getUserFlow = '/user/admin/getUserFlow'

export const oneYear = '/order/oneYear'

export const thisMonthSell = '/order/thisMonthSell'

export const thisMonthOrder = '/order/thisMonthOrder'

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $urlPath: Record<string, string>
  }
}


/****
 * {
 *    username:
 *    reditStatus:
 * }
 */