<template>
    <MainContent :post="post" :ref-card="refCard" :poscast="poscast" />
</template>

<script lang="ts">
import { DataCard } from "@/type/DataCard";
import { defineAsyncComponent } from "vue";
import apiClient from "@/api/service";
import axios from "axios";
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
            await this.fetchRecommendCategory();
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
        if (this.userId) {
            await this.incrementView();
            await this.fetchData();
            this.initializeRecommendations();
        } else {
            console.error("UserId is not set.");
        }
    },
};
</script>
