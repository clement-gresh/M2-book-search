<template>
  <el-container class="layout-container-demo">
    <el-header style="text-align: right; font-size: 12px;">
      <el-menu
        class="top-menu"
        mode="horizontal"
        :ellipsis="false"
      >
      <el-menu-item index="0" @click="goHome" style="padding-left: 10vh;"><text class="pageName">Book Search Engine</text></el-menu-item>
    <el-input
      v-model="bookSearch"
      class="w-50 m-2"
      size="large"
      :placeholder="bookSearchPlaceholder"
      :suffix-icon="Search"
      style="width:35vh;height: 5vh;margin-left: 1vh;margin-top: 1vh;"
      @keyup.enter="handleIconClick"
      :allow-spaces="true"
    />
    <el-button @click="handleIconClick" style="margin-top: 1.3vh; margin-left: 2vh;">Search</el-button>
    <div class="selectType">
      <el-radio-group v-model="state.radioSelect" class="ml-4" style="margin-left: 1vh; margin-top:1.5vh;">
        <el-radio :label="1" size="small">Content</el-radio>
        <el-radio :label="2" size="small">Author</el-radio>
        <el-radio :label="3" size="small">Title</el-radio>
      </el-radio-group>
      <el-radio-group v-model="state.formatSelect" class="ml-4" style="margin-left: 3vh; margin-top: 1.5vh;">
        <el-radio :label="1" size="small" :disabled="state.radio2Disabled[0]">Search</el-radio>
        <el-radio :label="2" size="small" :disabled="state.radio2Disabled[1]">Advance Search</el-radio>
      </el-radio-group>
    </div>
      </el-menu>
    </el-header>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, ref, watch, reactive} from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
interface Book {
  id:number;
  title: string;
}

export default defineComponent({
  setup() {
    const inputValue = ref('')
    const activeIndex = ref('1')
    const router = useRouter()
    const books = ref([] as Book[]);
    const bookSearch = ref('');
    const bookSearchPlaceholder = ref('ex: year-parent-govern for multiple words');


    const state = reactive({
      radioSelect: 1,
      formatSelect: 1,
      radio2Disabled: [false, false, false],
      radio1To2Map: {
        2: [1, 2], 
        3: [1,2], 
      },
    });

    watch(() => state.radioSelect, (value) => {
      const disabledOptions = state.radio1To2Map[value] || [];
      state.radio2Disabled = state.radio2Disabled.map((_, index) => {
        return disabledOptions.includes(index + 1);
      });
    });


    const handleSelect = (key: string, keyPath: string[]) => {
      console.log(key, keyPath)
    }

    const handleIconClick = () => {

      if (!bookSearch.value) {
        return;
      }
      router.push({
        name: 'book',
        query: {
          bookSearch: bookSearch.value.replace(/-/g, ' '),
          radioSelect: state.radioSelect,
          formatSelect: state.formatSelect
        },
      });
    };

    const   goHome = () =>{
      router.push('/')
    };

    watch(bookSearch, () => {
      bookSearchPlaceholder.value = bookSearch.value;
    });

    return {
      inputValue,
      activeIndex,
      router,
      books,
      bookSearch,
      handleIconClick,
      Search,
      bookSearchPlaceholder,
      goHome,
      state
    }
  },
  mounted() {
    if (this.$route.query.bookSearch) {
      this.bookSearch = this.$route.query.bookSearch;
      this.bookSearchPlaceholder = this.$route.query.bookSearch;
    }
  },
  name: 'TopMenu'
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
  font-size: 40px;
  font-weight: 700;
  color: var(--el-color-primary-light-7);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
</style>
