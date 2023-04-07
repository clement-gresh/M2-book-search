<template>
    <TopMenu />
    <el-container style="margin: 4%;">
        <el-row>
        <el-col
        v-for="(item, index) in books"
        :key="item.id"
        :span="3"
        :offset="index % 6 === 0 ? 0 : 1"
        >
            <el-card  style="height: 100%; width: auto;">
            <img
                :src="item.image"
                class="image"
            />
            <div style="padding: auto">
                <p>{{ item.title }}</p>
                <div class="bottom">
                <el-button style="width: 50%;" type="primary" plain class="button" @click="goContent(item.id)">More</el-button>
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
    id:number|string;
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
        })
        .catch(error => {
          console.log(error);
        });
  
      watchEffect(() => {
        console.log('books changed', books.value);
      });

      const goContent = (id:number|string) => {
        router.push({
            name: 'content',
            query: {
            id:id
            },
        });
        };
  
      return {
        inputValue,
        activeIndex,
        router,
        books,
        goContent
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
  