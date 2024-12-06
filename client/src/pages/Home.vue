<template>
    <div>
        <Categories />
        <MainContent :title="title" :cards="cards" ref="pageRef" />
    </div>
</template>

<script lang="ts">
import { DataCard } from "@/type/DataCard";
import { computed, defineAsyncComponent } from "vue";
import Categories from "@/components/Categories/Index.vue";
import apiClient from "@/api/service";
export default {
    components: {
        MainContent: defineAsyncComponent(
            () => import("@/components/BlogEtc/DataCard.vue")
        ),
        Categories,
    },
    data() {
        return {
            title: "Tài liệu nổi bật",
            page: {
                current: 1,
                max: 1,
            },
            cards: [] as DataCard[],
        };
    },
    methods: {
        async fetchData() {
            const paginationRequest = {
                page: this.page.current > 0 ? this.page.current - 1 : 0,
                size: 12,
                sortBy: "id", 
                sortDirection: "asc",
            };

            try {
                const response = await apiClient.get('/api/documents', { params: paginationRequest });
                const data = await response.data;

                if (data && data.content) {
                    // Tính toán vị trí bắt đầu và kết thúc cho phân trang
                    const start = (this.page.current - 1) * paginationRequest.size;
                    const end = this.page.current * paginationRequest.size;

                    this.cards = await Promise.all(
                        data.content.map(async (doc: any) => {
                            const thumbnailFilename = doc.thumbnail ? doc.thumbnail.replace('uploads/', '') : null;
                            const contentFilename = doc.content ? doc.content.replace('uploads/', '') : null;
                            console.log('thumbnail URL:', `/api/upload/thumbnail/${thumbnailFilename}`);
                            const thumbnailResponse = await apiClient.get(`/api/upload/thumbnail/${thumbnailFilename}`, { responseType: 'blob' });
                            const contentResponse = await apiClient.get(`/api/upload/content/${contentFilename}`, { responseType: 'blob' });
                            
                            return {
                                ...doc,
                                thumbnail: URL.createObjectURL(thumbnailResponse.data),
                                content: URL.createObjectURL(contentResponse.data),
                            };
                        })
                    );
                    console.log('Fetched data:', data); 
                    // Cập nhật lại phân trang
                    this.cards = this.cards.slice(start, end);
                    this.page.max = Math.ceil(data.content.length / paginationRequest.size);
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        },
        async gotoPage(page) {
            this.page.current = page;
            await this.fetchData();
            this.scrollToTop();
        },
        async gotoPrePage() {
            this.page.current--;
            await this.fetchData();
            this.scrollToTop();
        },
        async gotoNxtPage() {
            this.page.current++;
            await this.fetchData();
            this.scrollToTop();
        },
        scrollToTop() {
            window.scrollTo({ top: 0, behavior: "smooth" });
        },
    },
    provide() {
        return {
            gotoPage: this.gotoPage,
            gotoPrePage: this.gotoPrePage,
            gotoNxtPage: this.gotoNxtPage,
            page: computed(() => this.page),
        };
    },
    created() {
        this.fetchData();
    },
};
</script>
