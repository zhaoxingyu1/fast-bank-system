interface BaseInfoType {
  realName: string,
  age: number,
  gender: number,
  idCard: string,
  phone: string
}

interface CardInfoType {
  username: string,
  email: string,
  emailCode: string,
  password: string
}

export interface RegisterDateType extends BaseInfoType, CardInfoType{
  role: string
}