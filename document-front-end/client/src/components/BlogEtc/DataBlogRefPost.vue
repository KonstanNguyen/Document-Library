<template>
	<div v-if="refCard.length">
		<slot> hi </slot>
		<div class="row">
			<div
				:class="isCol ? 'col-12 mb-5' : 'col-md-6 col-lg-4 mb-10'"
				v-for="card in refCard"
				:key="card.id">
				<DataCardItem_Mini
					:card-content="card"
					:haveRM="!isCol"
					class="aos-init aos-animate"
					data-aos="fade-up"
					data-aos-delay="200" />
			</div>
		</div>
	</div>
</template>

<script lang="ts">
	import { DataCard } from '@/type/DataCard';
	import DataCardItem_Mini from './DataCardItem_Mini.vue';
	import { PropType } from 'vue';
	export default {
		name: 'DataBlogRefPost',
		components: { DataCardItem_Mini },
		props: {
			refCard: { type: Array as PropType<DataCard[]> },
			isCol: { type: Boolean, default: false },
		},
		methods: {},
	};
</script>
