<template>
    <TopMenu />
    <el-container style="margin: 10%;">
        <el-row>
        <el-col
        v-for="(item, index) in books"
        :key="item.id"
        :span="4"
        :offset="index % 5 === 0 ? 0 : 1"
        >
            <el-card body-style="{  }" style="height: 100%; width: auto;">
            <img
                :src="item.image"
                class="image"
            />
            <div style="padding: auto">
                <span>{{ item.title }}</span>
                <div class="bottom">
                <el-button text class="button">More</el-button>
                </div>
            </div>
            </el-card>
        </el-col>
        </el-row>
    </el-container>
  </template>
  
  <script lang="ts">
  import { defineComponent, ref, watchEffect} from 'vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios';
  import TopMenu from '../components/IndexView.vue'

  interface Book {
    id:number;
    title: string;
    image: string;
  }
  
  export default defineComponent({
    setup() {
      const inputValue = ref('')
      const activeIndex = ref('1')
      const router = useRouter()
      const books = ref([] as Book[]);
  
      axios.get('http://localhost:8080/gettopbooks/')
        .then(response => {
          books.value = response.data;
          //console.log("response:" + books.value[0].title);
        })
        .catch(error => {
          console.log(error);
        });
  
      watchEffect(() => {
        console.log('books changed', books.value);
      });
  
      return {
        inputValue,
        activeIndex,
        router,
        books
      }
    },

    name: 'Home',
    components: {
        TopMenu
    }
  })
  </script>
  
  <style scoped>
  .flex-grow {
    flex-grow: 1;
  }
  .layout-container-demo .el-header {
    position: relative;
    background-color: var(--el-color-primary-light-7);
    color: var(--el-text-color-primary);
  }
  .layout-container-demo .el-menu {
    border-right-width: 0;
    padding: 0%;
  }
  .layout-container-demo .el-main {
    padding: 0;
  }
  .pageName {
    font-size: 50px;
    font-weight: 700;
    color: var(--el-color-primary-light-7);
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  }

  .time {
  font-size: 12px;
  color: #999;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.button {
  padding: 0;
  min-height: auto;
}

.image {
  width: 100%;
  display: block;
}
  </style>
  