interface BaseProduct {
  price: number,
  stock: number,
  ctime: number,
  startTime: number,
  endTime: number,
  productDescribe: string,
  type: string,
}

interface FinancialProduct extends BaseProduct {
  financialProductId: string,
  financialProductName: string,
  rate: number,
}

interface LoanProduct extends BaseProduct {
  loanProductId: string,
  loanProductName: string,
  interest: number,
}