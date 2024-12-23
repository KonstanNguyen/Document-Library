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
				<span>{{ post.ratingAvg }} <i class="bi bi-star-fill" style="color:#FFDB00;"></i></span>
			</div>
		</div>
		<div class="post-author"><span class="fw-bold">Người đăng:</span> {{ post.authorName }}</div>
		<div class="post-content"><span class="fw-bold">Danh mục:</span> {{ post.categoryName }}</div>

		<div>
			<star-rating v-model="userRating" :max-stars="5" @ratingData="updateRating" />
			<p>Đánh giá của bạn: {{ userRating }}</p>
		</div>
		<PdfView v-if="post.content" class="mt-3" :pdfPath="post.content" :documentId="post.id" />
	</div>
</template>

<script lang="ts">
import { PropType } from 'vue';
import { DataCard } from '@/type/DataCard';
import PdfView from "@/components/PdfView.vue";
import apiClient from "@/api/service";
import StarRating from "@/components/StarRating.vue";

export default {
	name: 'DataBlog',
	components: {
		PdfView,
		StarRating
	},
	data() {
		return {
			userRating: 0,
			accountId: localStorage.getItem("userId") || "",
		};
	},
	props: {
		post: { type: Object as PropType<DataCard>, required: true },
	},
	methods: {
		updateRating(newRating) {
			this.userRating = newRating;
			this.sendRating(newRating);
		},
		async sendRating(rating) {
			try {
				const requestData = {
					accountId: this.accountId,
					documentId: this.post.id,
					rate: rating
				};

				const response = await apiClient.post('/api/ratings', requestData);
				alert("Cảm ơn bạn đã đánh giá!")
			} catch (error) {
				console.error('Lỗi khi gửi đánh giá:', error);
			}
		},
		async fetchRatingByAccount(accountId: string) {
			try {
				const response = await apiClient.get(`/api/ratings/${accountId}`);
				const ratings = response.data; 
				const ratingForDocument = ratings.find(
					(rating: { documentId: number; accountId: number; rate: number }) =>
						rating.documentId === this.post.id
				);

				if (ratingForDocument) {
					this.userRating = ratingForDocument.rate;
				} else {
					this.userRating = 0;
				}

			} catch (error) {
				console.error("Lỗi khi gọi API lấy tài liệu:", error);
				this.message = "Lỗi khi tải đánh giá!";
			}
		},

		getCurrentLink() {
			return window.location.origin;
		},
	},
	mounted() {
		if (this.accountId) {
			this.fetchRatingByAccount(this.accountId);
		}
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
};
</script>
