



<template>
  <TopMenu />
  <h1>Search Results for "{{ bookSearch }}"</h1>
  <el-container>
    <div >
      <el-row>
      <el-col
      v-for="(item, index) in books"
      :key="item.id"
      :span="7"
      :offset="index % 5 === 0 ? 0 : 1"
      >
          <el-card body-style="{  }" style="height: 100%; width: auto;">
          <img
              :src="item.image"
              class="image"
          />
          <div style="padding: auto">
            <span><router-link :to="{ name: 'post', params: { id: item.id } }">{{ item.title }}</router-link></span>
              <div class="bottom">
              <el-button text class="button">Operating</el-button>
              </div>
          </div>
          </el-card>
      </el-col>
      </el-row>
    </div>
  </el-container>
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
    //const reload = inject('reload'); // 注入函数
    const route = useRoute();
    const routeQuery = reactive(route.query);
    //const bookSearch = toRefs(routeQuery).bookSearch;
    const bookSearch = ref(routeQuery.bookSearch);
    const radioSelect = ref(routeQuery.radioSelect);
    const formatSelect = ref(routeQuery.formatSelect);
    const books = ref<Book[]>([]);
    
    
    const getBooks = async () => {
      
      console.log(routeQuery);
      try {
        console.log("je sais radioSelect="+radioSelect.value);
        console.log("je sais formatSelect="+formatSelect.value);
        if(String(radioSelect.value) === "1"){

          if(String(formatSelect.value) === "2"){
            console.log("refBookSearch="+bookSearch.value);
            //const response = await axios.get(`http://localhost:8080/searchbyregex?regex=`+JSON.stringify(bookSearch));
            
            const response = await axios({
              method: 'post',
              //url: 'http://localhost:8080/searchbyregex?regex='+JSON.stringify(bookSearch),
              url: 'http://localhost:8080/searchbyregex/',
              headers: {
                'Content-Type': 'application/json'
              },
              data: {
                regex: bookSearch.value
              }
            });
            books.value = response.data;
            //console.log("book.values regex = " + books.value);
          }else{
            const response = await axios.get(`http://localhost:8080/searchbycontent/${bookSearch.value}`);
            books.value = response.data;
            //console.log("book.values = "+books.value);
          }
          
        }else if(String(radioSelect.value) === "2"){
          const response = await axios.get(`http://localhost:8080/searchbyauthor/${bookSearch.value}`);
          books.value = response.data;
          //console.log("im in else if radio2, radioSelect="+radioSelect);
        }else{
          const response = await axios.get(`http://localhost:8080/searchbytitle/${bookSearch.value}`);
          books.value = response.data;
          //console.log("im in else if radio3, radioSelect="+radioSelect);  
        }
        console.log("In page book now")
        
      } catch (error) {
        console.error(error);
        // 显示错误消息
        alert('An error occurred while loading search results.');
      }

      console.log("getBooks! bookeSearch="+bookSearch+"")
    };
    
    onMounted(() => {

      getBooks();
      
    });

    watch(() => route.query, (newVal) => {
      console.log('route.query changed:', newVal);
      Object.assign(routeQuery, newVal);
      radioSelect.value = routeQuery.radioSelect;
      formatSelect.value = routeQuery.formatSelect;
      bookSearch.value = routeQuery.bookSearch;
      getBooks();
    }, { deep: true });

    
    
    const goBack = () => {
      history.back();
    };
    
    return {
      books,
      goBack,
      bookSearch,
      formatSelect,
      radioSelect
    }
  },
  name: 'Book',
  components: {
    TopMenu
  }
});
</script>
<style>
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

  
