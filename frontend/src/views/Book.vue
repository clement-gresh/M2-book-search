<template>
  <TopMenu />
  <el-container style="margin: 2%;">
    <div >
      <el-row >
        <el-col :span="2">
        </el-col>
        <el-col :span="16">
            <el-button type="primary" plain @click="goBack">Back</el-button>
        </el-col>
        <el-col :span="6">
        </el-col>
      </el-row>
      <el-row >
        <el-col :span="8">
        </el-col>
        <el-col :span="8">
            <h2>Search Resultats: {{ bookSearch }}</h2>
        </el-col>
        <el-col :span="8">
        </el-col>
      </el-row>
      <el-row >
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
            <div>
              <div class="bottom">
                <el-button style="width: 50%;" type="primary" plain class="button" @click="goContent(item.id)">More</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-row>
            <h3 style="margin-top: 5%;">Related Books</h3>
      </el-row>
      <el-row>
        <el-col
        v-for="(item, index) in relatedBooks"
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
              <span>{{ item.title }}</span>
              <div class="bottom">
              <el-button style="width: 50%;" type="primary" plain class="button" @click="goContent(item.id)">More</el-button>
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
import { useRoute,useRouter } from 'vue-router';

interface Book {
  id:number;
  title: string;
  image: string;
}

export default defineComponent({
  setup() {
    const route = useRoute();
    const router = useRouter();
    const routeQuery = reactive(route.query);
    const bookSearch = ref(routeQuery.bookSearch);
    const radioSelect = ref(routeQuery.radioSelect);
    const formatSelect = ref(routeQuery.formatSelect);
    const books = ref<Book[]>([]);
    const relatedBooks = ref([] as Book[]);
    const bookIds = ref();
    
    function goBack() {
            router.back();
    }
    
    const getBooks = async () => {
      
      console.log(routeQuery);
      try {
        console.log("je sais radioSelect="+radioSelect.value);
        console.log("je sais formatSelect="+formatSelect.value);
        if(String(radioSelect.value) === "1"){
          if(String(formatSelect.value) === "2"){
            console.log("refBookSearch="+bookSearch.value);
            const response = await axios({
              method: 'post',
              url: 'http://localhost:8080/searchbyregex/',
              headers: {
                'Content-Type': 'application/json'
              },
              data: {
                regex: bookSearch.value
              }
            });
            books.value = response.data;
          }else{
            const response = await axios.get(`http://localhost:8080/searchbycontent/${bookSearch.value}`);
            books.value = response.data;
          }
          
        }else if(String(radioSelect.value) === "2"){
          const response = await axios.get(`http://localhost:8080/searchbyauthor/${bookSearch.value}`);
          books.value = response.data;
        }else{
          const response = await axios.get(`http://localhost:8080/searchbytitle/${bookSearch.value}`);
          books.value = response.data;
        }
        console.log("In page book now")
      } catch (error) {
        console.error(error);
      }


      if (books.value.length > 0) {
        const firstThreeBooks = books.value.slice(0, 3);
        bookIds.value = firstThreeBooks.map(book => book.id).join(' ');
      }
      const responseRB = await axios.get(`http://localhost:8080/suggestionsfromresults/${bookIds.value}`);
      relatedBooks.value = responseRB.data;

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

    
    
    const goContent = (id) => {
      router.push({
        name: 'content',
        query: {
          id:id
        },
      });
    };
    
    return {
      books,
      goContent,
      bookSearch,
      formatSelect,
      radioSelect,
      relatedBooks,
      goBack
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

  
