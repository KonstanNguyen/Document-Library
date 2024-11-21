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
	.btn-download{
		padding: 5px 22px;
		height: 50px;
		background-color: #1976d2;
		border: none;
		color: #fff;
		text-transform: uppercase;
		font-size: 14px;
		font-weight: 600;
		transition: all 0.3s ease 0s;
		&:hover{
			background-color: rgb(18, 33, 121);
		}
	}
	.page-view{
		position: relative;
		padding-top: 1.25rem;
		left: -23%;
		color: gray;
	}
</style>

<template>
	<div class="post-content-wrap mb-5">
		<h2 class="post-title">{{ post.title }}</h2>
		<div class="d-flex justify-content-between">
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
			<span class="page-view" v-html="post.summary"></span>
			<button class="btn-download"><i class="bi bi-download"></i> Tải xuống</button>
		</div>
		<div class="post-author">{{ post.author }}</div>
		<div
			class="post-content"
			v-html="post.content"></div>
		<PdfView pdfUrl="/fakeData/20191_DATN_PHAN_XUAN_PHUC_20156248.pdf" />
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
