<template>
	<MainContent
		:post="post"
		:ref-card="refCard"
		:poscast="poscast" />
</template>

<script lang="ts">
	import { DataCard } from '@/type/DataCard';
	import { defineAsyncComponent } from 'vue';

	export default {
		components: {
			MainContent: defineAsyncComponent(() =>
				import('@/components/globalComponents').then(
					(module) => module.DataBlog
				)
			),
		},
		data() {
			return {
				post: {
					title: '',
					summary: '',
					content: '',
					image: '',
					href: '',
					slug: '',
				} as DataCard,
				refCard: [],
				poscast: [],
				currentPostId: -1,
			};
		},
		methods: {
			async fetchData() {
				try {
					const response = await fetch('/fakeData/data.json');
					const data = await response.json();
					this.post = data.news.find(
						(news) => news.slug === this.$route.params.slug
					);

					const filteredPosts = data.news.filter(
						(post) => post.id !== this.post.id
					);
					this.poscast = filteredPosts.slice(0, 5);
					this.refCard = data.learning.slice(0, 3);
				} catch (error) {
					console.error('Error fetching data:', error);
				}
			},
			scrollToTop() {
				window.scrollTo({ top: 0, behavior: 'smooth' });
			},
		},
		created() {
			this.fetchData();
		},
		updated() {
			this.fetchData();
		},
	};
</script>
