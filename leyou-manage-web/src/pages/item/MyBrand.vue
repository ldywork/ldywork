<template>
  <div>
    <v-layout class="px-3 pb-2">
      <v-flex xs2>
        <v-btn color="info">新增品牌</v-btn>
      </v-flex>
      <v-spacer/>
      <v-flex xs4>
        <v-text-field lable="搜索" hide-details append-icon="search" v-model="key"/>
      </v-flex>
    </v-layout>
    <v-data-table
      :headers="headers"
      :items="brands"
      :pagination.sync="pagination"
      :total-items="totalBrands"
      :loading="loading"
      class="elevation-1"
    >
      <template slot="items" slot-scope="props">
        <td class="text-xs-center">{{ props.item.id }}</td>
        <td class="text-xs-center">{{ props.item.name }}</td>
        <td class="text-xs-center">
          <img :src="props.item.name" alt />
        </td>
        <td class="text-xs-center">{{ props.item.letter }}</td>
        <td class="text-xs-center">
          <v-btn color="info">
            <v-icon>edit</v-icon>
          </v-btn>
          <v-btn color="error">
            <v-icon>delete</v-icon>
          </v-btn>
        </td>
      </template>
      >
    </v-data-table>
  </div>
</template>

<script>
export default {
  name: "MyBrand",
  data() {
    return {
      headers: [
        { text: "品牌id", align: "center", sortable: true, value: "id" },
        { text: "品牌名称", align: "center", sortable: true, value: "name" },
        { text: "品牌LOGO", align: "center", sortable: true, value: "image" },
        {
          text: "品牌首字母",
          align: "center",
          sortable: true,
          value: "letter" 
        },
        { text: "操作", align: "center", sortable: true }
      ],
      brands: [],
      pagination: {},
      totalBrands: 0,
      loading: false,
      key: "",
    };
  },
  created() {
    this.brands = [
      { id: 123, name: "老米1", image: "1.jpg", letter: "O" },
      { id: 234, name: "老米2", image: "12.jpg", letter: "q" },
      { id: 345, name: "老米3", image: "13.jpg", letter: "a" },
      { id: 456, name: "老米4", image: "14.jpg", letter: "s" }
    ];
    this.totalBrands = 15;
    //查询后台
    this.loadBrands();
  },
  watch:{
    key(){
      this.pagination.page = "";
    },
    pagination:{
      deep: true,
      handler(){
        this.loadBrands();
      }
    }
  },
  methods:{
    loadBrands(){
      this.loading = true;
      this.$http.get("/item/brand/page",{
        params:{
          page: this.pagination.page,
          rows: this.pagination.rowsPerpage,
          sortBy: this.pagination.sortBy,
          desc: this.pagination.descending,
          key: this.key,
        }
      }).then(resp =>{
        this.brands = resp.data.items;
        this.totalBrands = resp.data.total;
        this.loading = false;
      })
    }
  }
};
</script>

<style scoped>
</style>
