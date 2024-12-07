<style lang="scss">
.post-title {
	color: #212121;
	font-size: 30px;
	font-weight: 700;
	text-transform: capitalize;
}

.post-content-wrap {
	* {
		max-width: 100% !important;
	}

	.widget-social {
		&>* {
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

.page-view,
.page-rating {
	color: gray;
	font-weight: 600;
	margin-top: 1rem;
}
</style>

<template>
	<div class="post-content-wrap mb-5">
		<h2 class="post-title">{{ post.title }}</h2>
		<div class="d-flex gap-5">
			<div class="widget-social d-flex border-bottom pt-3 pb-3 mb-3">
				<a target="_blank" :href="`https://www.facebook.com/sharer/sharer.php?u=${getCurrentLink()}${post.href
					}`" title="Facebook" class="btn-social facebook">
					<i class="bi bi-facebook"></i>
				</a>
				<a target="_blank" :href="`https://www.linkedin.com/shareArticle?url=${getCurrentLink()}${post.href
					}`" title="Linkedin" class="btn-social linkedin">
					<i class="bi bi-linkedin"></i>
				</a>
				<a target="_blank" :href="`mailto:?subject=${post.title}&body=${getCurrentLink()}${post.href
					}`" title="Email" class="email">
					<i class="bi bi-envelope"></i>
				</a>
			</div>
			<div class="d-flex gap-2 page-view">
				<span>{{ post.views }} views</span>
				-
				<span>{{ post.ratingAvg }} <i class="bi bi-star-fill" style="color:darkgoldenrod;"></i></span>
			</div>
		</div>
		<div class="post-author"><span class="fw-bold">Người đăng:</span> {{ authorName }}</div>
		<div class="post-content" v-html="post.category?.name">
		</div>
		<PdfView v-if="post.content" class="mt-3" :pdfPath="post.content" />
	</div>
</template>

<script lang="ts">
import { PropType } from 'vue';
import { DataCard } from '@/type/DataCard';
import PdfView from "@/components/PdfView.vue";
import apiClient from "@/api/service";

export default {
	name: 'DataBlog',
	components: {
		PdfView,
	},
	props: {
		post: { type: Object as PropType<DataCard>, required: true },
	},
	data() {
		return {
			authorName: "",
		};
	},
	computed: {
		authorId() {
			return this.post ? this.post.authorId : null;
		},
	},
	methods: {
		async fetchDocUser(authorId: number) {
			console.log("Author id", authorId);
			try {
				const response = await apiClient.get(`/api/doc-users/${authorId}`);
				this.authorName = response.data.name;
			} catch (error) {
				console.error("Lỗi khi gọi API lấy thông tin tài khoản:", error);
			}
		},
		getCurrentLink() {
			return window.location.origin;
		},
	},
	watch: {
		post: {
			handler(newPost) {
				if (newPost && newPost.authorId) {
					this.fetchDocUser(newPost.authorId); 
				}
			},
			immediate: true,
		},
	},
	mounted() {
		if (this.authorId) {
			this.fetchDocUser(this.authorId);
		}
	},
};
</script>
