import { defineStore } from 'pinia'
import { UserState } from '../types'
import layoutStore from '../index'

import Avatar from '@/assets/img_avatar.gif'
import Cookies from 'js-cookie'
import { USER_INFO_KEY, USER_TOKEN_KEY } from '../keys'

const defaultAvatar = Avatar

const userInfo: UserState = JSON.parse(localStorage.getItem(USER_INFO_KEY) || '{}')

const useUserStore = defineStore('user', {
  state: () => {
    return {
      userId: userInfo.userId || null,
      roleId: userInfo.roleId || null,
      role: userInfo.role || null,
      token: userInfo.token || '' || undefined,
      username: userInfo.username || '',
      nickname: userInfo.nickname || '',
      avatar: userInfo.avatar || defaultAvatar,
      phone: userInfo.phone || null,
      email: userInfo.email || null,
      bankCard: userInfo.bankCard || null,
      idCard: userInfo.idCard || null,
      gender: userInfo.gender || 0,
      workingState: userInfo.workingState || 0,
      realName: userInfo.realName || null,
      userInfoId: userInfo.userInfoId || null,
      age: userInfo.age || 0,
      ctime: userInfo.ctime || null,
      creditStatus: userInfo.creditStatus || 0,
      overdueNumber: userInfo.overdueNumber || 0
    }
  },
  actions: {
    saveUser(userInfo: UserState) {
      return new Promise<void>((res) => {
        this.userId = userInfo.userId
        this.roleId = userInfo.roleId
        this.username = userInfo.username
        this.nickname = userInfo.nickname
        this.avatar = userInfo.avatar || defaultAvatar
        this.role = userInfo.role
        this.phone = userInfo.phone
        this.email = userInfo.email
        this.bankCard = userInfo.bankCard
        this.idCard = userInfo.idCard
        this.gender = userInfo.gender
        this.workingState = userInfo.workingState
        this.realName = userInfo.realName
        this.userInfoId = userInfo.userInfoId
        this.age = userInfo.age
        this.ctime = userInfo.ctime
        this.creditStatus = userInfo.creditStatus
        this.overdueNumber = userInfo.overdueNumber
        this.token = Cookies.get(USER_TOKEN_KEY)
        localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo))
        res()
      })
    },
    changeNickName(newNickName: string) {
      this.nickname = newNickName
    },
    logout() {
      return new Promise<void>((resolve) => {
        this.userId = ''
        this.avatar = ''
        this.roleId = ''
        this.role = ''
        this.username = ''
        this.nickname = ''
        this.token = ''
        this.phone = ''
        this.email = ''
        this.bankCard = ''
        this.idCard = ''
        this.gender = 0
        this.workingState = 0
        this.realName = ''
        this.userInfoId = ''
        this.age = 0
        this.ctime = ''
        this.creditStatus = 0
        this.overdueNumber = 0
        Cookies.remove(USER_TOKEN_KEY)
        localStorage.clear()
        layoutStore.reset()
        resolve()
      })
    },
  },
  getters: {
    isAdmin(): boolean {
      return this.role === 'admin'
    }
  }
})

export default useUserStore
