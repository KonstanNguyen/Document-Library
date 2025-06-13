<template>
    <MainContent :post="post" :ref-card="refCard" :poscast="poscast" />
</template>

<script lang="ts">
import { DataCard } from "@/type/DataCard";
import { defineAsyncComponent } from "vue";
import apiClient from "@/api/service";
export default {
    components: {
        MainContent: defineAsyncComponent(() =>
            import("@/components/globalComponents").then(
                (module) => module.DataBlog
            )
        ),
    },
    data() {
        return {
            post: {} as DataCard,
            refCard: [],
            poscast: [],
            currentPostId: -1,
            categoryId: null,
            accountId: localStorage.getItem("userId") || "",
            userId: null,
            page: {
                current: 1,
                max: 1,
            },
        };
    },
    methods: {
        async fetchDocUserId(accountId) {
            if (!accountId) {
                await this.fetchRandomCategory();
                return;
            }
            try {
                const response = await apiClient.get(`/api/accounts/${accountId}`);
                this.userId = response.data.userId;
                if (this.userId) {
                    await this.fetchRandomCategory();
                }
            } catch (error) {
                console.error("Lỗi khi gọi API lấy thông tin tài khoản:", error);
                await this.fetchRandomCategory();
            }
        },

        async fetchRandomCategory() {
            try {
                const response = await apiClient.get('/category');
                const categories = response.data;
                if (categories && categories.length > 0) {
                    // Randomly select a category
                    const randomIndex = Math.floor(Math.random() * categories.length);
                    this.categoryId = categories[randomIndex].id;
                }
            } catch (error) {
                console.error('Error fetching random category:', error);
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
                    const filteredDocs = data.content.filter((doc: any) => doc.id !== this.post.id);
                    this.poscast = await Promise.allSettled(
                        filteredDocs.map(async (doc: any) => {
                            try {
                                const thumbnailFilename = doc.thumbnail?.replace('uploads/', '') || '/imgs/students.png';
                                const contentFilename = doc.content?.replace('uploads/', '') || null;

                                const [thumbnailResponse, contentResponse] = await Promise.all([
                                    thumbnailFilename ? apiClient.get(`/api/upload/thumbnail/${thumbnailFilename}`, { responseType: 'arraybuffer' }) : null,
                                    contentFilename ? apiClient.get(`/api/upload/content/${contentFilename}`, { responseType: 'arraybuffer' }) : null,
                                ]);

                                const thumbnailData = thumbnailResponse ? URL.createObjectURL(new Blob([thumbnailResponse.data])) : '/imgs/students.png';
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
                                    thumbnail: '/imgs/students.png',
                                    content: null,
                                };
                            }
                        })
                    );

                    this.poscast = this.poscast
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
            try {
                const documentId = this.$route.params.id;
                const response = await apiClient.get(`/api/documents/${documentId}`);
                this.post = response.data;
                this.author = response.data.authorId;
            } catch (error) {
                console.error("Error fetching data:", error);
            }
        },
        async initializeRecommendations() {
            if (this.categoryId) {
                await this.fetchRecommendDocs();
            }
        },
        scrollToTop() {
            window.scrollTo({ top: 0, behavior: "smooth" });
        },
        async incrementView() {
            try {
                await apiClient.put(`/api/documents/${this.$route.params.id}/increment-view`);
            } catch (error) {
                console.error("Error incrementing view:", error);
            }
        },
    },
    async created() {
        await this.fetchDocUserId(this.accountId);
        await this.fetchData();
        await this.incrementView();
        await this.initializeRecommendations();
    },
};
</script>
