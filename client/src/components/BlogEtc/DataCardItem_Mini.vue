<style lang="scss" scoped>
	.fit-image {
		height: 100%;
		-o-object-fit: cover;
		object-fit: cover;
		-o-object-position: center;
		object-position: center;
		width: 100%;
	}
	.card-wrap {
		color: #666;
		font-size: 15px;
		font-style: normal;
		font-weight: 400;
		position: relative;
		visibility: visible;
		font-family: Arial, Helvetica, sans-serif;
	}

	.card-wrap {
		margin-bottom: -5px;
		padding: 0;

		.card-thumbnail {
			.card-link {
				display: block;
				position: relative;

				img {
					height: 195px;
					overflow: hidden;
				}

				&:hover::before {
					opacity: 1;
					transform: scaleX(1);
					visibility: visible;
				}

				&::before {
					background-color: rgba(0, 0, 0, 0.3);
					content: '';
					height: 100%;
					left: 0;
					opacity: 0;
					position: absolute;
					top: 0;
					transform: scaleX(0);
					transition: 0.4s;
					visibility: hidden;
					width: 100%;
					z-index: 2;
				}
			}
		}

		.card-content-wrap {
			border-left: 1px solid #bcbcbc;
			border-bottom: 1px solid #bcbcbc;
			border-right: 1px solid #bcbcbc;
			padding: 5px 25px;
			font-size: 16px;
			.title {
				min-height: 60px;
				line-height: 1.3;
				color: #1976d2;
				font-size: 20px;
				font-weight: 600;
				.card-link {
					color: #1976d2;
				}
			}
			.sumary-content {
				text-align: justify;
				font-size: 13px;
				height: 60px;
				overflow: hidden;
				word-wrap: break-word;
				line-height: 20px;
			}
		}
	}
</style>

<template>
	<div class="card-wrap">
		<div class="card-thumbnail">
			<router-link
				:to="cardContent.href"
				class="card-link">
				<img
					class="fit-image"
					:src="cardContent.thumbnail" />
			</router-link>
		</div>
		<div class="card-content-wrap pt-3 pb-2">
			<h3 class="title">
				<router-link
					:to="cardContent.href"
					class="card-link">
					{{ cardContent.title }}
				</router-link>
			</h3>
		</div>
		<div
			class="card-content-wrap pt-2 pb-2"
			v-if="haveRM">
			<div class="rm">
				<i class="bi bi-journal-text me-1"></i>
				<router-link
					:to="cardContent.href"
					class="card-link">
					Xem thêm
				</router-link>
			</div>
		</div>
	</div>
</template>

<script lang="ts">
	import { PropType } from 'vue';
	import { DataCard } from '@/type/DataCard';

	export default {
		name: 'DataCardItem',
		props: {
			cardContent: {
				type: Object as PropType<DataCard>,
				required: true,
			},
			haveRM: { type: Boolean, default: false },
		},
		methods: {
			getCurrentLink() {
				return window.location.origin;
			},
		},
	};
</script>
