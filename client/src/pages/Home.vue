<template>
    <div>
        <div class="w-100 p-3">
            <Categories />
        </div>
        <Recommend :title="titleR" :cards="cardsR" ref="pageRefR" />
        <section>
            <MainContent :title="title" :cards="cards" ref="pageRef" />
            <Pagination />
        </section>
    </div>
</template>

<script lang="ts">
import { DataCard } from "@/type/DataCard";
import { computed, defineAsyncComponent } from "vue";
import Categories from "@/components/Categories/Index.vue";
import apiClient from "@/api/service";
import Pagination from "@/components/BlogEtc/Pagination.vue";
import axios from "axios";
export default {
    components: {
        MainContent: defineAsyncComponent(
            () => import("@/components/BlogEtc/DataCard.vue")
        ),
        Recommend: defineAsyncComponent(
            () => import("@/components/BlogEtc/DataCard.vue")
        ),
        Categories,
        Pagination
    },
    data() {
        return {
            title: "Tài liệu được xem nhiều",
            page: {
                current: 1,
                max: 1,
            },
            cards: [] as DataCard[],

            titleR: "Tài liệu đề xuất",
            cardsR: [] as DataCard[],
            categoryId: null,
        };
    },
    methods: {
        async initializeRecommendations() {
            await this.fetchRecommendCategory();
            if (this.categoryId) {
                await this.fetchRecommendDocs();
            }
        },
        async fetchRecommendCategory() {
            try {
                const response = await axios.get('http://127.0.0.1:5000');
                this.categoryId = response.data.recommended_category_id;
            } catch (error) {
                console.error('Error fetching recommend category:', error);
            }
        },
        async fetchRecommendDocs() {
            if (!this.categoryId) {
                console.warn("Category ID is not set. Cannot fetch recommended documents.");
                return;
            }

            const paginationRequest = {
                page: this.page.current - 1,
                size: 6,
                sortBy: "views",
                sortDirection: "desc",
                status: 1,
            };

            try {
                const response = await apiClient.get(`/category/${this.categoryId}/documents`, { params: paginationRequest });
                const data = response.data;

                if (data && Array.isArray(data.content)) {
                    this.cardsR = await Promise.allSettled(
                        data.content.map(async (doc: any) => {
                            try {
                                const thumbnailFilename = doc.thumbnail?.replace('uploads/', '') || null;
                                const contentFilename = doc.content?.replace('uploads/', '') || null;

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
                        })
                    );

                    this.cardsR = this.cardsR
                        .map((result) => (result.status === 'fulfilled' ? result.value : null))
                        .filter(Boolean);
                    this.page.max = data.totalPages || 1;
                } else {
                    console.warn("No content found in the recommendation response:", data);
                }
            } catch (error) {
                console.error('Error fetching recommended documents:', error.message);
            }
        },
        async fetchData() {
            const paginationRequest = {
                page: this.page.current - 1,
                size: 9,
                sortBy: "views",
                sortDirection: "desc",
                status: 1,
            };

            try {
                const response = await apiClient.get('/api/documents', { params: paginationRequest });
                const data = await response.data;

                if (data && data.content) {
                    this.cards = await Promise.allSettled(
                        data.content.map(async (doc: any) => {
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
        async gotoPage(page: number) {
            if (page >= 1 && page <= this.page.max) {
                this.page.current = page;
                await this.fetchData();
                this.scrollToTop();
            }
        },

        async gotoPrePage() {
            if (this.page.current > 1) {
                this.page.current--;
                await this.fetchData();
                this.scrollToTop();
            }
        },

        async gotoNxtPage() {
            if (this.page.current < this.page.max) {
                this.page.current++;
                await this.fetchData();
                this.scrollToTop();
            }
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
        this.initializeRecommendations();
    },
};
</script>
