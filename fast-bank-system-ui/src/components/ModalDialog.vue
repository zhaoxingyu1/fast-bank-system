<template>
  <a-modal v-model:visible="showModal" :title="title" class="modal-dialog-wrapper" :width="width">
    <Scrollbar wrap-class="modal-dialog__wrap">
      <slot name="content"></slot>
    </Scrollbar>
    <template #footer>
      <a-space>
        <a-button @click="onCancel">{{ backText }}</a-button>
        <a-button v-if="showConfirm" type="primary" @click="onConfirm">{{ confirmText }} </a-button>
      </a-space>
    </template>
  </a-modal>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from 'vue'
import { useLayoutStore } from '@/layouts'
import { number } from 'echarts/core'

export default defineComponent({
  name: 'ModalDialog',
  props: {
    title: {
      type: String,
      default: '操作',
    },
    contentHeight: {
      type: String,
      default: '30vh',
    },
    showConfirm: {
      type: Boolean,
      default: true,
    },
    confirmText: {
      type: String,
      default: '确定'
    },
    backText: {
      type: String,
      default: '取消',
    },
    width: {
      type: String,
      default: '520px'
    }
  },
  emits: ['confirm', 'cancel'],
  setup(props, { emit }) {
    const showModal = ref(false)

    function toggle() {
      showModal.value = !showModal.value
      return Promise.resolve(showModal.value)
    }

    function show() {
      showModal.value = true
      return Promise.resolve(true)
    }

    function close() {
      showModal.value = false
      return Promise.resolve(false)
    }

    function onConfirm() {
      emit('confirm')
    }

    function onCancel() {
      showModal.value = false
      emit('cancel')
    }

    return {
      showModal,
      toggle,
      show,
      close,
      onConfirm,
      onCancel,
    }
  },
})
</script>
<style>
.modal-dialog__wrap {
  max-height: 80vh;
}
</style>
