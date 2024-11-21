<style lang="scss" scoped>
	.container {
		.page-title {
			padding: 50px 0;
			width: 90%;
			div {
				justify-content: center;
				h1 {
					border-bottom: 1px solid #bcbcbc;
					padding-bottom: 30px;
				}
			}
		}
	}
	.pagination {
		user-select: none;
		.page-link {
			cursor: pointer;
			background: #e9ecef;
			border-radius: 3px;
			display: inline-block;
			height: 40px;
			line-height: 40px;
			margin-left: 8px;
			text-align: center;
			width: 40px;
			padding: 0;
			&:hover {
				background: #122179;
				color: #fff;
			}
		}
	}
</style>

<template>
	<div class="container">
		<div class="page-title container">
			<div class="d-flex">
				<h1 class="col-12 text-center">{{ title }}</h1>
			</div>
		</div>
		<section
			class="contents-wrap"
			id="cards-list">
			<div class="row">
				<div
					v-for="(card, index) in cards"
					:key="index"
					class='ncard col-md-6 col-lg-4 mb-5'>
					<DataCardItem
						:cardContent="card"/>
				</div>
			</div>
		</section>
		<Pagination />
	</div>
</template>

<script lang="ts">
	import DataCardItem from './DataCardItem.vue';
	import Pagination from './Pagination.vue';
	import { DataCard } from '@/type/DataCard';

	const placeHolderCard = {
		title: 'Tiêu đề thẻ',
		summary: 'Tóm tắt của thẻ',
		content: '<h1>Nội dung thẻ</h1>',
		image: '/imgs/students.png',
		href: '/link',
		slug: 'link',
	};

	export default {
		name: 'DataCard',
		components: { DataCardItem, Pagination },
		props: {
			title: { type: String, default: 'Danh sách thẻ' },
			cards: {
				type: Array<DataCard>,
				default: [placeHolderCard, placeHolderCard],
			}, // This is from server, 10 each page
		},
		methods: {},
	};
</script>
