<template>
<TopMenu />

<el-container style="margin: 2%;">
    <div>
        <el-row >
            <el-col :span="2">
            </el-col>
            <el-col :span="16">
                <el-button type="primary" plain @click="goBack">Back</el-button>
            </el-col>
            <el-col :span="6">
            </el-col>
        </el-row>

        <el-row>
            <el-col :span="8">
            </el-col>
            <el-col :span="4">
                <img :src="book.image">
               
            </el-col>
            <el-col :span="4">
                <div v-for="author in book.authors" >
                    <h2>{{ book.title }}</h2>
                    <h5>{{ author.name }}</h5>
                    <h5>{{ author.birth_year }} - {{ author.death_year }}</h5>
                    <a :href="book.text" target="_blank" style="margin-top: 50vh;">Read</a>
                </div>
            </el-col>
            <el-col :span="8">
            </el-col>
        </el-row>
        <el-row>
            <h4>Related Books</h4>
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
import { resolvePackageData } from 'vite';

interface Author {
  name: string;
  birth_year: string;
  death_year: string;
}

interface Book {
  id:number;
  title: string;
  image: string;
  text:string;
  //authors: {name: string,birth_year: string,death_year:string}[]
  authors: Author[];
}

export default defineComponent({
    setup() {
        const route = useRoute();
        const router = useRouter();
        const routeQuery = reactive(route.query);
        const idBook = ref(routeQuery.id);
        const book = reactive<Book>({
            id: 0,
            title: "",
            image: "",
            text: "",
            authors: [],
        });

        function goBack() {
            router.back();
        }
        const relatedBooks = ref([] as Book[]);
        //const relatedBooks = ref<Book[]>([]);
        //const book = ref<Book>({ id: 0, title: '', image: '', text: '', authors: [] });
        //value.authors[0].name
        console.log("skip to Content!");
        const goContent = (id) => {
            router.push({
                name: 'content',
                query: {
                id:id
                },
            });
        };
        onMounted(async () => {
            const response = await axios.get(`http://localhost:8080/getbook/${idBook.value}`);
            Object.assign(book, response.data);
            console.log("im in content page, book.value="+book.authors);

            const responseRB = await axios.get(`http://localhost:8080/suggestions/${idBook.value}`);
            relatedBooks.value = responseRB.data;
            //Object.assign(book, response.data);
            console.log("im in content page, book.value="+book.authors);

        });
        return {
            book,
            relatedBooks,
            goContent,
            goBack
        };
    
    },
    name: 'Content',
    components: {
        TopMenu
    }

})
</script>

