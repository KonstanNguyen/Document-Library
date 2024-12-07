<style lang="scss">
	.refR-title {
		color: #1a3b67;
	}
</style>

<template>
	<div class="blog-section pt-5">
		<div class="container">
			<div class="row">
				<div class="col-lg-8 col-12 order-lg-1">
					<DataBlogContent
						class="aos-init aos-animate"
						data-aos="fade-up"
						data-aos-delay="200"
						:post="post" />
				</div>
				<div class="col-lg-4 col-12 order-lg-2">
					<DataBlogRefPost
						class="aos-init aos-animate"
						data-aos="fade-up"
						data-aos-delay="200"
						:isCol="true"
						:refCard="poscast">
						<h4 class="mb-2 refR-title fw-bold">Tài liệu đề xuất</h4>
					</DataBlogRefPost>
				</div>
				<div class="col-12 order-lg-3">
					<DataBlogRefPost2
						class="aos-init aos-animate"
						data-aos="fade-up"
						data-aos-delay="200"
						:refCard="refCard">
						<h3 class="refB-title mb-2 fw-bold">
							Tài liệu liên quan
						</h3>
					</DataBlogRefPost2>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts">
	import { defineAsyncComponent, hydrateOnVisible, PropType } from 'vue';
	import DataBlogContent from './DataBlogContent.vue';
	import { DataCard } from '@/type/DataCard';

	export default {
		name: 'DataBlog',
		components: {
			DataBlogContent,
			DataBlogRefPost: defineAsyncComponent({
				loader: () => import('./DataBlogRefPost.vue'),
			}),
			DataBlogRefPost2: defineAsyncComponent({
				loader: () => import('./DataBlogRefPost.vue'),
				onError: (error, retry, fail, attempts) => {
					if (attempts <= 3) {
						retry();
					} else {
						fail();
					}
				},
				suspensible: true,
				hydrate: hydrateOnVisible(),
			}),
		},
		props: {
			post: { type: Object as PropType<DataCard>, required: true },
			poscast: { type: Array<DataCard> },
			refCard: { type: Array<DataCard> },
		},
		methods: {},
	};
</script>
