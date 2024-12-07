<template>
    <div>
        <Categories />
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
            cardsR: [
                {
                    "id": 1,
                    "title": "Chương 4: Dự Báo Với Phương Pháp Bình Quân Di Động Và San Bằng Số Mũ",
                    "authorName": "Le Nguyen Truong Giang",
                    "description": "Dự Báo Với Phương Pháp Bình Quân Di Động Và San Bằng Số Mũ",
                    "content": "",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/chuong4binhquandidong-sanbangsomu-170425121551-thumbnail.jpg?width=560&fit=bounds",
                    "views": 5,
	                "ratingAvg": 3.2,
                },
                {
                    "id": 2,
                    "title": "Đề tài: Lập dự án quán cafe sinh viên, 9 ĐIỂM!",
                    "authorName": "Viết thuê",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/tieuluanquantrihoc20054-190922033524-thumbnail.jpg?width=560&fit=bounds",
                    "views": 9,
	                "ratingAvg": 3.9,
                },
                {
                    "id": 3,
                    "title": "[NCKH] thiết kế nghiên cứu khoa học",
                    "authorName": "CLBSVHTTCNCKH",
                    "content": "Content",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/nckhthitknghincukhoahc-151227113101-thumbnail.jpg?width=560&fit=bounds",
                    "views": 12,
	                "ratingAvg": 4.8,
                },
                {
                    "id": 4,
                    "title": "KINH TẾ HỌC VĨ MÔ - Chương 5 CHÍNH SÁCH TÀI CHÍNH VÀ NGOẠI THƯƠNG",
                    "authorName": "Digiword Ha Noi",
                    "content": "Content",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/5-chinhsachtaichinhngoaithuong-091225213241-phpapp02-thumbnail.jpg?width=560&fit=bounds",
                    "views": 9,
	                "ratingAvg": 4.0,
                },
                {
                    "id": 5,
                    "title": "2024 Trend Updates: What Really Works In SEO & Content Marketing",
                    "authorName": "Search Engine Journal",
                    "content": "Content",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/conductor-webinar-6182024-presentation-240618152603-d6c54198-thumbnail.jpg?width=560&fit=bounds",
                    "views": 7,
	                "ratingAvg": 4.5,
                },
                {
                    "id": 6,
                    "title": "Tổng hợp các công thức kinh tế vi mô.",
                    "authorName": "Hoa Trò",
                    "content": "Content",
                    "thumbnail": "https://cdn.slidesharecdn.com/ss_thumbnails/tnghpcccngthckinhtvim-120912111052-phpapp01-thumbnail.jpg?width=560&fit=bounds",
                    "views": 5,
	                "ratingAvg": 4.2,
                },
            ] as DataCard[],
        };
    },
    methods: {
        async fetchData() {
            const paginationRequest = {
                page: this.page.current,
                size: 6,
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

                    this.cards = this.cards.slice(start, end);
                    this.page.max = Math.ceil(data.totalElements / paginationRequest.size);
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
    },
};
</script>
