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
            accountId: localStorage.getItem("userId") || "",
            userId: null,
        };
    },
    methods: {
        async fetchDocUserId(accountId) {
            try {
                const response = await apiClient.get(`/api/accounts/${accountId}`);
                this.userId = response.data.userId;
                if (this.userId) {
                    await this.fetchRecommendCategory(this.userId);
                } else {
                    this.message = "Không tìm thấy userId!";
                }
            } catch (error) {
                console.error("Lỗi khi gọi API lấy thông tin tài khoản:", error);
                this.message = "Không thể tải thông tin người dùng.";
            }
        },
        async initializeRecommendations() {
            await this.fetchRecommendCategory();
            if (this.categoryId) {
                await this.fetchRecommendDocs();
            }
        },
        async fetchRecommendCategory(userId) {
            try {
                const response = await axios.get(`http://127.0.0.1:5000`, {
                    params: {
                        user_id: this.userId
                    }
                });
                this.categoryId = response.data.recommended_category_id;
            } catch (error) {
                console.error('Error fetching recommend category:', error);
            }
        },
        async fetchRecommendDocs() {
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
                                const thumbnailFilename = doc.thumbnail?.replace('uploads/', '') || '/imgs/students.png';
                                const contentFilename = doc.content?.replace('uploads/', '') || 'fakeData/20191_DATN_PHAN_XUAN_PHUC_20156248.pdf';

                                const [thumbnailResponse, contentResponse] = await Promise.all([
                                    thumbnailFilename ? apiClient.get(`/api/upload/thumbnail/${thumbnailFilename}`, { responseType: 'arraybuffer' }) : null,
                                    contentFilename ? apiClient.get(`/api/upload/content/${contentFilename}`, { responseType: 'arraybuffer' }) : null,
                                ]);

                                const thumbnailData = thumbnailResponse ? URL.createObjectURL(new Blob([thumbnailResponse.data])) : '/imgs/students.png';
                                const contentData = contentResponse ? URL.createObjectURL(new Blob([contentResponse.data])) : 'fakeData/20191_DATN_PHAN_XUAN_PHUC_20156248.pdf';

                                return {
                                    ...doc,
                                    thumbnail: thumbnailData,
                                    content: contentData,
                                };
                            } catch (error) {
                                console.error(`Error processing document ID ${doc.id}:`, error);
                                return {
                                    ...doc,
                                    thumbnail: '/imgs/students.png',
                                    content: 'fakeData/20191_DATN_PHAN_XUAN_PHUC_20156248.pdf',
                                };
                            }
                        })
                    );

                    this.cardsR = this.cardsR
                        .map((result) => (result.status === 'fulfilled' ? result.value : null))
                        .filter(Boolean);
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
                                const thumbnailFilename = doc.thumbnail ? doc.thumbnail.replace('uploads/', '') : '/imgs/students.png';
                                const contentFilename = doc.content ? doc.content.replace('uploads/', '') : 'fakeData/20191_DATN_PHAN_XUAN_PHUC_20156248.pdf';

                                const [thumbnailResponse, contentResponse] = await Promise.all([
                                    thumbnailFilename ? apiClient.get(`/api/upload/thumbnail/${thumbnailFilename}`, { responseType: 'arraybuffer' }) : null,
                                    contentFilename ? apiClient.get(`/api/upload/content/${contentFilename}`, { responseType: 'arraybuffer' }) : null,
                                ]);

                                const thumbnailData = thumbnailResponse ? URL.createObjectURL(new Blob([thumbnailResponse.data])) : '/imgs/students.png';
                                const contentData = contentResponse ? URL.createObjectURL(new Blob([contentResponse.data])) : 'fakeData/20191_DATN_PHAN_XUAN_PHUC_20156248.pdf';

                                return {
                                    ...doc,
                                    thumbnail: thumbnailData,
                                    content: contentData,
                                };
                            } catch (error) {
                                console.error(`Error processing document ID ${doc.id}:`, error);
                                return {
                                    ...doc,
                                    thumbnail: '/imgs/students.png',
                                    content: 'fakeData/20191_DATN_PHAN_XUAN_PHUC_20156248.pdf',
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
    async created() {
        await this.fetchDocUserId(this.accountId);
        await this.fetchRecommendCategory(this.userId);
        await this.fetchData();
        this.initializeRecommendations();
    },
};
</script>