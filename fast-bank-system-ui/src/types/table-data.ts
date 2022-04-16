interface OrderTableData {
  orderId: string,
  userId: string,
  productId: string,
  productType: string,
  state: "FULFILLED" | "REJECTED" | "PENDING",
  ctime: number,
}