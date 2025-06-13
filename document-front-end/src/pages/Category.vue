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
                current: 1,
                max: 1,
            },
            isLoading: false,
        };
    },
    watch: {
        "$route.params.id": {
            handler(newCategoryId) {
                this.categoryId = newCategoryId;
                this.page.current = 0;
                this.loadCategoryData();
            },
            immediate: true,
        },
    },
    methods: {
        async fetchCategoryName() {
            try {
                const response = await apiClient.get(`/category/${this.categoryId}`);
                this.categoryName = response.data.name;
            } catch (error) {
                console.error("Error fetching category name:", error);
                this.categoryName = "Không xác định";
            }
        },
        async fetchData() {
            const paginationRequest = {
                page: this.page.current,
                size: 9,
                sortBy: "views",
                sortDirection: "desc",
                status: 1,
            };

            try {
                const response = await apiClient.get(`/category/${this.categoryId}/documents`, { params: paginationRequest });
                const data = await response.data;
                if (data && data.content) {
                    this.cards = await Promise.allSettled(
                        data.content.map(async (doc: any) => {
                            if (doc.status === 1) {
                                try {
                                    const thumbnailFilename = doc.thumbnail ? doc.thumbnail.replace('uploads/', '') : null;
                                    const contentFilename = doc.content ? doc.content.replace('uploads/', '') : null;

                                    const [thumbnailResponse, contentResponse] = await Promise.all([
                                        thumbnailFilename ? apiClient.get(`/api/upload/thumbnail/${thumbnailFilename}`, { responseType: 'arraybuffer' }) : null,
                                        contentFilename ? apiClient.get(`/api/upload/content/${contentFilename}`, { responseType: 'arraybuffer' }) : null,
                                    ]);

                                    const thumbnailData = thumbnailResponse ? URL.createObjectURL(new Blob([thumbnailResponse.data])) : null;
                                    const contentData = contentResponse ? URL.createObjectURL(new Blob([contentResponse.data])) : null;

                                    return {
                                        ...doc,
                                        thumbnail: thumbnailData,
                                        content: contentData,
                                    };
                                } catch (error) {
                                    console.error(`Error processing document ID ${doc.id}:`, error);
                                    return {
                                        ...doc,
                                        thumbnail: null,
                                        content: null,
                                    };
                                }
                            }

                        })
                    );

                    this.cards = this.cards
                        .map((result) => (result.status === 'fulfilled' ? result.value : null))
                        .filter(Boolean);
                    this.page.max = data.totalPages;
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        },
        async loadCategoryData() {
            await this.fetchCategoryName();
            await this.fetchData();
        },
        async gotoPage(page) {
            if (page >= 0 && page < this.page.max) {
                this.page.current = page;
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