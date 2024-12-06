<style lang="scss">
	.post-title {
		color: #212121;
		font-size: 30px;
		font-weight: 700;
	}
	.post-content-wrap {
		* {
			max-width: 100% !important;
		}
		.widget-social {
			& > * {
				background: #e9ecef;
				border-radius: 50%;
				color: #212121;
				font-size: 16px;
				height: 35px;
				line-height: 35px;
				margin-right: 10px;
				text-align: center;
				width: 35px;
				i {
					font-weight: 400;
					font-family: 'Font Awesome 5 Brands';
				}
			}
			a:hover {
				background: #122179;
				color: #fff;
			}
		}
	}
	
	.page-view{
		position: relative;
		padding-top: 1.25rem;
		color: gray;
	}
</style>

<template>
	<div class="post-content-wrap mb-5">
		<h2 class="post-title">{{ post.title }}</h2>
		<div class="d-flex gap-5">
			<div class="widget-social d-flex border-bottom pt-3 pb-3 mb-3">
				<a
					target="_blank"
					:href="`https://www.facebook.com/sharer/sharer.php?u=${getCurrentLink()}${
						post.href
					}`"
					title="Facebook"
					class="btn-social facebook">
					<i class="bi bi-facebook"></i>
				</a>
				<a
					target="_blank"
					:href="`https://www.linkedin.com/shareArticle?url=${getCurrentLink()}${
						post.href
					}`"
					title="Linkedin"
					class="btn-social linkedin">
					<i class="bi bi-linkedin"></i>
				</a>
				<a
					target="_blank"
					:href="`mailto:?subject=${post.title}&body=${getCurrentLink()}${
						post.href
					}`"
					title="Email"
					class="email">
					<i class="bi bi-envelope"></i>
				</a>
			</div>
			<span class="page-view" v-html="post.views"></span>
			<span class="page-rating" v-html="post.ratings"></span>
		</div>
		<div class="post-author"><span class="fw-bold">Người đăng:</span> {{post.author?.name}}</div>
		<div
			class="post-content"
			v-html="post.category?.name">
		</div>
		<PdfView v-if="post.content" class="mt-3" :pdfPath="post.content" />
	</div>
</template>

<script lang="ts">
	import { PropType } from 'vue';
	import { DataCard } from '@/type/DataCard';
	import PdfView from "@/components/PdfView.vue";

	export default {
		name: 'DataBlog',
		components: {
			PdfView,
		},
		props: {
			post: { type: Object as PropType<DataCard>, required: true },
		},
		methods: {
			getCurrentLink() {
				return window.location.origin;
			},
		},
	};
</script>
