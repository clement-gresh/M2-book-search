
<template>
  <div id="app">
    <router-view v-if="isActive"/>
  </div>
</template>
<script>
export default {
  data () {
    return {
      isActive: true
    }
  },
  provide () {
    return {
      reload: this.reload
    }
  },
  methods: {
    reload () {
      this.isActive = false
      this.$nextTick(function () {
        this.isActive = true
      })
    }
  }
}
</script>

