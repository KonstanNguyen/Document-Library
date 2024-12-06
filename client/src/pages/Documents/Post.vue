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
        };
    },
    methods: {
        async fetchData() {
            try {
                const documentId = this.$route.params.id;
                const response = await apiClient.get(`/api/documents/${documentId}`);
                this.post = response.data;

                const allPostsResponse = await apiClient.get('/api/documents');
                const allPosts = allPostsResponse.data;

                const filteredPosts = allPosts.filter(post => post.id !== this.post.id);

                this.refCard = filteredPosts.slice(0, 3);
                this.poscast = filteredPosts.slice(3, 8);
            } catch (error) {
                console.error("Error fetching data:", error);
            }
        },
        scrollToTop() {
            window.scrollTo({ top: 0, behavior: "smooth" });
        },
    },
    created() {
        this.fetchData();
    },
};
</script>
