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
                size: 24,
                sortBy: "id",
                sortDirection: "asc",
            };

            try {
                const response = await apiClient.get('/api/documents', { params: paginationRequest });
                const data = await response.data;

                if (data && data.content) {
                    const start = (this.page.current - 1) * paginationRequest.size;
                    const end = this.page.current * paginationRequest.size;

                    this.cards = await Promise.allSettled(
                        data.content.map(async (doc: any) => {
                            try {
                                const thumbnailFilename = doc.thumbnail ? doc.thumbnail.replace('uploads/', '') : null;
                                const contentFilename = doc.content ? doc.content.replace('uploads/', '') : null;

                                console.log('Processing document:', doc.id);
                                console.log('Thumbnail Filename:', thumbnailFilename);
                                console.log('Content Filename:', contentFilename);

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

                    console.log('Fetched data:', this.cards);

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
