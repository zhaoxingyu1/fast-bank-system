import { Directive, DirectiveBinding } from "vue";
import moment from "moment";

const date: Directive = {
  mounted: parseDate,
  updated: parseDate
}

function parseDate(el: any, binding: DirectiveBinding) {
  el.textContent = moment(binding.value as number).format(binding.arg)
}

export default date
