import { Directive } from "vue";
import { CustomDirective } from "@/types/custom-directive";
import date from "@/directives/date";


const role: Directive = {
  mounted(el, binding) {
    if (binding.arg != binding.value) {
      el.remove()
    }
  }
}

export default {
  role,
  date
} as CustomDirective