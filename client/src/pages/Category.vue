<template>
  <div>
    <Categories />
    <MainContent
      :title="title"
      :cards="cards"
      ref="pageRef"
    />
  </div>
</template>

<script lang="ts">
import { DataCard } from '@/type/DataCard';
import { computed, defineAsyncComponent, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import Categories from '@/components/Categories/Index.vue';

export default {
  components: {
    MainContent: defineAsyncComponent(
      () => import('@/components/BlogEtc/DataCard.vue')
    ),
    Categories,
  },
  setup() {
    const route = useRoute();
    const categorySlug = ref(route.params.categorySlug || null);
    const title = ref('Tài liệu nổi bật');
    const cards = ref<DataCard[]>([]);
    const page = ref({ current: 1, max: 1 });
    const isLoading = ref(false);

    // Hàm lấy dữ liệu
    const fetchData = async () => {
      isLoading.value = true;
      try {
        const response = await fetch('/fakeData/data.json');
        const data = await response.json();

        // Lọc dữ liệu theo categorySlug (nếu có)
        const filteredData = categorySlug.value
          ? data.news.filter((item: DataCard) => item.categorySlug === categorySlug.value)
          : data.news;

        const start = (page.value.current - 1) * 10;
        const end = page.value.current * 10;
        cards.value = filteredData.slice(start, end);
        page.value.max = Math.ceil(filteredData.length / 10);

        // Cập nhật tiêu đề
        title.value = categorySlug.value
          ? `Danh mục: ${categorySlug.value}`
          : 'Tài liệu nổi bật';
      } catch (error) {
        console.error('Error fetching data:', error);
        cards.value = [];
      } finally {
        isLoading.value = false;
      }
    };

    // Theo dõi sự thay đổi của slug để tải lại dữ liệu
    watch(
      () => route.params.categorySlug,
      (newSlug) => {
        categorySlug.value = newSlug;
        page.value.current = 1; // Reset về trang đầu
        fetchData();
      }
    );

    // Lấy dữ liệu khi khởi tạo
    fetchData();

    return {
      title,
      cards,
      page,
      isLoading,
      gotoPage: async (pageNumber: number) => {
        page.value.current = pageNumber;
        await fetchData();
        window.scrollTo({ top: 0, behavior: 'smooth' });
      },
      gotoPrePage: async () => {
        if (page.value.current > 1) {
          page.value.current--;
          await fetchData();
          window.scrollTo({ top: 0, behavior: 'smooth' });
        }
      },
      gotoNxtPage: async () => {
        if (page.value.current < page.value.max) {
          page.value.current++;
          await fetchData();
          window.scrollTo({ top: 0, behavior: 'smooth' });
        }
      },
    };
  },
};
</script>
