import { defineStore } from 'pinia'

const useAppiontStore = defineStore('appiont', {
  state: () => {
    return {
      appiontIdList: ['']
    }
  },
  actions: {
    saveAppiont(appiont: Array<string>) {
      return new Promise<void>((res) => {
        this.appiontIdList = appiont
        res()
      })
    },
    filterAppiontId(id:string) {
      return this.appiontIdList.filter(it => it === id)?.length !== 0
    }
  }
})

export default useAppiontStore