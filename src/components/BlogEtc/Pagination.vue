<style lang="scss" scoped>
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
	<div class="pagination d-flex justify-content-center">
		<ul class="pagination">
			<li
				class="page-item"
				:class="page.current === 1 ? 'disabled' : ''">
				<div
					class="page-link"
					aria-label="Previous"
					@click="gotoPrePage()">
					<span aria-hidden="true">&laquo;</span>
				</div>
			</li>
			<li
				class="page-item"
				:class="page.current === 1 ? 'disabled' : ''">
				<div
					class="page-link"
					:style="
						page.current === 1
							? 'background: #122179;	color: #fff;'
							: ''
					"
					@click="gotoPage(1)">
					1
				</div>
			</li>
			<li
				class="page-item"
				v-if="page.current > 4">
				<div class="page-link">...</div>
			</li>
			<li
				class="page-item"
				v-for="pageNumber in getPageList()"
				:key="pageNumber"
				:class="page.current === pageNumber ? 'disabled' : ''">
				<div
					class="page-link"
					:style="
						page.current === pageNumber
							? 'background: #122179;	color: #fff;'
							: ''
					"
					@click="gotoPage(pageNumber)">
					{{ pageNumber }}
				</div>
			</li>
			<li
				class="page-item"
				v-if="page.current < page.max - 3">
				<div class="page-link">...</div>
			</li>
			<li
				class="page-item"
				:class="page.current === page.max ? 'disabled' : ''">
				<div
					class="page-link"
					:style="
						page.current === page.max
							? 'background: #122179;	color: #fff;'
							: ''
					"
					@click="gotoPage(page.max)">
					{{ page.max }}
				</div>
			</li>
			<li
				class="page-item"
				:class="page.current === page.max ? 'disabled' : ''">
				<div
					class="page-link"
					aria-label="Next"
					@click="gotoNxtPage()">
					<span aria-hidden="true">&raquo;</span>
				</div>
			</li>
		</ul>
	</div>
</template>

<script lang="ts">
	import { Page } from '@/type/Page';

	export default {
		name: 'Pagination',
		inject: {
			igotoPage: { from: 'gotoPage' },
			igotoNxtPage: { from: 'gotoNxtPage' },
			igotoPrePage: { from: 'gotoPrePage' },
			ipage: { from: 'page' },
		},
		data() {
			return {
				page: { current: 1, max: 1 },
			};
		},
		created() {
			this.page = this.ipage as Page;
		},
		methods: {
			getPageList() {
				let pages: number[] = [];
				var i = this.page.current - 2;
				for (i; i <= this.page.current; i++) {
					if (i > 1 && i < this.page.max) pages.push(i);
				}
				while (pages.length < 5 && i < this.page.max) {
					pages.push(i);
					i++;
				}
				return pages;
			},
			gotoPage(page) {
				this.igotoPage(page);
			},
			gotoNxtPage() {
				this.igotoNxtPage();
			},
			gotoPrePage() {
				this.igotoPrePage();
			},
		},
	};
</script>
