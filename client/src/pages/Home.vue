<template>
	<div>
		<Categories />
		<MainContent
			:title="title"
			:cards="cards"
			ref="pageRef" />
	</div>
</template>

<script lang="ts">
	import { DataCard } from '@/type/DataCard';
	import { computed, defineAsyncComponent } from 'vue';
	import Categories from '@/components/Categories/Index.vue'
	export default {
		components: {
			MainContent: defineAsyncComponent(
				() => import('@/components/BlogEtc/DataCard.vue')
			),
			Categories
		},
		data() {
			return {
				// Defined data
				title: 'Tài liệu nổi bật',
				// Fetch from server
				page: {
					// current page
					current: 1,
					// max pages, auto generaged, value = max-card-can-fetch/10
					max: 1,
				},
				// Fetch from server, max 10 cards each page
				cards: [
					{
						title: 'Title',
						summary: 'Sumary',
						content: 'Content',
						image: '/imgs/students.png',
						href: '/documents/1',
						slug: 'link',
					},
					{
						title: 'Title',
						summary: 'Sumary',
						content: 'Content',
						image: '/imgs/students.png',
						href: '/documents/1',
						slug: 'link',
					},
					{
						title: 'Title',
						summary: 'Sumary',
						content: 'Content',
						image: '/imgs/students.png',
						href: '/documents/1',
						slug: 'link',
					},
					{
						title: 'Title',
						summary: 'Sumary',
						content: 'Content',
						image: '/imgs/students.png',
						href: '/documents/1',
						slug: 'link',
					},
				] as DataCard[],
			};
		},
		methods: {
			async fetchData() {
				try {
					const response = await fetch('/fakeData/data.json');
					const data = await response.json();
					const start = (this.page.current - 1) * 10;
					const end = this.page.current * 10;
					this.cards = data.news.slice(start, end);
					this.page.max = Math.ceil(data.news.length / 10);
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
				window.scrollTo({ top: 0, behavior: 'smooth' });
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