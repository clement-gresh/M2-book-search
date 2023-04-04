<template>
<TopMenu />


</template>


<script lang="ts">
import { ref, onMounted, defineComponent,watch,reactive,inject,toRefs } from 'vue';
import axios from 'axios';
import TopMenu from '../components/IndexView.vue'
import { useRoute } from 'vue-router';
import { resolvePackageData } from 'vite';

interface Book {
  id:number;
  title: string;
  image: string;
}

export default defineComponent({
    setup() {
    
    
    }

})
</script>

