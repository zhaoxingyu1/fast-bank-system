import { Directive } from "vue";

export interface CustomDirective {
  [index: string]: Directive
}