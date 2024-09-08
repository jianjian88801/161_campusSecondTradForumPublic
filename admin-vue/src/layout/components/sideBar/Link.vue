<template>
  <component :is="type" v-bind="linkProps(to)">
    <slot/>
  </component>
</template>

<script>
import { isExternal } from '@/utils/validate'

/**
 * vue动态组件，路由链接展示router-link 外部链接展示a标签
 */
export default {
  props: {
    to: {
      type: [String, Object],
      required: true
    }
  },
  computed: {

    isExternal() {
      // console.log(this.to)
      return isExternal(this.to)
    },
    type() {

      if (this.isExternal) {
        return 'a'
      }
      return 'router-link'
    }
  },
  methods: {
    linkProps(to) {
      if (this.isExternal) {
        return {
          href: to,
          target: '_blank',
          rel: 'noopener'
        }
      }
      return {
        to: to
      }
    }
  }
}
</script>
