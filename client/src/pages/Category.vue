<template>
    <div>
        <Categories />
        <div class="container">
            <div class="page-title container">
                <div class="d-flex">
                    <h1 class="col-12 text-center">Tài liệu {{ categoryName }} được xem nhiều</h1>
                </div>
            </div>
            <section class="contents-wrap" id="cards-list">
                <div class="row">
                    <div v-for="(card, index) in cards" :key="index" class='ncard col-md-6 col-lg-4 mb-5'>
                        <MainContent :cardContent="card" />
                    </div>
                </div>
            </section>
        </div>
    </div>
</template>

<script lang="ts">
import { defineAsyncComponent } from "vue";
import { useRoute } from "vue-router";
import Categories from "@/components/Categories/Index.vue";
import apiClient from "@/api/service";

export default {
    components: {
        MainContent: defineAsyncComponent(
            () => import("@/components/BlogEtc/DataCardItem.vue")
        ),
        Categories,
    },
    data() {
        return {
            categoryId: this.$route.params.id,
            categoryName: "",
            cards: [],
            page: {
                current: 0,
                size: 6,
                max: 1,
            },
            isLoading: false,
        };
    },
    watch: {
        "$route.params.id": {
            handler(newCategoryId) {
                this.categoryId = newCategoryId;
                this.page.current = 0; // Reset trang về 0
                this.loadCategoryData();
            },
            immediate: true,
        },
    },
    methods: {
        async fetchCategoryName() {
            try {
                const response = await apiClient.get(`/category/${this.categoryId}`);
                this.categoryName = response.data.name; // Thay 'name' bằng trường trả về từ API
            } catch (error) {
                console.error("Error fetching category name:", error);
                this.categoryName = "Không xác định"; // Giá trị mặc định nếu API lỗi
            }
        },
        async fetchData() {
            this.isLoading = true;
            try {
                const response = await apiClient.get(
                    `/category/${this.categoryId}/documents`,
                    {
                        params: {
                            page: this.page.current,
                            size: this.page.size,
                            sortBy: "createAt",
                            sortDirection: "desc",
                        },
                    }
                );
                const data = response.data;
                this.cards = data.content;
                this.page.max = data.totalPages;
            } catch (error) {
                console.error("Error fetching data:", error);
                this.cards = [];
            } finally {
                this.isLoading = false;
            }
        },
        async loadCategoryData() {
            await this.fetchCategoryName();
            await this.fetchData();
        },
        async gotoPage(pageNumber) {
            if (pageNumber >= 0 && pageNumber < this.page.max) {
                this.page.current = pageNumber;
                await this.fetchData();
                window.scrollTo({ top: 0, behavior: "smooth" });
            }
        },
        async gotoPrePage() {
            if (this.page.current > 0) {
                this.page.current--;
                await this.fetchData();
                window.scrollTo({ top: 0, behavior: "smooth" });
            }
        },
        async gotoNxtPage() {
            if (this.page.current < this.page.max - 1) {
                this.page.current++;
                await this.fetchData();
                window.scrollTo({ top: 0, behavior: "smooth" });
            }
        },
    },
    created() {
        this.loadCategoryData();
    },
};
</script>


<style scoped lang="scss">
.page-title {
    padding: 50px 0;
    width: 100%;

    div {
        justify-content: center;

        h1 {
            border-bottom: 1px solid #bcbcbc;
            padding-bottom: 30px;
        }
    }
}
</style>