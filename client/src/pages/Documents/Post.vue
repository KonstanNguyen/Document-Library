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
            page: {
                current: 1,
                max: 1,
            },
        };
    },
    methods: {
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
                    this.poscast = await Promise.allSettled(
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

                    this.poscast = this.poscast
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
        
        scrollToTop() {
            window.scrollTo({ top: 0, behavior: "smooth" });
        },
    },
    created() {
        this.fetchData();
        this.initializeRecommendations();
    },
};
</script>
