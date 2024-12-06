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
            refCard: [
            {
                    "id": 1,
                    "title": "Chương 4: Dự Báo Với Phương Pháp Bình Quân Di Động Và San Bằng Số Mũ",
                    "author": "Le Nguyen Truong Giang",
                    "description": "Dự Báo Với Phương Pháp Bình Quân Di Động Và San Bằng Số Mũ",
                    "content": "",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/chuong4binhquandidong-sanbangsomu-170425121551-thumbnail.jpg?width=560&fit=bounds",
                    "views": 5,
	                "ratingAvg": 3.2,
                },
                {
                    "id": 2,
                    "title": "Đề tài: Lập dự án quán cafe sinh viên, 9 ĐIỂM!",
                    "author": "Viết thuê",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/tieuluanquantrihoc20054-190922033524-thumbnail.jpg?width=560&fit=bounds",
                    "views": 9,
	                "ratingAvg": 3.9,
                },
                {
                    "id": 3,
                    "title": "[NCKH] thiết kế nghiên cứu khoa học",
                    "author": "CLBSVHTTCNCKH",
                    "content": "Content",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/nckhthitknghincukhoahc-151227113101-thumbnail.jpg?width=560&fit=bounds",
                    "views": 12,
	                "ratingAvg": 4.8,
                },
                {
                    "id": 4,
                    "title": "KINH TẾ HỌC VĨ MÔ - Chương 5 CHÍNH SÁCH TÀI CHÍNH VÀ NGOẠI THƯƠNG",
                    "author": "Digiword Ha Noi",
                    "content": "Content",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/5-chinhsachtaichinhngoaithuong-091225213241-phpapp02-thumbnail.jpg?width=560&fit=bounds",
                    "views": 9,
	                "ratingAvg": 4.0,
                },
                {
                    "id": 5,
                    "title": "2024 Trend Updates: What Really Works In SEO & Content Marketing",
                    "author": "Search Engine Journal",
                    "content": "Content",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/conductor-webinar-6182024-presentation-240618152603-d6c54198-thumbnail.jpg?width=560&fit=bounds",
                    "views": 7,
	                "ratingAvg": 4.5,
                },
                {
                    "id": 6,
                    "title": "Tổng hợp các công thức kinh tế vi mô.",
                    "author": "Hoa Trò",
                    "content": "Content",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/tnghpcccngthckinhtvim-120912111052-phpapp01-thumbnail.jpg?width=560&fit=bounds",
                    "views": 5,
	                "ratingAvg": 4.2,
                },
            ],
            poscast: [
            {
                    "id": 1,
                    "title": "Chương 4: Dự Báo Với Phương Pháp Bình Quân Di Động Và San Bằng Số Mũ",
                    "author": "Le Nguyen Truong Giang",
                    "description": "Dự Báo Với Phương Pháp Bình Quân Di Động Và San Bằng Số Mũ",
                    "content": "",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/chuong4binhquandidong-sanbangsomu-170425121551-thumbnail.jpg?width=560&fit=bounds",
                    "views": 5,
	                "ratingAvg": 3.2,
                },
                {
                    "id": 2,
                    "title": "Đề tài: Lập dự án quán cafe sinh viên, 9 ĐIỂM!",
                    "author": "Viết thuê",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/tieuluanquantrihoc20054-190922033524-thumbnail.jpg?width=560&fit=bounds",
                    "views": 9,
	                "ratingAvg": 3.9,
                },
                {
                    "id": 3,
                    "title": "[NCKH] thiết kế nghiên cứu khoa học",
                    "author": "CLBSVHTTCNCKH",
                    "content": "Content",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/nckhthitknghincukhoahc-151227113101-thumbnail.jpg?width=560&fit=bounds",
                    "views": 12,
	                "ratingAvg": 4.8,
                },
                {
                    "id": 4,
                    "title": "KINH TẾ HỌC VĨ MÔ - Chương 5 CHÍNH SÁCH TÀI CHÍNH VÀ NGOẠI THƯƠNG",
                    "author": "Digiword Ha Noi",
                    "content": "Content",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/5-chinhsachtaichinhngoaithuong-091225213241-phpapp02-thumbnail.jpg?width=560&fit=bounds",
                    "views": 9,
	                "ratingAvg": 4.0,
                },
                {
                    "id": 5,
                    "title": "2024 Trend Updates: What Really Works In SEO & Content Marketing",
                    "author": "Search Engine Journal",
                    "content": "Content",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/conductor-webinar-6182024-presentation-240618152603-d6c54198-thumbnail.jpg?width=560&fit=bounds",
                    "views": 7,
	                "ratingAvg": 4.5,
                },
                {
                    "id": 6,
                    "title": "Tổng hợp các công thức kinh tế vi mô.",
                    "author": "Hoa Trò",
                    "content": "Content",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/tnghpcccngthckinhtvim-120912111052-phpapp01-thumbnail.jpg?width=560&fit=bounds",
                    "views": 5,
	                "ratingAvg": 4.2,
                },
            ],
            currentPostId: -1,
        };
    },
    methods: {
        async fetchData() {
            try {
                const documentId = this.$route.params.id;
                const response = await apiClient.get(`/api/documents/${documentId}`);
                this.post = response.data;

                // const allPostsResponse = await apiClient.get('/api/documents');
                // const allPosts = allPostsResponse.data;

                // const filteredPosts = allPosts.filter(post => post.id !== this.post.id);

                // this.refCard = filteredPosts.slice(0, 3);
                // this.poscast = filteredPosts.slice(3, 8);
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
